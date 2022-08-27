package org.flashtool.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;
import org.flashtool.system.PropertiesFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceApps {

	private static String fsep = OS.getFileSeparator();
	private PropertiesFile deviceList;
	private PropertiesFile customList;
	//private Properties x10List = new Properties();
	private PropertiesFile safeList;
	private HashSet<String> currentList;
	private String currentProfile="";
	private Properties realnames = new Properties();
	private HashMap<String,PropertiesFile> Allsafelist = new HashMap<String,PropertiesFile>();

	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	private void copyToAppsSaved(File src) throws IOException {
		File dst = new File(Devices.getCurrent().getAppsDir()+fsep+src.getName());
		if (!dst.exists()) {
		    InputStream in = new FileInputStream(src);
		    OutputStream out = new FileOutputStream(dst);
	
		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		}
	}
	
	public Properties deviceList() {
		return deviceList.getProperties();
	}

	public Properties customList() {
		return customList.getProperties();
	}

	public void addApk(File apk, String desc) {
		try {
			copyToAppsSaved(apk);
			customList.setProperty(apk.getName(), desc);
			customList.write("UTF-8");
			realnames.setProperty(desc, apk.getName());
			rescan();
		}
		catch (Exception e) {
		}
	}
	
	public void modApk(String apkname, String desc) {
		if (customList.getProperties().containsKey(apkname)) {
			customList.setProperty(apkname,desc);
			customList.write("UTF-8");
		}
		if (deviceList.getProperties().containsKey(apkname)) {
			deviceList.setProperty(apkname,desc);
			deviceList.write("UTF-8");
		}
		realnames.setProperty(desc, apkname);
		rescan();
	}
	
	private void rescan() {
		File[] dirlist = (new File(Devices.getCurrent().getCleanDir())).listFiles();
		for (int i=0;i<dirlist.length;i++) {
			if (dirlist[i].getName().contains("safelist")) {
				String key = dirlist[i].getName().replace((CharSequence)"safelist", (CharSequence)"");
				key=key.replace((CharSequence)".properties", (CharSequence)"");
				if (!Allsafelist.containsKey(key.toLowerCase()))
					Allsafelist.put(key.toLowerCase(),new PropertiesFile("",dirlist[i].getPath()));
			}
		}
	}
	
	public String getCurrentProfile() {
		return currentProfile;
	}
	
	public void updateFromDevice() {
		Iterator ic = currentList.iterator();
		boolean modded = false;
		while (ic.hasNext()) {
			String next = (String)ic.next();
			if (!deviceList.getProperties().containsKey(next)) {
				if (!modded) modded=true;
				deviceList.setProperty(next, next);
			}
		}
		if (modded)
			deviceList.write("UTF-8");		
	}

	public void updateFromSaved() {
		File[] list = new File(Devices.getCurrent().getAppsDir()).listFiles();
		boolean modded = false;
		for (int i=0;i<list.length;i++) {
			String apk = list[i].getName();
			if (!deviceList.getProperties().containsKey(apk)) {
				if (!modded) modded=true;
				deviceList.setProperty(apk, apk);
			}
		}
		if (modded)
			deviceList.write("UTF-8");		
	}
	
	public void feedMeta() {
		Enumeration<Object> e = deviceList.getProperties().keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			realnames.setProperty(deviceList.getProperty(key),key);
		}
	}
	
	public DeviceApps() {
		try {
			new File(Devices.getCurrent().getCleanDir()).mkdirs();
			new File(Devices.getCurrent().getAppsDir()).mkdirs();
			deviceList=new PropertiesFile("",Devices.getCurrent().getCleanDir()+fsep+"devicelist.properties");
			customList=new PropertiesFile("",Devices.getCurrent().getCleanDir()+fsep+"customlist.properties");
			currentList = AdbUtility.listSysApps();
			updateFromDevice();
			updateFromSaved();
			feedMeta();
			currentProfile="default";
			if (!new File(Devices.getCurrent().getCleanDir()+fsep+"safelist"+currentProfile+".properties").exists()) {
				safeList = new PropertiesFile();
				Iterator icur = deviceList.getProperties().keySet().iterator();
				while (icur.hasNext()) {
					String key = (String)icur.next();
					safeList.setProperty(key, "unsafe");
				}
				Allsafelist.put(currentProfile,safeList);
			}
			else {
				safeList=new PropertiesFile("",Devices.getCurrent().getCleanDir()+fsep+"safelist"+currentProfile+".properties");
				Iterator icur = deviceList.getProperties().keySet().iterator();
				while (icur.hasNext()) {
					String key = (String)icur.next();
					if (!safeList.getProperties().containsKey(key))
						safeList.setProperty(key, "unsafe");
				}
				Allsafelist.put(currentProfile,safeList);
			}
			saveProfile(currentProfile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setProfile(String profile) {
		currentProfile=profile;
		safeList = Allsafelist.get(profile);
		fillSet();
		deviceList.write("UTF-8");
		customList.write("UTF-8");
	}

	public void saveProfile() {
		safeList.write(Devices.getCurrent().getCleanDir()+fsep+"safelist"+currentProfile+".properties","UTF-8");
	}

	public void saveProfile(String name) {
		Allsafelist.get(currentProfile).write(Devices.getCurrent().getCleanDir()+fsep+"safelist"+name+".properties","UTF-8");
		rescan();
		setProfile(name.toLowerCase());
	}
	
	private void fillSet() {
		try {
			Iterator<String> i = currentList.iterator();
			while (i.hasNext()) {
				String apk = i.next();
				if (safeList.getProperty(apk)==null) {
					safeList.setProperty(apk,"unsafe");
				}
			}
			Enumeration<Object> e = safeList.getProperties().keys();
			while (e.hasMoreElements()) {
				String apk = (String)e.nextElement();
			}
			Iterator<Object> i1 = deviceList.keySet().iterator();
			while (i1.hasNext()) {
				String key = (String)i1.next();
				realnames.setProperty(deviceList.getProperty(key), key);
			}
		}
		catch (Exception e) {
		}
	}

	public Set<String> getProfiles() {
		return Allsafelist.keySet();
	}
	
	public HashSet<String> getCurrent() {
		return currentList;
	}
	
	public String getRealName(String apk) {
		return deviceList.getProperty(apk);
	}
	
	public String getApkName(String realName) {
		return realnames.getProperty(realName);
	}

	public void setSafe(String apkName) {
		safeList.setProperty(apkName, "safe");
		if (apkName.endsWith(".apk")) {
			String odex = apkName.replace(".apk",".odex");
		if (safeList.keySet().contains(odex))
			safeList.setProperty(odex, "safe");
		}
	}
	
	public void setUnsafe(String apkName) {
		safeList.setProperty(apkName, "unsafe");
		if (apkName.endsWith(".apk")) {
			String odex = apkName.replace(".apk",".odex");
		if (safeList.keySet().contains(odex))
			safeList.setProperty(odex, "unsafe");
		}
	}
	
	public Vector<String> getToBeRemoved(boolean withodex) {
		Vector<String> v = new Vector<String>();
		Iterator<String> ic = currentList.iterator();
		while (ic.hasNext()) {
			String apk=ic.next();
			if (apk.endsWith(".apk") || withodex==true)
				if (safeList.getProperty(apk).equals("safe")) v.add(deviceList.getProperty(apk));
		}
		return v;
	}

	public Vector<String> getRemoved(boolean withodex) {
		Vector<String> v = new Vector<String>();
		Iterator<Object> il = safeList.keySet().iterator();
		while (il.hasNext()) {
			String apk=(String)il.next();
			if (apk.endsWith(".apk") || withodex==true)
			if (!currentList.contains(apk) && safeList.getProperty(apk).equals("safe")) { 
				if (new File((Devices.getCurrent().getAppsDir()+fsep+apk)).exists())
					v.add(deviceList.getProperty(apk));
			}
		}
		return v;
	}

	public Vector<String> getToBeInstalled(boolean withodex) {
		Vector<String> v = new Vector<String>();
		Iterator<Object> il = safeList.keySet().iterator();
		while (il.hasNext()) {
			String apk=(String)il.next();
			if (apk.endsWith(".apk") || withodex==true)
			if (!currentList.contains(apk) && safeList.getProperty(apk).equals("unsafe")) {
				if (new File((Devices.getCurrent().getAppsDir()+fsep+apk)).exists())
					v.add(deviceList.getProperty(apk));
			}
		}
		return v;		
	}

	public Vector<String> getInstalled(boolean withodex) {
		Vector<String> v = new Vector<String>();
		Iterator<String> ic = currentList.iterator();
		while (ic.hasNext()) {
			String apk=ic.next();
			if (apk.endsWith(".apk") || withodex==true)
			if (safeList.getProperty(apk).equals("unsafe")) v.add(deviceList.getProperty(apk));
		}
		return v;
	}
	
}