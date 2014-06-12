package com.btr.proxy.selector.whitelist;

import java.util.ArrayList;
import java.util.List;

import com.btr.proxy.selector.whitelist.HostnameFilter.Mode;
import com.btr.proxy.util.UriFilter;

/*****************************************************************************
 * Default implementation for an white list parser. This will support the most
 * common forms of filters found in white lists. 
 * The white list is a comma (or space) separated list of domain names or IP addresses.
 * The following section shows some examples.
 * 
 *  .mynet.com  		- Filters all host names ending with .mynet.com
 *  *.mynet.com  		- Filters all host names ending with .mynet.com
 *  www.mynet.*  		- Filters all host names starting with www.mynet.
 *  123.12.32.1 		- Filters the IP 123.12.32.1
 *  123.12.32.1/255 	- Filters the IP range   
 *  http://www.mynet.com - Filters only HTTP protocol not FTP and no HTTPS
 * 
 * Example of a list:   
 * 
 * .mynet.com, *.my-other-net.org, 123.55.23.222, 123.55.23.0/24
 * 
 * Some info about this topic can be found here:
 * http://kb.mozillazine.org/No_proxy_for
 * http://technet.microsoft.com/en-us/library/dd361953.aspx
 * 
 * Note that this implementation does not cover all variations of all browsers
 * but should cover the most used formats. 
 * 
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class DefaultWhiteListParser implements WhiteListParser {

	/*************************************************************************
	 * parseWhiteList
	 * @see com.btr.proxy.selector.whitelist.WhiteListParser#parseWhiteList(java.lang.String)
	 ************************************************************************/
	
	public List<UriFilter> parseWhiteList(String whiteList) {
		List<UriFilter> result = new ArrayList<UriFilter>();
		
		String[] token = whiteList.split("[, ]+");
		for (int i = 0; i < token.length; i++) {
			String tkn = token[i].trim();
			if (isIP4SubnetFilter(tkn)) {
				result.add(new IpRangeFilter(tkn));
				continue;
			} else 
			if (tkn.endsWith("*")) {
				tkn = tkn.substring(0, tkn.length()-1);
				result.add(new HostnameFilter(Mode.BEGINS_WITH, tkn));
				continue;
			} else 
			if (tkn.trim().startsWith("*")) {
				tkn = tkn.substring(1);
				result.add(new HostnameFilter(Mode.ENDS_WITH, tkn));
			} else {
				result.add(new HostnameFilter(Mode.ENDS_WITH, tkn));
			}
		}
		
		return result;
	}

	/*************************************************************************
	 * @param token
	 * @return
	 ************************************************************************/
	
	private boolean isIP4SubnetFilter(String token) {
		return IPv4WithSubnetChecker.isValid(token);
	}

}
