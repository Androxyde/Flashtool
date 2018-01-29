package win32lib;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.system.Devices;
import org.util.BytesUtil;
import org.util.HexDump;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

import flashsystem.io.USBFlashLinux;


public class JKernel32 {

	static final Logger logger = LogManager.getLogger(JKernel32.class);

	public static Kernel32RW kernel32 = (Kernel32RW) Native.loadLibrary("kernel32", Kernel32RW.class, W32APIOptions.UNICODE_OPTIONS);
	static WinNT.HANDLE HandleToDevice = WinBase.INVALID_HANDLE_VALUE;
	static WinBase.OVERLAPPED ovwrite = new WinBase.OVERLAPPED();

	public static boolean openDevice() throws IOException {
        /* Kernel32RW.GENERIC_READ | Kernel32RW.GENERIC_WRITE not used in dwDesiredAccess field for system devices such a keyboard or mouse */
        int shareMode = WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE;
        int Access = WinNT.GENERIC_WRITE | WinNT.GENERIC_READ;
		HandleToDevice = Kernel32.INSTANCE.CreateFile(
                Devices.getConnectedDeviceWin32().getDevPath(), 
                Access, 
                shareMode, 
                null, 
                WinNT.OPEN_EXISTING, 
                0,//WinNT.FILE_FLAG_OVERLAPPED, 
                (WinNT.HANDLE)null);
		if (HandleToDevice == WinBase.INVALID_HANDLE_VALUE) throw new IOException(getLastError());
		return true;
	}

	public static boolean openDeviceAsync() throws IOException {
        /* Kernel32RW.GENERIC_READ | Kernel32RW.GENERIC_WRITE not used in dwDesiredAccess field for system devices such a keyboard or mouse */
        int shareMode = WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE;
        int Access = WinNT.GENERIC_WRITE | WinNT.GENERIC_READ;
		HandleToDevice = Kernel32.INSTANCE.CreateFile(
                Devices.getConnectedDeviceWin32().getDevPath(), 
                Access, 
                shareMode, 
                null, 
                WinNT.OPEN_EXISTING, 
                WinNT.FILE_FLAG_OVERLAPPED, 
                (WinNT.HANDLE)null);
		if (HandleToDevice == WinBase.INVALID_HANDLE_VALUE) throw new IOException(getLastError());
		return true;
	}

	public static byte[] readBytes(int length) throws IOException {
		IntByReference nbread = new IntByReference();
		byte[] b = new byte[length];
		boolean result = kernel32.ReadFile(HandleToDevice, b, length, nbread, null);
		if (!result) throw new IOException("Read error :"+getLastError());
		return BytesUtil.getReply(b,nbread.getValue());
	}

	public static WinNT.HANDLE createEvent() throws IOException {
		WinNT.HANDLE hevent = kernel32.CreateEvent(null, false, false, null);
		int res = kernel32.GetLastError();
		if (hevent == WinBase.INVALID_HANDLE_VALUE || res!=0)
				throw new IOException(JKernel32.getLastError());
		return hevent;
	}
	
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {}
	}
	
	public static boolean writeBytes(byte bytes[]) throws IOException {
		IntByReference nbwritten = new IntByReference();
		boolean result = kernel32.WriteFile(HandleToDevice, bytes, bytes.length, nbwritten, null);
		if (!result) throw new IOException(getLastError());
		if (nbwritten.getValue()!=bytes.length) throw new IOException("Did not write all data");
		bytes = null;
		return result;
	}

	public static boolean closeDevice() {
		boolean result = true;
		
		if (HandleToDevice != WinBase.INVALID_HANDLE_VALUE) {
			result = kernel32.CloseHandle(HandleToDevice);
		}
		HandleToDevice = WinBase.INVALID_HANDLE_VALUE;
		return result;
	}
	
	public static int getLastErrorCode() {
		return Kernel32.INSTANCE.GetLastError();
	}
	
	public static String getLastError() {
		return Kernel32Util.getLastErrorMessage();
	}

}