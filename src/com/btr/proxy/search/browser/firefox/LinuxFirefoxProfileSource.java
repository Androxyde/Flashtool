package com.btr.proxy.search.browser.firefox;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

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
	
	public File getProfileFolder()  throws IOException {
		File userDir = new File(System.getProperty("user.home"));

		File profiles = new File(userDir, ".mozilla"+File.separator+"firefox"+File.separator+"profiles.ini");
		if (!profiles.exists()) {
			logger.debug("Firefox linux settings not found.");
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
			logger.debug("Firefox linux settings folder is "+profileFolder);
			return new File(profileFolder);
		}
		return null;
	}

}
