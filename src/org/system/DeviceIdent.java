package org.system;

import java.util.Enumeration;
import java.util.Properties;

import org.adb.AdbUtility;

public class DeviceIdent {

	private String pid="";
	private String vid="";
	private String devicepath="";
	private Properties devid;
	private int maxsize=0;
	private String serial = "";
	private String driverdesc = "";
	private int drivermajor=0;
	private int driverminor=0;
	private int drivermili=0;
	private int drivermicro=0;
	
	public DeviceIdent() {
		pid="";
		vid="";
		devicepath="";
		devid=new Properties();
	}
	
	public DeviceIdent(DeviceIdent id) {
		devid=new Properties();
		Enumeration<Object> e = id.getIds().keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			addDevId(key);
			devid.setProperty(key, id.getIds().getProperty(key));
		}
		addDevPath(id.getDevPath());
	}

	public void addDevPath(String path) {
		if (path.length()>0)
			devicepath=path;
	}
	
	public String getDevPath() {
		return devicepath;
	}
	
	public void setDriver(String desc, int maj, int min,int mil, int mic) {
		driverdesc = desc;
		drivermajor=maj;
		driverminor=min;
		drivermili=mil;
		drivermicro=mic;
	}
	
	public String getDriverDescription() {
		return driverdesc;
	}
	
	public int getDriverMajor() {
		return drivermajor;
	}
	
	public int getDriverMinor() {
		return driverminor;
	}
	
	public int getDriverMili() {
		return drivermili;
	}

	public int getDriverMicro() {
		return drivermicro;
	}
	
	public void addDevId(String device) {
		if (device.length()>maxsize) maxsize=device.length();
		vid=device.substring(device.indexOf("VID_")+4, device.indexOf("PID_")-1);
		int begin = device.indexOf("PID_")+4;
		pid=device.substring(begin,begin+4);
		if (!device.contains("MI")) {
			serial = device.substring(begin+5,device.length());
		}
		devid.setProperty(device, Boolean.toString(true));
	}

	public void addDevId(String vendor, String product, String ser) {
		vid = vendor;
		pid = product;
		serial = ser;
		devid.setProperty("VID_"+vendor+"&"+"PID_"+product+"\\", "true");
	}
	
	public void setDriverOk(String device,boolean status) {
		devid.setProperty(device, Boolean.toString(status));
	}

	public String getPid() {
		return pid;
	}

	public String getVid() {
		return vid;
	}
	
	public String getSerial() {
		return serial;
	}

	public boolean isDriverOk() {
		Enumeration e = devid.elements();
		while (e.hasMoreElements()) {
			String value = (String)e.nextElement();
			boolean b = Boolean.parseBoolean(value);
			if (!b) return false;
		}
		return true;
	}
	
	public String getDeviceId() {
		Enumeration<Object> e = devid.keys();
		while (e.hasMoreElements()) {
			String value = (String)e.nextElement();
			if (!value.contains("MI")) return value;
		}
		return "none";
	}

	public String getStatus() {
		if (!getVid().equals("0FCE")) return "none";
		if (!isDriverOk()) return "notinstalled";
		if (getPid().equals("ADDE") || getPid().equals("B00B")) {
			if (OS.getName().equals("windows")) {
				if (drivermajor==3 && driverminor >=1)
					return "flash";
				if (drivermajor>3)
					return "flash";
				return "flash_obsolete";
			}
			else return "flash";
		}
		if (getPid().equals("0DDE")) return "fastboot";
		return AdbUtility.getStatus();
	}
	
	public Properties getIds() {
		return devid;
	}
	
	public int getMaxSize() {
		return maxsize;
	}
}
