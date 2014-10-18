package org.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.PropertiesFile;
import org.system.TextFile;
import org.system.UpdateURL;

import com.iagucool.xperifirm.CDFInfoLoader;
import com.iagucool.xperifirm.FileSet;

public class DeviceEntryModelUpdater {

	PropertiesFile _custid = new PropertiesFile();
	Properties _versions = new Properties();
	boolean tosave = false;
	String _model = "";
	String tac8 = "";
	Properties cdfinfos = new Properties();
	private static Logger logger = Logger.getLogger(DeviceEntryModelUpdater.class);


	public DeviceEntryModelUpdater(DeviceEntry entry, String model) {
		init(entry, model);
	}
	
	public void init(DeviceEntry entry, String model) {
		try {
			if (new File(entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"tac").exists()) {
				TextFile tf = new TextFile(entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"tac","ISO8859-15");
				tac8 = tf.getLines().iterator().next();
			}
			else if (new File(entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"tac").exists()) {
				TextFile tf = new TextFile(entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"tac","ISO8859-15");
				tac8 = tf.getLines().iterator().next();			
			}
			if (new File(entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"custlist.properties").exists()) {
				PropertiesFile pf = new PropertiesFile();
				pf.open("", entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"custlist.properties");
				_custid.mergeWith(pf);
			}
			if (new File(entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"custlist.properties").exists()) {
				PropertiesFile pf = new PropertiesFile();
				pf.open("", entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"custlist.properties");
				_custid.getProperties().clear();
				_custid.mergeWith(pf);
			}
			_model = model;
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}	
	}

	public String getReleaseOf(String custid) {
		try {
			CDFInfoLoader cdf = (CDFInfoLoader)cdfinfos.get(custid);
			if (cdf==null) {
				cdf = new CDFInfoLoader(tac8,custid);
				cdfinfos.put(custid, cdf);
			}
			return cdf.getRelease();
		} catch (Exception e) {
			logger.error(e);
			return "";
		}
	}

	public com.iagucool.xperifirm.Firmware getFilesOf(String custid) {
			CDFInfoLoader cdf = (CDFInfoLoader)cdfinfos.get(custid);
			return cdf.getFiles();
	}

	public void checkUpdates() {
		Iterator clist = _custid.keySet().iterator();
		while (clist.hasNext()) {
			URL u;
			String line="";
			final String custid=(String)clist.next();
			try {
				CDFInfoLoader cdf = new CDFInfoLoader(tac8,custid);
				_versions.setProperty(_custid.getProperty(custid), cdf.getRelease());
			}
			catch (Exception e1) {
				_versions.setProperty(_custid.getProperty(custid), "N/A");
				logger.warn("Network error while checking for "+custid+" ("+_custid.getProperty(custid)+")");
			}
		}
	}

	public Properties getVersions() {
		return _versions;
	}
	
	public DeviceEntry getDevice() {
		return Devices.getDevice(Devices.getIdFromVariant(_model));
	}

	public String getModel() {
		return _model;
	}
	
	public void AddCustId(String cda, String name) {
		_custid.setProperty(cda,name);
		tosave=true;
	}

	public PropertiesFile getCustIds() {
		return _custid;
	}
	
	public void RemoveCustId(String id) {
		_custid.getProperties().remove(id);
		tosave=true;
	}

	public void save() {
		_custid.setFileName(getDevice().getCustomDeviceDir()+File.separator+"updates"+File.separator+getModel()+File.separator+"custlist.properties");
		_custid.write("ISO8859-15");
	}
	
	public boolean canCheck(boolean withemptycustid) {
		return (tac8.length()>0 && (_custid.getProperties().size()>0 || withemptycustid));
	}

	public boolean hasIds() {
		return _custid.getProperties().size()>0;
	}
	
	public boolean isModified() {
		return tosave;
	}
}