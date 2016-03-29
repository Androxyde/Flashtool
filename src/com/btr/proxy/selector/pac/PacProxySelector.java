package com.btr.proxy.selector.pac;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.btr.proxy.util.ProxyUtil;


/*****************************************************************************
 * ProxySelector that will use a PAC script to find an proxy for a given URI.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/
public class PacProxySelector extends ProxySelector {

	private final boolean JAVAX_PARSER = ScriptAvailability.isJavaxScriptingAvailable();

	// private static final String PAC_PROXY = "PROXY";
	private static final String PAC_SOCKS = "SOCKS";
	private static final String PAC_DIRECT = "DIRECT";

	private PacScriptParser pacScriptParser;

	private static volatile boolean enabled = true;
	static final Logger logger = LogManager.getLogger(PacProxySelector.class);
	/*************************************************************************
	 * Constructor
	 * @param pacSource the source for the PAC file. 
	 ************************************************************************/

	public PacProxySelector(PacScriptSource pacSource) {
		super();
		selectEngine(pacSource);
	}
	
	/*************************************************************************
	 * Can be used to enable / disable the proxy selector.
	 * If disabled it will return DIRECT for all urls.
	 * @param enable the new status to set.
	 ************************************************************************/
	
	public static void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	/*************************************************************************
	 * Checks if the selector is currently enabled.
	 * @return true if enabled else false.
	 ************************************************************************/
	
	public static boolean isEnabled() {
		return enabled;
	}

	/*************************************************************************
	 * Selects one of the available PAC parser engines.
	 * @param pacSource to use as input.
	 ************************************************************************/
	
	private void selectEngine(PacScriptSource pacSource) {
		try {
			if (this.JAVAX_PARSER) {
				logger.debug("Using javax.script JavaScript engine.");
				this.pacScriptParser = new JavaxPacScriptParser(pacSource);
			} else {
				logger.debug("Using Rhino JavaScript engine.");
				this.pacScriptParser = new RhinoPacScriptParser(pacSource);
			}
		} catch (Exception e) {
			logger.error("PAC parser error :"+ e.getMessage());
		}
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
		if (uri == null) {
			throw new IllegalArgumentException("URI must not be null.");
		}
		
		// Fix for Java 1.6.16+ where we get a infinite loop because
		// URL.connect(Proxy.NO_PROXY) does not work as expected.
		if (!enabled) {
			return ProxyUtil.noProxyList();
		}

		return findProxy(uri);
	}

	/*************************************************************************
	 * Evaluation of the given URL with the PAC-file.
	 * 
	 * Two cases can be handled here:
	 * DIRECT 			Fetch the object directly from the content HTTP server denoted by its URL
	 * PROXY name:port 	Fetch the object via the proxy HTTP server at the given location (name and port) 
	 * 
	 * @param uri <code>URI</code> to be evaluated.
	 * @return <code>Proxy</code>-object list as result of the evaluation.
	 ************************************************************************/

	private List<Proxy> findProxy(URI uri) {
		try {
			List<Proxy> proxies = new ArrayList<Proxy>();
			String parseResult = this.pacScriptParser.evaluate(uri.toString(),
					uri.getHost());
			String[] proxyDefinitions = parseResult.split("[;]");
			for (String proxyDef : proxyDefinitions) {
				if (proxyDef.trim().length() > 0) {
					proxies.add(buildProxyFromPacResult(proxyDef));
				}
			}
			return proxies;
		} catch (ProxyEvaluationException e) {
			logger.error("PAC resolving error : "+e.getMessage());
			return ProxyUtil.noProxyList();
		}
	}

	/*************************************************************************
	 * The proxy evaluator will return a proxy string. This method will
	 * take this string and build a matching <code>Proxy</code> for it.
	 * @param pacResult the result from the PAC parser.
	 * @return a Proxy
	 ************************************************************************/

	private Proxy buildProxyFromPacResult(String pacResult) {
		if (pacResult == null || pacResult.trim().length() < 6) {
			return Proxy.NO_PROXY;
		}
		String proxyDef = pacResult.trim();
		if (proxyDef.toUpperCase().startsWith(PAC_DIRECT)) {
			return Proxy.NO_PROXY;
		}

		// Check proxy type.
		Proxy.Type type = Proxy.Type.HTTP;
		if (proxyDef.toUpperCase().startsWith(PAC_SOCKS)) {
			type = Proxy.Type.SOCKS;
		}

		String host = proxyDef.substring(6);
		Integer port = ProxyUtil.DEFAULT_PROXY_PORT;

		// Split port from host
		int indexOfPort = host.indexOf(':');
		if (indexOfPort != -1) {
			port = Integer.parseInt(host.substring(indexOfPort+1).trim());
			host = host.substring(0, indexOfPort).trim();
		}

		SocketAddress adr = InetSocketAddress.createUnresolved(host, port);
		return new Proxy(type, adr);
	}
	
}
