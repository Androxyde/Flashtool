package org.system;

import java.util.Properties;


public class GlobalState {
	
	private static Properties serials = new Properties();
	
	public synchronized static String getState (String pserial, String pid) {
		Properties p = (Properties) serials.get(pserial);
		if (p!=null) {
			String state = p.getProperty(pid);
			if (state==null) state = "";
			return state;
		}
		else return "";
	}

	public synchronized static void setState(String pserial, String pid, String status) {
		if (!serials.containsKey(pserial))
			serials.put(pserial, new Properties());
		((Properties)serials.get(pserial)).setProperty(pid, status);
	}
	
}
