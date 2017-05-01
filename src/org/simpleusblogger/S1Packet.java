package org.simpleusblogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.ta.parsers.TAUnit;
import org.util.BytesUtil;
import org.util.HexDump;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;
import com.igormaznitsa.jbbp.mapper.Bin;

public class S1Packet {
	@Bin byte[] command;
	@Bin byte[] flag;
	@Bin byte[] length;
	@Bin byte headercksum;
	byte[] data = null;
	byte[] crc = null;
	int startrecord=0;
	int datalength=0;
	int nbparts=1;
	String direction="";
	String sinname="";
	String action = "";
	String actionreply="";

	public int getStartRecord() {
		return startrecord;
	}
	public int getNbParts() {
		return nbparts;
	}
	
	public byte[] getHeader() {
		return BytesUtil.concatAll(command, flag, length);
	}
	
	public boolean isHeaderOK() {
		if (headercksum==0 && getCommandName().length()>0 && (getFlag()==1 || getFlag()==3 || getFlag()==7)) return true;
		byte computed = calcSum(BytesUtil.concatAll(command, flag, length));
		return (computed == headercksum) && (getCommandName().length() >0);
	}
	
	public int getCommand() {
		return BytesUtil.getInt(command);
	}

	public int getFlag() {
		return BytesUtil.getInt(flag);
	}

	public String getCommandName() {
		if (getCommand() == 0x01) return "getLoaderInfos";
		if (getCommand() == 0x09) return "openTA";
		if (getCommand() == 0x0A) return "closeTA";
		if (getCommand() == 0x0C) return "readTA";
		if (getCommand() == 0x0D) return "writeTA";
		if (getCommand() == 0x05) return "uploadImage";
		if (getCommand() == 0x06) return "Send sin data";
		if (getCommand() == 0x19) return "setLoaderConfig";
		if (getCommand() == 0x04) return "End flashing";
		if (getCommand() == 0x07) return "Get Error";
		//System.out.println(HexDump.toHex(BytesUtil.concatAll(command, flag, length)));
		return "";
	}
	
	public String getDirection() {
		return direction;
	}
	
	public int getLength() {
		return BytesUtil.getInt(length);
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
	
	public void finalise() throws IOException {
		if (data!=null) {
			JBBPBitInputStream dataStream = new JBBPBitInputStream(new ByteArrayInputStream(data));
			if (data.length >4)
				data = dataStream.readByteArray(data.length-4);
			else data = null;
			crc = dataStream.readByteArray(4);
		}
		if (data==null) datalength=0;
		else datalength = data.length;		
	}
	
	public void addData(byte[] pdata) {
		if (data==null)
			data = pdata;
		else
			data = BytesUtil.concatAll(data, pdata);
		nbparts++;
	}
	
	public void setRecord(int recnum) {
		startrecord=recnum;
	}
	
	public void setDirection(int dir) {
		if (dir==0) direction = "WRITE";
		else direction = "READ REPLY";
	}

	public TAUnit getTA() {
		try {
			JBBPBitInputStream taStream = new JBBPBitInputStream(new ByteArrayInputStream(data));
			int unit=taStream.readInt(JBBPByteOrder.BIG_ENDIAN);
			int talength = taStream.readInt(JBBPByteOrder.BIG_ENDIAN);
			TAUnit u = new TAUnit(unit, taStream.readByteArray(talength));
			taStream.close();
			return u;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setFileName(String name) {
		sinname = name;
	}
	
	public String getInfo() {
		TAUnit ta = null;
		if (this.getCommand()==0x0D) ;
			ta=getTA();
		if (ta!=null) return ta.toString();
		if (getCommand()==5)
			return sinname;
		if (getCommand()==0x09)
			return "Partition : "+BytesUtil.getInt(data);
		if (getCommand()==0x0C) {
			if (direction.equals("READ REPLY"))
				return "Value : "+HexDump.toHex(data);
			else
				return "Unit : "+HexDump.toHex(data);
		}
		return "";
	}
	
	public String toString() {	
		return direction + " : " + getCommandName()+" "+getInfo();
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getSin() {
		if (sinname==null) return "";
		return sinname;
	}
}
