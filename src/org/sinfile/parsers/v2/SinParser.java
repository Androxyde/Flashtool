package org.sinfile.parsers.v2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import org.apache.log4j.Logger;
import org.logger.LogProgress;
import org.sinfile.parsers.v3.AddrBlock;
import org.sinfile.parsers.v3.LZ4ABlock;
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
	  public int certLen;
	  public byte[] cert;
	  private File sinfile;
	  private long dataSize=0L;
	  private static Logger logger = Logger.getLogger(SinParser.class);
	  
	  public void setFile(File f) {
		  sinfile = f;
	  }
	  
	  public void parseHash(JBBPBitInputStream sinStream) throws IOException {
		  JBBPParser hashBlocksV2 = JBBPParser.prepare(
		            "block[_] {int offset;"
		                    + "int length;"
		                    + "byte hashLen;"
		                    + "byte[hashLen] crc;}"
		  );
		  byte[] hashBlocks = sinStream.readByteArray(hashLen);
		  blocks = hashBlocksV2.parse(hashBlocks).mapTo(org.sinfile.parsers.v2.HashBlocks.class);
		  certLen = sinStream.readInt(JBBPByteOrder.BIG_ENDIAN);
		  cert = sinStream.readByteArray(certLen);
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

	  public String getDataType(byte[] res) throws IOException {
			if (BytesUtil.startsWith(res, new byte[] {0x7F,0x45,0x4C,0x46})) return "elf";
			int pos = BytesUtil.indexOf(res, new byte[]{0x53,(byte)0xEF});
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

	  public String getDataType() throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] res=null;
			/*
			byte[] rescomp=null;
			res = new byte[blocks.block[0].length];
			fin.seek(getDataOffset());
			fin.read(res);
			fin.close();*/		
			return getDataType(res);
	  }

	  public void dumpImage() throws IOException {
/*			try {
				// First I write partition info bytearray in a .partinfo file
				if (hasPartitionInfo()) {
					FileOutputStream foutpart = new FileOutputStream(new File(getPartInfoFileName()));
					foutpart.write(sinheader.getPartitionInfo());
					foutpart.flush();
					foutpart.close();
				}		
				logger.info("Generating container file");
				String ext = "."+getDataType();
				String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+ext;
				logger.info("Finished Generating container file");
				RandomAccessFile findata = new RandomAccessFile(sinfile,"r");		
				// Positionning in files
				logger.info("Extracting data into container");
				findata.seek(sinheader.getHeaderSize());
				Vector<SinHashBlock> blocks = sinheader.getHashBlocks();
				LogProgress.initProgress(blocks.size());
				for (int i=0;i<blocks.size();i++) {
					SinHashBlock b = blocks.elementAt(i);
					byte[] data = new byte[b.getLength()];
					findata.read(data);
					b.validate(data);
					fout.seek(blocks.size()==1?0:b.getOffset());
					fout.write(data);
					LogProgress.updateProgress();
				}
				LogProgress.initProgress(0);
				fout.close();
				findata.close();
				logger.info("Data Extraction finished");
			}
			catch (Exception e) {
				logger.error("Error while extracting data : "+e.getMessage());
				LogProgress.initProgress(0);
				e.printStackTrace();
			}*/
		}

}