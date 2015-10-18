package org.ta.parsers;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.util.BytesUtil;
import org.util.HexDump;

public class TAUnit {
    private int aUnitNumber;
    private byte[] aUnitData;

    public TAUnit(int l, byte[] arrby) {
        this.aUnitNumber = l;
        this.aUnitData = arrby;
    }

    public byte[] getUnitData() {
        return this.aUnitData;
    }
    
    public int getDataLength() {
    	return aUnitData.length;
    }

    public long getUnitNumber() {
        return this.aUnitNumber;
    }

    public boolean equals(Object object) {
        boolean bl = false;
        if (object instanceof TAUnit) {
            TAUnit tAUnit = (TAUnit)object;
            if (tAUnit.aUnitNumber == this.aUnitNumber) {
                bl = Arrays.equals(tAUnit.aUnitData, this.aUnitData);
            }
        }
        return bl;
    }
    
    public String getUnitHex() {
    	return HexDump.toHex(aUnitNumber);
    }
    
    public String toString() {
    	String result = HexDump.toHex(aUnitNumber) + " " + HexDump.toHex((short)aUnitData.length) + " ";
    	try {
	    	ByteArrayInputStream is = new ByteArrayInputStream(aUnitData);
	    	byte[] part = new byte[16];
	    	int nbread = is.read(part);
	    	result += HexDump.toHex(BytesUtil.getReply(part, nbread));
	    	
	    	while ((nbread=is.read(part))>0) {
	    		result+="\n              "+HexDump.toHex(BytesUtil.getReply(part, nbread));
	    	}
    	} catch (Exception e) {}
    	return result;
    }

}