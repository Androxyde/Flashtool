package org.flashtool.system;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalState {
	
	private static Properties serials = new Properties();
	private static boolean isgui=false;
	
	public synchronized static String getStates (String pserial, String pid) {
		Properties p = (Properties) serials.get(pserial);
		if (p!=null) {
			String state = p.getProperty(pid);
			if (state==null) state = "";
			return state;
		}
		else return "";
	}

	public synchronized static void setStates(String pserial, String pid, String status) {
		if (!serials.containsKey(pserial))
			serials.put(pserial, new Properties());
		((Properties)serials.get(pserial)).setProperty(pid, status);
	}

	public synchronized static boolean isGUI() {
		return isgui;
	}

	public synchronized static void setGUI() {
		isgui=true;
	}

}