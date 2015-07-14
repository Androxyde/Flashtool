package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.util.HexDump;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;

import win32lib.JKernel32;

public class USBFlashWin32 {
	
	private static int lastflags;
	private static byte[] lastreply;
	private static Logger logger = Logger.getLogger(USBFlashWin32.class);
	private static int usbbuffer=4*(512*1024);
	
	
	public static void setUSBBuffer(int buffer) {
		usbbuffer=buffer;
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
		
		if (p.getDataLength()>usbbuffer) {
			JKernel32.writeBytes(p.getHeaderWithChecksum());
			JBBPBitInputStream dataStream = new JBBPBitInputStream(new ByteArrayInputStream(p.getDataArray()));
			int sent=0;
			while (dataStream.hasAvailableData()) {
				if (sent+usbbuffer>p.getDataLength()) {
					byte[] buf = dataStream.readByteArray(p.getDataLength()-sent);
					JKernel32.writeBytes(buf);
				}
				else {
					byte[] buf = dataStream.readByteArray(usbbuffer);
					JKernel32.writeBytes(buf);
					sent+=usbbuffer;
				}
			}
			JKernel32.writeBytes(p.getCRC32());
		}
		else
			JKernel32.writeBytes(p.getByteArray());
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
    	S1Packet p=new S1Packet(JKernel32.readBytes());
    	while (p.hasMoreToRead()) {
    		p.addData(JKernel32.readBytes());
    	}
		p.validate();
		logger.debug("IN : " + p);
		lastreply = p.getDataArray();
		lastflags = p.getFlags();
    }

    public static void windowsReadReply() throws X10FlashException, IOException {
    	lastreply = JKernel32.readBytes();
    }
    
    public static int windowsGetLastFlags() {
    	return lastflags;
    }
    
    public static byte[] windowsGetLastReply() {
    	return lastreply;
    }

}