package org.ta.parsers;

import java.util.Arrays;

public class TAUnit {
    private long aUnitNumber;
    private byte[] aUnitData;

    public TAUnit(long l, byte[] arrby) {
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
}
