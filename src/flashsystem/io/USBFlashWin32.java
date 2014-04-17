package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;
import java.io.IOException;
import org.logger.MyLogger;
import win32lib.JKernel32;

public class USBFlashWin32 {
	
	private static int lastflags;
	private static byte[] lastreply;
	
	public static void windowsOpen(String pid) throws IOException {
		try {
    		MyLogger.getLogger().info("Opening device for R/W");
			JKernel32.openDevice();
		}catch (Exception e) {
			if (lastreply == null) throw new IOException("Unable to read from device");
		}
	}

	public static void windowsClose() {
		JKernel32.closeDevice();
	}
	
	private static void windowsSleep(int len) {
		try {
			Thread.sleep(len);
		}
		catch (Exception e) {}
	}

	public static boolean windowsWriteS1(S1Packet p) throws IOException,X10FlashException {
		MyLogger.getLogger().debug("Writing packet to phone");
		JKernel32.writeBytes(p.getHeader());
		if (p.getDataLength()>0)
			JKernel32.writeBytes(p.getDataArray());
		JKernel32.writeBytes(p.getCRC32());
		MyLogger.getLogger().debug("OUT : " + p);
		return true;
	}

	public static boolean windowsWrite(byte[] array) throws IOException,X10FlashException {
		JKernel32.writeBytes(array);
		return true;
	}
	
    public static  void windowsReadS1Reply() throws X10FlashException, IOException
    {
    	MyLogger.getLogger().debug("Reading packet from phone");
    	byte[] read = JKernel32.readBytes(13);
    	S1Packet p=new S1Packet(read);
    	if (p.getDataLength()>0) {
    		read = JKernel32.readBytes(p.getDataLength());
    		p.addData(read);
    	}
    	read = JKernel32.readBytes(4);
    	p.addData(read);
		p.validate();
		MyLogger.getLogger().debug("IN : " + p);
		lastreply = p.getDataArray();
		lastflags = p.getFlags();
    }

    public static void windowsReadReply() throws X10FlashException, IOException {
    	lastreply = JKernel32.readBytes(0x10000);
    }
    
    public static int windowsGetLastFlags() {
    	return lastflags;
    }
    
    public static byte[] windowsGetLastReply() {
    	return lastreply;
    }

}