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
	
	public SinFile(File f) throws SinFileException {
		sinfile = f;

		JBBPParser sinParserV2 = JBBPParser.prepare(
			    "byte multipleHeaders;"
              + "int headerLen;"
              + "byte payloadType;"
              + "short unknown;"
              + "byte memId;"
              + "byte compression;"
              + "int hashLen;"
        );		

		JBBPParser sinParserV3 = JBBPParser.prepare(
			    "byte[3] magic;"
              + "int headerLen;"
              + "int payloadType;"
              + "int hashType;"
              + "int reserved;"
              + "int hashLen;"
        );

		JBBPBitInputStream sinStream=null;
		try {
			sinStream = new JBBPBitInputStream(new FileInputStream(sinfile));
			version = sinStream.readByte();
			if (version!=2 && version!=3) throw new SinFileException("Not a sin file");
			if (version==2) {
				sinv2 = sinParserV2.parse(sinStream).mapTo(org.sinfile.parsers.v2.SinParser.class);
				if (sinv2.hashLen>sinv2.headerLen) throw new SinFileException("Error parsing sin file");
				sinv2.parseHash(sinStream);
				sinStream.close();
			}
			if (version==3) {
				sinv3 = sinParserV3.parse(sinStream).mapTo(org.sinfile.parsers.v3.SinParser.class);
				if (!new String(sinv3.magic).equals("SIN")) throw new SinFileException("Error parsing sin file");
				if (sinv3.hashLen>sinv3.headerLen) throw new SinFileException("Error parsing sin file");
				sinv3.parseHash(sinStream);
				sinv3.parseDataHeader(sinStream);
				sinStream.close();
			}
		} catch (IOException ioe) {
			try {
				sinStream.close();
			} catch (Exception e) {}
			throw new SinFileException(ioe.getMessage());
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
			builder.append("Version : "+version+"\n"
					     + "Multiple Headers : "+sinv2.multipleHeaders+"\n"
						 + "Header Length : "+sinv2.headerLen+"\n"
						 + "PayLoad Type : "+sinv2.payloadType+"\n"
					     + "Mem Id : "+sinv2.memId+"\n"
					     + "Compressoin : "+sinv2.compression+"\n"
					     + "Hash Length : "+sinv2.hashLen+"\n"
			             + "Cert Length "+sinv2.certLen+"\n");
		}
		if (version==3) {
			builder.append("Version : "+version+"\nMagic : "+new String(sinv3.magic)+"\nHeader Length : "+sinv3.headerLen+"\nPayLoad Type : "+sinv3.payloadType+"\nHash type : "+sinv3.hashType+"\nReserved : "+sinv3.reserved+"\nHashList Length : "+sinv3.hashLen+" ("+sinv3.blocks.blocks.length+" hashblocks) \nCert len : "+sinv3.certLen+"\n");
			builder.append(sinv3.addrBlocks.addrBlocks[0].dataLen+"\n");
		}
		return builder.toString();
	}
	
	public String getType() {
		if (sinv2!=null) {
			if (new String(sinv2.cert).contains("S1_Loader")) return "LOADER";
			if (new String(sinv2.cert).contains("S1_Boot")) return "BOOT";
		}
		if (sinv3!=null) {
			if (new String(sinv3.cert).contains("S1_Loader")) return "LOADER";
			if (new String(sinv3.cert).contains("S1_Boot")) return "BOOT";
		}
		return "";
	}
}