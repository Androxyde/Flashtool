package org.flashtool.system;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateURL {

	String url = "";
	Properties parameters = new Properties();
	String path;
	String cpath;
	
	public UpdateURL(String fullurl) throws Exception {
		url = fullurl.substring(0,fullurl.indexOf("?"));
		String params = fullurl.substring(fullurl.indexOf("?")+1,fullurl.length());
		String[] list = params.split("&");
		for (int i=0;i<list.length;i++) {
			parameters.setProperty(list[i].split("=")[0], list[i].split("=")[1]);
		}
		String devId = getDeviceID();
		if (devId.length()==0) throw new Exception("Device not found in database");
		DeviceEntry ent = Devices.getDevice(devId);
		path = ent.getDeviceDir()+File.separator+"updates"+File.separator+getVariant();
		cpath = ent.getMyDeviceDir()+File.separator+"updates"+File.separator+getVariant();
	}

	public boolean exists() {
		return (new File(path).exists() || new File(cpath).exists());
	}
	
	public String getParameters() {
		return parameters.toString();
	}
	
	public String getParameter(String parameter) {
		return parameters.getProperty(parameter);
	}

	public void setParameter(String parameter, String value) {
		parameters.setProperty(parameter, value);
	}

	public String getFullURL() {
		String fullurl = url + "?";
		Enumeration<Object> e = parameters.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			fullurl = fullurl+key + "=" + parameters.getProperty(key)+"&";
		}
		return fullurl.substring(0, fullurl.length()-1);
	}
	
	public String getDeviceID() {
		String uid = this.getParameter("model");
		String id = Devices.getDeviceFromVariant(uid).getId();
		if (id.equals(uid)) return "";
		return id;
	}

	public String getVariant() {
		String uid = this.getParameter("model");
		return uid;
	}
	
	public void dumpToFile() throws IOException {
		TextFile t = new TextFile(cpath+File.separator+"updateurl","ISO8859-15");
		t.open(false);
		t.write(this.getFullURL());
		t.close();
	}
}