package org.flashtool.flashsystem.io;


import java.io.IOException;
import org.flashtool.flashsystem.S1Command;
import org.flashtool.flashsystem.X10FlashException;
import org.flashtool.jna.win32.JKernel32;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class USBFlashWin32 {
	

	public static void windowsOpen(String pid) throws IOException {
    		log.info("Opening device for R/W");
			JKernel32.openDevice();
			log.info("Device ready for R/W.");
	}

	public static void windowsClose() {
		JKernel32.closeDevice();
	}

	public static boolean windowsWrite(byte[] array) throws IOException,X10FlashException {
		JKernel32.writeBytes(array);
		return true;
	}

    public static byte[] windowsRead(int length) throws IOException {
    	return JKernel32.readBytes(length);
    }

}