package flashsystem;

import org.util.BytesUtil;

public class SinPartInfo {

	byte[] partinfo=null;
	int sinversion;
	long blockcount;

	public SinPartInfo() {
	}

	public SinPartInfo(byte[] array, int version) {
		setPartInfo(array,version);
	}

	public void setPartInfo(byte[] array, int version) {
		partinfo = array;
		sinversion = version;
		byte[] nbblocks = new byte[4];
		System.arraycopy(partinfo, 12, nbblocks, 0, 4);
		BytesUtil.revert(nbblocks);
		blockcount=BytesUtil.getLong(nbblocks);
	}

	public byte[] getPartInfo() {
		if (partinfo == null) {
			byte[] b = new byte[1];
			b[0] = 0;
			return b;
		}
		return partinfo;
	}
	
	public long getNbPartitionBlocks() {
		if (partinfo == null) return 0;
		return blockcount;
	}
}
