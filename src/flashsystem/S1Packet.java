package flashsystem;

import java.io.FileOutputStream;

import org.util.BytesUtil;
import org.util.HexDump;


public class S1Packet {

	//[DWORD]  CMD
	//[DWORD]  FLAGS ( 1 | 2 | 4 )
	//[DWORD]  LEN
	//[BYTE]   HDR CHECKSUM
	//[BYTE[LEN]]  DATA
	//[DWORD]  DATA CHECKSUM (CRC32)
	byte[] cmd = new byte[4];
	byte[] flags = new byte[4];
	byte[] datalen = new byte[4];
	byte hdr;
	byte[] data;
	byte[] crc32 = new byte[4];
	int lastdatapos = 0;
	int lastcrcpos = 0;
	boolean finalized = false;
	
	public S1Packet(byte[] pdata) throws X10FlashException {
		try {
			if (pdata==null) {
				validate();
				return;
			}
			if (pdata.length==0) {
				validate();
				return;
			}
			System.arraycopy(pdata, 0, cmd, 0, 4);
			System.arraycopy(pdata, 4, flags, 0, 4);
			System.arraycopy(pdata, 8, datalen, 0, 4);
			hdr = pdata[12];
			if (getDataLength()>65553)
				throw new X10FlashException("Incorect read packet. Bad Data length");
			data = new byte[getDataLength()];
			int totransfer=pdata.length-13;
			if (totransfer>getDataLength()) totransfer=getDataLength();
			lastdatapos = totransfer;
			System.arraycopy(pdata, 13, data, 0, totransfer);
			if (pdata.length>13+totransfer) {
				System.arraycopy(pdata, 13+totransfer, crc32, 0, 4);
				finalized=true;
				if (BytesUtil.getLong(calculatedCRC32())!=BytesUtil.getLong(crc32))
					throw new X10FlashException("S1 Data CRC32 Error");
				if (calculateHeaderCkSum()!=hdr)
					throw new X10FlashException("S1 Header checksum Error");
			}
		}
		catch (Exception e) {
			throw new X10FlashException(e.getMessage());
		}
	}

	public void validate() throws X10FlashException {
		try {
			if (BytesUtil.getLong(calculatedCRC32())!=BytesUtil.getLong(crc32))
				throw new X10FlashException("S1 Data CRC32 Error");
			if (calculateHeaderCkSum()!=hdr)
				throw new X10FlashException("S1 Header checksum Error");
		}
		catch (Exception e) {
			throw new X10FlashException(e.getMessage());
		}
	}
	
	public byte[] getByteArray() {
		byte[] array = new byte[17+data.length];
		System.arraycopy(cmd, 0, array, 0, 4);
		System.arraycopy(flags, 0, array, 4, 4);
		System.arraycopy(datalen, 0, array, 8, 4);
		array[12] = hdr;
		System.arraycopy(data, 0, array, 13, data.length);
		System.arraycopy(crc32, 0, array, array.length-4, 4);
		return array;
	}

	public void release() {
		cmd = null;
		flags = null;
		datalen = null;
		data = null;
		crc32 = null;
	}
	
	public S1Packet(int pcommand, byte[] pdata, boolean ongoing) {
		cmd = BytesUtil.getBytesWord(pcommand, 4);
		setFlags(false,true,ongoing);
		if (pdata==null) data = new byte[0]; else data=pdata;
		datalen = BytesUtil.getBytesWord(data.length, 4);
		hdr = calculateHeaderCkSum();
		crc32=calculatedCRC32();
	}

	public S1Packet(int pcommand, byte pdata, boolean ongoing) {
		cmd = BytesUtil.getBytesWord(pcommand, 4);
		setFlags(false,true,ongoing);
		data = new byte[] {pdata};
		datalen = BytesUtil.getBytesWord(data.length, 4);
		hdr = calculateHeaderCkSum();
		crc32=calculatedCRC32();		
	}

