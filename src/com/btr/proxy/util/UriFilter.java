package com.btr.proxy.util;

import java.net.URI;

/*****************************************************************************
 * Interface for an URI filter. 
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public interface UriFilter {
	
	/*************************************************************************
	 * Tests an URI against a given matching criteria. 
	 * @param uri the URI to test.
	 * @return true if it matches the criteria else false.
	 ************************************************************************/
	
	public abstract boolean accept(URI uri);

}
