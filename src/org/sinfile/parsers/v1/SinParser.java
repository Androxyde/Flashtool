package org.sinfile.parsers.v1;

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
	  public byte[] hashBlocks;
	  public int certLen;
	  public byte[] cert;
	  private File sinfile;
	  private long dataSize=0L;

	  public void parseHash(JBBPBitInputStream sinStream) throws IOException {
		  JBBPParser hashBlocksV2 = JBBPParser.prepare(
		            "block[_] {int offset;"
		                    + "int length;"
		                    + "byte hashLen;"
		                    + "byte[hashLen] crc;}"
		  );
		  hashBlocks = sinStream.readByteArray(hashLen);
		  org.sinfile.parsers.v2.HashBlocks blocks = hashBlocksV2.parse(hashBlocks).mapTo(org.sinfile.parsers.v2.HashBlocks.class);
		  certLen = sinStream.readInt(JBBPByteOrder.BIG_ENDIAN);
		  cert = sinStream.readByteArray(certLen);
	  }

	  public void setFile(File f) {
		  sinfile = f;
	  }

	  public byte[] getHeader()  throws IOException {
			RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
			byte[] buff = new byte[headerLen];
			fin.read(buff);
			fin.close();
			return buff;
	  }

}