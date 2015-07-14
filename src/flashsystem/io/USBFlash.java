package flashsystem.io;

import flashsystem.S1Packet;
import flashsystem.X10FlashException;
import java.io.IOException;
import org.system.DeviceChangedListener;
import org.system.OS;

public class USBFlash {

	public static void open(String pid) throws IOException, Exception {
		DeviceChangedListener.pause(true);
		if (OS.getName().equals("windows")) {
			USBFlashWin32.windowsOpen(pid);
		}
		else {
			USBFlashLinux.linuxOpen(pid);
		}
	}

	public static void setUSBBuffer(int buffer) {
		if (OS.getName().equals("windows")) {
			USBFlashWin32.setUSBBuffer(buffer);
		}
		else {
			USBFlashLinux.setUSBBuffer(buffer);
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
    	DeviceChangedListener.pause(false);
    }
}