package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;
import flashsystem.CommandPacket;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.system.OS;

public class USBFlash {

	static final Logger logger = LogManager.getLogger(USBFlash.class);
	private static int buffersize=0;
	private static byte[] lastreply;
	private static int lastflags;
	
	public static void setUSBBufferSize(int size) {
		buffersize=size;
	}

	public static int getUSBBufferSize() {
		return buffersize;
	}
	
	public static void open(String pid) throws IOException, Exception {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsOpen(pid);
		}
		else {
			USBFlashLinux.linuxOpen(pid);
		}
	}

	public static void close() {
    	if (OS.getName().equals("windows")) {
    		USBFlashWin32.windowsClose();
    	}
    	else
    		USBFlashLinux.linuxClose();
    }

	public static boolean writeS1(S1Packet p) throws IOException,X10FlashException {
		logger.debug("Writing packet to phone");		
		write(p.getHeaderWithChecksum());
		if (p.getDataLength()>buffersize && buffersize>0) {
			int totalread=0;
			ByteArrayInputStream in = new ByteArrayInputStream(p.getDataArray());
			while (totalread<p.getDataLength()) {
				long remaining = p.getDataLength()-totalread;
				long bufsize=(remaining<buffersize)?remaining:buffersize;
				byte[] buf = new byte[(int)bufsize];
				int read = in.read(buf);
				write(p.getHeaderWithChecksum());
				totalread+=read;
			}
			in.close();
		}
		else {
			write(p.getHeaderWithChecksum());
		}
		write(p.getCRC32());
		logger.debug("OUT : " + p);
		readS1Reply();
		return true;
	}

	public static void write(byte[] array) throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsWrite(array);
		}
		else {
			USBFlashLinux.linuxWrite(array);
		}
	}

    public static  void readS1Reply() throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	byte[] read = read(0x1000);
    	S1Packet p=new S1Packet(read);
    	while (p.hasMoreToRead()) {
    		read = read(0x1000);
    		p.addData(read);
    	}    		
		p.validate();
		logger.debug("IN : " + p);
		lastreply = p.getDataArray();
		lastflags = p.getFlags();
    }

    public static  CommandPacket readCommandReply(boolean withOK) throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	byte[] read = read(0x10000);
    	CommandPacket p=new CommandPacket(read,withOK);
    	while (p.hasMoreToRead()) {
    		read = read(0x10000);
    		p.addData(read);
    	}
    	
		logger.debug("IN : " + p);
		lastreply = p.getDataArray();
		return p;
		//lastflags = p.getFlags();
    }

	public static byte[] read(int length)  throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.windowsRead(length);
		}
		else {
			return USBFlashLinux.linuxRead(length);
		}
	}

	public static int getLastFlags() {
		return lastflags;
    }

    public static byte[] getLastReply() {
    	return lastreply;
    }

}