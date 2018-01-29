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
	private static int buffersize=512*1024;
	private static int readbuffer=512*1024;
	
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

	public static S1Packet writeS1(S1Packet p) throws IOException,X10FlashException {
		write(p.getHeaderWithChecksum());
		if (p.getDataLength()>0) {
			long totalread=0;
			ByteArrayInputStream in = new ByteArrayInputStream(p.getDataArray());
			while (totalread<p.getDataLength()) {
				long remaining = p.getDataLength()-totalread;
				long bufsize=(remaining<buffersize)?remaining:buffersize;
				byte[] buf = new byte[(int)bufsize];
				int read = in.read(buf);
				write(buf);
				totalread+=read;
			}
			in.close();
		}
		write(p.getCRC32());
		return readS1Reply();
	}

	public static void write(byte[] array) throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsWrite(array);
		}
		else {
			USBFlashLinux.linuxWrite(array);
		}
	}

    public static  S1Packet readS1Reply() throws X10FlashException, IOException
    {
    	byte[] read = read(readbuffer);
    	S1Packet p=new S1Packet(read);
    	while (p.hasMoreToRead()) {
    		read = read(readbuffer);
    		p.addData(read);
    	}
		p.validate();
		return p;
    }

    public static  CommandPacket readCommandReply(boolean withOK) throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	byte[] read = read(readbuffer);
    	CommandPacket p=new CommandPacket(read,withOK);
    	while (p.hasMoreToRead()) {
    		read = read(readbuffer);
    		p.addData(read);
    	}
    	
		logger.debug("IN : " + p);
		return p;
		//lastflags = p.getFlags();
    }

	private static byte[] read(int length)  throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.windowsRead(length);
		}
		else {
			return USBFlashLinux.linuxRead(length);
		}
	}

}