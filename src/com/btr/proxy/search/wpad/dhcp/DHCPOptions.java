package com.btr.proxy.search.wpad.dhcp;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class represents a linked list of options for a DHCP message.
 * Its purpose is to ease option handling such as add, remove or change.
 * 
 * @author Jason Goldschmidt and Simon Frankenberger
 */
public class DHCPOptions {
	public static final int OPTION_PAD = 0;
	public static final int OPTION_NETMASK = 1;
	public static final int OPTION_TIME_OFFSET = 2;
	public static final int OPTION_ROUTERS = 3;
	public static final int OPTION_TIME_SERVERS = 4;
	public static final int OPTION_NAME_SERVERS = 5;
	public static final int OPTION_DNS_SERVERS = 6;
	public static final int OPTION_LOG_SERVERS = 7;
	public static final int OPTION_COOKIE_SERVERS = 8;
	public static final int OPTION_LPR_SERVERS = 9;
	public static final int OPTION_IMPRESS_SERVERS = 10;
	public static final int OPTION_RESSOURCE_LOCATION_SERVERS = 11;
	public static final int OPTION_HOSTNAME = 12;
	public static final int OPTION_BOOT_FILESIZE = 13;
	public static final int OPTION_MERIT_DUMPFILE = 14;
	public static final int OPTION_DOMAIN_NAME = 15;
	public static final int OPTION_SWAP_SERVER = 16;
	public static final int OPTION_ROOT_PATH = 17;
	public static final int OPTION_EXTENSIONS_PATH = 18;
	public static final int OPTION_END = 255;
	
	public static final int OPTION_IP_HOST_FORWARDING_ENABLE = 19;
	public static final int OPTION_IP_HOST_NON_LOCAL_SOURCE_ROUTING_ENABLE = 20;
	public static final int OPTION_IP_HOST_POLICY_FILTERS = 21;
	public static final int OPTION_IP_HOST_MAXIMUM_DATAGRAM_REASSEMBLY_SIZE = 22;
	public static final int OPTION_IP_HOST_DEFAULT_TTL = 23;
	public static final int OPTION_IP_HOST_MTU_AGEING_TIMEOUT = 24;
	public static final int OPTION_IP_HOST_MTU_PLATEAU_TABLE = 25;
	
	public static final int OPTION_IP_INTERFACE_MTU = 26;
	public static final int OPTION_IP_INTERFACE_ALL_SUBNETS_LOCAL_ENABLE = 27;
	public static final int OPTION_IP_INTERFACE_BROADCAST_ADDRESS = 28;
	public static final int OPTION_IP_INTERFACE_PERFORM_MASK_DISCOVERY_ENABLE = 29;
	public static final int OPTION_IP_INTERFACE_MASK_SUPPLIER_ENABLE = 30;
	public static final int OPTION_IP_INTERFACE_PERFORM_ROUTER_DISCOVERY_ENABLE = 31;
	public static final int OPTION_IP_INTERFACE_ROUTER_SOLICITATION_ADDRESS = 32;
	public static final int OPTION_IP_INTERFACE_STATIC_ROUTES = 33;
	
	public static final int OPTION_LINK_TRAILER_ENCAPSULATION_ENABLE = 34;
	public static final int OPTION_LINK_ARP_CACHE_TIMEOUT = 35;
	public static final int OPTION_LINK_ETHERNET_ENCAPSULATION_ENABLE = 36;
	
	public static final int OPTION_TCP_DEFAULT_TTL = 37;
	public static final int OPTION_TCP_KEEP_ALIVE_INTERVAL = 38;
	public static final int OPTION_TCP_KEEP_ALIVE_GERBAGE_ENABLE = 39;
	
	public static final int OPTION_NIS_DOMAIN = 40;
	public static final int OPTION_NIS_SERVERS = 41;
	public static final int OPTION_NTP_SERVERS = 42;
	
	public static final int OPTION_SERVICE_VENDOR_SPECIFIC_INFORMATIONS = 43;
	public static final int OPTION_SERVICE_NETBOIS_NAME_SERVERS = 44;
	public static final int OPTION_SERVICE_NETBOIS_DATAGRAM_DISTRIBUTION_SERVERS = 45;
	public static final int OPTION_SERVICE_NETBOIS_NODE_TYPE = 46;
	public static final int OPTION_SERVICE_NETBOIS_SCOPE_TYPE = 47;
	public static final int OPTION_SERVICE_X_FONT_SERVERS = 48;
	public static final int OPTION_SERVICE_X_DISPLAY_MANAGERS = 49;
	
