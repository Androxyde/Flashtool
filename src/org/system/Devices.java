package org.system;

import gui.tools.WidgetTask;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.adb.AdbUtility;
import org.adb.FastbootUtility;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.logger.MyLogger;

public class Devices  {

	private static DeviceEntry _current=null;;
	private static Properties props = null;
	private static boolean waitforreboot=false;
	private static Logger logger = Logger.getLogger(Devices.class);
	
	public static boolean HasOneAdbConnected() {
		return AdbUtility.isConnected();
	}
	
	public static boolean HasOneFastbootConnected() {
		return FastbootUtility.getDevices().hasMoreElements();
	}

	public static Enumeration<Object> listDevices(boolean reload) {
		if (reload || props==null) load();
		return props.keys();
	}
	
	public static String getDevicesDir() {
		return OS.getWorkDir()+File.separator+"devices";
	}
	
	public static String getCustomDevicesDir() {
		return OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+"config";
	}
	public static DeviceEntry getDevice(String device) {
		try {
			if (props.containsKey(device))
				return (DeviceEntry)props.get(device);
			else {
				File f = new File(Devices.getDevicesDir()+File.separator+device+".ftd");
				if (f.exists()) {
					DeviceEntry ent=null;
					JarFile jar = new JarFile(f);
					Enumeration e = jar.entries();
			    	while (e.hasMoreElements()) {
			    	    JarEntry file = (JarEntry) e.nextElement();
			    	    if (file.getName().endsWith(device+".properties")) {
				    	    InputStream is = jar.getInputStream(file); // get the input stream
				    	    PropertiesFile p = new PropertiesFile();
				    	    p.load(is);
				    	    ent = new DeviceEntry(p);
			    	    }
			    	}
			    	return ent;
				}
				else return null;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void setCurrent(String device) {
		AdbUtility.init();
		_current = (DeviceEntry)props.get(device);
		_current.queryAll();
	}
	
	public static DeviceEntry getCurrent() {
		return _current;
	}
	
	private static void load() {
		if (props==null) props=new Properties();
		else props.clear();
		File[] list = (new File(Devices.getDevicesDir()).listFiles());
		if (list==null) return;
		for (int i=0;i<list.length;i++) {
			if (list[i].isDirectory()) {
				PropertiesFile p = new PropertiesFile();
				String device = list[i].getPath().substring(list[i].getPath().lastIndexOf(OS.getFileSeparator())+1);
				try {
					if (!device.toLowerCase().equals("busybox") && !device.toLowerCase().equals(".git")) {
						p.open("",new File(list[i].getPath()+OS.getFileSeparator()+device+".properties").getAbsolutePath());
						DeviceEntry entry = new DeviceEntry(p);
						if (device.equals(entry.getId()))
							props.put(device, entry);
						else logger.error(device + " : this bundle is not valid");
					}
				}
				catch (Exception fne) {
					logger.error(device + " : this bundle is not valid");
				}
			}
		}
	}

	public static void waitForReboot(boolean tobeforced) {
		if (!tobeforced)
			logger.info("Waiting for device");
		else
			logger.info("Waiting for device. After 60secs, stop waiting will be forced");
		waitforreboot=true;
		int count=0;
		while (waitforreboot) {
			sleep(20);
			if (tobeforced) {
				count++;
				if (Device.getLastConnected(false).getStatus().equals("adb") && count==3000) {
					logger.info("Forced stop waiting.");
					waitforreboot=false;
				}
				else if (count==3000) count=0;
			}
		}
	}

	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		}
		catch (Exception e) {}
	}
	
	public static void stopWaitForReboot() {
		waitforreboot=false;
	}
	
	public static void setWaitForReboot() {
		waitforreboot=true;
	}
	
	public static boolean isWaitingForReboot() {
		return waitforreboot;
	}

	public static String identFromRecognition() {
		Enumeration<Object> e = Devices.listDevices(true);
		if (!e.hasMoreElements()) {
			logger.error("No device is registered in Flashtool.");
			logger.error("You can only flash devices.");
			return "";
		}
		boolean found = false;
		Properties founditems = new Properties();
		founditems.clear();
		Properties buildprop = new Properties();
		buildprop.clear();
		while (e.hasMoreElements()) {
			DeviceEntry current = Devices.getDevice((String)e.nextElement());
			String prop = current.getBuildProp();
			if (!buildprop.containsKey(prop)) {
				String readprop = DeviceProperties.getProperty(prop);
				buildprop.setProperty(prop,readprop);
			}
			Iterator<String> i = current.getRecognitionList().iterator();
			String localdev = buildprop.getProperty(prop);
			while (i.hasNext()) {
				String pattern = i.next().toUpperCase();
				if (localdev.toUpperCase().equals(pattern)) {
					founditems.put(current.getId(), current.getName());
				}
			}
		}
		if (founditems.size()==1) {
			return (String)founditems.keys().nextElement();
		}
		else return "";
	}

	public static String getVariantName(String dev) {
		Enumeration<Object> e = Devices.listDevices(true);
		while (e.hasMoreElements()) {
			DeviceEntry current = Devices.getDevice((String)e.nextElement());
			if (current.getVariantList().contains(dev)) return current.getName() + " ("+dev+")";
		}
		return dev;
	}

	public static String getIdFromVariant(String variant) {
		Enumeration<Object> e = Devices.listDevices(true);
		while (e.hasMoreElements()) {
			DeviceEntry current = Devices.getDevice((String)e.nextElement());
			if (current.getVariantList().contains(variant)) return current.getId();
		}
		return variant;
	}
	
	public static String getIdFromRecognition(String variant) {
		Enumeration<Object> e = Devices.listDevices(true);
		while (e.hasMoreElements()) {
			DeviceEntry current = Devices.getDevice((String)e.nextElement());
			if (current.getRecognitionList().contains(variant)) return current.getId();
		}
		return "N/A";
	}

}