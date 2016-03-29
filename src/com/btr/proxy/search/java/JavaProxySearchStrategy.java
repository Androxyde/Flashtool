package com.btr.proxy.search.java;

import java.net.ProxySelector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;

/*****************************************************************************
 * Reads some java system properties and extracts the proxy settings from them.
 * The following variables are read:
 * <ul>
 * <li><i>http.proxyHost</i> (default: none)</li>
 * <li><i>http.proxyPort</i> (default: 80 if http.proxyHost specified)</li>
 * <li><i>http.nonProxyHosts</i> (default: none)</li>
 * </ul>
 * <ul>
 * <li><i>https.proxyHost</i> (default: none)</li>
 * <li><i>https.proxyPort</i> (default: 443 if https.proxyHost specified)</li>
 * </ul>
 * <ul>
 * <li><i>ftp.proxyHost</i> (default: none)</li>
 * <li><i>ftp.proxyPort</i> (default: 80 if ftp.proxyHost specified)</li>
 * <li><i>ftp.nonProxyHosts</i> (default: none)</li> 
 * </ul>
 * <ul>
 * <li><i>socksProxyHost</i></li>
 * <li><i>socksProxyPort</i> (default: 1080)</li>
 * </ul>
 * <p>
 * This is based on information found here: <br/>
 * http://download.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
 * </p>
 * If the "http.proxyHost" property is not set then the no proxy selector is setup
 * This property is used as marker to signal that the System settings should be used. 
 *  
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class JavaProxySearchStrategy implements ProxySearchStrategy {
	
	static final Logger logger = LogManager.getLogger(JavaProxySearchStrategy.class);
	/*************************************************************************
	 * Constructor
	 * Will use the default environment variables.
	 ************************************************************************/
	
	public JavaProxySearchStrategy() {
		super();
	}
	
	/*************************************************************************
	 * Loads the proxy settings from environment variables.
	 * @return a configured ProxySelector, null if none is found.
	 ************************************************************************/

	public ProxySelector getProxySelector() {
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		
		if (!proxyPropertyPresent()) {
			return null;
		}
		logger.debug("Using settings from Java System Properties");
		
		
		setupProxyForProtocol(ps, "http", 80);
		setupProxyForProtocol(ps, "https", 443);
		setupProxyForProtocol(ps, "ftp", 80);
		setupProxyForProtocol(ps, "ftps", 80);
		setupSocktProxy(ps);
		
		return ps;
	}

	/*************************************************************************
	 * @return true if the http.proxyHost is available as system property.
	 ************************************************************************/
	
	private boolean proxyPropertyPresent() {
		return System.getProperty("http.proxyHost") != null 
				&& System.getProperty("http.proxyHost").trim().length() > 0;
	}

	/*************************************************************************
	 * Parse SOCKS settings
	 * @param ps
	 * @throws NumberFormatException
	 ************************************************************************/
	
	
	private void setupSocktProxy(ProtocolDispatchSelector ps) {
		String host = System.getProperty("socksProxyHost");
		String port = System.getProperty("socksProxyPort", "1080");
		if (host != null && host.trim().length() > 0) {
			logger.debug("Socks proxy "+host+":"+port+" found");
			ps.setSelector("socks", new FixedSocksSelector(host, Integer.parseInt(port)));
		}
	}

	/*************************************************************************
	 * Parse properties for the given protocol.
	 * @param ps
	 * @param protocol
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void setupProxyForProtocol(ProtocolDispatchSelector ps, String protocol, int defaultPort) {
		String host = System.getProperty(protocol+".proxyHost");
		String port = System.getProperty(protocol+".proxyPort", ""+defaultPort);
		String whiteList = System.getProperty(protocol+".nonProxyHosts", "").replace('|', ',');
		
		if ("https".equalsIgnoreCase(protocol)) { // This is dirty but https has no own property for it.
			whiteList = System.getProperty("http.nonProxyHosts", "").replace('|', ',');
		}

		if (host == null || host.trim().length() == 0) {
			return;
		}
		
		logger.debug(protocol.toUpperCase()+" proxy "+host+":"+port+" found using whitelist: "+whiteList);
		
		ProxySelector protocolSelector = new FixedProxySelector(host, Integer.parseInt(port));
		if (whiteList.trim().length() > 0) {
			protocolSelector = new ProxyBypassListSelector(whiteList, protocolSelector);
		}
	
		ps.setSelector(protocol, protocolSelector);
	}

	public String getName() {
		return "java";
	}
}
