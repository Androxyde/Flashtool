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
 * it will return DIRECT else it will pass the URI to an delegate for inspection.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class ProxyBypassListSelector extends ProxySelector {
	
	private ProxySelector delegate;
	private List<UriFilter> whiteListFilter;
	
	
	/*************************************************************************
	 * Constructor
	 * @param whiteListFilter a list of filters for whitelist URLs.
	 * @param proxySelector the proxy selector to use.
	 ************************************************************************/
	
	public ProxyBypassListSelector(List<UriFilter> whiteListFilter, ProxySelector proxySelector) {
		super();
		if (whiteListFilter == null) {
			throw new NullPointerException("Whitelist must not be null.");
		}
		if (proxySelector == null) {
			throw new NullPointerException("ProxySelector must not be null.");
		}
		
		this.delegate = proxySelector;
		this.whiteListFilter = whiteListFilter;
	}


	/*************************************************************************
	 * Constructor
	 * @param whiteList a list of filters for whitelist URLs as comma/space separated string.
	 * @param proxySelector the proxy selector to use.
	 ************************************************************************/
	
	public ProxyBypassListSelector(String whiteList, ProxySelector proxySelector) {
		this(new DefaultWhiteListParser().parseWhiteList(whiteList), proxySelector);
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

		// If in white list, use DIRECT connection. 
		for (UriFilter filter : this.whiteListFilter) {
			if (filter.accept(uri)) {
				return ProxyUtil.noProxyList();
			}
		}
		
		return this.delegate.select(uri);
	}

}
