package gui.models;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.logger.MyLogger;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.PropertiesFile;
import org.system.TextFile;
import org.system.UpdateURL;

public class ModelUpdater {

	UpdateURL _url;
	PropertiesFile _custid = new PropertiesFile();
	Properties _versions = new Properties();
	boolean tosave = false;
	String _model = "";
	private static Logger logger = Logger.getLogger(ModelUpdater.class);

	public ModelUpdater(UpdateURL u) {
		_url = u;
		_model = u.getVariant();
		init(Devices.getDevice(u.getDeviceID()),u.getVariant());
	}

	public ModelUpdater(DeviceEntry entry, String model) {
		init(entry, model);
	}
	
	public void init(DeviceEntry entry, String model) {
		try {
			if (new File(entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"updateurl").exists()) {
				TextFile tf = new TextFile(entry.getDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"updateurl","ISO8859-15");
				_url = new UpdateURL(tf.getLines().iterator().next());
			}
			else if (new File(entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"updateurl").exists()) {
				TextFile tf = new TextFile(entry.getCustomDeviceDir()+File.separator+"updates"+File.separator+model+File.separator+"updateurl","ISO8859-15");
				_url = new UpdateURL(tf.getLines().iterator().next());			
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
			if (_url!=null) {
				_model = _url.getVariant();
			}
			else _model = model;
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}	
	}


	public void addURL(String url) {
		try {
			_url = new UpdateURL(url);
			_url.dumpToFile();			
		}
		catch (Exception e) {
			logger.warn(e.getMessage());
			e.printStackTrace();
		}
	}

	public void checkUpdates() {
		Iterator clist = _custid.keySet().iterator();
		while (clist.hasNext()) {
			URL u;
			String line="";
			final String custid=(String)clist.next();
			try {
				_url.setParameter("cdfId", custid);
				_url.setParameter("cdfVer", "R1A");
				u = new URL(_url.getFullURL());
				Scanner sc = new Scanner(u.openStream());
				while (sc.hasNextLine()) {
					line = line+sc.nextLine();
				}
				try {
					String latest = line.substring(line.indexOf("<swVersion>")+11, line.indexOf("</swVersion>"));
					_versions.setProperty(_custid.getProperty(custid), latest);
				}
				catch (Exception e) {
					_versions.setProperty(_custid.getProperty(custid), "N/A");
					logger.warn("Cannot check update for "+custid+" ("+_custid.getProperty(custid)+")");
				}
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
	
	public void AddCustId(CustIdItem ci) {
		_custid.setProperty(ci.getDef().getValueOf(0), ci.getDef().getValueOf(1));
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
		return (_url!=null && (_custid.getProperties().size()>0 || withemptycustid));
	}

	public boolean hasIds() {
		return _custid.getProperties().size()>0;
	}
	
	public boolean isModified() {
		return tosave;
	}
}