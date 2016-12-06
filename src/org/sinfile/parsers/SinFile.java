package org.sinfile.parsers;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sinfile.parsers.v3.AddrBlock;
import org.sinfile.parsers.v3.HashBlock;
import org.system.OS;
import org.util.BytesUtil;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

public class SinFile {

	File sinfile=null;
	int version=0;
	JBBPBitInputStream sinStream = null;
	FileInputStream fin = null;
	BufferedInputStream bin = null;
	private long packetsize=0;
	private long nbchunks=0;
	private long filesize;
	long totalread = 0;
	int partcount=0;

	public org.sinfile.parsers.v1.SinParser sinv1 = null;
	public org.sinfile.parsers.v2.SinParser sinv2 = null;
	public org.sinfile.parsers.v3.SinParser sinv3 = null;
	
	static final Logger logger = LogManager.getLogger(SinFile.class);
	
	public SinFile(File f) throws SinFileException {
		sinfile = f;

		JBBPParser sinParserV1 = JBBPParser.prepare(
			    "byte multipleHeaders;"
              + "int headerLen;"
              + "byte payloadType;"
              + "short unknown;"
              + "byte memId;"
              + "byte compression;"
              + "int hashLen;"
        );		

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

		try {
			openStreams();
			version = sinStream.readByte();
			if (version!=1 && version!=2 && version!=3) throw new SinFileException("Not a sin file");
			if (version==1) {
				sinv1 = sinParserV1.parse(sinStream).mapTo(org.sinfile.parsers.v1.SinParser.class);
				sinv1.setLength(sinfile.length());
				sinv1.setFile(sinfile);
				if (sinv1.hashLen>sinv1.headerLen) throw new SinFileException("Error parsing sin file");
				sinv1.parseHash(sinStream);
				closeStreams();
			}
			if (version==2) {
				sinv2 = sinParserV2.parse(sinStream).mapTo(org.sinfile.parsers.v2.SinParser.class);
				sinv2.setLength(sinfile.length());
				sinv2.setFile(sinfile);
				if (sinv2.hashLen>sinv2.headerLen) throw new SinFileException("Error parsing sin file");
				sinv2.parseHash(sinStream);
				closeStreams();
			}
			if (version==3) {
				sinv3 = sinParserV3.parse(sinStream).mapTo(org.sinfile.parsers.v3.SinParser.class);
				sinv3.setLength(sinfile.length());
				sinv3.setFile(sinfile);
				if (!new String(sinv3.magic).equals("SIN")) throw new SinFileException("Error parsing sin file");
				if (sinv3.hashLen>sinv3.headerLen) throw new SinFileException("Error parsing sin file");
				sinv3.parseHash(sinStream);
				openStreams();
				sinStream.skip(sinv3.headerLen);
				sinv3.parseDataHeader(sinStream);
				closeStreams();
			}
		} catch (Exception ioe) {
			closeStreams();
			ioe.printStackTrace();
			throw new SinFileException(ioe.getMessage());
		}
	}

	public byte[] getHeader() throws IOException {
		if (sinv1!=null) {
			return sinv1.getHeader();
		}
		if (sinv2!=null) {
			return sinv2.getHeader();
		}
		if (sinv3!=null) {
			return sinv3.getHeader();
		}
		return null;		
	}
	
	public byte getPartitionType() {
		if (sinv1!=null) {
			return sinv1.payloadType;
		}
		if (sinv2!=null) {
			return sinv2.payloadType;
		}
		if (sinv3!=null) {
			return (byte)sinv3.payloadType;
		}
		return 0;
	}

	public String getPartypeString() {
		if (getPartitionType()==0x09)
			return "Without spare";
		if (getPartitionType()==0x0A)
			return "With spare";
		if (getPartitionType()==0x20)
			return "Loader";
		if (getPartitionType()==0)
			return "Loader";
		if (getPartitionType()==3)
			return "Boot";
		if (getPartitionType()==0x24)
			return "MBR";
		if (getPartitionType()==14)
			return "MBR";
		if (getPartitionType()==15)
			return "Without spare";
		if (getPartitionType()==0x27)
			return "MMC";
		return String.valueOf(getPartitionType());
	}
 
	public void closeStreams() {
		try {
			sinStream.close();
		} catch (Exception e) {}
		try {
			fin.close();
		} catch (Exception e) {}
		try {
			bin.close();
		} catch (Exception e) {}
	}
	
	public void openStreams() throws FileNotFoundException {
		closeStreams();
		fin=new FileInputStream(sinfile);
		sinStream = new JBBPBitInputStream(fin);
	}
	
	public String getName() {
		return sinfile.getName();
	}

	public int getVersion() {
		return version;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (version==1) {
			builder.append("Version : "+version+"\n"
					     + "Multiple Headers : "+sinv1.multipleHeaders+"\n"
						 + "Header Length : "+sinv1.headerLen+"\n"
						 + "PayLoad Type : "+sinv1.payloadType+"\n"
					     + "Mem Id : "+sinv1.memId+"\n"
					     + "Compression : "+sinv1.compression+"\n"
					     + "Hash Length : "+sinv1.hashLen+"\n"
			             + "Cert Length "+sinv1.certLen+"\n");
		}
		if (version==2) {
			builder.append("Version : "+version+"\n"
					     + "Multiple Headers : "+sinv2.multipleHeaders+"\n"
						 + "Header Length : "+sinv2.headerLen+"\n"
						 + "PayLoad Type : "+sinv2.payloadType+"\n"
					     + "Mem Id : "+sinv2.memId+"\n"
					     + "Compression : "+sinv2.compression+"\n"
					     + "Hash Length : "+sinv2.hashLen+"\n"
			             + "Cert Length "+sinv2.certLen+"\n");
		}
		if (version==3) {
			builder.append("Version : "+version+"\nMagic : "+new String(sinv3.magic)+"\nHeader Length : "+sinv3.headerLen+"\nPayLoad Type : "+sinv3.payloadType+"\nHash type : "+sinv3.hashType+"\nReserved : "+sinv3.reserved+"\nHashList Length : "+sinv3.hashLen+" ("+sinv3.blocks.blocks.length+" hashblocks) \nCert len : "+sinv3.certLen+"\n");
		}
		return builder.toString();
	}
	
