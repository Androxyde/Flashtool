package flashsystem.sin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.logger.LogProgress;
import org.system.OS;
import org.util.BytesUtil;
import org.util.HexDump;


public class SinFile {

	byte[] ident = new byte[16];
	private byte[] readarray;
	File sinfile;
	byte[] yaffs2 = {0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	String datatype = null;
	long nbchunks = 0;
	long chunksize = 0;
	SinFileHeader sinheader;
	private static Logger logger = Logger.getLogger(SinFile.class);
	
	public SinFileHeader getSinHeader() {
		return sinheader;
	}
	
	public SinFile(String file) throws FileNotFoundException,IOException {
		sinfile = new File(file);
		processHeader();
		datatype = getDatatype();
		if (sinheader.hasPartitionInfo()) {
			if (sinheader.getVersion()==1) {
				int cursize = sinheader.getHashBlocks().get(1).getLength();
				sinheader.setBlockSize(cursize);
				if (sinheader.getPartitionType()==0x0A) {
					for (int i=0;i<sinheader.getHashBlocks().size();i++) {
						sinheader.getHashBlocks().get(i).setSpare(cursize%131072);
					}
				}
					
			}
			else
				sinheader.setBlockSize(512);
		}
	}

	public String getLongFileName() {
		return sinfile.getAbsolutePath();
	}
	
	public String getShortFileName() {
		return sinfile.getName();
	}
	
	public String getHeaderFileName() {
		String path = sinfile.getAbsolutePath(); 
		return path.substring(0, path.length()-3)+"header";
	}
	
	public byte[] getHeaderBytes() {
		return sinheader.getHeader();
	}	
	
	public byte[] getChunckBytes(int chunkId) throws IOException {
		RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
		long offset = sinheader.getHeaderSize()+(chunkId*chunksize);
		fin.seek(offset);
		int readcount=0;
		readcount = fin.read(readarray);
		fin.close();
		return BytesUtil.getReply(readarray,readcount);
	}
	
	public void setChunkSize(long size) {
		long datasize = sinfile.length()-sinheader.getHeaderSize();
		nbchunks = datasize / size;
		chunksize = size;
		if (datasize%size>0) nbchunks++;
		readarray = new byte[(int)size];
		sinheader.setChunkSize((int)size);
	}
	
	public long getChunkSize() {
		return chunksize;
	}
	
	public long getNbChunks() {
		return nbchunks;
	}
	
	public String getImageFileName() throws IOException {
		String path = sinfile.getAbsolutePath(); 
		return path.substring(0, path.length()-3)+getDataType();
	}

	public String getRawFileName() throws IOException {
		String path = sinfile.getAbsolutePath(); 
		return path.substring(0, path.length()-3)+"raw";
	}

	public String getPartInfoFileName() {
		String path = sinfile.getAbsolutePath(); 
		return path.substring(0, path.length()-3)+"partinfo";		
	}
	
	public void dumpRaw() throws IOException {
		logger.info("Extracting "+getShortFileName() + " header to " + getRawFileName());
		RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
		byte[] chunk = new byte[512*1024];
		int nbread;
		fin.seek(sinheader.getHeaderSize());
		FileOutputStream fout = new FileOutputStream(new File(getRawFileName()));
		while ((nbread=fin.read(chunk))!=-1) fout.write(BytesUtil.getReply(chunk, nbread));
		fout.flush();
		fout.close();
		fin.close();
		logger.info("RAW Extraction finished");
	}

	public void dumpHeader() throws IOException {
		logger.info("Extracting "+getShortFileName() + " raw data to " + getHeaderFileName());
		FileOutputStream fout = new FileOutputStream(new File(getHeaderFileName()));
		fout.write(sinheader.getHeader());
		fout.flush();
		fout.close();
		logger.info("HEADER Extraction finished");
	}
	
	public void dumpImage() throws IOException {
		if (sinheader.getVersion()==1||sinheader.getVersion()==2)
			dumpImageV1_2();
		if (sinheader.getVersion()==3) {
			dumpImageV3();
		}
	}
	
	public void dumpImageV1_2() throws IOException {
				try {
					// First I write partition info bytearray in a .partinfo file
					if (sinheader.hasPartitionInfo()) {
						FileOutputStream foutpart = new FileOutputStream(new File(getPartInfoFileName()));
						foutpart.write(sinheader.getPartitionInfo());
						foutpart.flush();
						foutpart.close();
					}		
					logger.info("Generating container file");
					RandomAccessFile fout = OS.generateEmptyFile(getImageFileName(), sinheader.getOutfileLength(), (byte)0xFF);
					logger.info("Finished Generating container file");
					RandomAccessFile findata = new RandomAccessFile(sinfile,"r");		
					// Positionning in files
					logger.info("Extracting data into container");
					findata.seek(sinheader.getHeaderSize());
					Vector<SinHashBlock> blocks = sinheader.getHashBlocks();
					LogProgress.initProgress(blocks.size());
					for (int i=0;i<blocks.size();i++) {
						SinHashBlock b = blocks.elementAt(i);
						byte[] data = new byte[b.getLength()];
						findata.read(data);
						b.validate(data);
						fout.seek(blocks.size()==1?0:b.getOffset());
						fout.write(data);
						LogProgress.updateProgress();
					}
					LogProgress.initProgress(0);
					fout.close();
					findata.close();
					logger.info("Data Extraction finished");
				}
				catch (Exception e) {
					logger.error("Error while extracting data : "+e.getMessage());
					LogProgress.initProgress(0);
					e.printStackTrace();
				}
	}

	public void dumpImageV3() throws FileNotFoundException, IOException {
		RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
		fin.seek(sinheader.getHeaderSize());
		SinDataHeader dhead = new SinDataHeader(sinheader.getHeaderSize());
		byte[] chunk = new byte[65*1024];
		fin.read(dhead.mmcfmagic);
		fin.read(dhead.mmcflength);
		fin.read(dhead.gptpmagic);
		fin.read(dhead.gptplength);
		fin.seek(sinheader.getHeaderSize()+dhead.getDataHeaderSize());
		byte[] addrmagic = new byte[4];
		boolean isaddr = true;
		int count = 0;
		long progressMax = 0;
		while (isaddr) {
			fin.read(addrmagic);
			isaddr = new String(addrmagic).equals("ADDR");
			if (isaddr) {
				SinAddr a = new SinAddr();
				System.arraycopy(addrmagic, 0, a.enregtype,0,4);
				fin.read(a.enregsize);
				fin.read(a.addrsrc);
				fin.read(a.datalen);
				fin.read(a.addrdest);
				fin.read(a.hashtype);
				a.allocateHash();
				fin.read(a.hashvalue);
				progressMax += ((a.getDataLength()/chunk.length));
				if ((a.getDataLength()%chunk.length)>0) progressMax++;
				dhead.addAddr(a);
			}
		}
		String foutname = sinfile.getAbsolutePath().substring(0, sinfile.getAbsolutePath().length()-4)+"."+dhead.computeDataSizeAndType(fin);
		logger.info("Generating empty container file");
		RandomAccessFile fout = OS.generateEmptyFile(foutname, dhead.getOutputSize(), (byte)0xFF);
		if (fout!=null) {
			logger.info("Container generated. Now extracting data to container");
			Iterator i = dhead.getAddrs().keySet().iterator();
			LogProgress.initProgress(progressMax);
			int nbloop = 0;
			while (i.hasNext()) {
				int key = ((Integer)i.next()).intValue();
				SinAddr ad = (SinAddr)dhead.getAddrs().get(key);
				fin.seek(sinheader.getHeaderSize()+dhead.getDataOffset()+ad.getSrcOffset());
				fout.seek(ad.getDestOffset());
				for (long j=0;j<ad.getDataLength()/chunk.length;j++) {
					int nbread = fin.read(chunk);
					fout.write(chunk);
					nbloop++;
					LogProgress.updateProgress();
				}
				byte[] finres = new byte[(int)(ad.getDataLength()%chunk.length)];
				if (finres.length>0) {
					int nbread = fin.read(finres);
					if (nbread!=finres.length) 
						fout.write(BytesUtil.getReply(finres, nbread));
					else
						fout.write(finres);
					nbloop++;
					LogProgress.updateProgress();
				}
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
	
	private void processHeader() throws IOException {
		byte magic[] = new byte[4];
		int nbread;
		byte headersize[] = new byte[4];
		RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
		nbread = fin.read(magic);
		if (nbread != magic.length) {
			fin.close();
			throw new IOException("Error in processHeader");			
		}
		if (HexDump.toHex(magic).equals("[03, 53, 49, 4E]")) {
			nbread = fin.read(headersize);
		}
		else {
			fin.seek(2);
			nbread = fin.read(headersize);
		}
		if(nbread != headersize.length) {
			fin.close();
			throw new IOException("Error in processHeader");
		}
		byte[] header = new byte[BytesUtil.getInt(headersize)];
		fin.seek(0);
		nbread = fin.read(header);
		sinheader = new SinFileHeader(header);
		if (sinheader.hasPartitionInfo()) {
			fin.seek(sinheader.getHeaderSize());
			byte[] part = new byte[sinheader.getPartitionInfoLength()];
			fin.read(part);
			sinheader.setPartitionInfo(part);
		}
		fin.close();
    }

	public byte[] getPartitionInfoBytes() throws IOException {
		return sinheader.getPartitionInfo();
	}

	public String getDatatype() throws IOException {
		RandomAccessFile fin = new RandomAccessFile(sinfile,"r");
		fin.seek(sinheader.getHeaderSize()+sinheader.getPartitionInfo().length);
		int read;
		try {
			read = fin.read(ident);
			if (read!=ident.length) {
				fin.close();
				throw new IOException("Error in retrieving data type");
			}
			String result = new String(ident);
			String yaffs = new String(yaffs2);
			if (result.equals(yaffs)) {
				fin.close();
				return "yaffs2";
			}
			if (result.contains("ELF")) {
				fin.close();
				return "elf";
			}
			boolean isnull = true;
			int count=0;
			for (int i=0;i<ident.length;i++) {
				if (ident[i]!=0) {
					isnull=false;
				}
			}
			while (isnull) {
				read = fin.read(ident);
				if (read==-1) throw new Exception ("End of file");
				for (int i=0;i<read;i++) {
					if (ident[i]!=0) {
						isnull=false;
					}
				}
			}
			fin.seek(fin.getFilePointer()-16+0x38);
			byte[] ident1 = new byte[2];
			read = fin.read(ident1);
			if (read==-1) throw new Exception ("End of file");
			fin.close();
			if (HexDump.toHex(ident1).contains("53, EF")) return "ext4";
			return "unknown";
		} catch (Exception e) {
			fin.close();
			return "unknown";
		}
	}

	public String getDataType() throws IOException {
		return datatype;
	}

	public byte getSpareBytes() {
		return sinheader.getPartitionType();
	}

	public String toString() {
		return sinfile.getName() + " : "+sinheader.toString();
	}
}