	public void setFlags(boolean flag1, boolean flag2, boolean ongoing) {
		flags = BytesUtil.getBytesWord(getFlag(flag1,flag2,ongoing), 4);
	}
	
	private int getFlag(boolean flag1, boolean flag2, boolean ongoing)
    {
        boolean flag = !flag1;
        byte byte0 = (byte)(flag2 ? 2 : 0);
        byte byte1 = (byte)(ongoing ? 4 : 0);
        return (((byte)(flag ? 1 : 0))) | byte0 | byte1;
    }

	public int getFlags() {
		return BytesUtil.getInt(flags);
	}
	
	public String getFlagsAsString() {
		String result = "";
		int flag1 = getFlags()&1;
		int flag2 = getFlags()&2;
		int flag3 = getFlags()&4;
		if (flag1==0) result = "true"; else result="false";
		if (flag2==0) result += ",false"; else result+=",true";
		if (flag3==0) result += ",false"; else result+=",true";
		return result;
	}
	
	public int getCommand() {
		return BytesUtil.getInt(cmd);
	}

	public int getDataLength() {
		return BytesUtil.getInt(datalen);
	}
	
	public byte[] getDataArray() {
		return data;
	}

	public String getDataString() {
		return new String(data);
	}

	public void addData(byte[] datachunk) throws X10FlashException {
		if (lastdatapos < data.length) {
			int totransfer=data.length-lastdatapos;
			if (datachunk.length<=totransfer) totransfer=datachunk.length;
			System.arraycopy(datachunk, 0, data, lastdatapos, totransfer);
			lastdatapos+=totransfer;
			if (datachunk.length>totransfer) {
				int lasttransfer=datachunk.length-totransfer;
				System.arraycopy(datachunk, totransfer, crc32, lastcrcpos, lasttransfer);
				lastcrcpos+=lasttransfer;
				finalized = (lastcrcpos==4);
			}
		}
		else {
			int lasttransfer=4-lastcrcpos;
			if (datachunk.length<lasttransfer) lasttransfer=datachunk.length;
			System.arraycopy(datachunk, 0, crc32, lastcrcpos, lasttransfer);
			lastcrcpos+=lasttransfer;
			finalized = (lastcrcpos==4);
		}
		if (finalized) {
			if (BytesUtil.getLong(calculatedCRC32())!=BytesUtil.getLong(crc32))
				throw new X10FlashException("S1 Data CRC32 Error");
			if (calculateHeaderCkSum()!=hdr)
				throw new X10FlashException("S1 Header checksum Error");
		}
	}

	public String toString() { 	
	    return "CommandID : "+getCommand()+" / Flags : "+this.getFlagsAsString()+" / Data length : "+this.getDataLength()+" / Data CRC32 : "+HexDump.toHex(crc32);
	}
	
	public byte[] calculatedCRC32() {
		if (data ==null) return null;
		return BytesUtil.getCRC32(data);
	}
	
	public boolean hasMoreToRead() {
		return !finalized;
	}

	public byte calculateHeaderCkSum()
    {
        byte header[] = new byte[12];
        System.arraycopy(cmd, 0, header, 0, 4);
        System.arraycopy(flags, 0, header, 4, 4);
        System.arraycopy(datalen, 0, header, 8, 4);
        byte result = calcSum(header);
        header = null;
        return result;
    }

	private byte calcSum(byte paramArray[])
    {
        byte byte0 = 0;
        if(paramArray.length < 12)
            return 0;
        for(int i = 0; i < 12; i++)
            byte0 ^= paramArray[i];

        byte0 += 7;
        return byte0;
    }
	
	public void saveDataAs(String file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	public byte[] getCRC32() {
		return crc32;
	}
	
	public byte[] getHeader() {
		byte[] array = new byte[13];
		System.arraycopy(cmd, 0, array, 0, 4);
		System.arraycopy(flags, 0, array, 4, 4);
		System.arraycopy(datalen, 0, array, 8, 4);
		array[12] = hdr;
		return array;
	}

}
