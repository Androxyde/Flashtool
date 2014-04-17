package win32lib;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public interface Kernel32RW extends Kernel32 {
	 
    /** 
     * CreateFile constants 
     */
    /** 
     * Enable read access 
     */
    int GENERIC_READ        = 0x80000000;
    /** 
     * Enable write access 
     */
    int GENERIC_WRITE       = 0x40000000;

    /** 
     * Read data from USB HID device.
     */
    boolean ReadFile(WinNT.HANDLE Handle, byte[] buffer, int nNumberOfBytesToRead,  IntByReference NumberOfBytesRead,  OVERLAPPED Overlapped);
    /** 
     * Write data to the USB HID device.
     */
    boolean WriteFile(WinNT.HANDLE Handle, byte[] buffer, int NumberOfBytesToWrite, IntByReference NumberOfBytesWritten, OVERLAPPED Overlapped);

    boolean GetOverlappedResult(WinNT.HANDLE Handle,OVERLAPPED Overlapped,IntByReference NumberOfBytesRead, boolean wait);

    boolean CancelIo(WinNT.HANDLE Handle);
    
    //WinNT.HANDLE CreateEvent(Pointer securityAttributes, boolean manualReset, boolean initialState, String name);

}