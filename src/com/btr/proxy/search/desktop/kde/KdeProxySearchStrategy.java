package com.btr.proxy.search.desktop.kde;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.env.EnvProxySearchStrategy;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.selector.whitelist.UseProxyWhiteListSelector;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;

/*****************************************************************************
 * Loads the KDE4 proxy settings from the KDE <i>kioslaverc</i> file.
 * This will load properties from the file
 * <p>
 * <i>.kde/share/config/kioslaverc</i>
 * </P>
 * starting from the current users home directory.
 * <p>
 * The following settings are extracted from the section "[Proxy Settings]":
 * </p>
 * <ul>
 * <li><i>AuthMode</i> 	-> 0 = no auth., 1 = use login.</li>
 * <li><i>ProxyType</i> -> 0 = direct 1 = use fixed settings, 2 = use PAC, 3 = automatic (WPAD) , 4 = Use environment variables?</li>
 * <li><i>Proxy Config</i> Script -> URL to PAC file</li>
 * <li><i>ftpProxy</i> -> Fixed ftp proxy address e.g. http://www.ftp-proxy.com:8080</li>
 * <li><i>httpProxy</i> -> Fixed http proxy e.g http://www.http-proxy.com:8080</li>
 * <li><i>httpsProxy</i> -> Fixed https proxy e.g http://www.https-proxy.com:8080</li>
 * <li><i>NoProxyFor</i> -> Proxy white list</li>
 * <li><i>ReversedException</i> -> false = use NoProxyFor, true = revert meaning of the NoProxyFor list</li>
 * </ul>
 *
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class KdeProxySearchStrategy implements ProxySearchStrategy {

	private KdeSettingsParser settingsParser;

	static final Logger logger = LogManager.getLogger(KdeProxySearchStrategy.class);
	/*************************************************************************
	 * ProxySelector using the given parser.
	 * @see java.net.ProxySelector#ProxySelector()
	 ************************************************************************/

	public KdeProxySearchStrategy() {
		this(new KdeSettingsParser());
	}

	/*************************************************************************
	 * ProxySelector
	 * @see java.net.ProxySelector#ProxySelector()
	 ************************************************************************/

	public KdeProxySearchStrategy(KdeSettingsParser settingsParser) {
		super();
		this.settingsParser = settingsParser;
	}

	/*************************************************************************
	 * Loads the proxy settings and initializes a proxy selector for the firefox
	 * proxy settings.
	 * @return a configured ProxySelector, null if none is found.
	 * @throws ProxyException on file reading error.
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {

		logger.debug("Detecting Kde proxy settings");

		Properties settings = readSettings();
		if (settings == null) {
			return null;
		}

		ProxySelector result = null;
		int type = Integer.parseInt(settings.getProperty("ProxyType", "-1"));
		switch (type) {
			case 0: // Use no proxy
				logger.debug("Kde uses no proxy");
				result = NoProxySelector.getInstance();
				break;
			case 1: // Fixed settings
				logger.debug("Kde uses manual proxy settings");
				result = setupFixedProxySelector(settings);
				break;
			case 2: // PAC Script
				String pacScriptUrl = settings.getProperty("Proxy Config Script", "");
				logger.debug("Kde uses autodetect script "+pacScriptUrl);
				result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
				break;
			case 3: // WPAD
				logger.debug("Kde uses WPAD to detect the proxy");
				result = new WpadProxySearchStrategy().getProxySelector();
				break;
			case 4: // Use environment variables
				logger.debug("Kde reads proxy from environment");
				result = setupEnvVarSelector(settings);
				break;
			default:
				break;
		}

		return result;
	}

	/*************************************************************************
	 * Reads the settings and stores them in a properties map.
	 * @return the parsed settings.
	 * @throws ProxyException
	 ************************************************************************/

	private Properties readSettings() throws ProxyException {
		try {
			return  this.settingsParser.parseSettings();
		} catch (IOException e) {
			logger.error("Can't parse settings : "+e.getMessage());
			throw new ProxyException(e);
		}
	}

	/*************************************************************************
	 * Builds an environment variable selector.
	 * @param settings the settings to read from.
	 * @return the ProxySelector using environment variables.
	 ************************************************************************/

	private ProxySelector setupEnvVarSelector(Properties settings) {
		ProxySelector result;
		result = new EnvProxySearchStrategy(
				settings.getProperty("httpProxy"),
				settings.getProperty("httpsProxy"),
				settings.getProperty("ftpProxy"),
				settings.getProperty("NoProxyFor")
				).getProxySelector();
		return result;
	}

	/*************************************************************************
	 * Parse the fixed proxy settings and build an ProxySelector for this a
	 * chained configuration.
	 * @param settings the proxy settings to evaluate.
	 ************************************************************************/

	private ProxySelector setupFixedProxySelector(Properties settings) {
		String proxyVar = settings.getProperty("httpProxy", null);
		FixedProxySelector httpPS = ProxyUtil.parseProxySettings(proxyVar);
		if (httpPS == null) {
			logger.debug("Kde http proxy is "+proxyVar);
			return null;
		}

		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		ps.setSelector("http", httpPS);

		proxyVar = settings.getProperty("httpsProxy", null);
		FixedProxySelector httpsPS = ProxyUtil.parseProxySettings(proxyVar);
		if (httpsPS != null) {
			logger.debug("Kde https proxy is "+proxyVar);
			ps.setSelector("https", httpsPS);
		}

		proxyVar = settings.getProperty("ftpProxy", null);
		FixedProxySelector ftpPS = ProxyUtil.parseProxySettings(proxyVar);
		if (ftpPS != null) {
			logger.debug("Kde ftp proxy is "+proxyVar);
			ps.setSelector("ftp", ftpPS);
		}

		// Wrap in white list filter.
		String noProxyList = settings.getProperty("NoProxyFor", null);
		if (noProxyList != null && noProxyList.trim().length() > 0) {
			boolean reverse = "true".equals(settings.getProperty("ReversedException", "false"));
			if (reverse) {
				logger.debug("Kde proxy blacklist is "+noProxyList);
				return new UseProxyWhiteListSelector(noProxyList, ps);
			} else {
				logger.debug("Kde proxy whitelist is "+noProxyList);
				return new ProxyBypassListSelector(noProxyList, ps);
			}
		}

		return ps;
	}


	public String getName() {
		return "kde";
	}

}
