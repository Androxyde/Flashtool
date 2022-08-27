package org.flashtool.flashsystem;

import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.flashtool.flashsystem.io.USBFlash;
import org.flashtool.logger.LogProgress;
import org.flashtool.logger.MyLogger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S1Command {

    private boolean _simulate;
    private S1Packet reply;

    public static final byte[] TA_FLASH_STARTUP_SHUTDOWN_RESULT_ONGOING	 = {
    	0x00, 0x00, 0x27, 0x74, 0x00, 0x00, 0x00, 0x01, 0x01};
	public static final byte[] TA_FLASH_STARTUP_SHUTDOWN_RESULT_FINISHED		 = {
		0x00, 0x00, 0x27, 0x74, 0x00, 0x00, 0x00, 0x01, 0x00};

	public static final byte[] TA_MODEL = {
		(byte)0x00, (byte)0x00, (byte)0x08, (byte)0xA2
	};

	public static final byte[] TA_SERIAL = {
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

	static final int CMD01 = 1;
	static final int CMD04 = 4;
	static final int CMD05 = 5;
	static final int CMD06 = 6;
	static final int CMD07 = 7;
	static final int CMD09 = 9;
	static final int CMD10 = 10;
	static final int CMD12 = 12;
	static final int CMD13 = 13;
	static final int CMD18 = 18;
	static final int CMD25 = 25;


	static final byte[] VALNULL = new byte[0];
	static final byte[] VAL1 = new byte[] {1};
	static final byte[] VAL2 = new byte[] {2};

	public S1Command(boolean simulate) {
		_simulate = simulate;
	}
	
    public S1Packet getLastReply() {
    	return reply;
    }    

    private S1Packet writeCommand(int command, byte data[], boolean ongoing) throws X10FlashException, IOException {
    	S1Packet reply=null;
    	if (!_simulate) {
    			if (MyLogger.getLevel()==Level.DEBUG) {
    				try {
    					Thread.sleep(125);
    				}catch (Exception e) {}
    			}
	    		S1Packet p = new S1Packet(command,data,ongoing);
	    		try {
		    		reply =  USBFlash.writeS1(p);
	    		}
	    		catch (X10FlashException xe) {
	    			xe.printStackTrace();
	    			p.release();
	    			throw new X10FlashException(xe.getMessage());
	    		}
	    		catch (IOException ioe) {
	    			ioe.printStackTrace();
	    			p.release();
	    			throw new IOException(ioe.getMessage());
	    		}
	    }
    	return reply;
    }

    public void send(int cmd, byte data[], boolean ongoing) throws X10FlashException, IOException
    {
    	reply = writeCommand(cmd, data, ongoing);
    	if (reply.hasErrors()) {
    		reply = writeCommand(S1Command.CMD07, S1Command.VALNULL, false);
    		throw new X10FlashException(reply.getDataString());
    	}
    	while(reply.isMultiPacket()) {
    		S1Packet subreply = writeCommand(cmd, data, ongoing);
	    	if (subreply.hasErrors()) {
	    		writeCommand(S1Command.CMD07, S1Command.VALNULL, false);
	    		throw new X10FlashException(reply.getDataString());
	    	}
	    	reply.mergeWith(subreply);
	    	
    	}
    	LogProgress.updateProgress();
    }
}