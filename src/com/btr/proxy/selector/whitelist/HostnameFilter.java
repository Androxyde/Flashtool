package com.btr.proxy.selector.whitelist;

import java.net.URI;
import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 * Tests if a host name of a given URI matches some criteria. 
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class HostnameFilter implements UriFilter {

	private static final String PROTOCOL_ENDING = "://";

	public enum Mode {BEGINS_WITH, ENDS_WITH, REGEX}
	
	private String matchTo;
	private String protocolFilter;
	private Mode mode;
	
	/*************************************************************************
	 * Constructor
	 * @param mode the filter mode.
	 * @param matchTo the match criteria.
	 ************************************************************************/
	
	public HostnameFilter(Mode mode, String matchTo) {
		super();
		this.mode = mode;
		this.matchTo = matchTo.toLowerCase();
		
		extractProtocolFilter();
	}

	/*************************************************************************
	 * Extracts the protocol if one is given to initialize the protocol matcher.
	 ************************************************************************/
	
	private void extractProtocolFilter() {
		int protocolIndex = this.matchTo.indexOf(PROTOCOL_ENDING);
		if (protocolIndex != -1) {
			this.protocolFilter = this.matchTo.substring(0, protocolIndex);
			this.matchTo = this.matchTo.substring(protocolIndex+PROTOCOL_ENDING.length());
		}
	}
	
	/*************************************************************************
	 * accept
	 * @see com.btr.proxy.util.UriFilter#accept(java.net.URI)
	 ************************************************************************/
	
	public boolean accept(URI uri) {
		if (uri == null || uri.getAuthority() == null) {
			return false;
		}
		
		if (!isProtocolMatching(uri)) {
			return false;
		}
		
		String host = uri.getAuthority();
		
		// Strip away port.
		int index = host.indexOf(':');
		if (index != -1) {
			host = host.substring(0, index);
		}

		switch (this.mode) {
			case BEGINS_WITH :
				return host.toLowerCase().startsWith(this.matchTo);
			case ENDS_WITH :
				return host.toLowerCase().endsWith(this.matchTo);
			case REGEX :
				return host.toLowerCase().matches(this.matchTo);
		}
		return false;
	}

	/*************************************************************************
	 * Applies the protocol filter if available to see if we have a match.
	 * @param uri to test for a correct protocol.
	 * @return true if passed else false.
	 ************************************************************************/
	
	private boolean isProtocolMatching(URI uri) {
		return this.protocolFilter == null 
				|| uri.getScheme() == null 
				|| uri.getScheme().equalsIgnoreCase(this.protocolFilter);
	}
	
}
