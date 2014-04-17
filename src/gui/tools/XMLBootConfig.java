package gui.tools;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.logger.MyLogger;

public class XMLBootConfig {

	private String _configname = "";
	private String _configta = "";
	private Vector<String> files = new Vector<String>();
	private Properties attributes = new Properties();
	private String _folder="";

	public XMLBootConfig(String name) {
		_configname = name;
	}
	
	public Vector getFiles() {
		return files;
	}
	
	public void setFolder(String folder) {
		_folder = folder+"/boot";
	}

	public String getAppsBootFile() {
		Iterator<String> file = files.iterator();
		while(file.hasNext()) {
			String appsboot = file.next();
			if (appsboot.toUpperCase().contains("APPSBOOT")) return (_folder.length()>0?_folder+"/":"")+appsboot;
		}
		return "";
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
	
	public boolean matches(String otp_lock_status, String otp_data, String idcode) {
		boolean check_otp_lock_status=true;
		boolean check_otp_data=true;
		boolean check_idcode=true;
		if (attributes.containsKey("OTP_LOCK_STATUS_1"))
			check_otp_lock_status=otp_lock_status.equals(getAttribute("OTP_LOCK_STATUS_1"));
		if (attributes.containsKey("OTP_DATA_1"))
			check_otp_data=otp_data.equals(getAttribute("OTP_DATA_1"));
		if (attributes.containsKey("IDCODE_1"))
			check_idcode=idcode.substring(1).equals(getAttribute("IDCODE_1").substring(1));
		return (check_otp_lock_status && check_otp_data && check_idcode); 
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
			MyLogger.getLogger().error("missing "+getTA());
			return false;
		}
		if (!new File(getAppsBootFile()).exists()) {
			MyLogger.getLogger().error("missing "+getAppsBootFile());
			return false;
		}
		Iterator<String> i = getOtherFiles().iterator();
		while (i.hasNext()) {
			String f = i.next();
			if (!new File(f).exists()) {
				MyLogger.getLogger().error("missing "+f);
				return false;
			}
		}
		return true;
	}
}