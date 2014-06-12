package com.btr.proxy.search.browser.firefox;

import java.io.File;
import java.io.IOException;

/*****************************************************************************
 * A profile source for Firefox profiles. 
 * 
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

interface FirefoxProfileSource {
	
	/*************************************************************************
	 * Gets a profile folder found on the current system.
	 * If multiple profile folders are available the "default" profile is chosen. 
	 * @return a profile folder.
	 * @throws IOException on error.
	 ************************************************************************/
	
	public File getProfileFolder() throws IOException;

}
