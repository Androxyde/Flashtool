package com.btr.proxy.search.browser.firefox;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

import com.btr.jna.WinShell;
/*****************************************************************************
 * Finds the Firefox profile on Windows platforms. 
 * On Windows the profiles are located in the users appdata directory under:
 * <p>
 * <i>Mozilla\Firefox\Profiles\</i>
 * </p> 
 * The location of the appdata folder is read from the windows registry.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

class WinFirefoxProfileSource implements FirefoxProfileSource {

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	private static Logger logger = Logger.getLogger(WinFirefoxProfileSource.class);
	
	public WinFirefoxProfileSource() {
		super();
	}
	
	/*************************************************************************
	 * Reads the current location of the app data folder from the registry.
	 * @return a path to the folder.
	 ************************************************************************/
	
	private String getAppFolder() {
		return WinShell.getUserDir();
	}
	
	/*************************************************************************
	 * Get profile folder for the Windows Firefox profile
	 * @throws IOException on error. 
	 ************************************************************************/

	public File getProfileFolder() throws IOException {
		
		File appDataDir = new File(getAppFolder());
		File cfgDir = new File(appDataDir, "Mozilla"+File.separator+"Firefox"+File.separator+"Profiles");
		
		if (!cfgDir.exists()) {
			logger.debug("Firefox windows settings folder not found.");
			return null;
		}
		File[] profiles = cfgDir.listFiles();
		if (profiles == null || profiles.length == 0) {
			logger.debug("Firefox windows settings folder not found.");
			return null;
		}
		for (File p : profiles) {
			if (p.getName().endsWith(".default")) {
				logger.debug("Firefox windows settings folder is "+p);
				return p;
			}
		}
		
		// Fall back -> take the first one found.
		logger.debug("Firefox windows settings folder is "+profiles[0]);
		return profiles[0];
	}

}
