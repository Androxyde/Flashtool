package org.sinfile.parsers.v1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logger.LogProgress;
import org.sinfile.parsers.v2.HashBlock;
import org.sinfile.parsers.v2.HashBlocks;
import org.system.OS;
import org.util.BytesUtil;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;


public class SinParser {
	  @Bin public byte multipleHeaders;
	  @Bin public int headerLen;
	  @Bin public byte payloadType;
	  @Bin public short unknown;
	  @Bin public byte memId;
	  @Bin public byte compression;
	  @Bin public int hashLen;
	  public HashBlocks blocks;
	  public PartitionInfo parti;
	  public int certLen;
	  public byte[] cert;
	  public byte[] partitioninfo = null;
	  private File sinfile;
	  private long size;
	  private long dataSize=0L;
	  String dataType;
	  static final Logger logger = LogManager.getLogger(SinParser.class);

	  public void parseHash(JBBPBitInputStream sinStream) throws IOException {
		  JBBPParser hashBlocksV2 = JBBPParser.prepare(
		            "block[_] {int offset;"
		                    + "int length;"
		                    + "byte hashLen;"
		                    + "byte[hashLen] crc;}"
		  );
		  if (hashLen>0) {
			  byte[] hashBlocks = sinStream.readByteArray(hashLen);
			  blocks = hashBlocksV2.parse(hashBlocks).mapTo(new org.sinfile.parsers.v2.HashBlocks());
			  certLen = sinStream.readInt(JBBPByteOrder.BIG_ENDIAN);
			  cert = sinStream.readByteArray(certLen);
			  if (blocks.block.length==1 && blocks.block[0].offset!=0) blocks.block[0].offset=0;
			  if (blocks.block[0].length==16) {
				  partitioninfo = sinStream.readByteArray(16);
				  JBBPParser partInfo = JBBPParser.prepare(
				            "<int mot1;"
				          + "<int mot2;"
				          + "<int offset;"
				          + "<int blockcount;"
				  );
				  parti = partInfo.parse(partitioninfo).mapTo(new org.sinfile.parsers.v1.PartitionInfo());
				  if (blocks.block.length>1)
					  dataSize=parti.blockcount*blocks.block[1].length;
			  }
			  blocks.setSpare(this.payloadType);
		  }
		  dataType=getDataTypePriv();
	  }

	  public void setFile(File f) {
		  sinfile = f;
	  }

	  public void setLength(long s) {
		  size=s;
	  }

	  public String getDataType() {
		  return dataType;
	  }
	  
	  public long getDataSize() {
		  return dataSize;
	  }
	  
	  public byte[] getHeader()  throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] buff = new byte[headerLen];
			fin.read(buff);
			fin.close();
			return buff;
	  }

	  public boolean hasPartitionInfo() {
		  return partitioninfo!=null;
	  }

	  public byte[] getPartitionInfo() throws IOException {
		    return partitioninfo;
	  }

		public String getDataTypePriv(byte[] res) throws IOException {
			if (BytesUtil.startsWith(res, new byte[] {0x7F,0x45,0x4C,0x46})) return "elf";
			if (BytesUtil.startsWith(res, new byte[] {0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00})) return "yaffs2";
			int pos = BytesUtil.indexOf(res, new byte[]{(byte)0x53,(byte)0xEF,(byte)0x01,(byte)0x00});
			if (pos==-1) return "unknown";
			pos = pos - 56;
			byte[] header = new byte[58];
			System.arraycopy(res, pos, header, 0, header.length);
			byte[] bcount = new byte[4];
			System.arraycopy(header, 4, bcount, 0, bcount.length);
			BytesUtil.revert(bcount);
			long blockcount = BytesUtil.getInt(bcount);
			dataSize = blockcount*4L*1024L;
			return "ext4";
		}

		public String getDataTypePriv() throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] res=null;
			fin.seek(headerLen);
			if (hashLen==0) {
				res = new byte[(int)size-headerLen];
				fin.read(res);
			}
			else {
				int i=0;
				while (i < blocks.block.length && blocks.block[i].offset==0 ) {
					res = new byte[blocks.block[i].length];
					fin.read(res);
					i++;
				}
			}
			fin.close();
			return getDataTypePriv(res);
		}

		public void dumpImage() throws IOException {
				try {
					RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
					logger.info("Generating container file");
					String ext = "."+getDataType();
					String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+ext;
					RandomAccessFile fout = OS.generateEmptyFile(foutname, dataSize, (byte)0xFF);
					logger.info("Finished Generating container file");
					// Positionning in files
					logger.info("Extracting data into container");
					fin.seek(headerLen);
					LogProgress.initProgress(blocks.block.length);
					for (int i=0;i<blocks.block.length;i++) {
						HashBlock b = blocks.block[i];
						byte[] data = new byte[b.length];
						fin.read(data);
						if (!b.validate(data)) throw new Exception("Corrupted data");
						fout.seek(blocks.block.length==1?0:b.offset);
						fout.write(data);
						LogProgress.updateProgress();
					}
					LogProgress.initProgress(0);
					fout.close();
					logger.info("Extraction finished to "+foutname);
				}
				catch (Exception e) {
					logger.error("Error while extracting data : "+e.getMessage());
					LogProgress.initProgress(0);
					e.printStackTrace();
				}
			}

}