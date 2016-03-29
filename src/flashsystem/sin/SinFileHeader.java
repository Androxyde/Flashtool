package flashsystem.sin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.util.BytesUtil;

public class SinFileHeader {

	private byte[] version = new byte[1];
	private byte[] magic = new byte[3];
	private byte[] nextHeader = new byte[1];;
	private byte[] headersize = new byte[4];
	private byte[] partitionType = new byte[1];;
	private byte[] sinreserved = new byte[4];
	private byte[] hashlistsize = new byte[4];
	private SinPartInfo partitioninfo = new SinPartInfo();
	private byte[] header;
	private Vector<SinHashBlock> blocks = new Vector<SinHashBlock>();
	private int blocksize;
	private String unit="";
	private long partitionsize;
	private int chunksize;
	private byte[] readarray;
	static final Logger logger = LogManager.getLogger(SinFileHeader.class);
	
	public void setChunkSize(int size) {
		chunksize = size;
		readarray=new byte[chunksize];
	}

	public SinFileHeader(byte[] header) {
		this.header = header;
		// Sin version
		System.arraycopy(header, 0, version, 0, 1);
		if (getVersion()==3) {
			System.arraycopy(header, 1, magic, 0, 3);
			System.arraycopy(header, 4, headersize, 0, 4);
		}
		else {
			System.arraycopy(header, 1, nextHeader, 0, 1);
			System.arraycopy(header, 2, headersize, 0, 4);
			partitionType[0] = header[6];
			System.arraycopy(header, 7, sinreserved, 0, 4);
			System.arraycopy(header, 11, hashlistsize, 0, 4);
		}
		if (getHashListSize()>0) {
				int hashoffset = 15;
				int read = 0;
				byte[] block = new byte[9];
				int index = 0;
				do {
					System.arraycopy(header, hashoffset, block, 0, block.length);
					hashoffset+=block.length;
					read+=block.length;
					SinHashBlock b = new SinHashBlock(block,index);
					byte[] hash = new byte[b.getHashSize()];
					System.arraycopy(header, hashoffset, hash, 0, hash.length);
					hashoffset+=hash.length;
					read+=hash.length;
					b.setHash(hash);
					blocks.add(b);
					if (b.getLength()>0x10)
						index++;
				} while (read < BytesUtil.getInt(hashlistsize));
		}
	}

	public int getPartitionInfoLength() {
		return blocks.get(0).getLength();
	}
	
	public boolean hasPartitionInfo() {
		return (blocks.size()>1);
	}
	
	public void setPartitionInfo(byte[] partinfo) {
		partitioninfo.setPartInfo(partinfo,getVersion());
	}
	
	public byte[] getPartitionInfo() {
		return partitioninfo.getPartInfo();
	}
	
	public void setBlockSize(int bksize) {
		blocksize = bksize;
		partitionsize = partitioninfo.getNbPartitionBlocks()*blocksize;
		long gig = 1024*1024*1024;
		long meg = 1024*1024;
		if (partitionsize/gig>0) {
			unit = unit+Long.toString(partitionsize/gig)+"."+Long.toString((partitionsize%gig)/meg)+"Gb";
		}
		else {
			unit = unit+Long.toString(partitionsize/meg)+"."+Long.toString((partitionsize%meg)/1024)+"Mb";
		}
		logger.debug("Sin version "+getVersion()+" ; Partition block count : " + partitioninfo.getNbPartitionBlocks()+ " ; Partition block size : "+blocksize+" ; Partition size : "+unit);
	}
	
	public long getOutfileLength() {
		return partitionsize;
	}

	public String getOutfileLengthString() {
		return unit;
	}

	public int getNbHashBlocks() {
		return blocks.size();
	}
	
	public int getVersion() {
		return BytesUtil.getInt(version);
	}
	
	public int getNextHeader() {
		return BytesUtil.getInt(nextHeader);
	}
	
	public byte getPartitionType() {
		return partitionType[0];
	}

	public byte[] getHeader() {
		return header;
	}
	
	public String getPartypeString() {
		if (partitionType[0]==0x09)
			return "Without spare";
		if (partitionType[0]==0x0A)
			return "With spare";
		return "unknown";
	}
	
	public Vector<SinHashBlock> getHashBlocks() {
		return blocks;
	}
	
	public int getHashListSize() {
		return BytesUtil.getInt(hashlistsize);
	}

	public int getHeaderSize() {
		return BytesUtil.getInt(headersize);
	}

	public int getNbChunks() {
		int nbparts = getHeaderSize() / (int)chunksize;
		if (getHeaderSize() % (int)chunksize>0) nbparts++;
		return nbparts;

	}
	public byte[] getChunckBytes(int chunkId) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(header);
		int offset = chunkId*chunksize;
		is.skip(offset);
		int readcount = is.read(readarray);
		is.close();
		if (readcount<chunksize)
			return BytesUtil.getReply(readarray,readcount);
		else
			return readarray;
	}

	public String toString() {
		return "header size : "+getHeaderSize();
	}
}
