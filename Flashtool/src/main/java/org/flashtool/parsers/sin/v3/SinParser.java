package org.flashtool.parsers.sin.v3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.logger.LogProgress;
import org.flashtool.parsers.sin.v1.PartitionInfo;
import org.flashtool.system.OS;
import org.flashtool.util.BytesUtil;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	//public byte[] dheader;
	public HashBlocks blocks;
	public DataHeader dataHeader;
	public Vector<Object> dataBlocks;
	public long size;
	private File sinfile;
	private long dataSize=0L;
	public byte[] partitioninfo = null;
	static final Logger logger = LogManager.getLogger(SinParser.class);
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
		  blocks = hashBlocksV3.parse(hashBlocks).mapTo(new org.flashtool.parsers.sin.v3.HashBlocks());

	  }
	  
	  public void parseDataHeader(JBBPBitInputStream sinStream) throws IOException {
			JBBPParser dataHeaderParser = JBBPParser.prepare(
					"byte[4] mmcfMagic;"
				  + "int mmcfLen;"
	              + "byte[4] gptpMagic;"
				  + "int gptpLen;"
	              + "byte[gptpLen-8] gptpuid;"
	        );

			JBBPParser addrBlocksParser = JBBPParser.prepare(
				    "int blockLen;"
				  + ">long dataOffset;"
	              + ">long dataLen;"
				  + ">long fileOffset;"
	              + "int hashType;"
	              + "byte[blockLen-36] checksum;"
	        );

			JBBPParser LZ4ABlocksParser = JBBPParser.prepare(
					  "int blockLen;"
					+ ">long dataOffset;"
				    + ">long uncompDataLen;"
					+ ">long compDataLen;"
				    + ">long fileOffset;"
					+ ">long reserved;"
					+ "int hashType;"
				    + "byte[blockLen-52] checksum;"
		        );

		    // First hash block seems to be Data header (addr map)
			//dheader = sinStream.readByteArray(blocks.blocks[0].length);
			//JBBPBitInputStream dheaderStream = new JBBPBitInputStream(new ByteArrayInputStream(dheader));
			try {
				byte[] mmcfmagic = new byte[4];
				sinStream.read(mmcfmagic);
				if (!new String(mmcfmagic).equals("MMCF")) throw new Exception("No MMCF");
				sinStream.skip(-4);
				dataHeader = dataHeaderParser.parse(sinStream).mapTo(new DataHeader());
			} catch (Exception e) {
				dataHeader = new DataHeader();
				dataHeader.mmcfMagic=new String("NULL").getBytes();
				dataHeader.gptpLen=0;
				dataHeader.mmcfLen=0;				
			}
			if (new String(dataHeader.mmcfMagic).equals("MMCF")) {
				long addrLength = dataHeader.mmcfLen-dataHeader.gptpLen;
				long read=0;
				byte[] amagic = new byte[4];
				dataBlocks = new Vector<Object>();
				while (read<addrLength) {
					sinStream.read(amagic);
					read+=4;
					if (new String(amagic).equals("ADDR")) {
						AddrBlock addrBlock = addrBlocksParser.parse(sinStream).mapTo(new AddrBlock());
						dataBlocks.add(addrBlock);
						read+=(addrBlock.blockLen-4);
					}
					if (new String(amagic).equals("LZ4A")) {
						LZ4ABlock lz4aBlock = LZ4ABlocksParser.parse(sinStream).mapTo(new LZ4ABlock());
						dataBlocks.add(lz4aBlock);
						read+=(lz4aBlock.blockLen-4);
					}
				}
			}
			else {
				dataHeader = new DataHeader();
				dataHeader.gptpLen=0;
				dataHeader.mmcfLen=0;
			}
			dataType=getDataTypePriv();
			dataSize = getDataSizePriv();
	  }
	  
	  public void setLength(long s) {
		  size=s;
	  }
	  
	  
	  public long getDataSizePriv() throws IOException {
		  if (dataSize>0) return dataSize;
		  if (dataHeader.mmcfLen>0) {
			  Object last = dataBlocks.lastElement();
			  if (last instanceof AddrBlock)
				  return ((AddrBlock)last).fileOffset+((AddrBlock)last).dataLen;
			  else
				  return ((LZ4ABlock)last).fileOffset+((LZ4ABlock)last).uncompDataLen;
		  }
		  else {
			  long size=0;
			  for (int i=0;i<this.blocks.blocks.length;i++) {
				  size+=this.blocks.blocks[i].length;
			  }
			  return size;
		  }		  
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
			byte[] rescomp=null;
			if (dataHeader.mmcfLen>0) {
				Object block = dataBlocks.firstElement();
				if (block instanceof AddrBlock) {
					res = new byte[(int)((AddrBlock)block).dataLen];
					fin.seek(getDataOffset()+((AddrBlock)block).dataOffset);
					fin.read(res);
					fin.close();
				}
				else {
					rescomp = new byte[(int)((LZ4ABlock)block).compDataLen];
					fin.seek(getDataOffset()+((LZ4ABlock)block).dataOffset);
					fin.read(rescomp);
					fin.close();
					LZ4Factory factory = LZ4Factory.fastestInstance();
					LZ4FastDecompressor decomp = factory.fastDecompressor();
					res = decomp.decompress(rescomp, (int)((LZ4ABlock)block).uncompDataLen);
				}
			}
			else {
				res = new byte[blocks.blocks[0].length];
				fin.seek(getDataOffset());
				fin.read(res);
				fin.close();
			}
			return getDataTypePriv(res);
		}
		
		public boolean hasPartitionInfo() {
			return false;
		}
		
		public byte[] getPartitionInfo() {
			return null;
		}
		
		public long getDataSize() {
			return dataSize;
		}
		
		public String getDataType() {
			return dataType;
		}

		public void dumpImage() throws IOException{
				RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
				int count = 0;
				int bcount=0;
				if (dataHeader.mmcfLen>0)
					bcount = dataBlocks.size();
				else
					bcount = blocks.blocks.length;
				String ext = "."+dataType;
				String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+ext;
				logger.info("Generating empty container file");
				RandomAccessFile fout = OS.generateEmptyFile(foutname, dataSize, (byte)0xFF);
				if (fout!=null) {
					logger.info("Container generated. Now extracting data to container");
					LogProgress.initProgress(bcount);
					long srcoffset=0;
					long destoffset=0;
					long dataLen=0;
					LZ4Factory factory = LZ4Factory.fastestInstance();
					LZ4FastDecompressor decomp = factory.fastDecompressor();
					long uncompLen=0;
					for (int i=0;i<bcount;i++) {
						if (dataHeader.mmcfLen>0) {
							Object objblock = dataBlocks.elementAt(i);
							if (objblock instanceof AddrBlock) {
								AddrBlock block = (AddrBlock)objblock;
								srcoffset=getDataOffset()+block.dataOffset;
								destoffset=block.fileOffset;
								dataLen=block.dataLen;
								uncompLen=0;
							}
							else {
								LZ4ABlock block = (LZ4ABlock)objblock;
								srcoffset=getDataOffset()+block.dataOffset;
								destoffset=block.fileOffset;
								dataLen=block.compDataLen;
								uncompLen=block.uncompDataLen;
							}
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
						if (uncompLen>0) {
							byte[] res = factory.fastDecompressor().decompress(data,(int)uncompLen);
							fout.write(res);
						}
						else
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

		public byte[] getHeader()  throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] buff = new byte[headerLen];
			fin.read(buff);
			fin.close();
			return buff;
		}

		public int getDataOffset() {
			if (dataHeader.mmcfLen>0)
				return headerLen+dataHeader.mmcfLen+8;
			else
				return headerLen;
		}

}
