package com.btr.proxy.search.browser.firefox;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.desktop.DesktopProxySearchStrategy;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.util.PlatformUtil.Platform;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;

/*****************************************************************************
 * Loads the Firefox3 proxy settings from the users Firefox3 settings.
 * This will load the file <i>prefs.js</i> that is located in the 
 * <p>
 * <i>.mozilla/firefox/(profile)/</i> folder. 
 * </p>
 * 
 * See <a href="https://developer.mozilla.org/En/Mozilla_Networking_Preferences">Mozilla_Networking_Preferences</a> 
 * for an explanation of the proxy settings.
 * <p>
 * The following settings are extracted from 
 * this file: 
 * </p> 
 * Some generic settings:<br/>
 * <ul>
 * <li><i>network.proxy.type</i> -> n/a = use system settings, 0 = direct, 1 = Fixed proxy settings, 2 = proxy script (PAC), 3 = also direct , 4 = auto detect (WPAD)</li>
 * <li><i>network.proxy.share_proxy_settings</i> -> true = use same proxy for all protocols</li>
 * <li><i>network.proxy.no_proxies_on</i> -> a comma separated ignore list. </li>
 * <li><i>network.proxy.autoconfig_url</i> -> a URL to an proxy configuration script</li>
 * </ul>
 * Host names and ports per protocol are stored in the following settings:
 * <ul>
 * <li><i>network.proxy.http</i></li>
 * <li><i>network.proxy.http_port</i></li>
 * <li><i>network.proxy.ssl</i></li>
 * <li><i>network.proxy.ssl_port</i></li>
 * <li><i>network.proxy.ftp</i></li>
 * <li><i>network.proxy.ftp_port</i></li>
 * <li><i>network.proxy.gopher</i></li>
 * <li><i>network.proxy.gopher_port</i></li>
 * <li><i>network.proxy.socks</i></li>
 * <li><i>network.proxy.socks_port</i></li>
 * <li><i>network.proxy.socks_version</i> -> 4 or 5</li>
 * </u>
 * <p>
 * Note that if there are more than one profile the first profile found will be used.
 * </p>
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class FirefoxProxySearchStrategy implements ProxySearchStrategy {
	
	private FirefoxProfileSource profileScanner;
	private FirefoxSettingParser settingsParser;
	static final Logger logger = LogManager.getLogger(FirefoxProxySearchStrategy.class);
	
	/*************************************************************************
	 * ProxySelector
	 * @see java.net.ProxySelector#ProxySelector()
	 ************************************************************************/
	
	public FirefoxProxySearchStrategy() {
		super();
		if (PlatformUtil.getCurrentPlattform() == Platform.WIN) {
			this.profileScanner = new WinFirefoxProfileSource();
		} else {
			this.profileScanner = new LinuxFirefoxProfileSource();
		}
		this.settingsParser = new FirefoxSettingParser();
	}
	
	/*************************************************************************
	 * Loads the proxy settings and initializes a proxy selector for the firefox
	 * proxy settings.
	 * @return a configured ProxySelector, null if none is found.
	 * @throws ProxyException on file reading error. 
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {
		logger.debug("Detecting Firefox settings.");

		Properties settings = readSettings();
		
		ProxySelector result = null; 
		int type = Integer.parseInt(settings.getProperty("network.proxy.type", "-1"));
		switch (type) {
			case -1: // Use system settings
				logger.debug("Firefox uses system settings");
				result = new DesktopProxySearchStrategy().getProxySelector();
				break;
			case 0: // Use no proxy 
				logger.debug("Firefox uses no proxy");
				result = NoProxySelector.getInstance();
				break;
			case 1: // Fixed settings
				logger.debug("Firefox uses manual settings");
				result = setupFixedProxySelector(settings);
				break;
			case 2: // PAC Script
				String pacScriptUrl = settings.getProperty("network.proxy.autoconfig_url", "");
				logger.debug("Firefox uses script (PAC) "+pacScriptUrl);
				result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
				break;
			case 3: // Backward compatibility to netscape.
				logger.debug("Netscape compability mode -> uses no proxy");
				result = NoProxySelector.getInstance();
				break;
			case 4: // WPAD auto config
				logger.debug("Firefox uses automatic detection (WPAD)");
				result = new WpadProxySearchStrategy().getProxySelector();
				break;
			default:
				break;
		}

		// Wrap in white list filter.
		String noProxyList = settings.getProperty("network.proxy.no_proxies_on", null);
		if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
			logger.debug("Firefox uses proxy bypass list for: "+noProxyList);
			result = new ProxyBypassListSelector(noProxyList, result);
		}
		
		return result;
	}

	/*************************************************************************
	 * Reads the settings file and stores all settings in a Properties map.
	 * @return the parsed settings.
	 * @throws ProxyException on read error.
	 ************************************************************************/
	
	public Properties readSettings() throws ProxyException {
		try {
			Properties settings = this.settingsParser.parseSettings(this.profileScanner);
			return settings;
		} catch (IOException e) {
			throw new ProxyException("No Firefox installation found");
		}
	}

	/*************************************************************************
	 * Parse the fixed proxy settings and build an ProxySelector for this a 
	 * chained configuration.
	 * @param settings the proxy settings to evaluate.
	 ************************************************************************/
	
	private ProxySelector setupFixedProxySelector(Properties settings) {
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		installHttpProxy(ps, settings);
		if (isProxyShared(settings)) {
			installSharedProxy(ps);
		} else {
			installFtpProxy(ps, settings);
			installSecureProxy(ps, settings);
	        installSocksProxy(ps, settings);
		}
		return ps;
	}

	/*************************************************************************
	 * @param ps
	 * @param settings
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installFtpProxy(ProtocolDispatchSelector ps,
			Properties settings) throws NumberFormatException {
		installSelectorForProtocol(ps, settings, "ftp");
	}

	/*************************************************************************
	 * @param ps
	 * @param settings
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installHttpProxy(ProtocolDispatchSelector ps,
			Properties settings) throws NumberFormatException {
		installSelectorForProtocol(ps, settings, "http");
	}

	/*************************************************************************
	 * @param settings
	 * @return
	 ************************************************************************/
	
	private boolean isProxyShared(Properties settings) {
		return Boolean.TRUE.toString().equals(settings.getProperty("network.proxy.share_proxy_settings", "false").toLowerCase());
	}

	/*************************************************************************
	 * @param ps
	 ************************************************************************/
	
	private void installSharedProxy(ProtocolDispatchSelector ps) {
		ProxySelector httpProxy = ps.getSelector("http");
		if (httpProxy != null) {
			ps.setFallbackSelector(httpProxy);
		}
	}

	/*************************************************************************
	 * @param ps
	 * @param settings
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installSocksProxy(ProtocolDispatchSelector ps,
			Properties settings) throws NumberFormatException {
		String proxyHost = settings.getProperty("network.proxy.socks", null);
        int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.socks_port", "0"));
        if (proxyHost != null && proxyPort != 0) {
        	logger.debug("Firefox socks proxy is "+proxyHost+":"+proxyPort);
                ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
        }
	}

	/*************************************************************************
	 * @param ps
	 * @param settings
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installSecureProxy(ProtocolDispatchSelector ps,
			Properties settings) throws NumberFormatException {
		String proxyHost = settings.getProperty("network.proxy.ssl", null);
		int proxyPort = Integer.parseInt(settings.getProperty("network.proxy.ssl_port", "0"));
		if (proxyHost != null && proxyPort != 0) {
			logger.debug("Firefox secure proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector("https", new FixedProxySelector(proxyHost, proxyPort));
			ps.setSelector("sftp", new FixedProxySelector(proxyHost, proxyPort));
		}
	}

	/*************************************************************************
	 * Installs a proxy selector for the given protocol when settings are 
	 * available.
	 * @param ps a ProtocolDispatchSelector to configure.
	 * @param settings to read the config from.
	 * @param protocol to configure.
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installSelectorForProtocol(ProtocolDispatchSelector ps,
			Properties settings, String protocol) throws NumberFormatException {
		
		String proxyHost = settings.getProperty("network.proxy."+protocol, null);
		int proxyPort = Integer.parseInt(settings.getProperty("network.proxy."+protocol+"_port", "0"));
		if (proxyHost != null && proxyPort != 0) {
			logger.debug("Firefox "+protocol+" proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector(protocol, new FixedProxySelector(proxyHost, proxyPort));
		}
	}
	
	public String getName() {
		return "firefox";
	}

}
