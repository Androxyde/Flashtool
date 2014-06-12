package com.btr.proxy.selector.whitelist;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 * Filters an URI by inspecting it's IP address is in a given range. 
 * The range  as must be defined in CIDR notation.
 * e.g. 192.0.2.1/24,
 *
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class IpRangeFilter implements UriFilter {

	private byte[] matchTo;
	int numOfBits;

	/*************************************************************************
	 * Constructor
	 * @param matchTo the match subnet in CIDR notation.
	 ************************************************************************/
	
	public IpRangeFilter(String matchTo) {
		super();
		
		String[] parts = matchTo.split("/");
		if (parts.length != 2) {
			throw new IllegalArgumentException("IP range is not valid:"+matchTo);
		}
		
		try {
			InetAddress address = InetAddress.getByName(parts[0].trim());
			this.matchTo = address.getAddress();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("IP range is not valid:"+matchTo);
		}
		
		this.numOfBits = Integer.parseInt(parts[1].trim());
	}
	
	/*************************************************************************
	 * accept
	 * @see com.btr.proxy.util.UriFilter#accept(java.net.URI)
	 ************************************************************************/

	public boolean accept(URI uri) {
		if (uri == null || uri.getHost() == null) {
			return false;
		}
		try {
			InetAddress address = InetAddress.getByName(uri.getHost());
			byte[] addr = address.getAddress();
			
			// Comparing IP6 against IP4?
			if (addr.length != this.matchTo.length) {
				return false;
			}

			int bit = 0;
			for (int nibble = 0; nibble < addr.length; nibble++) {
				for (int nibblePos = 7; nibblePos >= 0; nibblePos--) {
					int mask = 1 << nibblePos;
					if ((this.matchTo[nibble] & mask) != (addr[nibble] & mask)) {
						return false;
					}
					bit++;
					if (bit >= this.numOfBits) {
						return true;
					}
				}
			}
			
		} catch (UnknownHostException e) {
			// In this case we can not get the IP do not match.
		}
		return false;
	}

}
