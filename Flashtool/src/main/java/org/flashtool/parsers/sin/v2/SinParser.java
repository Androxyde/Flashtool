package org.flashtool.parsers.sin.v2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import org.flashtool.log.LogProgress;
import org.flashtool.parsers.sin.v1.PartitionInfo;
import org.flashtool.parsers.sin.v3.AddrBlock;
import org.flashtool.parsers.sin.v3.LZ4ABlock;
import org.flashtool.system.OS;
import org.flashtool.util.BytesUtil;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	  private File sinfile;
	  private long size;
	  private long dataSize=0L;
	  String dataType;
	  
	  public void setFile(File f) {
		  sinfile = f;
	  }

	  public void setLength(long s) {
		  size=s;
	  }

	  public void parseHash(JBBPBitInputStream sinStream) throws IOException {
		  JBBPParser hashBlocksV2 = JBBPParser.prepare(
		            "block[_] {int offset;"
		                    + "int length;"
		                    + "byte hashLen;"
		                    + "byte[hashLen] crc;}"
		  );
		  if (hashLen>0) {
			  byte[] hashBlocks = sinStream.readByteArray(hashLen);
			  blocks = hashBlocksV2.parse(hashBlocks).mapTo(new org.flashtool.parsers.sin.v2.HashBlocks());
			  certLen = sinStream.readInt(JBBPByteOrder.BIG_ENDIAN);
			  cert = sinStream.readByteArray(certLen);
			  if (blocks.block.length==1 && blocks.block[0].offset!=0) blocks.block[0].offset=0; 
			  if (blocks.block[0].length==16) {
				  byte[] partinfo = sinStream.readByteArray(16);
				  JBBPParser partInfo = JBBPParser.prepare(
				            "<int mot1;"
				          + "<int mot2;"
				          + "<int offset;"
				          + "<int blockcount;"
				  );
				  parti = partInfo.parse(partinfo).mapTo(new org.flashtool.parsers.sin.v1.PartitionInfo());
			  }
		  }
		  dataType=getDataTypePriv();
		  dataSize = getDataSizePriv();
	  }

	  public byte[] getHeader()  throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] buff = new byte[headerLen];
			fin.read(buff);
			fin.close();
			return buff;
	  }
	  
	  public boolean hasPartitionInfo() {
		  return blocks.block[0].length==16 && blocks.block.length>1;
	  }

	  public byte[] getPartitionInfo() throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] buff = new byte[blocks.block[0].length];
			fin.seek(headerLen+blocks.block[0].offset);
			fin.read(buff);
			fin.close();
			return buff;
	  }

	  public long getDataSizePriv() throws IOException {
		  if (dataSize>0) return dataSize;
		  if (hashLen==0) dataSize=size-headerLen;
		  else {
			  HashBlock last = blocks.block[blocks.block.length-1];
			  dataSize=last.offset+last.length;
		  }
		  return dataSize;
	  }

		public String getDataTypePriv(byte[] res) throws IOException {
			if (BytesUtil.startsWith(res, new byte[] {0x7F,0x45,0x4C,0x46})) return "elf";
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

		public long getDataSize() {
			return dataSize;
		}

		public String getDataType() {
			return dataType;
		}

	  public void dumpImage() throws IOException {
			try {
				RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
				log.info("Generating container file");
				String ext = "."+getDataType();
				String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+ext;
				RandomAccessFile fout = OS.generateEmptyFile(foutname, dataSize, (byte)0xFF);
				log.info("Finished Generating container file");
				// Positionning in files
				log.info("Extracting data into container");
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
				log.info("Extraction finished to "+foutname);
			}
			catch (Exception e) {
				log.error("Error while extracting data : "+e.getMessage());
				LogProgress.initProgress(0);
				e.printStackTrace();
			}
		}

	  public void dumpHeader()  throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+".header";
			RandomAccessFile fout = new RandomAccessFile(foutname,"rw");
			byte[] buff = new byte[headerLen];
			fin.read(buff);
			fout.write(buff);
			fout.close();
			fin.close();
			log.info("Extraction finished to "+foutname);
		}

}