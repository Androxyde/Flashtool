package flashsystem.io;

import flashsystem.X10FlashException;
import libusb.LibUsbException;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import linuxlib.JUsb;

public class USBFlashLinux {

	static final Logger logger = LogManager.getLogger(USBFlashLinux.class);

	public static void linuxOpen(String pid) throws IOException, Exception  {
			logger.info("Opening device for R/W");
			JUsb.fillDevice(false);
			JUsb.open();
			logger.info("Device ready for R/W.");
	}

	public static void linuxClose() {
		try {
			JUsb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static byte[] linuxRead(int length) throws LibUsbException {
    	return JUsb.readBytes(length);
    }

}