package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

import linuxlib.JUsb;

public class USBFlashLinux {
	
	private static int lastflags;
	private static byte[] lastreply;
	private static Logger logger = Logger.getLogger(USBFlashLinux.class);
	
	public static void linuxOpen(String pid) throws IOException, Exception  {
			logger.info("Opening device for R/W");
			JUsb.fillDevice(false);
			JUsb.open();
			logger.info("Device ready for R/W.");
	}

	public static void linuxWriteS1(S1Packet p) throws IOException,X10FlashException {
		try {
			logger.debug("Writing packet to phone");
			JUsb.writeBytes(p.getHeader());
			if (p.getDataLength()>0)
				JUsb.writeBytes(p.getDataArray());
			JUsb.writeBytes(p.getCRC32());
			logger.debug("OUT : " + p);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void linuxWrite(byte[] array) throws IOException,X10FlashException {
		try {
			JUsb.writeBytes(array);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static  void linuxReadS1Reply() throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	byte[] read = JUsb.readBytes(13);
    	S1Packet p=new S1Packet(read);
    	if (p.getDataLength()>0) {
    		read = JUsb.readBytes(p.getDataLength());
    		p.addData(read);
    	}
    	read = JUsb.readBytes(4);
    	p.addData(read);
		p.validate();
		logger.debug("IN : " + p);
		lastreply = p.getDataArray();
		lastflags = p.getFlags();
    }

    public static void linuxReadReply()  throws X10FlashException, IOException {
    	try {
			lastreply = JUsb.readBytes(0x10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static int linuxGetLastFlags() {
    	return lastflags;
    }
    
    public static byte[] linuxGetLastReply() {
    	return lastreply;
    }

    public static void linuxClose() {
		try {
			JUsb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}