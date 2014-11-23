package org.sinfile.parsers.v3;

import java.io.IOException;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.mapper.Bin;


public class SinParser {
	@Bin public byte[] magic;
	@Bin public int headerLen;
	@Bin public int payloadType;
	@Bin public int hashType;
	@Bin public int reserved;
	@Bin public int hashLen;
	@Bin public byte[] hashBlocks;
	@Bin public int certLen;
	@Bin public byte[] cert;
	static byte[] hashv3len = {0, 0, 32};
	public HashBlocks blocks;
	public DataHeader dataHeader;
	public AddrBlocks addrBlocks;
	
	  
	  public void parseHash() throws IOException {
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
