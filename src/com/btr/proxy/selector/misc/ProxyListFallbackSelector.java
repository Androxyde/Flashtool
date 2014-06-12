package com.btr.proxy.selector.misc;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/*****************************************************************************
 * Implements a fallback selector to warp it around an existing ProxySelector.
 * This will remove proxies from a list of proxies and implement an automatic
 * retry mechanism.
 *  
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2011
 ****************************************************************************/

public class ProxyListFallbackSelector extends ProxySelector {

	// Retry a unresponsive proxy after 10 minutes per default.
	private static final int DEFAULT_RETRY_DELAY = 1000*60*10;
	
	private ProxySelector delegate;
	private ConcurrentHashMap<SocketAddress, Long> failedDelayCache;
	private long retryAfterMs; 
	
	/*************************************************************************
	 * Constructor
	 * @param delegate the delegate to use.
	 ************************************************************************/
	
	public ProxyListFallbackSelector(ProxySelector delegate) {
		this(DEFAULT_RETRY_DELAY, delegate);
	}
	
	/*************************************************************************
	 * Constructor
	 * @param retryAfterMs the "retry delay" as amount of milliseconds. 
	 * @param delegate the delegate to use.
	 ************************************************************************/
	
	public ProxyListFallbackSelector(long retryAfterMs, ProxySelector delegate) {
		super();
		this.failedDelayCache = new ConcurrentHashMap<SocketAddress, Long>();
		this.delegate = delegate;
		this.retryAfterMs = retryAfterMs;
	}
	
	/*************************************************************************
	 * connectFailed
	 * @see java.net.ProxySelector#connectFailed(java.net.URI, java.net.SocketAddress, java.io.IOException)
	 ************************************************************************/

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		this.failedDelayCache.put(sa, System.currentTimeMillis());
	}

	/*************************************************************************
	 * select
	 * @see java.net.ProxySelector#select(java.net.URI)
	 ************************************************************************/

	@Override
	public List<Proxy> select(URI uri) {
		cleanupCache();
		List<Proxy> proxyList = this.delegate.select(uri);
		List<Proxy> result = filterUnresponsiveProxiesFromList(proxyList);
		return result;
	}

	/*************************************************************************
	 * Cleanup the entries from the cache that are no longer unresponsive.
	 ************************************************************************/
	
	private void cleanupCache() {
		Iterator<Entry<SocketAddress, Long>> it 
					= this.failedDelayCache.entrySet().iterator();
		while (it.hasNext()) {
			Entry<SocketAddress, Long> e = it.next();
			Long lastFailTime = e.getValue();
			if (retryDelayHasPassedBy(lastFailTime)) {
				it.remove();
			}
		}
	}

	/*************************************************************************
	 * @param proxyList
	 * @return
	 ************************************************************************/
	
	private List<Proxy> filterUnresponsiveProxiesFromList(List<Proxy> proxyList) {
		if (this.failedDelayCache.isEmpty()) {
			return proxyList;
		}
		List<Proxy> result = new ArrayList<Proxy>(proxyList.size());
		for (Proxy proxy : proxyList) {
			if (isDirect(proxy) || isNotUnresponsive(proxy)) {
				result.add(proxy);
			}
		}
		return result;
	}

	/*************************************************************************
	 * @param proxy
	 * @return
	 ************************************************************************/
	
	private boolean isDirect(Proxy proxy) {
		return Proxy.NO_PROXY.equals(proxy);
	}

	/*************************************************************************
	 * @param proxy
	 * @return
	 ************************************************************************/
	
	private boolean isNotUnresponsive(Proxy proxy) {
		Long lastFailTime = this.failedDelayCache.get(proxy.address());
		return retryDelayHasPassedBy(lastFailTime);
	}

	/*************************************************************************
	 * @param lastFailTime
	 * @return
	 ************************************************************************/
	
	private boolean retryDelayHasPassedBy(Long lastFailTime) {
		return lastFailTime == null 
				|| lastFailTime + this.retryAfterMs < System.currentTimeMillis();
	}

	/*************************************************************************
	 * Only used for unit testing not part of the public API.
	 * @param retryAfterMs The retryAfterMs to set.
	 ************************************************************************/
	
	final void setRetryAfterMs(long retryAfterMs) {
		this.retryAfterMs = retryAfterMs;
	}
	
	

}
