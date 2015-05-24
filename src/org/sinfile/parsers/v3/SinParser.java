package org.sinfile.parsers.v3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;
import org.logger.LogProgress;
import org.system.OS;
import org.util.BytesUtil;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;


public class SinParser {
	static byte[] hashv3len = {0, 0, 32};
	@Bin public byte[] magic;
	@Bin public int headerLen;
	@Bin public int payloadType;
	@Bin public int hashType;
	@Bin public int reserved;
	@Bin public int hashLen;
	public byte[] hashBlocks;
	public int certLen;
	public byte[] cert;
	public byte[] dheader;
	public HashBlocks blocks;
	public DataHeader dataHeader;
	public AddrBlocks addrBlocks;
	public long size;
	private File sinfile;
	private long dataSize=0L;
	private static Logger logger = Logger.getLogger(SinParser.class);
	String dataType;
	
	public void setFile(File f) {
		sinfile=f;
	}
	
	  public void parseHash(JBBPBitInputStream sinStream) throws IOException {

		  hashBlocks = sinStream.readByteArray(hashLen);
		  certLen = sinStream.readInt(JBBPByteOrder.BIG_ENDIAN);
		  cert = sinStream.readByteArray(certLen);
		  
		  JBBPParser hashBlocksV3 = JBBPParser.prepare(
		            "blocks[_] {int length;"
	              + "byte["+hashv3len[hashType]+"] crc;}"
	      );
		  blocks = hashBlocksV3.parse(hashBlocks).mapTo(org.sinfile.parsers.v3.HashBlocks.class);

	  }
	  
	  public void parseDataHeader(JBBPBitInputStream sinStream) throws IOException {
			JBBPParser dataHeaderParser = JBBPParser.prepare(
					"byte[4] mmcfMagic;"
				  + "int mmcfLen;"
	              + "byte[4] gptpMagic;"
				  + "int gptpLen;"
	              + "byte[16] uuid;"
	        );

			JBBPParser addrBlocksParser = JBBPParser.prepare(
					"addrBlocks[_] {byte[4] addrMagic;"
				  + "int addrLen;"
				  + ">long dataOffset;"
	              + ">long dataLen;"
				  + ">long fileOffset;"
	              + "int hashType;"
	              + "byte[addrLen-36] crc;}"
	        );

		    // First hash block seems to be Data header (addr map)
			dheader = sinStream.readByteArray(blocks.blocks[0].length);
			JBBPBitInputStream dheaderStream = new JBBPBitInputStream(new ByteArrayInputStream(dheader));
			dataHeader = dataHeaderParser.parse(dheaderStream).mapTo(DataHeader.class);
			if (new String(dataHeader.mmcfMagic).equals("MMCF")) {
				dataHeader.addrList = dheaderStream.readByteArray(dataHeader.gptpLen-dataHeader.mmcfLen);
				addrBlocks = addrBlocksParser.parse(dataHeader.addrList).mapTo(AddrBlocks.class);
			}
			else {
				dataHeader.gptpLen=0;
				dataHeader.mmcfLen=0;
			}
		  
	  }
	  
	  public void setLength(long s) {
		  size=s;
	  }
	  
	  public long getDataSize() throws IOException {
		  dataType=getDataType();
		  if (dataSize>0) return dataSize;
		  if (dataHeader.mmcfLen>0) {
		  AddrBlock last = addrBlocks.addrBlocks[addrBlocks.addrBlocks.length-1];
		  return last.fileOffset+last.dataLen;
		  }
		  else {
			  long size=0;
			  for (int i=0;i<this.blocks.blocks.length;i++) {
				  size+=this.blocks.blocks[i].length;
			  }
			  return size;
		  }
		  
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
			if (dataHeader.mmcfLen>0) {
				AddrBlock block = addrBlocks.addrBlocks[0];
				res = new byte[(int)block.dataLen];
				fin.seek(getDataOffset()+block.dataOffset);
				fin.read(res);
				fin.close();
			}
			else {
				res = new byte[blocks.blocks[0].length];
				fin.seek(getDataOffset());
				fin.read(res);
				fin.close();
			}
			return getDataType(res);
		}
		
		public void dumpImage() throws IOException{
				RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
				int count = 0;
				int bcount=0;
				if (dataHeader.mmcfLen>0)
					bcount = addrBlocks.addrBlocks.length;
				else
					bcount = blocks.blocks.length;
				String ext = "."+getDataType();
				String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+ext;
				logger.info("Generating empty container file");
				RandomAccessFile fout = OS.generateEmptyFile(foutname, getDataSize(), (byte)0xFF);
				if (fout!=null) {
					logger.info("Container generated. Now extracting data to container");
					LogProgress.initProgress(bcount);
					long srcoffset=0;
					long destoffset=0;
					long dataLen=0;
					for (int i=0;i<bcount;i++) {
						if (dataHeader.mmcfLen>0) {
							AddrBlock block = addrBlocks.addrBlocks[i];
							srcoffset=getDataOffset()+block.dataOffset;
							destoffset=block.fileOffset;
							dataLen=block.dataLen;
						}
						else {
							HashBlock block = blocks.blocks[i];
							if (i>0) srcoffset=srcoffset+blocks.blocks[i-1].length;
							else srcoffset=getDataOffset();
							destoffset=srcoffset-getDataOffset();
							dataLen=block.length;
						}
						fin.seek(srcoffset);
						fout.seek(destoffset);
						byte[] data = new byte[(int)dataLen];
						int nbread = fin.read(data);
						fout.write(data);
						LogProgress.updateProgress();
					}
				fout.close();
				fin.close();
				LogProgress.initProgress(0);
				logger.info("Extraction finished to "+foutname);
			}
			else {
				logger.error("An error occured while generating container");
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
			logger.info("Extraction finished to "+foutname);
			
		}

		public int getDataOffset() {
			if (dataHeader.mmcfLen>0)
				return headerLen+dataHeader.mmcfLen+8;
			else
				return headerLen;
		}

}
