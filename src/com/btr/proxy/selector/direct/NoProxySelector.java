package com.btr.proxy.selector.direct;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import com.btr.proxy.util.ProxyUtil;

/*****************************************************************************
 * This proxy selector will always return a "DIRECT" proxy.
 * Implemented as singleton.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class NoProxySelector extends ProxySelector {
	
	private static NoProxySelector instance;
	
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	private NoProxySelector() {
		super();
	}
	
	/*************************************************************************
	 * Gets the one and only instance of this selector.
	 * @return a DirectSelector.
	 ************************************************************************/
	
	public static synchronized NoProxySelector getInstance() {
		if (NoProxySelector.instance == null) {
			NoProxySelector.instance = new NoProxySelector();
		}
		return instance;
	}

	/*************************************************************************
	 * connectFailed
	 * @see java.net.ProxySelector#connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)
	 ************************************************************************/

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		// Not used.
	}

	/*************************************************************************
	 * select
	 * @see java.net.ProxySelector#select(java.net.URI)
	 ************************************************************************/

	@Override
	public List<Proxy> select(URI uri) {
		return ProxyUtil.noProxyList();
	}

}




/*
 * $Log: $
 */