package flashsystem.io;

import flashsystem.CommandPacket;
import flashsystem.S1Packet;
import flashsystem.X10FlashException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.util.BytesUtil;
import org.util.HexDump;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

import win32lib.JKernel32;

public class USBFlashWin32 {
	
	private static int lastflags;
	private static byte[] lastreply;
	static final Logger logger = LogManager.getLogger(USBFlashWin32.class);
	private static int buffersize=0;

	public static void setUSBBufferSize(int size) {
		buffersize=size;
	}

	public static int getBufferSize() {
		return buffersize;
	}

	public static void windowsOpen(String pid) throws IOException {
		try {
    		logger.info("Opening device for R/W");
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
		logger.debug("Writing packet to phone");		
		JKernel32.writeBytes(p.getHeaderWithChecksum());
		if (p.getDataLength()>buffersize && buffersize>0) {
			int totalread=0;
			ByteArrayInputStream in = new ByteArrayInputStream(p.getDataArray());
			while (totalread<p.getDataLength()) {
				long remaining = p.getDataLength()-totalread;
				long bufsize=(remaining<buffersize)?remaining:buffersize;
				byte[] buf = new byte[(int)bufsize];
				int read = in.read(buf);
				JKernel32.writeBytes(buf);
				totalread+=read;
			}
			in.close();
		}
		else
			JKernel32.writeBytes(p.getDataArray());
		JKernel32.writeBytes(p.getCRC32());
		logger.debug("OUT : " + p);
		return true;
	}

	public static boolean windowsWrite(byte[] array) throws IOException,X10FlashException {
		JKernel32.writeBytes(array);
		return true;
	}
	
    public static  void windowsReadS1Reply() throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	S1Packet p=new S1Packet(JKernel32.readBytes(13));
    	logger.debug("Read header");
    	if (p.getDataLength()>0)
    		p.addData(JKernel32.readBytes(p.getDataLength()));
    	p.addData(JKernel32.readBytes(4));
		p.validate();
		logger.debug("IN : " + p);
		lastreply = p.getDataArray();
		lastflags = p.getFlags();
    }

    public static  CommandPacket windowsReadCommandReply() throws X10FlashException, IOException
    {
    	logger.debug("Reading packet from phone");
    	byte[] reply = JKernel32.readBytes(0x1000);
    	CommandPacket c = new CommandPacket();
    	c.setStatus(new String(Arrays.copyOfRange(reply, 0, 4)));
    	if (c.getStatus()==CommandPacket.DATA) {
    		int length = Integer.parseInt(new String(Arrays.copyOfRange(reply, 4, 12)),16);
    		if (length>0) {
    			reply=JKernel32.readBytes(length);
    			c.setMessage(new String(reply));
    		}
    		else c.setMessage("");
    		reply=JKernel32.readBytes(4);
    		c.setStatus(new String(reply));
    	}
    	else {
    		c.setMessage(new String(Arrays.copyOfRange(reply, 4,reply.length)));
    	}
		logger.debug("IN : " + c.getMessage());
		lastreply = c.getMessage().getBytes();
		lastflags = c.getStatus();
		return c;
    }

    public static void windowsReadReply() throws IOException {
    	lastreply = JKernel32.readBytes(buffersize);
    }

    public static int windowsGetLastFlags() {
    	return lastflags;
    }

    public static byte[] windowsGetLastReply() {
    	return lastreply;
    }

}