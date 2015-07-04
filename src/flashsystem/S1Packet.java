package flashsystem;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.util.BytesUtil;
import org.util.HexDump;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;

public class S1Packet {

	//[DWORD]  CMD
	//[DWORD]  FLAGS ( 1 | 2 | 4 )
	//[DWORD]  LEN
	//[BYTE]   HDR CHECKSUM
	//[BYTE[LEN]]  DATA
	//[DWORD]  DATA CHECKSUM (CRC32)
	int command=0;
	int flag=0;
	int length=0;
	byte hdrcksum=0;

	byte[] data=null;
	byte[] crc32=null;
	private byte[] tempbuffer=null;
	

	public S1Packet(byte[] pdata) throws IOException {
		addData(pdata);
	}

	public S1Packet(int pcommand, byte[] pdata, boolean ongoing) {
		command = pcommand;
		setFlags(false,true,ongoing);
		if (pdata==null) 
			length = 0;
		else
			length = pdata.length;
		data=pdata;
		hdrcksum = calculateHeaderCkSum();
		crc32=calculatedCRC32();
	}

	public S1Packet(int pcommand, byte pdata, boolean ongoing) {
		command = pcommand;
		setFlags(false,true,ongoing);
		data = new byte[] {pdata};
		length=1;
		hdrcksum = calculateHeaderCkSum();
		crc32=calculatedCRC32();		
	}

	public boolean isValid() {
		if (BytesUtil.getLong(calculatedCRC32())!=BytesUtil.getLong(crc32))
			return false;
		if (calculateHeaderCkSum()!=hdrcksum)
			return false;
		return true;
	}
	
	public void validate() throws X10FlashException {
		try {
			if (BytesUtil.getLong(calculatedCRC32())!=BytesUtil.getLong(crc32))
				throw new X10FlashException("S1 Data CRC32 Error");
			if (calculateHeaderCkSum()!=hdrcksum)
				throw new X10FlashException("S1 Header checksum Error");
		}
		catch (Exception e) {
			throw new X10FlashException(e.getMessage());
		}
	}
	
	public byte[] getByteArray() {
		if (length==0)
			return BytesUtil.concatAll(getHeader(), new byte[] {hdrcksum}, crc32);
		else
			return BytesUtil.concatAll(getHeader(), new byte[] {hdrcksum}, data, crc32);
	}

	public void release() {
		data = null;
		crc32 = null;
	}

	public void setFlags(boolean flag1, boolean flag2, boolean ongoing) {
		flag = getFlag(flag1,flag2,ongoing);
	}

	private int getFlag(boolean flag1, boolean flag2, boolean ongoing)
    {
        boolean flag = !flag1;
        byte byte0 = (byte)(flag2 ? 2 : 0);
        byte byte1 = (byte)(ongoing ? 4 : 0);
        return (((byte)(flag ? 1 : 0))) | byte0 | byte1;
    }

	public int getFlags() {
		 return flag;
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
		return command;
	}

	public int getDataLength() {
		return length;
	}
	
	public byte[] getDataArray() {
		return data;
	}

	public String getDataString() {
		return new String(data);
	}

	public void addData(byte[] datachunk) throws IOException  {
		JBBPBitInputStream chunkStream = new JBBPBitInputStream(new ByteArrayInputStream(datachunk));
		while (chunkStream.hasAvailableData()) {
			if (tempbuffer==null)
				tempbuffer = chunkStream.readByteArray(1);
			else
				tempbuffer=BytesUtil.concatAll(tempbuffer, chunkStream.readByteArray(1));
			if (!isHeaderValid()) {
				if (tempbuffer.length==13) {
					JBBPBitInputStream headerStream = new JBBPBitInputStream(new ByteArrayInputStream(tempbuffer));
					command=headerStream.readInt(JBBPByteOrder.BIG_ENDIAN);
					flag=headerStream.readInt(JBBPByteOrder.BIG_ENDIAN);
					length=headerStream.readInt(JBBPByteOrder.BIG_ENDIAN);
					hdrcksum=(byte)headerStream.readByte();
					headerStream.close();
					tempbuffer=null;
				}
			}
			else if (!isDataComplete()) {
				if (tempbuffer.length==length) {
					JBBPBitInputStream dataStream = new JBBPBitInputStream(new ByteArrayInputStream(tempbuffer));
					data = dataStream.readByteArray(length);
					dataStream.close();
					tempbuffer=null;
				}
			}
			else if (!isCRCComplete()) {
				if (tempbuffer.length==4) {
					JBBPBitInputStream crcStream = new JBBPBitInputStream(new ByteArrayInputStream(tempbuffer));
					crc32 = crcStream.readByteArray(4);
					crcStream.close();
					tempbuffer=null;
				}
			}
		}
		chunkStream.close();
	}

	public String toString() { 	
	    return "CommandID : "+getCommand()+" / Flags : "+this.getFlagsAsString()+" / Data length : "+this.getDataLength()+" / Data CRC32 : "+HexDump.toHex(crc32);
	}

	public byte[] calculatedCRC32() {
		if (data ==null) return null;
		return BytesUtil.getCRC32(data);
	}

	public boolean isHeaderValid() {
		if (command==0) return false;
		return hdrcksum==calculateHeaderCkSum();
	}

	public boolean isDataComplete() {
		if (data==null && length==0) return true;
		if (data==null) return false;
		if (data.length<length) return false;
		return true;
	}

	public boolean isCRCComplete() {
		if (crc32==null) return false;
		if (crc32.length<4) return false;
		return true;
	}

	public boolean hasMoreToRead() {
		return !(isHeaderValid() && isDataComplete() && isCRCComplete());
	}

	public byte calculateHeaderCkSum()
    {
        byte header[] = getHeader();
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
		return BytesUtil.concatAll(BytesUtil.getBytesWord(command, 4),
				                   BytesUtil.getBytesWord(flag, 4),
				                   BytesUtil.getBytesWord(length, 4)
				                  );
	}

	public byte[] getHeaderWithChecksum() {
		return BytesUtil.concatAll(BytesUtil.getBytesWord(command, 4),
				                   BytesUtil.getBytesWord(flag, 4),
				                   BytesUtil.getBytesWord(length, 4),
				                   new byte[] {hdrcksum}
				                  );
	}

	public byte getCksum() {
		return hdrcksum;
	}

}