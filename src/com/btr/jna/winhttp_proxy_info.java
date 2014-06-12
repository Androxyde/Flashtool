package com.btr.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;

public class winhttp_proxy_info extends Structure
{
	public WinDef.DWORD dwAccessType;
	public WTypes.LPWSTR lpszProxy;
	public WTypes.LPWSTR lpszProxyBypass;

  @Override
protected List getFieldOrder() {
  	return Arrays.asList("dwAccessType",
  				     	 "lpszProxy",
  				     	 "lpszProxyBypass");
  }

  public winhttp_proxy_info() {}

  @Override
public winhttp_proxy_info[] toArray(int size)
  {
    return (winhttp_proxy_info[])super.toArray(size);
  }

  public winhttp_proxy_info(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends winhttp_proxy_info
    implements Structure.ByReference
  {
  }

  public static class ByValue extends winhttp_proxy_info
    implements Structure.ByValue
  {
  }

}