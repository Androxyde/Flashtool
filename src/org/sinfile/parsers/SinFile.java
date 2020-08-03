package org.sinfile.parsers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import flashsystem.Category;

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
	File unpackFolder=null;

	public org.sinfile.parsers.v1.SinParser sinv1 = null;
	public org.sinfile.parsers.v2.SinParser sinv2 = null;
	public org.sinfile.parsers.v3.SinParser sinv3 = null;
	public org.sinfile.parsers.v4.SinParser sinv4 = null;
	
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

		JBBPParser sinParserv4 = JBBPParser.prepare(
			    "byte[2] magic;"
        );

		
		try {
			openStreams();
			version = sinStream.readByte();
			if (version==1) {
				sinv1 = sinParserV1.parse(sinStream).mapTo(new org.sinfile.parsers.v1.SinParser());
				sinv1.setLength(sinfile.length());
				sinv1.setFile(sinfile);
				if (sinv1.hashLen>sinv1.headerLen) throw new SinFileException("Error parsing sin file");
				sinv1.parseHash(sinStream);
				closeStreams();
			}
			if (version==2) {
				sinv2 = sinParserV2.parse(sinStream).mapTo(new org.sinfile.parsers.v2.SinParser());
				sinv2.setLength(sinfile.length());
				sinv2.setFile(sinfile);
				if (sinv2.hashLen>sinv2.headerLen) throw new SinFileException("Error parsing sin file");
				sinv2.parseHash(sinStream);
				closeStreams();
			}
			if (version==3) {
				sinv3 = sinParserV3.parse(sinStream).mapTo(new org.sinfile.parsers.v3.SinParser());
				sinv3.setLength(sinfile.length());
				sinv3.setFile(sinfile);
				if (!new String(sinv3.magic).equals("SIN")) throw new SinFileException("Error parsing sin file");
				if (sinv3.hashLen>sinv3.headerLen) throw new SinFileException("Error parsing sin file");
				sinv3.parseHash(sinStream);
				openStreams();
				sinStream.skip(sinv3.headerLen);
				sinv3.parseDataHeader(sinStream);
				closeStreams();
			} else {
				closeStreams();
				version=4;
				sinv4 = new org.sinfile.parsers.v4.SinParser(sinfile);
			}
		} catch (Exception ioe) {
			closeStreams();
			throw new SinFileException(ioe.getMessage());
		}
	}

	public File getFile() {
		return sinfile;
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
		if (sinv4!=null) {
			return sinv4.getHeader();
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
	
	public void openStreams() throws FileNotFoundException, IOException {
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
		if (sinv4!=null) {
			logger.error("This sin version is not yet supported");
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
		if (sinv4!=null) {
			logger.error("This sin version is not yet supported");
		}
		return;
	}

	public String getShortName() {
		return SinFile.getShortName(this.getName());
	}
	
	public static String getShortName(String pname) {
		String name = pname;
		int extpos = name.lastIndexOf(".");
		if (name.toUpperCase().endsWith(".TA")) {
			if (extpos!=-1)
				name = name.substring(0,extpos);
			return name;
		}
		if (extpos!=-1) {
			name = name.substring(0,extpos);
		}
		if (name.indexOf("_AID")!=-1)
			name = name.substring(0, name.indexOf("_AID"));
		if (name.indexOf("_PLATFORM")!=-1)
			name = name.substring(0, name.indexOf("_PLATFORM"));
		if (name.indexOf("_S1")!=-1)
			name = name.substring(0, name.indexOf("_S1"));
		if (name.indexOf("_X-")!=-1)
			name = name.substring(0, name.indexOf("_X-"));
		if (name.indexOf("_X_BOOT")!=-1)
			name = name.substring(0, name.indexOf("_X_BOOT"));
		if (name.indexOf("_X_Boot")!=-1)
			name = name.substring(0, name.indexOf("_X_Boot"));
		if (name.indexOf("-LUN")!=-1)
			name = name.substring(0, name.indexOf("-LUN")+5);
		if (name.toUpperCase().matches("^PARTITION-IMAGE_[0-9]+"))
			name = "partition-image";
		if (name.startsWith("elabel"))
			name = "elabel";
		//if (name.indexOf("-")!=-1)
			//name = name.substring(0, name.indexOf("-"));
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
	
	public void unpackContent() throws IOException {
		if (getVersion()==4) {
			Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
			unpackFolder=new File(sinfile.getParent()+File.separator+Category.getCategoryFromName(sinfile.getName()));
			unpackFolder.mkdirs();
			logger.info("Extracting sin content to "+Category.getCategoryFromName(sinfile.getName()));
			archiver.extract(sinfile.getAbsoluteFile(), unpackFolder);
		}
	}
	
	public String getPartitionName() {
		if (getVersion()==4) {
			Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
			try {
				String content = archiver.stream(sinfile).getNextEntry().getName();
				return content.substring(0, content.lastIndexOf("."));
			} catch (IOException ioe) {
				return "";
			}
		}
		return "";
	}
	
	public TarArchiveInputStream getTarInputStream() throws FileNotFoundException, IOException {
		if (sinv4.isGZipped())
			return new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sinfile)));
		else
			return new TarArchiveInputStream(new FileInputStream(sinfile));

	}

}