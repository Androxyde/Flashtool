package org.sinfile.parsers;

import java.io.File;
import java.io.FileInputStream;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

public class SinFile {

	File sinfile=null;
	int version=0;
	
	public SinFile(File f) {
		sinfile = f;
		JBBPParser sinParserV2 = JBBPParser.prepare(
			    "byte multipleHeaders;"
              + "int headerLen;"
              + "byte payloadType;"
              + "short unknown;"
              + "byte memId;"
              + "byte compression;"
              + "int hashLen;"
              + "byte[hashLen] hashblocks;"
              + "int certLen;"
              + "byte[certLen] cert;"
        );
		JBBPParser hashBlocksV2 = JBBPParser.prepare(
	            "hashblock[_] {int offset;"
              + "int length;"
              + "byte hashlength;"
              + "byte[hashlength] crc;}"
		);
		JBBPParser sinParserV3 = JBBPParser.prepare(
			    "byte[3] magic;"
              + "int headerLen;"
              + "int payloadType;"
              + "int hashType;"
              + "int reserved;"
              + "int hashLen;"
              + "hashblocks[hash_len];"
              + "int certLen;"
              + "byte[certLen] cert;"
        );
		JBBPBitInputStream sinStream = new JBBPBitInputStream(new FileInputStream(sinfile));
		version = sinStream.readByte();
		if (version==2) {
			org.sinfile.parsers.v2.SinParser sin = sinParserV2.parse(sinStream).mapTo(org.sinfile.parsers.v2.SinParser.class);
			org.sinfile.parsers.v2.HashBlocks blocks = hashBlocksV2.parse(sin.hashblocks).mapTo(org.sinfile.parsers.v2.HashBlocks.class);
	      //SinParserV2 sinV2=null;
	      //if (sin.version==2)
	      //  sinV2 = sinParserV2.parse(sinStream).mapTo(SinParserV2.class);
	      System.out.println("Version : "+version+"\nMultiple Headers : "+sin.multipleHeaders+"\nHeader Length : "+sin.headerLen+"\nPayLoad Type : "+sin.payloadType+"\nMem Id : "+sin.memId+"\nCompressoin : "+sin.compression+"\nHash Length : "+sin.hashLen);
	      System.out.println(sin.certLen);
		}
		if (version==3) {
			org.sinfile.parsers.v3.SinParser sin = sinParserV3.parse(sinStream).mapTo(org.sinfile.parsers.v3.SinParser.class);
		      //HashBlocksV3 blocks = hashBlocksV3.parse(sin.blocks).mapTo(HashBlocksV3.class);
		      System.out.println("Version : "+version+"\nMagic : "+new String(sin.magic)+"\nHeader Length : "+sin.header_len+"\nPayLoad Type : "+sin.payload_type+"\nHash type : "+sin.hash_type+"\nReserved : "+sin.reserved+"\nHashList Length : "+sin.hash_len+" ("+sin.hashblocks.length+" hashblocks) \nCert len : "+sin.cert_len);
			}
	}

	public String getName() {
		return sinfile.getName();
	}

	public int getVersion() {
		return version;
	}

}