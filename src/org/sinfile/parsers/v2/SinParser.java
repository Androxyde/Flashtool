package org.sinfile.parsers.v2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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
}