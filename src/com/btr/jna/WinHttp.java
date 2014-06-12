package com.btr.jna;

import com.sun.jna.platform.win32.WTypes;

public class WinHttp {
	  
	public static Win32IESettings readSettings() {
		String pacUrl = null;
		String proxy = null;
		String proxypass = null;
		winhttp_current_user_ie_proxy_config c = new winhttp_current_user_ie_proxy_config();
		LibWinHttp.libWinhttp.WinHttpGetIEProxyConfigForCurrentUser(c);
		if (c.fAutoDetect) {
			WTypes.LPWSTR pac = new WTypes.LPWSTR();
			LibWinHttp.libWinhttp.WinHttpDetectAutoProxyConfigUrl(
					  LibWinHttp.WINHTTP_AUTO_DETECT_TYPE_DHCP+
					  LibWinHttp.WINHTTP_AUTO_DETECT_TYPE_DNS_A, pac);
			try {
				pacUrl = pac.getValue().trim();
			} catch (NullPointerException npe1) {
				pacUrl = "";
			}
			if (pacUrl.length()==0) {
				try  {
					pacUrl = c.lpszAutoConfigUrl.getValue().trim();
				} catch (NullPointerException npe1) {
					pacUrl = null;
				}
			}
		}
		try {
			proxy = c.lpszProxy.getValue().trim();
		} catch (NullPointerException npe1) {
			proxy = null;
		}
		try {
			proxypass = c.lpszProxy.getValue().trim();
		} catch (NullPointerException npe1) {
			proxypass = null;
		}
		Win32IESettings s = new Win32IESettings(c.fAutoDetect,pacUrl,proxy,proxypass);
		return s;
	  }

}

