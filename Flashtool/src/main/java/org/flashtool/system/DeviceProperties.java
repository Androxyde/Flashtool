package org.flashtool.system;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.flashtool.jna.adb.AdbUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		catch (Exception e) {
			try {
				devprops.setProperty("ro.build.version.release", AdbUtility.run("getprop ro.build.version.release"));
				devprops.setProperty("ro.build.id", AdbUtility.run("getprop ro.build.id"));
				devprops.setProperty("ro.product.cpu.abi", AdbUtility.run("getprop ro.product.cpu.abi"));
				devprops.setProperty("ro.product.device", AdbUtility.run("getprop ro.product.device"));
				devprops.setProperty("ro.product.model", AdbUtility.run("getprop ro.product.model"));
			} catch (Exception e1) {}
		}
	}

	public static String getProperty(String key) {
		String read = devprops.getProperty(key);
		if (read==null) read = "";
		return read;
	}

}