package flashsystem;

import org.system.OS;
import org.util.BytesUtil;
import org.util.HexDump;

public class SinHashBlock {

	long boffset;
	int blength;
	int hashsize;
	byte[] hash;
	int blockindex;
	int spare=0;

	public SinHashBlock(byte[] hashblock, int index) {
		this.spare = spare;
		byte[] offset = new byte[4];
		byte[] length = new byte[4];
		System.arraycopy(hashblock, 0, offset, 0, 4);
		System.arraycopy(hashblock, 4, length, 0, 4);
		boffset = BytesUtil.getLong(offset);
		blength = BytesUtil.getInt(length);
		hashsize=hashblock[8];
		blockindex=index;
	}
	
	public void setSpare(int spare) {
		this.spare = spare;
	}
	
	public int getHashSize() {
		return hashsize;
	}

	public long getOffset() {
		return boffset+(spare*blockindex);
	}
	
	public int getLength() {
		return blength;
	}

	public void setHash(byte[] array) {
		hash = array;
	}
	
	public byte[] getHash() {
		return hash;
	}

	public void validate(byte[] data) throws Exception {
		if (hash.length==32) {
			if (!HexDump.toHex(hash).equals(OS.getSHA256(data)))
				throw new Exception("Bad data");
		}
		else {
			if (hash.length==20) {
				if (!HexDump.toHex(hash).equals(OS.getSHA1(data)))
					throw new Exception("Bad data");
			}
			else throw new Exception("Cannot determine digest");
		}
		
	}
	
	public String toString() {
		return "Block "+blockindex+" ; Data size : "+getLength()+" ; Data hash size "+getHashSize()+" ; Dest offset : "+getOffset()+" ; Spare size : " + spare;
	}
}
