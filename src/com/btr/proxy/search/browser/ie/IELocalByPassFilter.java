package com.btr.proxy.search.browser.ie;

import java.net.URI;

import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 *  
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class IELocalByPassFilter implements UriFilter {

	/*************************************************************************
	 * accept
	 * @see com.btr.proxy.util.UriFilter#accept(java.net.URI)
	 ************************************************************************/

	public boolean accept(URI uri) {
		if (uri == null) {
			return false;
		}
		String host = uri.getAuthority();
		return host != null && !host.contains(".");
	}

}