	public String getType() {
		if (sinv1!=null) {
			if (new String(sinv1.cert).contains("S1_Loader")) return "LOADER";
			if (new String(sinv1.cert).contains("S1_Boot")) return "BOOT";
		}
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
	
	public int getHeaderLength() {
		if (sinv1!=null) {
			return sinv1.headerLen;
		}
		if (sinv2!=null) {
			return sinv2.headerLen;
		}
		if (sinv3!=null) {
			return sinv3.headerLen;
		}
		return 0;
	}

	public boolean hasPartitionInfo() {
		if (sinv1!=null) {
			return sinv1.hasPartitionInfo();
		}
		if (sinv2!=null) {
			return sinv2.hasPartitionInfo();
		}
		if (sinv3!=null) {
			return sinv3.hasPartitionInfo();
		}
		return false;
	}

	public byte[] getPartitionInfo() throws IOException {
		if (hasPartitionInfo()) {
			if (sinv1!=null) {
				return sinv1.getPartitionInfo();
			}
			if (sinv2!=null) {
				return sinv2.getPartitionInfo();
			}
			if (sinv3!=null) {
				return sinv3.getPartitionInfo();
			}
			return null;
		}
		else return null;
	}
	
	public int getDataOffset() {
		if (sinv1!=null) {
			return sinv1.headerLen;
		}
		if (sinv2!=null) {
			return sinv2.headerLen;
		}
		if (sinv3!=null) {
			return sinv3.getDataOffset();
		}
		return 0;
	}

	public String getDataType() throws IOException {
		if (sinv1!=null) {
			return sinv1.getDataType();
		}
		if (sinv2!=null) {
			return sinv2.getDataType();
		}
		if (sinv3!=null) {
			return sinv3.getDataType();
		}
		return "";		
	}
	
	public long getDataSize() throws IOException{
		if (sinv1!=null) {
			return sinv1.getDataSize()/1024/1024;
		}
		if (sinv2!=null) {
			return sinv2.getDataSize();
		}
		if (sinv3!=null) {
			return sinv3.getDataSize();
		}
		return 0;
	}

	public void dumpImage() throws IOException{
		if (sinv1!=null) {
			sinv1.dumpImage();
		}
		if (sinv2!=null) {
			sinv2.dumpImage();
		}
		if (sinv3!=null) {
			sinv3.dumpImage();
		}
		return;
	}

	public void dumpHeader() throws IOException {
		if (sinv1!=null) {
			return;
		}
		if (sinv2!=null) {
			sinv2.dumpHeader();
		}
		if (sinv3!=null) {
			sinv3.dumpHeader();
		}
		return;		
	}

	public static String getShortName(String pname) {
		String name = pname;
		int extpos = name.lastIndexOf(".");
		if (name.toUpperCase().endsWith(".TA")) {
			if (extpos!=-1)
				name = name.substring(0,extpos);
			return name;
		}
		if (name.indexOf("_AID")!=-1)
			name = name.substring(0, name.indexOf("_AID"));
		if (name.indexOf("_PLATFORM")!=-1)
			name = name.substring(0, name.indexOf("_PLATFORM"));
		if (name.indexOf("_S1")!=-1)
			name = name.substring(0, name.indexOf("_S1"));
		if (name.startsWith("elabel"))
			name = "elabel";
		if (name.indexOf("-")!=-1)
			name = name.substring(0, name.indexOf("-"));
		extpos = name.lastIndexOf(".");
		if (extpos!=-1) {
			name = name.substring(0,extpos);
		}
		return name;
	}

	public void setChunkSize(long size) {
		packetsize=size;
		filesize=sinfile.length()-getHeaderLength();
		try {
			nbchunks = filesize/packetsize;
			if (filesize%packetsize>0) nbchunks++;
		} catch (Exception e) {}

	}

	public long getChunkSize() {
		return packetsize;
	}

	public long getNbChunks() throws IOException {
		return nbchunks;
	}

	public void openForSending() throws IOException {
		fin = new FileInputStream(sinfile);
		bin = new BufferedInputStream(fin);
		bin.skip(getHeaderLength());
		totalread=getHeaderLength();
		partcount=0;
	}
	
	public boolean hasData() throws IOException {
		return totalread < sinfile.length();
	}
	
	public void closeFromSending() {
		try {
			bin.close();
			fin.close();
		} catch (Exception e) {}
	}

	public byte[] getNextChunk() throws IOException {
			long remaining = sinfile.length()-totalread;
			long bufsize=(remaining<packetsize)?remaining:packetsize;
			byte[] buf = new byte[(int)bufsize];
			int read = bin.read(buf);
			totalread+=bufsize;
			partcount++;
			return buf;
	}

}