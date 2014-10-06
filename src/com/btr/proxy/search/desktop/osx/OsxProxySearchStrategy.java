package com.btr.proxy.search.desktop.osx;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ProxySelector;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.browser.ie.IELocalByPassFilter;
import com.btr.proxy.search.wpad.WpadProxySearchStrategy;
import com.btr.proxy.selector.direct.NoProxySelector;
import com.btr.proxy.selector.fixed.FixedProxySelector;
import com.btr.proxy.selector.fixed.FixedSocksSelector;
import com.btr.proxy.selector.misc.ProtocolDispatchSelector;
import com.btr.proxy.selector.whitelist.ProxyBypassListSelector;
import com.btr.proxy.util.PListParser;
import com.btr.proxy.util.PListParser.Dict;
import com.btr.proxy.util.PListParser.XmlParseException;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;
import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 * Loads the OSX system proxy settings from the settings file.
 * <p>
 * All settings are stored in OSX in a special XML file format.
 * These settings file are named plist files and contain nested dictionaries, arrays and values.
 * </p><p>
 * To parse this file we use a parser that is derived from a plist parser that 
 * comes with the xmlwise XML parser package:
 * </p><p>
 * http://code.google.com/p/xmlwise/
 * </p><p>
 * I modified that parser to work with the default Java XML parsing library.
 *  </p><p>
 * The plist file is located on OSX at:
 * </p><p>
 * /Library/Preferences/SystemConfiguration/preferences.plist
 * </p> 
 * 
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2011
 ****************************************************************************/

public class OsxProxySearchStrategy implements ProxySearchStrategy {
	
    public static final String OVERRIDE_SETTINGS_FILE = "com.btr.proxy.osx.settingsFile";
	public static final String OVERRIDE_ACCEPTED_DEVICES = "com.btr.proxy.osx.acceptedDevices"; 
	
	private static final String SETTINGS_FILE = "/Library/Preferences/SystemConfiguration/preferences.plist";
		
	private static Logger logger = Logger.getLogger(OsxProxySearchStrategy.class);
	/*************************************************************************
	 * ProxySelector
	 * @see java.net.ProxySelector#ProxySelector()
	 ************************************************************************/
	
	public OsxProxySearchStrategy() {
		super();
	}
	
	/*************************************************************************
	 * Loads the proxy settings and initializes a proxy selector for the OSX
	 * proxy settings.
	 * @return a configured ProxySelector, null if none is found.
	 * @throws ProxyException on file reading error. 
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {
		
		logger.debug("Detecting OSX proxy settings");
        
        try {
           List<String> acceptedInterfaces = getNetworkInterfaces();
           
           Dict settings = PListParser.load(getSettingsFile());
           Object currentSet = settings.getAtPath("/CurrentSet");
           if (currentSet == null) {
              throw new ProxyException("CurrentSet not defined");
           }
           
           Dict networkSet = (Dict) settings.getAtPath(String.valueOf(currentSet));
           List<?> serviceOrder = (List<?>) networkSet.getAtPath("/Network/Global/IPv4/ServiceOrder");
           if (serviceOrder == null || serviceOrder.size() == 0) {
              throw new ProxyException("ServiceOrder not defined");
           }

           // Look at the Services in priority order and pick the first one that was
           // also accepted above
           Dict proxySettings = null;
           for (int i = 0; i < serviceOrder.size() && proxySettings == null; i++) {
              Object candidateService = serviceOrder.get(i);
              Object networkService = networkSet.getAtPath("/Network/Service/"+candidateService+"/__LINK__");
              if (networkService == null ) {
                 throw new ProxyException("NetworkService not defined.");
              }
              Dict selectedServiceSettings = (Dict) settings.getAtPath(""+networkService);
              String interfaceName = (String) selectedServiceSettings.getAtPath("/Interface/DeviceName");
              if (acceptedInterfaces.contains(interfaceName)) {
            	  logger.debug("Looking up proxies for device " + interfaceName);
                 proxySettings = (Dict) selectedServiceSettings.getAtPath("/Proxies");
              }
           }
           if (proxySettings == null) {
              return NoProxySelector.getInstance();
           }
           
          return buildSelector(proxySettings);
        } catch (XmlParseException e) {
           throw new ProxyException(e);
        } catch (IOException e) {
           throw new ProxyException(e);
        }
	}

	/*************************************************************************
	 * Build a selector from the given settings.
	 * @param proxySettings to parse 
	 * @return the configured selector
	 * @throws ProxyException on error
	 ************************************************************************/
	
	private ProxySelector buildSelector(Dict proxySettings) throws ProxyException {
		ProtocolDispatchSelector ps = new ProtocolDispatchSelector();
		installSelectorForProtocol(proxySettings, ps, "HTTP");
		installSelectorForProtocol(proxySettings, ps, "HTTPS");
		installSelectorForProtocol(proxySettings, ps, "FTP");
		installSelectorForProtocol(proxySettings, ps, "Gopher");
		installSelectorForProtocol(proxySettings, ps, "RTSP");
		installSocksProxy(proxySettings, ps);

		ProxySelector result = ps;
		result = installPacProxyIfAvailable(proxySettings, result);
		result = autodetectProxyIfAvailable(proxySettings, result);

		result = installExceptionList(proxySettings, result);
		result = installSimpleHostFilter(proxySettings, result);
		return result;
	}

	/*************************************************************************
	 * Create a list of Ethernet interfaces that are connected
	 * @return
	 * @throws SocketException 
	 ************************************************************************/
	
