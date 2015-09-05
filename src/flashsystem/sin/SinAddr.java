package flashsystem.sin;

import org.util.BytesUtil;

public class SinAddr {

	public byte[] enregtype = new byte[4];
	public byte[] enregsize = new byte[4];
	public byte[] addrsrc = new byte[8];
	public byte[] addrdest = new byte[8];
	public byte[] datalen = new byte[8];
	public byte[] hashtype = new byte[4];
	public byte[] hashvalue;

	public long getSrcOffset() {
		return BytesUtil.getLong(addrsrc);
	}

	public long getDestOffset() {
		return BytesUtil.getLong(addrdest);
	}
	
	public long getDataLength() {
		return BytesUtil.getLong(datalen);
	}

	public int getRecordLength() {
		return BytesUtil.getInt(enregsize);
	}

	public int getHashType() {
		return BytesUtil.getInt(hashtype);
	}
	
	public String toString() {
		return "ADDR : "+getRecordLength();
	}
	
	public void allocateHash() {
		if (getHashType()==2) {
			hashvalue = new byte[32];
		}
	}
}
