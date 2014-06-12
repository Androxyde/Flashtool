package com.btr.proxy.selector.whitelist;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import com.btr.proxy.util.ProxyUtil;
import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 * Special purpose ProxySelector used as Facade on top of a normal ProxySelector.
 * A wrapper that will first check the URI against a white list and if it matches
 * it will use a proxy as provided by the delegate ProxySelector else it will 
 * return DIRECT.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class UseProxyWhiteListSelector extends ProxySelector {
	
	private ProxySelector delegate;
	private List<UriFilter> whiteListFilter;

	/*************************************************************************
	 * Constructor
	 * @param proxySelector the proxy selector to use.
	 ************************************************************************/
	
	public UseProxyWhiteListSelector(String whiteList, ProxySelector proxySelector) {
		super();
		if (whiteList == null) {
			throw new NullPointerException("Whitelist must not be null.");
		}
		if (proxySelector == null) {
			throw new NullPointerException("ProxySelector must not be null.");
		}
		
		this.delegate = proxySelector;

		WhiteListParser parser = new DefaultWhiteListParser();
		this.whiteListFilter = parser.parseWhiteList(whiteList);
	}
	
	/*************************************************************************
	 * connectFailed
	 * @see java.net.ProxySelector#connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)
	 ************************************************************************/
	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		this.delegate.connectFailed(uri, sa, ioe);
	}

	/*************************************************************************
	 * select
	 * @see java.net.ProxySelector#select(java.net.URI)
	 ************************************************************************/

	@Override
	public List<Proxy> select(URI uri) {

		// If in white list, use proxy selector. 
		for (UriFilter filter : this.whiteListFilter) {
			if (filter.accept(uri)) {
				return this.delegate.select(uri);
			}
		}
		
		return ProxyUtil.noProxyList();
	}

}