	public static final int OPTION_DHCP_IP_ADRESS_REQUESTED = 50;
	public static final int OPTION_DHCP_IP_LEASE_TIME = 51;
	public static final int OPTION_DHCP_OVERLOAD = 52;
	public static final int OPTION_DHCP_MESSAGE_TYPE = 53;
	public static final int OPTION_DHCP_SERVER_IDENTIFIER = 54;
	public static final int OPTION_DHCP_PARAMETER_REQUEST_LIST = 55;
	public static final int OPTION_DHCP_MESSAGE = 56;
	public static final int OPTION_DHCP_MAXIMUM_MESSAGE_SIZE = 57;
	public static final int OPTION_DHCP_RENEWAL_TIME = 58;
	public static final int OPTION_DHCP_REBIND_TIME = 59;
	public static final int OPTION_DHCP_CLASS_IDENTIFIER = 60;
	public static final int OPTION_DHCP_CLIENT_IDENTIFIER = 61;
	
	/**
	 *This inner class represent an entry in the Option Table
	 */
	
	class DHCPOptionsEntry {
		protected byte code;
		protected byte length;
		protected byte content[];
		
		public DHCPOptionsEntry(byte entryCode, byte entryLength,
		    byte entryContent[]) {
			this.code = entryCode;
			this.length = entryLength;
			this.content = entryContent;
		}
		
		@Override
		public String toString() {
			return "Code: " + this.code + "\nContent: " + new String(this.content);
		}
	}
	
	private Hashtable<Byte, DHCPOptionsEntry> optionsTable = null;
	
	public DHCPOptions() {
		this.optionsTable = new Hashtable<Byte, DHCPOptionsEntry>();
	}
	
	/**
	 * Removes option with specified bytecode
	 * @param entryCode The code of option to be removed
	 */
	
	public void removeOption(byte entryCode) {
		this.optionsTable.remove(new Byte(entryCode));
	}
	
	/**
	 * Returns true if option code is set in list; false otherwise
	 * @param entryCode The node's option code
	 * @return true if option is set, otherwise false
	 */
	public boolean contains(byte entryCode) {
		return this.optionsTable.containsKey(new Byte(entryCode));
	}
	
	/**
	 * Determines if list is empty
	 * @return true if there are no options set, otherwise false
	 */
	public boolean isEmpty() {
		return this.optionsTable.isEmpty();
	}
	
	/**
	 * Fetches value of option by its option code
	 * @param entryCode The node's option code
	 * @return byte array containing the value of option entryCode.
	 *         null is returned if option is not set.
	 */
	public byte[] getOption(byte entryCode) {
		if (this.contains(entryCode)) {
			DHCPOptionsEntry ent = this.optionsTable.get(new Byte(entryCode));
			return ent.content;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Changes an existing option to new value
	 * @param entryCode The node's option code
	 * @param value Content of node option
	 */
	public void setOption(byte entryCode, byte value[]) {
		DHCPOptionsEntry opt = new DHCPOptionsEntry(entryCode, (byte) value.length, value);
		this.optionsTable.put(new Byte(entryCode), opt);
	}
	
	/**
	 * Returns the option value of a specified option code in a byte array
	 * @param length Length of option content
	 * @param position Location in array of option node
	 * @param options The byte array of options
	 * @return byte array containing the value for the option
	 */
	private byte[] getArrayOption(int length, int position, byte options[]) {
		byte value[] = new byte[length];
		for (int i = 0; i < length; i++) {
			value[i] = options[position + i];
		}
		return value;
	}
	
	/**
	 * Converts an options byte array to a linked list
	 * @param optionsArray The byte array representation of the options list
	 */
	public void internalize(byte[] optionsArray) {
		
		/* Assume options valid and correct */
		int pos = 4; // ignore vendor magic cookie
		byte code, length;
		byte value[];
		
		while (optionsArray[pos] != (byte) 255) { // until end option
			code = optionsArray[pos++];
			length = optionsArray[pos++];
			value = getArrayOption(length, pos, optionsArray);
			setOption(code, value);
			pos += length; // increment position pointer
		}
	}
	
	/**
	 * Converts a linked options list to a byte array
	 * @return array representation of optionsTable
	 */
	// todo provide overflow return
	public byte[] externalize() {
		byte[] options = new byte[312];
		
		options[0] = (byte) 99; // insert vendor magic cookie
		options[1] = (byte) 130;
		options[2] = (byte) 83;
		options[3] = (byte) 99;
		
		int position = 4;
		Enumeration<DHCPOptionsEntry> e = this.optionsTable.elements();
		
		while (e.hasMoreElements()) {
			DHCPOptionsEntry entry = e.nextElement();
			options[position++] = entry.code;
			options[position++] = entry.length;
			for (int i = 0; i < entry.length; ++i) {
				options[position++] = entry.content[i];
			}
		}
		
		options[position] = (byte) 255; // insert end option
		return options;
	}
	
	/**
	 *	Prints the options linked list: For testing only.
	 */
	public void printList() {
		System.out.println(this.optionsTable.toString());
	}
}
