package org.flashtool.gui.tools;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.flashsystem.Category;
import org.flashtool.gui.TARestore;
import org.flashtool.parsers.sin.SinFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XMLBootConfig {

	private String _configname = "";
	private String _configta = "";
	private Vector<String> files = new Vector<String>();
	private Properties attributes = new Properties();
	private String _folder="";
	static final Logger logger = LogManager.getLogger(XMLBootConfig.class);
	private Properties matcher = new Properties();

	public XMLBootConfig(String name) {
		_configname = name;
	}
	
	public Vector getFiles() {
		return files;
	}
	
	public String getMatchingFile(String match) {
		Vector<String> matched = new Vector<String>();
		Iterator<String> file = files.iterator();
		while(file.hasNext()) {
			String name = file.next();
			if (SinFile.getShortName(name).equals(SinFile.getShortName(match)))
				matched.add(name);
		}
		if (matched.size()==1)
			return (_folder.length()>0?_folder+"/":"")+matched.get(0);
		return null;
	}
	
	public void setFolder(String folder) {
		_folder = folder+File.separator+"boot";
	}

	public String getAppsBootFile() {
		Iterator<String> file = files.iterator();
		while(file.hasNext()) {
			String appsboot = file.next();
			if (appsboot.toUpperCase().contains("APPSBOOT")) return (_folder.length()>0?_folder+"/":"")+appsboot;
		}
		return "";
	}

	public boolean hasAppsBootFile() {
		return getAppsBootFile().trim().length()>0;
	}

	public Vector<String> getOtherFiles() {
		Vector<String> otherfiles = new Vector<String>();
		Iterator<String> file = files.iterator();
		while(file.hasNext()) {
			String curfile = file.next();
			if (!curfile.toUpperCase().contains("APPSBOOT"))
				otherfiles.add((_folder.length()>0?_folder+"/":"")+curfile);
		}
		return otherfiles;
	}
	
	public String getName() {
		return _configname;
	}
	
	public void setTA(String config) {
		_configta = config;
	}
	
	public String getTA() {
		return (_folder.length()>0?_folder+"/":"")+_configta;
	}
	
	public void addFile(String file) {
		files.add(file);
	}
	
	public int getFileCount() {
		return files.size();
	}
	
	public void setAttributes(String attribs) {
		String[] list = attribs.split(";");
		for (int i=0;i<list.length;i++) {
			attributes.setProperty(list[i].split("=")[0], list[i].split("=")[1].replace("\"", ""));
		}
	}
	
	public String getAttribute(String att) {
		return attributes.getProperty(att, " ");
	}
	
	public void addMatcher(String attr, String value) {
		matcher.setProperty(attr, value);
	}
	
	public boolean matches(String otp_lock_status, String otp_data, String idcode, String plfroot) {
		boolean check_otp_lock_status=true;
		boolean check_otp_data=true;
		boolean check_idcode=true;
		boolean check_plfroot=true;
		if (attributes.containsKey("OTP_LOCK_STATUS_1"))
			check_otp_lock_status=otp_lock_status.equals(getAttribute("OTP_LOCK_STATUS_1"));
		if (attributes.containsKey("OTP_DATA_1"))
			check_otp_data=otp_data.equals(getAttribute("OTP_DATA_1"));
		if (attributes.containsKey("IDCODE_1"))
			check_idcode=idcode.substring(1).equals(getAttribute("IDCODE_1").substring(1));
		if (attributes.containsKey("PLF_ROOT_1"))
			check_plfroot=plfroot.equals(getAttribute("PLF_ROOT_1"));
		return (check_otp_lock_status && check_otp_data && check_idcode && check_plfroot); 
	}

	public boolean matchAttributes() {
		boolean match = true;
		Enumeration<Object> keys = matcher.keys();
		while (keys.hasMoreElements()) {
			String key=(String)keys.nextElement();
			match = attributes.containsKey(key);
			if (attributes.containsKey(key) && match)
				match=matcher.getProperty(key).equals(getAttribute(key));
		}
		return match;
	}

	public boolean matches(String plf_root_hash) {
		boolean check_plfroot=false;
		if (attributes.containsKey("PLF_ROOT_HASH"))
			check_plfroot=plf_root_hash.equals(getAttribute("PLF_ROOT_HASH"));
		return check_plfroot;
	}

	public int compare(XMLBootConfig c) {
		if (this.getName().equals(c.getName())) return 0;
		boolean match = false;
		Iterator<String> file1 = files.iterator();
		while (file1.hasNext()) {
			String file = file1.next();
			Iterator<String> file2 = c.getFiles().iterator();
			match = false;
			while (file2.hasNext()) {
				String filecompare = file2.next();
				if (filecompare.equals(file)) match = true;
			}
			if (!match) break;
		}
		if (!match) 
			return 2;
		else return 1;
	}

	public boolean isComplete() {
		if (!new File(getTA()).exists()) {
			log.error("missing TA "+getTA());
			return false;
		}
		if (hasAppsBootFile()) {
			if (!new File(getAppsBootFile()).exists()) {
				log.error("missing appsboot "+getAppsBootFile());
				return false;
			}
		}
		Iterator<String> i = getOtherFiles().iterator();
		while (i.hasNext()) {
			String f = i.next();
			if (f.length()>0) { 
			if (!new File(f).exists()) {
				log.error("missing file "+f); 
				return false;
			}
			}
		}
		return true;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name : "+_configname+" / TA : "+_configta+" / Attributes : "+attributes+" / Files : "+files);
		return sb.toString();
	}

}