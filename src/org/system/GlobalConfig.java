package org.system;

public class GlobalConfig {
	private static PropertiesFile config;
	
	public static String getProperty (String property) {
		if (config==null) {
			reloadProperties();
		}
		return config.getProperty(property);
	}

	public static void setProperty(String property, String value) {
		config.setProperty(property, value);
		config.write("UTF-8");
	}
	
	public static void reloadProperties() {
		config = new PropertiesFile("gui/ressources/config.properties","./config.properties");
		if (config.getProperty("devfeatures")==null)
			config.setProperty("devfeatures", "no");
		if (config.getProperty("bundle")!=null)
			config.remove("bundle");
		config.write("UTF-8");		
	}
	
}
