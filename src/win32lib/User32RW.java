package win32lib;


import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;


public interface User32RW extends User32 {

    public static final User32RW MYINSTANCE = (User32RW) Native.loadLibrary("user32", User32RW.class, W32APIOptions.UNICODE_OPTIONS);

	final int GWL_EXSTYLE = -20;
	final int GWL_STYLE = -16;
	final int GWL_WNDPROC = -4;
	final int GWL_HINSTANCE = -6;
	final int GWL_ID = -12;
	final int GWL_USERDATA = -21;
	final int DWL_DLGPROC = 4;
	final int DWL_MSGRESULT = 0;
	final int DWL_USER = 8;
	final int WS_EX_COMPOSITED = 0x20000000;
	final int WS_EX_LAYERED = 0x80000;
	final int WS_EX_TRANSPARENT = 32;

	final int WM_DESTROY = 0x0002;
	final int WM_CHANGECBCHAIN = 0x030D;
	final int WM_DRAWCLIPBOARD = 0x0308;

	int GetWindowLong(HWND hWnd, int nIndex);

	int SetWindowLong(HWND hWnd, int nIndex, int dwNewLong);

	interface WNDPROC extends StdCallCallback {
		int callback(HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
	}

	int SetWindowLong(HWND hWnd, int nIndex, WNDPROC proc);

	HWND CreateWindowEx(int styleEx, String className, String windowName,
			int style, int x, int y, int width, int height, HWND hndParent,
			int hndMenu, int hndInst, Object parm);

	final HWND HWND_TOPMOST = new HWND(Pointer.createConstant(-1));
	final int SWP_NOSIZE = 1;

	boolean SetWindowPos(HWND hWnd, HWND hWndInsAfter, int x, int y, int cx,
			int cy, short uFlags);

	boolean DestroyWindow(HWND hwnd);

	HWND SetClipboardViewer(HWND hWndNewViewer);

	boolean ChangeClipboardChain(HWND hWndRemove, HWND hWndNewNext);

	// http://msdn.microsoft.com/en-us/library/ms644958(VS.85).aspx
	public static class POINT extends Structure {
		public int x;
		public int y;
        protected List getFieldOrder() {
        	return Arrays.asList("x",
        				     "y");
        }
	}

	/*
	 * PeekMessage() Options
	 */
	final int PM_NOREMOVE = 0x0000;
	final int PM_REMOVE = 0x0001;
	final int PM_NOYIELD = 0x0002;

	class MSG extends Structure {
		public int hWnd;
		public int message;
		public short wParam;
		public int lParam;
		public int time;
		public POINT pt;
        protected List getFieldOrder() {
        	return Arrays.asList("hWnd",
        				     "message",
        				     "wParam",
        				     "lParam",
        				     "time",
        				     "pt");
        }
	}

	// http://msdn.microsoft.com/en-us/library/ms644936(VS.85).aspx
	int GetMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin, int wMsgFilterMax);

	boolean PeekMessage(MSG lpMsg, HWND hWnd, int wMsgFilterMin,
			int wMsgFilterMax, int wRemoveMsg);

	boolean TranslateMessage(MSG lpMsg);

	int DispatchMessage(MSG lpMsg);

	final int QS_KEY = 0x0001;
	final int QS_MOUSEMOVE = 0x0002;
	final int QS_MOUSEBUTTON = 0x0004;
	final int QS_POSTMESSAGE = 0x0008;
	final int QS_TIMER = 0x0010;
	final int QS_PAINT = 0x0020;
	final int QS_SENDMESSAGE = 0x0040;
	final int QS_HOTKEY = 0x0080;
	final int QS_ALLPOSTMESSAGE = 0x0100;
	final int QS_RAWINPUT = 0x0400;

	final int QS_MOUSE = (QS_MOUSEMOVE | QS_MOUSEBUTTON);

	final int QS_INPUT = (QS_MOUSE | QS_KEY | QS_RAWINPUT);

	final int QS_ALLEVENTS = (QS_INPUT | QS_POSTMESSAGE | QS_TIMER | QS_PAINT | QS_HOTKEY);

	final int QS_ALLINPUT = (QS_INPUT | QS_POSTMESSAGE | QS_TIMER | QS_PAINT
			| QS_HOTKEY | QS_SENDMESSAGE);

	int MsgWaitForMultipleObjects(int nCount, HANDLE[] pHandles,
			boolean bWaitAll, int dwMilliseconds, int dwWakeMask);

//	void SendMessage(HWND hWnd, int message, WPARAM wParam, LPARAM lParam);

	void PostMessage(HWND hWnd, int message, WPARAM wParam, LPARAM lParam);

	LRESULT DefWindowProc(HWND hWnd, int msg, WPARAM wParam, LPARAM lParam);

}