package org.system;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.adb.AdbUtility;

public class DeviceProperties {

	private static Properties devprops = new Properties();
	private static String fsep = OS.getFileSeparator();
	
	public static void reload() {
		devprops.clear();
		try {
			AdbUtility.pull("/system/build.prop", OS.getFolderCustom()+fsep+"root"+fsep+"build.prop",false);
			File build = new File(OS.getFolderCustom()+fsep+"root"+fsep+"build.prop");
			FileInputStream fis = new FileInputStream(build);
			devprops.load(fis);
			fis.close();
			build.delete();
		}
		catch (Exception e) {}
	}

	public static String getProperty(String key) {
		String read = devprops.getProperty(key);
		if (read==null) read = "";
		return read;
	}

}