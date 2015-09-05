package flashsystem.sin;

import java.util.HashMap;
import java.util.Set;

public class SinAddrMap extends HashMap {

	private int mapsize = 0;
	
	public SinAddr put(Integer pos, SinAddr addr) {
		SinAddr old = (SinAddr)super.put(pos, addr);
		mapsize+=addr.getRecordLength();
		return old;
	}

	public SinAddr put(int pos, SinAddr addr) {
		SinAddr old = (SinAddr)put(new Integer(pos), addr);
		return old;
	}
	
	public SinAddr remove(SinAddr addr) {
		SinAddr old = (SinAddr)super.remove(addr);
		mapsize-=addr.getRecordLength();
		return old;
	}
	
	public int getSize() {
		return mapsize;
	}

	public Set<Integer> keySet() {
		Set<Integer> s = super.keySet();
		return s;
	}

	public SinAddr get(int pos) {
		SinAddr a = (SinAddr)super.get(new Integer(pos));
		return a;
	}
	
	public String toString() {
		return "Total length : "+getSize()+" / Number of records : "+size();
	}
}