package com.btr.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WTypes;

public abstract interface LibWinHttp extends Library
{
  public static final LibWinHttp libWinhttp = (LibWinHttp)Native.loadLibrary("winhttp", LibWinHttp.class);

  public abstract int WinHttpGetDefaultProxyConfiguration (winhttp_proxy_info info);

  public abstract int WinHttpGetIEProxyConfigForCurrentUser (winhttp_current_user_ie_proxy_config config);

  public abstract int WinHttpDetectAutoProxyConfigUrl(int dwAutoDetectFlags, WTypes.LPWSTR ppwszAutoConfigUrl);
  
  public static final int WINHTTP_ACCESS_TYPE_NO_PROXY = 1;
  public static final int WINHTTP_ACCESS_TYPE_DEFAULT_PROXY = 0;
  public static final int WINHTTP_ACCESS_TYPE_NAMED_PROXY = 3;
  public static final int WINHTTP_AUTO_DETECT_TYPE_DHCP  = 0x00000001;
  public static final int WINHTTP_AUTO_DETECT_TYPE_DNS_A = 0x00000002;

}