package com.btr.proxy.search.desktop;

import java.net.ProxySelector;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.btr.proxy.search.ProxySearchStrategy;
import com.btr.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.btr.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.btr.proxy.search.desktop.osx.OsxProxySearchStrategy;
import com.btr.proxy.search.desktop.win.WinProxySearchStrategy;
import com.btr.proxy.util.PlatformUtil;
import com.btr.proxy.util.ProxyException;
import com.btr.proxy.util.PlatformUtil.Desktop;
import com.btr.proxy.util.PlatformUtil.Platform;

/*****************************************************************************
 * This search provider will try to find out on which desktop platform we 
 * are running and then will initialize the default proxy search. 
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class DesktopProxySearchStrategy implements ProxySearchStrategy {

	static final Logger logger = LogManager.getLogger(DesktopProxySearchStrategy.class);
	
	/*************************************************************************
	 * Gets the default ProxySelector for the current platform.
	 * @return a ProxySelector, null if none is found.
	 * @throws ProxyException on error.
	 ************************************************************************/
	
	public ProxySelector getProxySelector() throws ProxyException {
		ProxySearchStrategy strategy = findDesktopSpecificStrategy();
		return strategy == null? null : strategy.getProxySelector();
	}

	/*************************************************************************
	 * Determine the desktop and create a strategy for it.
	 * @return a desktop specific strategy, null if none was found.
	 ************************************************************************/
	
	private ProxySearchStrategy findDesktopSpecificStrategy() {
		Platform pf = PlatformUtil.getCurrentPlattform();
		Desktop dt = PlatformUtil.getCurrentDesktop();
		
		logger.debug("Detecting system settings.");
		
		ProxySearchStrategy strategy = null;
		
		if (pf == Platform.WIN) {
			logger.debug("We are running on Windows.");
			strategy = new WinProxySearchStrategy();
		} else
		if (dt == Desktop.KDE) {
			logger.debug("We are running on KDE.");
			strategy = new KdeProxySearchStrategy();
		} else 
		if (dt == Desktop.GNOME) {
			logger.debug("We are running on Gnome.");
			strategy = new GnomeProxySearchStrategy();
		} else
		if (dt == Desktop.MAC_OS) {
			logger.debug("We are running on Mac OSX.");
			strategy = new OsxProxySearchStrategy();
		}
		return strategy;
	}

	public String getName() {
		return "desktop";
	}
}
