package org.sinfile.parsers.v3;

import java.io.IOException;

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
	public HashBlocks blocks;
	public DataHeader dataHeader;
	public AddrBlocks addrBlocks;
	
	  
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
	              + "byte[mmcfLen-gptpLen] addrList;"
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
			byte[] dheader = sinStream.readByteArray(blocks.blocks[0].length);
			dataHeader = dataHeaderParser.parse(dheader).mapTo(DataHeader.class);
			addrBlocks = addrBlocksParser.parse(dataHeader.addrList).mapTo(AddrBlocks.class);
		  
	  }
}
