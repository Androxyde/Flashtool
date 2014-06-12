package com.btr.proxy.search.browser.ie;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

import com.btr.jna.LibWinHttp;
import com.btr.jna.Win32IESettings;
import com.btr.jna.WinHttp;
import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;
import com.btr.proxy.util.UriFilter;
import com.sun.jna.platform.win32.WTypes;

/*****************************************************************************
 * Extracts the proxy settings for Microsoft Internet Explorer.
 * The settings are read by invoking native Windows API methods.  
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class IEProxySearchStrategy implements ProxySearchStrategy {

	/*************************************************************************
	 * getProxySelector
	 * @see com.btr.proxy.search.ProxySearchStrategy#getProxySelector()
	 ************************************************************************/
	private static Logger logger = Logger.getLogger(IEProxySearchStrategy.class);
	
	public ProxySelector getProxySelector() throws ProxyException {
		
		logger.debug("Detecting IE proxy settings");
		
		Win32IESettings ieSettings = WinHttp.readSettings();
		ProxySelector result=null;
		
		if (ieSettings.hasPac()) {
			result = createPacSelector(ieSettings);
		}
		
		if (result == null) {
			result = createFixedProxySelector(ieSettings);
		}
		
		return result;
	}
	

	/*************************************************************************
	 * Parses the settings and creates an PAC ProxySelector for it.
	 * @param ieSettings the IE settings to use.
	 * @return a PacProxySelector the selector or null.
	 ************************************************************************/
	
	private PacProxySelector createPacSelector(Win32IESettings ieSettings) {
		WTypes.LPWSTR pac = null;

		if (ieSettings.isAutoDetect()) {
			logger.debug("Autodetecting script URL.");
			// This will take some time.
			LibWinHttp.libWinhttp.WinHttpDetectAutoProxyConfigUrl(
					LibWinHttp.WINHTTP_AUTO_DETECT_TYPE_DHCP+
					LibWinHttp.WINHTTP_AUTO_DETECT_TYPE_DNS_A, pac);
		}
		String pacUrl = pac.getValue();
		
		if (pacUrl == null) {
			pacUrl = ieSettings.getAutoConfigUrl();
		}
		if (pacUrl != null && pacUrl.trim().length() > 0) {
			logger.debug("IE uses script: "+pacUrl);
		     
			// Fix for issue 9
			// If the IE has a file URL and it only starts has 2 slashes, 
			// add a third so it can be properly converted to the URL class
		    if (pacUrl.startsWith("file://") && !pacUrl.startsWith("file:///")) {
		    	pacUrl = "file:///" + pacUrl.substring(7);
		    }
			return ProxyUtil.buildPacSelectorForUrl(pacUrl);
		}
		
		return null;
	}

	/*************************************************************************
	 * Parses the proxy settings into an ProxySelector.
	 * @param ieSettings the settings to use.
	 * @return a ProxySelector, null if no settings are set.
	 * @throws ProxyException on error.
	 ************************************************************************/
	
	private ProxySelector createFixedProxySelector(Win32IESettings ieSettings) throws ProxyException {
		String proxyString = ieSettings.getProxy();
		String bypassList = ieSettings.getProxyBypass();
		if (proxyString == null) {
			return null;
		}
		logger.debug("IE uses manual settings: "+proxyString+" with bypass list: "+bypassList);
		
		Properties p = parseProxyList(proxyString);
		
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		addSelectorForProtocol(p, "http", ps);
		addSelectorForProtocol(p, "https", ps);
		addSelectorForProtocol(p, "ftp", ps);
		addSelectorForProtocol(p, "gopher", ps);
		addSelectorForProtocol(p, "socks", ps);
		addFallbackSelector(p, ps);

		ProxySelector result = setByPassListOnSelector(bypassList, ps);
		return result;
	}

	/*************************************************************************
	 * @param bypassList
	 * @param ps
	 * @return 
	 ************************************************************************/
	
	private ProxySelector setByPassListOnSelector(String bypassList,
			ProtocolDispatchSelector ps) {
		if (bypassList != null && bypassList.trim().length() > 0) {
			ProxyBypassListSelector result; 
			if ("<local>".equals(bypassList.trim())) {
				result = buildLocalBypassSelector(ps);
			} else {
				bypassList = bypassList.replace(';', ',');
				result = new ProxyBypassListSelector(bypassList, ps);
			}
			return result;
		}
		return ps;
	}

	/*************************************************************************
	 * @param ps
	 * @return
	 ************************************************************************/
	
	private ProxyBypassListSelector buildLocalBypassSelector(
			ProtocolDispatchSelector ps) {
		List<UriFilter> localBypassFilter = new ArrayList<UriFilter>();
		localBypassFilter.add(new IELocalByPassFilter());
		return new ProxyBypassListSelector(localBypassFilter, ps);
	}

	/*************************************************************************
	 * Installs a fallback selector that is used whenever no protocol specific
	 * selector is defined.
	 * @param settings to take the proxy settings from.
	 * @param ps to install the created selector on.
	 ************************************************************************/
	
	private void addFallbackSelector(Properties settings, ProtocolDispatchSelector ps) {
		String proxy = settings.getProperty("default");
		if (proxy != null) {
			ps.setFallbackSelector(ProxyUtil.parseProxySettings(proxy));
		}
	}

	/*************************************************************************
	 * Creates a selector for a given protocol. The proxy will be taken
	 * from the settings and installed on the dispatch selector.
	 * @param settings to take the proxy settings from.
	 * @param protocol to create a selector for.
	 * @param ps to install the created selector on.
	 ************************************************************************/
	
	private void addSelectorForProtocol(Properties settings, String protocol, ProtocolDispatchSelector ps) {
		String proxy = settings.getProperty(protocol);
		if (proxy != null) {
			FixedProxySelector protocolSelector = ProxyUtil.parseProxySettings(proxy);
			ps.setSelector(protocol, protocolSelector);
		}
	}

	/*************************************************************************
	 * Parses the proxy list and splits it by protocol.
	 * @param proxyString the proxy list string
	 * @return Properties with separated settings.
	 * @throws ProxyException on parse error.
	 ************************************************************************/
	
	private Properties parseProxyList(String proxyString) throws ProxyException {
		Properties p = new Properties();
		if (proxyString.indexOf('=') == -1) {
			p.setProperty("default", proxyString);
		} else {
			try {
				proxyString = proxyString.replace(';', '\n');
				p.load(new ByteArrayInputStream(proxyString.getBytes("ISO-8859-1")));
			} catch (IOException e) {
				logger.error("Error reading IE settings as properties: "+e.getMessage());
	
				throw new ProxyException(e);
			}
		}
		return p;
	}

	public String getName() {
		return "IE";
	}
}
