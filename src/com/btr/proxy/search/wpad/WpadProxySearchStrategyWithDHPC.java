package com.btr.proxy.search.wpad;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.wpad.dhcp.DHCPMessage;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.ProxyUtil;

/*****************************************************************************
 * Uses automatic proxy script search (WPAD) to find an PAC file automatically.
 * <p>
 * Note: at the moment only the DNS name guessing schema is implemented. 
 * All others are missing.
 * </p><p>
 * For more information about WPAD:
 * <a href="http://en.wikipedia.org/wiki/Web_Proxy_Autodiscovery_Protocol">Web_Proxy_Autodiscovery_Protocol</a>
 * </p><p>
 * Outdated RFC draft:
 * <a href="http://www.web-cache.com/Writings/Internet-Drafts/draft-ietf-wrec-wpad-01.txt">draft-ietf-wrec-wpad-01.txt</a>
 * </p>
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class WpadProxySearchStrategyWithDHPC implements ProxySearchStrategy {
	
	static final Logger logger = LogManager.getLogger(WpadProxySearchStrategyWithDHPC.class);
	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	public WpadProxySearchStrategyWithDHPC() {
		super();
	}
	
	/*************************************************************************
	 * Loads the proxy settings from a PAC file. 
	 * The location of the PAC file is determined automatically.
	 * @return a configured ProxySelector, null if none is found.
	 * @throws ProxyException on error. 
	 ************************************************************************/

	public ProxySelector getProxySelector() throws ProxyException {
		try {
			logger.debug("Using WPAD to find a proxy");

			String pacScriptUrl = detectScriptUrlPerDHCP();
			if (pacScriptUrl == null) {
				pacScriptUrl = detectScriptUrlPerDNS();
			}
			if (pacScriptUrl == null) {
				return null;
			}
			logger.debug("PAC script url found: "+pacScriptUrl);
			return ProxyUtil.buildPacSelectorForUrl(pacScriptUrl);
		} catch (IOException e) {
			logger.error("Error during WPAD search : "+ e.getMessage());
			throw new ProxyException(e);
		}
	}
	
	/*************************************************************************
	 * Loads the settings and stores them in a properties map.
	 * @return the settings.
	 ************************************************************************/
	
	public Properties readSettings() {
		try {
			String pacScriptUrl = detectScriptUrlPerDHCP();
			if (pacScriptUrl == null) {
				pacScriptUrl = detectScriptUrlPerDNS();
			}
			if (pacScriptUrl == null) {
				return null;
			}
			Properties result = new Properties();
			result.setProperty("url", pacScriptUrl);
			return result;
		} catch (IOException e) {
			// Ignore and return empty properties.
			return new Properties();
		}
	}

	/*************************************************************************
	 * Uses DNS to find the script URL.
	 * Attention: this detection method is known to have some severe security issues. 
	 * @return the URL, null if not found.
	 ************************************************************************/
	
	private String detectScriptUrlPerDNS() throws IOException {
		String result = null;
		String fqdn = InetAddress.getLocalHost().getCanonicalHostName();

		logger.debug("Searching per DNS guessing.");

		int index = fqdn.indexOf('.');
		while (index != -1 && result == null) {
			fqdn = fqdn.substring(index+1);
			
			// if we are already on TLD level then escape 
			if (fqdn.indexOf('.') == -1) {
				break;
			}
			
			// Try to connect to URL
			try {
				URL lookupURL = new URL("http://wpad."+ fqdn +"/wpad.dat");
				logger.debug("Trying url: "+lookupURL);

				HttpURLConnection con = (HttpURLConnection) lookupURL.openConnection(Proxy.NO_PROXY);
				con.setInstanceFollowRedirects(true);
				con.setRequestProperty("accept", "application/x-ns-proxy-autoconfig");
				if (con.getResponseCode() == 200) {
					result = lookupURL.toString();
				}
				con.disconnect();
			} catch (UnknownHostException e) {
				logger.debug("Not available!");
				// Not a real error, try next address
			}

			index = fqdn.indexOf('.');
		}
			
		return result;
	}

	/*************************************************************************
	 * Uses DHCP to find the script URL.
	 * @return the URL, null if not found.
	 ************************************************************************/
	
	private String detectScriptUrlPerDHCP() {
		logger.debug("Searching per DHCP not supported yet.");
		// TODO Rossi 28.04.2009 Not implemented yet.
		return null;
	}
	
	
	// Sends DHCPREQUEST Message and returns server message
	
	private DHCPMessage SendRequest(DHCPMessage offerMessageIn) {
//		DHCPSocket bindSocket = new DHCPSocket(DHCPMessage.CLIENT_PORT);
//
//		DHCPMessage messageOut = new DHCPMessage(DHCPMessage.SERVER_PORT);
//		DHCPMessage messageIn = new DHCPMessage(DHCPMessage.CLIENT_PORT);
//		try {
//			messageOut = offerMessageIn;
//			messageOut.setOp((byte) 1); // setup message to send a DCHPREQUEST
//			byte[] opt = new byte[1];
//			opt[0] = DHCPMessage.DHCPREQUEST;
//			messageOut.setOption(DHCPMessage.DHCPDISCOVER, opt); // change message type
//			messageOut.setOption(REQUESTED_IP, offerMessageIn.getYiaddr());
//			bindSocket.send(messageOut); // send DHCPREQUEST
//			System.out.print(this.getName());
//			System.out.print(" sending DHCPREQUEST for ");
//			System.out.println(bytesToString(offerMessageIn
//					.getOption(REQUESTED_IP)));
//			boolean sentinal = true;
//			while (sentinal) {
//				if (bindSocket.receive(messageIn)) {
//					if (messageOut.getXid() == messageIn.getXid()) {
//						sentinal = false;
//					} else {
//						bindSocket.send(messageOut);
//					}
//				} else {
//					bindSocket.send(messageOut);
//				}
//			}
//		} catch (SocketException e) {
//			System.err.println(e);
//		} catch (IOException e) {
//			System.err.println(e);
//		} // end catch
//		return messageIn;
		return null;
	}
	
	public String getName() {
		return "wpadwithdhpc";
	}

}
