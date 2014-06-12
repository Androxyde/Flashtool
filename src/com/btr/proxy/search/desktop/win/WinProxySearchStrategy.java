package com.btr.proxy.search.desktop.win;

import java.net.ProxySelector;

import com.btr.jna.LibWinHttp;
import com.btr.jna.WinHttp;
import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.browser.ie.IEProxySearchStrategy;
import com.btr.proxy.util.ProxyException;
import com.btr.jna.Win32IESettings;

/*****************************************************************************
 * Extracts the proxy settings from the windows registry.
 * This will read the windows system proxy settings. 
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class WinProxySearchStrategy implements ProxySearchStrategy {
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public WinProxySearchStrategy() {
		super();
	}

	/*************************************************************************
	 * getProxySelector
	 * @see com.btr.proxy.search.ProxySearchStrategy#getProxySelector()
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {
		// TODO Rossi 08.05.2009 Implement this by using Win API calls.
		// new Win32ProxyUtils().winHttpGetDefaultProxyConfiguration()
		// Current fallback is to use the IE settings. This is better
		// because the registry settings are most of the time not set.
		// Some Windows server installations may use it though.
		return new IEProxySearchStrategy().getProxySelector();
	}
	
	/*************************************************************************
	 * Loads the settings. 
	 * @return a WinIESettings object containing all proxy settings.
	 ************************************************************************/
	
	public Win32IESettings readSettings() {
		return WinHttp.readSettings();		
	}

	public String getName() {
		return "windows";
	}
	
	

}
