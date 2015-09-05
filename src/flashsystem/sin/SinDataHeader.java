package flashsystem.sin;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.util.BytesUtil;
import org.util.HexDump;

public class SinDataHeader {

	public byte[] mmcfmagic = new byte[4];
	public byte[] gptpmagic = new byte[4];
	public byte[] mmcflength = new byte[4];
	public byte[] gptplength = new byte[4];
	SinAddrMap map = new SinAddrMap();
	long outputsize = 0L;
	long _sinheader = 0L;
	
	public SinDataHeader(int sinheader) {
		_sinheader = sinheader;
	}
	
	public int getMmcfLength() {
		return BytesUtil.getInt(mmcflength);
	}

	public SinAddrMap getAddrs() {
		return map;
	}

	public int getGptpLength() {
		return BytesUtil.getInt(gptplength);
	}

	public int getDataHeaderSize() {
		return mmcfmagic.length+mmcflength.length+getGptpLength();
	}

	public String toString() {
		return "MMCF : "+getMmcfLength()+" / GPTP : "+getGptpLength();
	}

	public long getOutputSize() {
		if (outputsize > 0L) return outputsize;
		return map.get(map.size()-1).getDestOffset()+map.get(map.size()-1).getDataLength();
	}

	public void addAddr(SinAddr addr) {
		map.put(map.size(),addr);
	}

	public int getDataOffset() {
		return getDataHeaderSize() + map.getSize();
	}
	
	public String computeDataSizeAndType(RandomAccessFile fin) throws IOException {
		SinAddr a = map.get(0);
		byte[] res = new byte[(int)a.getDataLength()];
		fin.seek(_sinheader+getDataOffset()+a.getSrcOffset());
		fin.read(res);
		byte[] magic = new byte[4];
		int pos = 0;
		while (res[pos]==0) pos++;
		int startpos = pos;
		pos = 0;
		System.arraycopy(res, startpos + pos, magic, 0, magic.length);
		if (new String(magic).contains("ELF")) return "elf";
		while (!HexDump.toHex(magic).endsWith(", 53, EF]")) {
			pos++;
			try {
				System.arraycopy(res, startpos+pos, magic, 0, magic.length);
			}
			catch (Exception e) {
				return "unknown";
			}
			if (new String(magic).contains("ELF")) return "elf";
		}
		//if (pos>=500) return "unknown";
		pos = pos - 54;
		byte[] header = new byte[58];
		System.arraycopy(res, startpos + pos, header, 0, header.length);
		byte[] bcount = new byte[4];
		System.arraycopy(header, 4, bcount, 0, bcount.length);
		BytesUtil.revert(bcount);
		long blockcount = BytesUtil.getInt(bcount);
		outputsize = blockcount*4L*1024L;
		return "ext4";
	}
}