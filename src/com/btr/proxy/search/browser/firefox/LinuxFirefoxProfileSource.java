package com.btr.proxy.search.browser.firefox;

import java.io.File;

import org.apache.log4j.Logger;
import org.logger.MyLogger;

/*****************************************************************************
 * Searches for Firefox profile on an Linux / Unix base system.
 * This will scan the <i>.mozilla</i> folder in the users home directory to find the 
 * profiles. 
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

class LinuxFirefoxProfileSource implements FirefoxProfileSource {

	/*************************************************************************
	 * Get profile folder for the Linux Firefox profile
	 ************************************************************************/
	private static Logger logger = Logger.getLogger(LinuxFirefoxProfileSource.class);
	
	public File getProfileFolder() {
		File userDir = new File(System.getProperty("user.home"));
		File cfgDir = new File(userDir, ".mozilla"+File.separator+"firefox"+File.separator);
		if (!cfgDir.exists()) {
			logger.debug("Firefox settings folder not found!");
			return null;
		}
		File[] profiles = cfgDir.listFiles();
		if (profiles == null || profiles.length == 0) {
			logger.debug("Firefox settings folder not found!");
			return null;
		}
		for (File p : profiles) {
			if (p.getName().endsWith(".default")) {
				logger.debug("Firefox settings folder is "+p);
				return p;
			}
		}
		
		// Fall back -> take the first one found.
		logger.debug("Firefox settings folder is "+profiles[0]);
		return profiles[0];
	}

}
