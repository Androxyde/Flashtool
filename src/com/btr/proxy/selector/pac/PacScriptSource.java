package com.btr.proxy.selector.pac;

import java.io.IOException;

/*****************************************************************************
 * An source to fetch the PAC script from.
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public interface PacScriptSource {
	
	/*************************************************************************
	 * Gets the PAC script content as String.
	 * @return a script.
	 * @throws IOException on read error. 
	 ************************************************************************/
	
	public String getScriptContent() throws IOException;  
	
	/*************************************************************************
	 * Checks if the content of the script is valid and if it is possible
	 * to use this script source for a PAC selector.
	 * Note that this might trigger a download of the script content from
	 * a remote location.
	 * @return true if everything is fine, else false.
	 ************************************************************************/
	
	public boolean isScriptValid();

}