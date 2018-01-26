package flashsystem.io;


import flashsystem.X10FlashException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import win32lib.JKernel32;

public class USBFlashWin32 {
	
	static final Logger logger = LogManager.getLogger(USBFlashWin32.class);


	public static void windowsOpen(String pid) throws IOException {
    		logger.info("Opening device for R/W");
			JKernel32.openDevice();
			logger.info("Device ready for R/W.");
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


	public static boolean windowsWrite(byte[] array) throws IOException,X10FlashException {
		JKernel32.writeBytes(array);
		return true;
	}

    public static byte[] windowsRead(int length) throws IOException {
    	return JKernel32.readBytes(length);
    }

}