	private List<String> getNetworkInterfaces() throws SocketException {
		String override = System.getProperty(OVERRIDE_ACCEPTED_DEVICES);
		if (override != null && override.length() > 0) {
			return Arrays.asList(override.split(";"));
		}

		List<String> acceptedInterfaces = new ArrayList<String>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface ni = interfaces.nextElement();
			if (isInterfaceAllowed(ni)) {
				acceptedInterfaces.add(ni.getName());
			}
		}
		return acceptedInterfaces;
	}

	/*************************************************************************
	 * Check if a given network interface is interesting for us.
	 * @param ni the interface to check
	 * @return true if accepted else false.
	 * @throws SocketException on error.
	 ************************************************************************/
	
	private boolean isInterfaceAllowed(NetworkInterface ni) throws SocketException {
		return !ni.isLoopback() && 
			   !ni.isPointToPoint() && // Not sure if we should filter the point to point interfaces? 
			   !ni.isVirtual() && 
			   ni.isUp();
	}

	/*************************************************************************
	 * @return
	 ************************************************************************/
	
	private File getSettingsFile() {
		File result = new File(SETTINGS_FILE); 
		String overrideFile = System.getProperty(OVERRIDE_SETTINGS_FILE);
		if (overrideFile != null) { 
			return new File(overrideFile);
		}
		return result;
	}

	/*************************************************************************
	 * @param proxySettings
	 * @param result
	 * @return
	 ************************************************************************/
	
	private ProxySelector installSimpleHostFilter(
			Dict proxySettings, ProxySelector result) {
		if (isActive(proxySettings.get("ExcludeSimpleHostnames"))) {
			List<UriFilter> localBypassFilter = new ArrayList<UriFilter>();
			localBypassFilter.add(new IELocalByPassFilter());
			result = new ProxyBypassListSelector(localBypassFilter, result);
		}
		return result;
	}

	/*************************************************************************
	 * @param proxySettings
	 * @param result
	 * @return
	 ************************************************************************/
	
	private ProxySelector installExceptionList(
			Dict proxySettings, ProxySelector result) {
		List<?> proxyExceptions = (List<?>) proxySettings.get("ExceptionsList");
		if (proxyExceptions != null && proxyExceptions.size() > 0) {
			logger.debug("OSX uses proxy bypass list: "+proxyExceptions);
			String noProxyList = toCommaSeparatedString(proxyExceptions);
			result = new ProxyBypassListSelector(noProxyList, result);
		}
		return result;
	}

	/*************************************************************************
	 * Convert a list to a comma separated list.
	 * @param proxyExceptions list of elements.
	 * @return a comma separated string of the list's content.
	 ************************************************************************/
	
	private String toCommaSeparatedString(List<?> proxyExceptions) {
		StringBuilder result = new StringBuilder();
		for (Object object : proxyExceptions) {
			if (result.length() > 0) {
				result.append(",");
			}
			result.append(object);
		}
		return result.toString();
	}

	/*************************************************************************
	 * @param proxySettings
	 * @param result
	 * @return
	 * @throws ProxyException
	 ************************************************************************/
	
	private ProxySelector autodetectProxyIfAvailable(
			Dict proxySettings, ProxySelector result)
			throws ProxyException {
		if (isActive(proxySettings.get("ProxyAutoDiscoveryEnable"))) {
			ProxySelector wp = new WpadProxySearchStrategy().getProxySelector();
			if (wp != null) {
				result = wp;
			}
		}
		return result;
	}

	/*************************************************************************
	 * @param proxySettings
	 * @param result
	 * @return
	 ************************************************************************/
	
	private ProxySelector installPacProxyIfAvailable(Dict proxySettings,
			ProxySelector result) {
		if (isActive(proxySettings.get("ProxyAutoConfigEnable"))) {
			String url = (String) proxySettings.get("ProxyAutoConfigURLString");
			result = ProxyUtil.buildPacSelectorForUrl(url);
		}
		return result;
	}

	/*************************************************************************
	 * Build a socks proxy and set it for the socks protocol.
	 * @param proxySettings to read the config values from.
	 * @param ps the ProtocolDispatchSelector to install the new proxy on.
	 ************************************************************************/
	
	private void installSocksProxy(Dict proxySettings,
			ProtocolDispatchSelector ps) {
		if (isActive(proxySettings.get("SOCKSEnable"))) {
			String proxyHost = (String) proxySettings.get("SOCKSProxy");
			int proxyPort = (Integer) proxySettings.get("SOCKSPort");
		    ps.setSelector("socks", new FixedSocksSelector(proxyHost, proxyPort));
		    logger.debug("OSX socks proxy is "+proxyHost+":"+proxyPort);
		}
	}

	/*************************************************************************
	 * Installs a proxy selector for the given protocoll on the ProtocolDispatchSelector
	 * @param proxySettings to read the config for the procotol from.
	 * @param ps the ProtocolDispatchSelector to install the new selector on.
	 * @param protocol to use.
	 ************************************************************************/
	
	private void installSelectorForProtocol(Dict proxySettings,
			ProtocolDispatchSelector ps, String protocol) {
		String prefix = protocol.trim(); 
		if (isActive(proxySettings.get(prefix+"Enable"))) {
			String proxyHost = (String) proxySettings.get(prefix+"Proxy");
			int proxyPort = (Integer) proxySettings.get(prefix+"Port");
			FixedProxySelector fp = new FixedProxySelector(proxyHost, proxyPort);
			ps.setSelector(protocol.toLowerCase(), fp);
			logger.debug("OSX uses for "+protocol+" the proxy "+proxyHost+":"+proxyPort);
		}
	}

	/*************************************************************************
	 * Checks if the given value is set to "on". 
	 * @param value the value to test.
	 * @return true if it is set else false.
	 ************************************************************************/
	
	private boolean isActive(Object value) {
		return Integer.valueOf(1).equals(value);
	}

	public String getName() {
		return "osx";
	}

}