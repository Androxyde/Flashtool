package com.btr.proxy.search.browser.firefox;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.ini4j.Ini;

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
	static final Logger logger = LogManager.getLogger(WinFirefoxProfileSource.class);
	
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
		
		File profiles = new File(appDataDir, "Mozilla"+File.separator+"Firefox"+File.separator+"profiles.ini");
		
		if (!profiles.exists()) {
			logger.debug("Firefox windows settings not found.");
			return null;
		}
		String profileFolder=null;
		Ini profilesIni = new Ini(profiles);
		Iterator i = profilesIni.keySet().iterator();
		while (i.hasNext()) {
			String section = (String)i.next();
			if ("default".equals(profilesIni.get(section).get("Name"))) {
				if ("1".equals(profilesIni.get(section).get("IsRelative")))
					profileFolder = profiles.getParentFile().getAbsolutePath()+File.separator+profilesIni.get(section).get("Path");
			}
		}
		if (profileFolder!=null) {
			logger.debug("Firefox windows settings folder is "+profileFolder);
			return new File(profileFolder);
		}
		return null;
	}
}
