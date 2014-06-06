package flashsystem;

import java.io.IOException;

import org.logger.LogProgress;
import org.logger.MyLogger;

import flashsystem.io.USBFlash;

public class Command {

    private boolean _simulate;

    public static final byte[] TA_FLASH_STARTUP_SHUTDOWN_RESULT_ONGOING	 = {
    	0x00, 0x00, 0x27, 0x74, 0x00, 0x00, 0x00, 0x01, 0x01};
	public static final byte[] TA_FLASH_STARTUP_SHUTDOWN_RESULT_FINISHED		 = {
		0x00, 0x00, 0x27, 0x74, 0x00, 0x00, 0x00, 0x01, 0x00};

	public static final byte[] TA_DEVID1 = {
		(byte)0x00, (byte)0x00, (byte)0x08, (byte)0xA2
	};

	public static final byte[] TA_DEVID2 = {
		(byte)0x00, (byte)0x00, (byte)0x13, (byte)0x24
	};
	
	public static final byte[] TA_DEVID3 = {
		(byte)0x00, (byte)0x00, (byte)0x08, (byte)0x9D
	};
	
	public static final byte[] TA_DEVID4 = {
		(byte)0x00, (byte)0x00, (byte)0x08, (byte)0x9A
	};
	
	public static final byte[] TA_DEVID5 = {
		(byte)0x00, (byte)0x00, (byte)0x08, (byte)0x9E
	};
	public static final byte[] DISABLEFINALVERIF = {
		0x00, 0x01, 0x00, 0x00, 0x00, 0x01
	};

	static final int CMD01 = 1;
	static final int CMD04 = 4;
	static final int CMD05 = 5;
	static final int CMD06 = 6;
	static final int CMD07 = 7;
	static final int CMD09 = 9;
	static final int CMD10 = 10;
	static final int CMD12 = 12;
	static final int CMD13 = 13;
	static final int CMD25 = 25;
	
	
	static final byte[] VALNULL = new byte[0];
	static final byte[] VAL1 = new byte[] {1};
	static final byte[] VAL2 = new byte[] {2};

	public Command(boolean simulate) {
		_simulate = simulate;
	}
	
    public String getLastReplyString() {
    	try {
    		return new String(USBFlash.getLastReply());
    	}
    	catch (Exception e) {
    		return "";
    	}
    }

    public String getLastReplyHex() {
    	try {
    		return HexDump.toHex(USBFlash.getLastReply());
    	}
    	catch (Exception e) {
    		return "";
    	}
    }

    public short getLastReplyLength() {
    	try {
    		return (short)USBFlash.getLastReply().length;
    	}
    	catch (Exception e) {
    		return 0;
    	}
    }

    private void writeCommand(int command, byte data[], boolean ongoing) throws X10FlashException, IOException {
    	if (!_simulate) {
    			if (MyLogger.curlevel.equals("debug")) {
    				try {
    					Thread.sleep(125);
    				}catch (Exception e) {}
    			}
	    		S1Packet p = new S1Packet(command,data,ongoing);
	    		try {
		    		USBFlash.writeS1(p, true);
	    		}
	    		catch (X10FlashException xe) {
	    			p.release();
	    			throw new X10FlashException(xe.getMessage());
	    		}
	    		catch (IOException ioe) {
	    			p.release();
	    			throw new IOException(ioe.getMessage());
	    		}
	    }
    }

    public void send(int cmd, byte data[], boolean ongoing) throws X10FlashException, IOException
    {
    	writeCommand(cmd, data, ongoing);
    	LogProgress.updateProgress();
    	if (USBFlash.getLastFlags()==0) {
    		writeCommand(Command.CMD07, Command.VALNULL, false);
    		throw new X10FlashException(getLastReplyString());
    	}
		
    }

}