package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;
import flashsystem.CommandPacket;
import flashsystem.Flasher;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.system.DeviceChangedListener;
import org.system.OS;

public class USBFlash {

	static final Logger logger = LogManager.getLogger(USBFlash.class);
	
	public static void setUSBBufferSize(int size) {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.setUSBBufferSize(size);
		}
		else {
			USBFlashLinux.setUSBBufferSize(size);
		}		
	}

	public static int getUSBBufferSize() {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.getBufferSize();
		}
		else {
			return USBFlashLinux.getBufferSize();
		}		
	}
	
	public static void open(String pid) throws IOException, Exception {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsOpen(pid);
		}
		else {
			USBFlashLinux.linuxOpen(pid);
		}
	}

	public static void writeS1(S1Packet p,boolean withprogressupdate) throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsWriteS1(p);
		}
		else {
			USBFlashLinux.linuxWriteS1(p);
		}
		readS1Reply();
	}

	public static void write(byte[] array) throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsWrite(array);
		}
		else {
			USBFlashLinux.linuxWrite(array);
		}		
	}

	public static void readS1Reply()  throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsReadS1Reply();
		}
		else {
			USBFlashLinux.linuxReadS1Reply();
		}
	}

	public static void readReply()  throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsReadReply();
		}
		else {
			USBFlashLinux.linuxReadReply();
		}
	}

	public static CommandPacket readCommandReply() throws IOException,X10FlashException {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.windowsReadCommandReply();
		}
		else {
			return USBFlashLinux.linuxReadCommandReply();
		}		
	}

	public static int getLastFlags() {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.windowsGetLastFlags();
		}
		else {
			return USBFlashLinux.linuxGetLastFlags();
		}
    }

    public static byte[] getLastReply() {
		if (OS.getName().equals("windows")) {
			return USBFlashWin32.windowsGetLastReply();
		}
		else {
			return USBFlashLinux.linuxGetLastReply();
		}
    }

    public static void close() {
    	if (OS.getName().equals("windows")) {
    		USBFlashWin32.windowsClose();
    	}
    	else
    		USBFlashLinux.linuxClose();
    }
}