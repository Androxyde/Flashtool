package com.btr.proxy.selector.fixed;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*****************************************************************************
 * This proxy selector is configured with a fixed proxy. This proxy will be 
 * returned for all URIs passed to the select method.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class FixedProxySelector extends ProxySelector {
	
	private final List<Proxy> proxyList;
	
	
	/*************************************************************************
	 * Constructor
	 * @param proxy the proxy to use.
	 ************************************************************************/
	
	public FixedProxySelector(Proxy proxy) {
		super();

		List<Proxy> list = new ArrayList<Proxy>(1);
		list.add(proxy);
		this.proxyList = Collections.unmodifiableList(list);
	}
	
	/*************************************************************************
	 * Constructor
	 * @param proxyHost the host name or IP address of the proxy to use.
	 * @param proxyPort the port of the proxy.
	 ************************************************************************/
	
	public FixedProxySelector(String proxyHost, int proxyPort) {
		this(new Proxy(Proxy.Type.HTTP, 
				InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
	}

	/*************************************************************************
	 * connectFailed
	 * @see java.net.ProxySelector#connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)
	 ************************************************************************/

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		// Not used
	}

	/*************************************************************************
	 * select
	 * @see java.net.ProxySelector#select(java.net.URI)
	 ************************************************************************/

	@Override
	public List<Proxy> select(URI uri) {
		return this.proxyList;
	}

}
