package org.flashtool.system;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.jna.adb.FastbootUtility;
import org.flashtool.jna.linux.JUsb;
import org.flashtool.jna.win32.JsetupAPi;
import org.flashtool.jna.win32.SetupApi.HDEVINFO;
import org.flashtool.jna.win32.SetupApi.SP_DRVINFO_DATA;
import org.flashtool.util.BytesUtil;
import org.flashtool.util.HexDump;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Shell;
import java.util.Enumeration;

import com.sun.jna.platform.win32.WinBase;
import com.google.common.primitives.Longs;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Devices  {

	private static DeviceEntry _current=null;;
	public static Properties devices = null;
	public static Properties models = null;
	private static boolean waitforreboot=false;
	private static final String DriversInfoData = null;
	static DeviceIdent lastid = new DeviceIdent();
	static String laststatus = "";

	public static boolean HasOneAdbConnected() {
		return AdbUtility.isConnected();
	}
	
	public static boolean HasOneFastbootConnected() {
		return FastbootUtility.getDevices().hasMoreElements();
	}

	public static Enumeration<Object> listDevices(boolean reload) {
		if (reload || devices==null) load();
		return devices.keys();
	}
	
	
	public static DeviceEntry getDevice(String device) {
		try {
			if (device==null) return null;
			if (devices.containsKey(device))
				return (DeviceEntry)devices.get(device);
			else {
				File f = new File(OS.getFolderMyDevices()+File.separator+device+".ftd");
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
		_current = (DeviceEntry)devices.get(device);
		_current.queryAll();
	}
	
	public static DeviceEntry getCurrent() {
		return _current;
	}
	
	public static void load() {
		log.info("Loading devices database");
		if (devices==null) {
			devices=new Properties();
			models = new Properties();
		}
		else {
			devices.clear();
			models.clear();
		}
		File[] list = (new File(OS.getFolderMyDevices()).listFiles());
		if (list==null) return;
		for (int i=0;i<list.length;i++) {
			if (list[i].isDirectory()) {
				PropertiesFile p = new PropertiesFile();
				String device = list[i].getPath().substring(list[i].getPath().lastIndexOf(OS.getFileSeparator())+1);
				try {
					File propfile = new File(list[i].getPath()+OS.getFileSeparator()+device+".properties");
					if (propfile.exists()) {
						p.open("",propfile.getAbsolutePath());
						DeviceEntry entry = new DeviceEntry(p);
						if (device.equals(entry.getId())) {
							devices.put(device, entry);
							Iterator<String> iv = entry.getVariantList().iterator();
							while (iv.hasNext()) {
								String variant = iv.next();
								models.put(variant, entry);
							}
						}
						else log.error(device + " : this bundle is not valid");
					}
				}
				catch (Exception fne) {
					log.error(device + " : this bundle is not valid");
				}
			}
		}
		list = (new File(OS.getFolderDevices()).listFiles());
		if (list==null) return;
		for (int i=0;i<list.length;i++) {
			if (list[i].isDirectory()) {
				PropertiesFile p = new PropertiesFile();
				String device = list[i].getPath().substring(list[i].getPath().lastIndexOf(OS.getFileSeparator())+1);
				try {
					File propfile = new File(list[i].getPath()+OS.getFileSeparator()+device+".properties");
					if (propfile.exists()) {
						p.open("",propfile.getAbsolutePath());
						DeviceEntry entry = new DeviceEntry(p);
						if (device.equals(entry.getId())) {
							devices.put(device, entry);
							Iterator<String> iv = entry.getVariantList().iterator();
							while (iv.hasNext()) {
								String variant = iv.next();
								models.put(variant, entry);
							}
						}
						else log.error(device + " : this bundle is not valid");
					}
				}
				catch (Exception fne) {
					log.error(device + " : this bundle is not valid");
				}
			}
		}
		log.info("Loaded "+devices.size()+" devices");
	}

	public static void waitForReboot(boolean tobeforced) {
		if (!tobeforced)
			log.info("Waiting for device");
		else
			log.info("Waiting for device. After 60secs, stop waiting will be forced");
		waitforreboot=true;
		int count=0;
		while (waitforreboot) {
			sleep(20);
			if (tobeforced) {
				count++;
				if (Devices.getLastConnected(false).getStatus().equals("adb") && count==3000) {
					log.info("Forced stop waiting.");
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
		if (devices.isEmpty()) {
			log.error("No device is registered in Flashtool.");
			log.error("You can only flash devices.");
			return "";
		}
		String dev = DeviceProperties.getProperty("ro.product.device");
		String model = DeviceProperties.getProperty("ro.product.model");
		DeviceEntry entry = Devices.getDeviceFromVariant(dev);
		if (entry != null) return entry.getId();
		entry = Devices.getDeviceFromVariant(model);
		if (entry != null) return entry.getId();
		return "";
	}

	public static String getVariantName(String variant) {
		try {
			return getDeviceFromVariant(variant).getName() + "(" + variant + ")";
		} catch (Exception e) {
			return "Not found ("+variant+")";
		}
	}

	public static DeviceEntry getDeviceFromVariant(String variant) 
	{
		return (DeviceEntry)models.get(variant);
	}

	public static DeviceIdent getLastConnected(boolean force) {
		if (force) return getConnectedDevice();
		DeviceIdent id = null;
		synchronized (lastid) {
			id = new DeviceIdent(lastid);
		}
		return id;
	}

	public static synchronized DeviceIdent getConnectedDevice() {
		DeviceIdent id;
		if (OS.getName().equals("windows")) id=getConnectedDeviceWin32();
		else id=getConnectedDeviceLinux();
		int count=0;
		while (!id.isDriverOk()) {
			try {
				Thread.sleep(200);
			}
			catch (Exception e) {
			}
			if (OS.getName().equals("windows")) id=getConnectedDeviceWin32();
			else id=getConnectedDeviceLinux();
			count++;
			if (count==5) break;
		}
		return id;
	}
	
	public static byte[] longToBytes(long l) {
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        while (l != 0) {
            bytes.add((byte) (l % (0xff + 1)));
            l = l >> 8;
        }
        byte[] bytesp = new byte[bytes.size()];
        for (int i = bytes.size() - 1, j = 0; i >= 0; i--, j++) {
            bytesp[j] = bytes.get(i);
        }
        return bytesp;
    }
    
	public static DeviceIdent getConnectedDeviceWin32() {
    	DeviceIdent id = new DeviceIdent();
    	HDEVINFO hDevInfo = JsetupAPi.getHandleForConnectedInterfaces();
        if (hDevInfo.equals(WinBase.INVALID_HANDLE_VALUE)) {
        	log.error("Cannot have device list");
        }
        else {
        	SP_DEVINFO_DATA DeviceInfoData;
        	int index = 0;
	        do {
	        	DeviceInfoData = JsetupAPi.enumDevInfo(hDevInfo, index);
	            String devid = JsetupAPi.getDevId(hDevInfo, DeviceInfoData);
	            if (devid.contains("VID_0FCE")) {
	            	id.addDevPath(JsetupAPi.getDevicePath(hDevInfo, DeviceInfoData));
	            	id.addDevId(devid);
	            	if (!JsetupAPi.isInstalled(hDevInfo, DeviceInfoData))
	            		id.setDriverOk(devid,false);
	            	else
	            		id.setDriverOk(devid,true);
	            }
	            index++;
	        } while (DeviceInfoData!=null);
	        JsetupAPi.destroyHandle(hDevInfo);
        }
    	hDevInfo = JsetupAPi.getHandleForConnectedDevices();
        if (hDevInfo.equals(WinBase.INVALID_HANDLE_VALUE)) {
        	log.error("Cannot have device list");
        }
        else {
        	SP_DEVINFO_DATA DeviceInfoData;
        	SP_DRVINFO_DATA DriversInfoData;
        	int index = 0;
	        do {
	        	DeviceInfoData = JsetupAPi.enumDevInfo(hDevInfo, index);
	            String devid = JsetupAPi.getDevId(hDevInfo, DeviceInfoData);
	            if (devid.contains("VID_0FCE")) {
	            	id.addDevId(devid);
					if (!JsetupAPi.isInstalled(hDevInfo, DeviceInfoData))
	            		id.setDriverOk(devid,false);
	            	else {
	            		id.setDriverOk(devid,true);
	            		DriversInfoData=null;
	            		//DriversInfoData=JsetupAPi.getDriver(hDevInfo, DeviceInfoData);
	            		if (JsetupAPi.buildDriverList(hDevInfo, DeviceInfoData)) {
	            		DriversInfoData=null;
						int driverindex = 0;
						do {
							DriversInfoData = JsetupAPi.enumDriverInfo(hDevInfo, DeviceInfoData, driverindex);
							if (DriversInfoData!=null) {
								String desc = new String(Native.toString(DriversInfoData.Description));
								int major = BytesUtil.getInt(Arrays.copyOfRange(Longs.toByteArray(DriversInfoData.DriverVersion), 0, 2));
								int minor = BytesUtil.getInt(Arrays.copyOfRange(Longs.toByteArray(DriversInfoData.DriverVersion), 2, 4));
								int mili = BytesUtil.getInt(Arrays.copyOfRange(Longs.toByteArray(DriversInfoData.DriverVersion), 4, 6));
								int micro = BytesUtil.getInt(Arrays.copyOfRange(Longs.toByteArray(DriversInfoData.DriverVersion), 6, 8));
								id.setDriver(desc, major, minor, mili, micro);
							}
							driverindex++;
						} while (DriversInfoData!=null);
	            		}
	            	}
	            }
	            index++;
	        } while (DeviceInfoData!=null);
	        JsetupAPi.destroyHandle(hDevInfo);
        }
        synchronized (lastid) {
    		lastid=id;
    	}
    	return id;
    }

    public static DeviceIdent getConnectedDeviceLinux() {
    	DeviceIdent id = new DeviceIdent();
    	try {
    		JUsb.fillDevice(true);
    		if (JUsb.getVendorId().equals("0FCE")) {
	    		id.addDevId(JUsb.getVendorId(),JUsb.getProductId(),JUsb.getSerial());
	        }
	        synchronized (lastid) {
	        	lastid=id;
	        }
    	}
    	catch (UnsatisfiedLinkError e) {
    		log.error("libusb-1.0 is not installed");
    		log.error(e.getMessage());
    	}
    	catch (NoClassDefFoundError e1) {
    	}
    	return id;
    }

    public static void CheckAdbDrivers() {
    	log.info("List of connected devices (Device Id) :");
    	DeviceIdent id=getLastConnected(false);
    	String driverstatus;
    	int maxsize = id.getMaxSize();
    	Enumeration e = id.getIds().keys();
    	while (e.hasMoreElements()) {
    		String dev = (String)e.nextElement();
    		String driver = id.getIds().getProperty(dev);
    		log.info("      - "+String.format("%1$-" + maxsize + "s", dev)+"\tDriver installed : "+driver);
    	}
	    log.info("List of ADB devices :");
	    Enumeration<String> e1 = AdbUtility.getDevices();
	    if (e1.hasMoreElements()) {
	    while (e1.hasMoreElements()) {
	    	log.info("      - "+e1.nextElement() + " (Device : " + DeviceProperties.getProperty("ro.product.device") + ". Model : "+DeviceProperties.getProperty("ro.product.model")+")");
	    }
	    }
	    else log.info("      - none");
	    log.info("List of fastboot devices :");
	    Enumeration<String> e2 = FastbootUtility.getDevices();
	    if (e2.hasMoreElements()) {
	    while (e2.hasMoreElements()) {
	    	log.info("      - "+e2.nextElement());
	    }
	    }
	    else log.info("      - none");
    }

    public static void clean() {
    	if (!OS.getName().equals("windows"))
			try {
				JUsb.cleanup();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    }

}