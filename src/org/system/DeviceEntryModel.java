package org.system;

import java.io.File;

public class DeviceEntryModel {

	DeviceEntry _parent;
	String modelId;
	String tac8 = "";
	PropertiesFile _custIds = null;
	
	public DeviceEntryModel(DeviceEntry p, String pmodelId) {
		_parent = p;
		modelId = pmodelId;
		String dirPath = _parent.getDeviceDir()+File.separator+"updates"+File.separator+modelId+File.separator;
		String customDirPath = _parent.getCustomDeviceDir()+File.separator+"updates"+File.separator+modelId+File.separator;
		try {
			if (new File(dirPath+"tac").exists()) {
				TextFile tf = new TextFile(dirPath+"tac","ISO8859-15");
				tac8 = tf.getLines().iterator().next();
			}
			else if (new File(customDirPath+"tac").exists()) {
				TextFile tf = new TextFile(customDirPath+"tac","ISO8859-15");
				tac8 = tf.getLines().iterator().next();			
			}
			if (new File(dirPath+"custlist.properties").exists()) {
				PropertiesFile pf = new PropertiesFile();
				pf.open("", dirPath+"custlist.properties");
				_custIds.mergeWith(pf);
			}
			if (new File(customDirPath+"custlist.properties").exists()) {
				PropertiesFile pf = new PropertiesFile();
				pf.open("", customDirPath+"custlist.properties");
				_custIds.getProperties().clear();
				_custIds.mergeWith(pf);
			}
		} catch (Exception ioe) {
		}
	}
	
	public String getTac() {
		return tac8;
	}
	
	public String getId() {
		return modelId;
	}

}