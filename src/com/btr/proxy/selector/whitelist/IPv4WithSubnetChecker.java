package com.btr.proxy.selector.whitelist;

import java.util.regex.Pattern;

/*****************************************************************************
 * Checks if the given string is a IP4 range subnet definition
 * of the format 192.168.0/24
 * Based on a contribution by Jan Engler  
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ****************************************************************************/

public class IPv4WithSubnetChecker {

	private static Pattern IP_SUB_PATTERN = Pattern.compile(
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])/(\\d|([12]\\d|3[0-2]))$");

	/*************************************************************************
	 * Tests if a given string is of in the correct format for an IP4 subnet mask.
	 * @param possibleIPAddress to test for valid format.
	 * @return true if valid else false.
	 ************************************************************************/
	
	public static boolean isValid(String possibleIPAddress) {
		return IP_SUB_PATTERN.matcher(possibleIPAddress).matches();
	}
}
