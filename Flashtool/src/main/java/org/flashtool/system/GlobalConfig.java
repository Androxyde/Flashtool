package org.flashtool.system;

import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		String folder = OS.getUserHome()+File.separator+".flashTool";
		new File(folder).mkdirs();
		Properties p = null;
		try {
			File pfile = new File(folder+File.separator+"config.properties");
			if (pfile.exists()) {
				p = new Properties();
				FileReader pread = new FileReader(pfile);
				p.load(pread);
				pread.close();
				pfile.delete();
			}
		} catch (Exception e) {
			p = null;
		}
		config = new PropertiesFile("gui/ressources/config.properties","");
		config.setFileName(folder+File.separator+"config.properties");
		if (p!=null) {
			Enumeration keys = p.keys();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				GlobalConfig.setProperty(key, p.getProperty(key));
			}
		}
		if (config.getProperty("devfeatures")==null)
			config.setProperty("devfeatures", "no");
		if (config.getProperty("bundle")!=null)
			config.remove("bundle");
		config.write("UTF-8");
		String userfolder = GlobalConfig.getProperty("user.flashtool");
		if (userfolder ==null) GlobalConfig.setProperty("user.flashtool", folder);
	}

}