package com.btr.proxy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/*****************************************************************************
 * Defines some helper methods to find the correct platform.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class PlatformUtil {

	public enum Platform {WIN, LINUX, MAC_OS, SOLARIS, OTHER} 
	public enum Desktop  {WIN, KDE, GNOME, MAC_OS, OTHER} 
	public enum Browser  {IE, FIREFOX}
	static final Logger logger = LogManager.getLogger(PlatformUtil.class);
	
	/*************************************************************************
	 * Gets the platform we are currently running on.
	 * @return a platform code.
	 ************************************************************************/
	
	public static Platform getCurrentPlattform() {
		String osName = System.getProperty("os.name");
		logger.debug("Detecting platform. Name is: "+osName);
		
		if (osName.toLowerCase().contains("windows")) {
			logger.debug("Detected Windows platform: "+osName);
			return Platform.WIN;
		} 
		if (osName.toLowerCase().contains("linux")) {
			logger.debug("Detected Linux platform: "+ osName);
			return Platform.LINUX;
		} 
		if (osName.startsWith("Mac OS")) {
			logger.debug("Detected Mac OS platform: "+ osName);
			return Platform.MAC_OS;
		} 
		if (osName.startsWith("SunOS")) {
			logger.debug("Detected Solaris platform: "+ osName);
			return Platform.SOLARIS;
		}
		
		return Platform.OTHER;
	}
	
	/*************************************************************************
	 * Gets the ID for the platform default browser.
	 * @return a browser ID, null if no supported browser was detected.
	 ************************************************************************/
	
	public static Browser getDefaultBrowser() {
		// Use better logic to detect default browser?
		if (getCurrentPlattform() == Platform.WIN) {
			logger.debug("Detected Browser is InternetExplorer");
			return Browser.IE;
		} else {
			logger.debug("Detected Browser Firefox. Fallback?");
			return Browser.FIREFOX;
		}
	}
	
	/*************************************************************************
	 * Gets the desktop that we are running on.
	 * @return the desktop identifier.
	 ************************************************************************/
	
	public static Desktop getCurrentDesktop() {
		Platform platf = getCurrentPlattform();
		
		if (platf == Platform.WIN) {
			logger.debug("Detected Windows desktop");
			return Desktop.WIN;
		} 
		if (platf == Platform.MAC_OS) {
			logger.debug("Detected Mac OS desktop");
			return Desktop.MAC_OS;
		} 

		if (platf == Platform.LINUX 
				|| platf == Platform.SOLARIS 
				|| platf == Platform.OTHER) {
			
			if (isKDE()) {
				logger.debug("Detected KDE desktop");
				return Desktop.KDE;
			}
			if (isGnome()) {
				logger.debug("Detected Gnome desktop");
				return Desktop.GNOME;
			}
		} 
		logger.debug("Detected Unknown desktop");
		return Desktop.OTHER;
	}

	/*************************************************************************
	 * Checks if we are currently running under Gnome desktop.
	 * @return true if it is a Gnome else false.
	 ************************************************************************/
	
	private static boolean isGnome() {
		return System.getenv("GNOME_DESKTOP_SESSION_ID") != null;
	}

	/*************************************************************************
	 * Checks if we are currently running under KDE desktop.
	 * @return true if it is a KDE else false. 
	 ************************************************************************/
	
	private static boolean isKDE() {
		return System.getenv("KDE_SESSION_VERSION") != null;
	}
	
}
