package com.btr.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes;

public class winhttp_current_user_ie_proxy_config extends Structure
{
	public boolean fAutoDetect;
	public WTypes.LPWSTR lpszAutoConfigUrl;
	public WTypes.LPWSTR lpszProxy;
    public WTypes.LPWSTR lpszProxyBypass;

  @Override
protected List getFieldOrder() {
  	return Arrays.asList("fAutoDetect",
  				     	 "lpszAutoConfigUrl",
  				     	 "lpszProxy",
  				     	 "lpszProxyBypass");
  }

  public winhttp_current_user_ie_proxy_config() {}

  @Override
public winhttp_current_user_ie_proxy_config[] toArray(int size)
  {
    return (winhttp_current_user_ie_proxy_config[])super.toArray(size);
  }

  public winhttp_current_user_ie_proxy_config(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends winhttp_current_user_ie_proxy_config
    implements Structure.ByReference
  {
  }

  public static class ByValue extends winhttp_current_user_ie_proxy_config
    implements Structure.ByValue
  {
  }

  @Override
  public String toString() {
	  return new String(this.fAutoDetect+":"+this.lpszAutoConfigUrl+":"+this.lpszProxy+":"+this.lpszProxyBypass);
  }

}