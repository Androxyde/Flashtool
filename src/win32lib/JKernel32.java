package win32lib;

import java.io.IOException;
import org.system.Device;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

import flashsystem.BytesUtil;

public class JKernel32 {

	public static Kernel32RW kernel32 = (Kernel32RW) Native.loadLibrary("kernel32", Kernel32RW.class, W32APIOptions.UNICODE_OPTIONS);
	static WinNT.HANDLE HandleToDevice = WinBase.INVALID_HANDLE_VALUE;
	static WinBase.OVERLAPPED ovwrite = new WinBase.OVERLAPPED();

	public static boolean openDevice() throws IOException {
        /* Kernel32RW.GENERIC_READ | Kernel32RW.GENERIC_WRITE not used in dwDesiredAccess field for system devices such a keyboard or mouse */
        int shareMode = WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE;
        int Access = WinNT.GENERIC_WRITE | WinNT.GENERIC_READ;
		HandleToDevice = Kernel32.INSTANCE.CreateFile(
                Device.getConnectedDeviceWin32().getDevPath(), 
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
                Device.getConnectedDeviceWin32().getDevPath(), 
                Access, 
                shareMode, 
                null, 
                WinNT.OPEN_EXISTING, 
                WinNT.FILE_FLAG_OVERLAPPED, 
                (WinNT.HANDLE)null);
		if (HandleToDevice == WinBase.INVALID_HANDLE_VALUE) throw new IOException(getLastError());
		return true;
	}

	public static byte[] readBytes(int bufsize) throws IOException {
		IntByReference nbread = new IntByReference();
		byte[] b = new byte[bufsize];
		boolean result = kernel32.ReadFile(HandleToDevice, b, bufsize, nbread, null);
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
		if (!result) if (!result) throw new IOException(getLastError());
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
		int code = Kernel32.INSTANCE.GetLastError();
	    Kernel32 lib = Kernel32.INSTANCE;
	    PointerByReference pref = new PointerByReference();
	    /*OVERLAPPED ov = new OVERLAPPED();
	    ov.Offset=0;
	    ov.OffsetHigh=0;
	    kernel32.CreateEvent(null, arg1, arg2, arg3)*/
	    lib.FormatMessage(
	        WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER | WinBase.FORMAT_MESSAGE_FROM_SYSTEM | WinBase.FORMAT_MESSAGE_IGNORE_INSERTS, 
	        null, 
	        code, 
	        0, 
	        pref, 
	        0, 
	        null);
	    String s = code + " : " +pref.getValue().getString(0, !Boolean.getBoolean("w32.ascii"));
	    lib.LocalFree(pref.getValue());
	    return s.replaceAll("[\n\r]+"," ");
	}

}
