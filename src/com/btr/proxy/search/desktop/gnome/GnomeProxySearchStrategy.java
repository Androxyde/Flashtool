package com.btr.proxy.search.desktop.gnome;

import java.io.File;
import java.io.IOException;
import java.net.ProxySelector;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.util.EmptyXMLResolver;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;

/*****************************************************************************
 * Loads the Gnome proxy settings from the Gnome GConf settings.
 * <p>
 * The following settings are extracted from the configuration that is stored 
 * in <i>.gconf</i> folder found in the user's home directory: 
 * </p>
 * <ul>
 * <li><i>/system/http_proxy/use_http_proxy</i>          ->   bool     used only by gnome-vfs </li>
 * <li><i>/system/http_proxy/host</i>                    ->   string   "my-proxy.example.com" without "http://"</li>
 * <li><i>/system/http_proxy/port</i>                    ->   int</li>
 * <li><i>/system/http_proxy/use_authentication</i>      ->   bool</li>
 * <li><i>/system/http_proxy/authentication_user</i>     ->   string</li>
 * <li><i>/system/http_proxy/authentication_password</i> ->   string</li>
 * <li><i>/system/http_proxy/ignore_hosts</i>            ->   list-of-string</li>
 * <li><i>/system/proxy/mode</i>                         ->   string   THIS IS THE CANONICAL KEY; SEE BELOW</li>
 * <li><i>/system/proxy/secure_host</i>                  ->   string   "proxy-for-https.example.com"</li>
 * <li><i>/system/proxy/secure_port</i>                  ->   int</li>
 * <li><i>/system/proxy/ftp_host</i>                     ->   string   "proxy-for-ftp.example.com"</li>
 * <li><i>/system/proxy/ftp_port</i>                     ->   int</li>
 * <li><i>/system/proxy/socks_host</i>                   ->   string   "proxy-for-socks.example.com"</li>
 * <li><i>/system/proxy/socks_port</i>                   ->   int</li>
 * <li><i>/system/proxy/autoconfig_url</i>               ->   string   "http://proxy-autoconfig.example.com"</li>
 * </ul>
 * <i>/system/proxy/mode</i> can be either:<br/>
 * "none" -> No proxy is used<br/>
 * "manual" -> The user's configuration values are used (/system/http_proxy/{host,port,etc.})<br/>
 * "auto" -> The "/system/proxy/autoconfig_url" key is used  <br/>
 * <p>
 * GNOME Proxy_configuration settings are explained 
 * <a href="http://en.opensuse.org/GNOME/Proxy_configuration">here</a> in detail
 * </p>
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class GnomeProxySearchStrategy implements ProxySearchStrategy {
	
	private static Logger logger = Logger.getLogger(GnomeProxySearchStrategy.class);
	/*************************************************************************
	 * ProxySelector
	 * @see java.net.ProxySelector#ProxySelector()
	 ************************************************************************/
	
	public GnomeProxySearchStrategy() {
		super();
	}
	
	/*************************************************************************
	 * Loads the proxy settings and initializes a proxy selector for the Gnome
	 * proxy settings.
	 * @return a configured ProxySelector, null if none is found.
	 * @throws ProxyException on file reading error. 
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {

		logger.debug("Detecting Gnome proxy settings");

		Properties settings = readSettings();
		
		String type = settings.getProperty("/system/proxy/mode");
		ProxySelector result = null; 
		if (type == null) {
			String useProxy = settings.getProperty("/system/http_proxy/use_http_proxy");
			if (useProxy == null) {
				return null;
			}
			type = Boolean.parseBoolean(useProxy)?"manual":"none";
		} 
		
		if ("none".equals(type)) {
			logger.debug("Gnome uses no proxy");
			result = NoProxySelector.getInstance();
		}
		if ("manual".equals(type)) {
			logger.debug("Gnome uses manual proxy settings");
			result = setupFixedProxySelector(settings);
		}
		if ("auto".equals(type)) {
			String pacScriptUrl = settings.getProperty("/system/proxy/autoconfig_url", "");
			logger.debug("Gnome uses autodetect script "+pacScriptUrl);
			result = ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
		}

		// Wrap into white-list filter?
		String noProxyList = settings.getProperty("/system/http_proxy/ignore_hosts", null);
		if (result != null && noProxyList != null && noProxyList.trim().length() > 0) {
			logger.debug("Gnome uses proxy bypass list: "+noProxyList);
			result = new ProxyBypassListSelector(noProxyList, result);
		}				
		
		return result;
	}

	/*************************************************************************
	 * Load the proxy settings from the  gconf settings XML file.  
	 * @return the loaded settings stored in a properties object.
	 * @throws ProxyException on processing error.
	 ************************************************************************/
	
	public Properties readSettings() throws ProxyException {
		Properties settings = new Properties();
		try {
			parseSettings("/system/proxy/", settings);
			parseSettings("/system/http_proxy/", settings);
		} catch (IOException e) {
			logger.error("Gnome settings file error : "+e.getMessage());
			throw new ProxyException(e);
		}
		return settings;
	}

	/*************************************************************************
	 * Finds the Gnome GConf settings file.
	 * @param context the gconf context to parse. 
	 * @return a file or null if does not exist. 
	 ************************************************************************/
	
	private File findSettingsFile(String context) {
		// Normally we should inspect /etc/gconf/<version>/path to find out where the actual file is.
		// But for normal systems this is always stored in .gconf folder in the user's home directory.
		File userDir = new File(System.getProperty("user.home"));
		
		// Build directory path for context
		StringBuilder path = new StringBuilder();
		String[] parts = context.split("/");
		for (String part : parts) {
			path.append(part);
			path.append(File.separator);
		}
		
		File settingsFile = new File(userDir, ".gconf"+File.separator+path.toString()+"%gconf.xml");
		if (!settingsFile.exists()) {
			logger.debug("Gnome settings: "+settingsFile+" not found.");
			return null;
		}
		return settingsFile;
	}

	/*************************************************************************
	 * Parse the fixed proxy settings and build an ProxySelector for this a 
	 * chained configuration.
	 * @param settings the proxy settings to evaluate.
	 ************************************************************************/
	
	private ProxySelector setupFixedProxySelector(Properties settings) {
		if (!hasProxySettings(settings)) {
			return null;
		}
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		installHttpSelector(settings, ps);

		if (useForAllProtocols(settings)) {
			ps.setFallbackSelector(ps.getSelector("http"));
		} else {
			installSecureSelector(settings, ps);
			installFtpSelector(settings, ps);
			installSocksSelector(settings, ps);
		}
		return ps;
	}

	/*************************************************************************
	 * Check if the http proxy should also be used for all other protocols.
	 * @param settings to inspect.
	 * @return true if only one proxy is configured else false.
	 ************************************************************************/
	
	private boolean useForAllProtocols(Properties settings) {
		return Boolean.parseBoolean(
				settings.getProperty("/system/http_proxy/use_same_proxy", "false"));
	}

	/*************************************************************************
	 * Checks if we have Proxy configuration settings in the properties.
	 * @param settings to inspect.
	 * @return true if we have found Proxy settings.
	 ************************************************************************/
	
	private boolean hasProxySettings(Properties settings) {
		String proxyHost = settings.getProperty("/system/http_proxy/host", null);
		return proxyHost != null && proxyHost.length() > 0;
	}
	
	/*************************************************************************
	 * Install a http proxy from the given settings.
	 * @param settings to inspect
	 * @param ps the dispatch selector to configure.
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installHttpSelector(Properties settings,
			ProtocolDispatchSelector ps) throws NumberFormatException {
		String proxyHost = settings.getProperty("/system/http_proxy/host", null);
		int proxyPort = Integer.parseInt(settings.getProperty("/system/http_proxy/port", "0").trim());
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
			logger.debug("Gnome http proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector("http", new FixedProxySelector(proxyHost.trim(), proxyPort));
		}
	}

	/*************************************************************************
	 * Install a socks proxy from the given settings.
	 * @param settings to inspect
	 * @param ps the dispatch selector to configure.
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installSocksSelector(Properties settings,
			ProtocolDispatchSelector ps) throws NumberFormatException {
		String proxyHost = settings.getProperty("/system/proxy/socks_host", null);
		int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/socks_port", "0").trim());
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
			logger.debug("Gnome socks proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector("socks", new FixedProxySelector(proxyHost.trim(), proxyPort));
		}
	}

	/*************************************************************************
	 * @param settings
	 * @param ps
	 * @throws NumberFormatException
	 ************************************************************************/
	
	private void installFtpSelector(Properties settings,
			ProtocolDispatchSelector ps) throws NumberFormatException {
		String proxyHost = settings.getProperty("/system/proxy/ftp_host", null);
		int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/ftp_port", "0").trim());
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
			logger.debug("Gnome ftp proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector("ftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
		}
	}

	/*************************************************************************
	 * @param settings
	 * @param ps
	 * @throws NumberFormatException
	 ************************************************************************/
	
	
	private void installSecureSelector(Properties settings,
			ProtocolDispatchSelector ps) throws NumberFormatException {
		String proxyHost = settings.getProperty("/system/proxy/secure_host", null);
		int proxyPort = Integer.parseInt(settings.getProperty("/system/proxy/secure_port", "0").trim());
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
			logger.debug("Gnome secure proxy is "+proxyHost+":"+proxyPort);
			ps.setSelector("https", new FixedProxySelector(proxyHost.trim(), proxyPort));
			ps.setSelector("sftp", new FixedProxySelector(proxyHost.trim(), proxyPort));
		}
	}
	
	/*************************************************************************
	 * Parse the settings file and extract all network.proxy.* settings from it.
	 * @param context the gconf context to parse.
	 * @param settings the settings object to fill.
	 * @return the parsed properties.
	 * @throws IOException on read error.
	 ************************************************************************/
	
	private Properties parseSettings(String context, Properties settings) throws IOException {
		
		// Read settings from file
		File settingsFile = findSettingsFile(context);
		if (settingsFile == null) {
			return settings;
		}
		
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			documentBuilder.setEntityResolver(new EmptyXMLResolver());
			Document doc = documentBuilder.parse(settingsFile);
			Element root = doc.getDocumentElement();
			Node entry = root.getFirstChild();
			while (entry != null) {
				if ("entry".equals(entry.getNodeName()) && entry instanceof Element) {
					String entryName = ((Element)entry).getAttribute("name");
					settings.setProperty(context+entryName, getEntryValue((Element) entry));
				}
				entry = entry.getNextSibling();
			}
		} catch (SAXException e) {
			logger.error("Gnome settings parse error : "+e.getMessage());
			throw new IOException(e.getMessage());
		} catch (ParserConfigurationException e) {
			logger.error("Gnome settings parse error : "+e.getMessage());
			throw new IOException(e.getMessage());
		}
		
		return settings;
	}

	/*************************************************************************
	 * Parse an entry value from a given entry node.
	 * @param entry the XML node to inspect.
	 * @return the value, null if it has no value.
	 ************************************************************************/
	
	private String getEntryValue(Element entry) {
		String type = entry.getAttribute("type");
	
		if ("int".equals(type) || "bool".equals(type)) {
			return entry.getAttribute("value"); 
		}
		if ("string".equals(type)) {
			NodeList list = entry.getElementsByTagName("stringvalue");
			if (list.getLength() > 0) {
				return list.item(0).getTextContent();
			}
		}
		if ("list".equals(type)) {
			StringBuilder result = new StringBuilder();
			NodeList list = entry.getElementsByTagName("li");

			// Build comma separated list of items
			for (int i = 0; i < list.getLength(); i++) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(getEntryValue((Element) list.item(i)));
			}
			return result.toString();
		}
		return null;
	}

	public String getName() {
		return "gnome";
	}

}
