package org.sinfile.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.sinfile.parsers.v3.AddrBlock;
import org.sinfile.parsers.v3.AddrBlocks;
import org.sinfile.parsers.v3.DataHeader;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

public class SinFile {

	File sinfile=null;
	int version=0;
	
	org.sinfile.parsers.v2.SinParser sinv2 = null;
	org.sinfile.parsers.v3.SinParser sinv3 = null;
	
	public SinFile(File f) throws IOException {
		sinfile = f;
		
		JBBPParser sinParserV2 = JBBPParser.prepare(
			    "byte multipleHeaders;"
              + "int headerLen;"
              + "byte payloadType;"
              + "short unknown;"
              + "byte memId;"
              + "byte compression;"
              + "int hashLen;"
              + "byte[hashLen] hashBlocks;"
              + "int certLen;"
              + "byte[certLen] cert;"
        );
		
		JBBPParser hashBlocksV2 = JBBPParser.prepare(
	            "block[_] {int offset;"
              + "int length;"
              + "byte hashLen;"
              + "byte[hashLen] crc;}"
		);
		
		JBBPParser sinParserV3 = JBBPParser.prepare(
			    "byte[3] magic;"
              + "int headerLen;"
              + "int payloadType;"
              + "int hashType;"
              + "int reserved;"
              + "int hashLen;"
              + "byte[hashLen] hashBlocks;"
              + "int certLen;"
              + "byte[certLen] cert;"
        );


		JBBPBitInputStream sinStream = new JBBPBitInputStream(new FileInputStream(sinfile));
		version = sinStream.readByte();
		if (version==2) {
			sinv2 = sinParserV2.parse(sinStream).mapTo(org.sinfile.parsers.v2.SinParser.class);
			org.sinfile.parsers.v2.HashBlocks blocks = hashBlocksV2.parse(sinv2.hashBlocks).mapTo(org.sinfile.parsers.v2.HashBlocks.class);
		}
		if (version==3) {
			sinv3 = sinParserV3.parse(sinStream).mapTo(org.sinfile.parsers.v3.SinParser.class);
			sinv3.parseHash();
			sinv3.parseDataHeader(sinStream);
		}
	}

	public String getName() {
		return sinfile.getName();
	}

	public int getVersion() {
		return version;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (version==2) {
			builder.append("Version : "+version+"\nMultiple Headers : "+sinv2.multipleHeaders+"\nHeader Length : "+sinv2.headerLen+"\nPayLoad Type : "+sinv2.payloadType+"\nMem Id : "+sinv2.memId+"\nCompressoin : "+sinv2.compression+"\nHash Length : "+sinv2.hashLen+"\n");
			builder.append(sinv2.certLen+"\n");
		}
		if (version==3) {
			builder.append("Version : "+version+"\nMagic : "+new String(sinv3.magic)+"\nHeader Length : "+sinv3.headerLen+"\nPayLoad Type : "+sinv3.payloadType+"\nHash type : "+sinv3.hashType+"\nReserved : "+sinv3.reserved+"\nHashList Length : "+sinv3.hashLen+" ("+sinv3.blocks.blocks.length+" hashblocks) \nCert len : "+sinv3.certLen+"\n");
			builder.append(sinv3.addrBlocks.addrBlocks[0].dataLen+"\n");
		}
		return builder.toString();
	}
}