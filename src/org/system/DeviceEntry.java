package org.system;

//import gui.tools.WidgetTask;
import gui.tools.WidgetTask;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import org.adb.AdbUtility;
import org.eclipse.swt.widgets.Display;

public class DeviceEntry {

	PropertiesFile _entry;
	private Boolean hasBusybox=null;
	private boolean isRecoveryMode=false;
	//private HashSet<DeviceEntryModel> models = new HashSet<DeviceEntryModel>();
	
	public void queryAll() {
		setVersion();
		setKernelVersion();
		try {
			isRecoveryMode=!AdbUtility.isMounted("/system");
		}
		catch (Exception e) {
		}
	}

	public boolean hasRoot() {
		if (AdbUtility.hasRootNative(false)) return AdbUtility.hasRootNative(false);
		return AdbUtility.hasRootPerms();
	}

	public boolean isRecovery() {
		return isRecoveryMode;
	}

	public boolean hasSU() {
		try {
		return AdbUtility.hasSU();
		}
		catch (Exception e) {
			return false;
		}
	}

	/*public void rebootSelectedRecovery() throws Exception {
		RecoveryBootSelectGUI rsel = new RecoveryBootSelectGUI();
		rsel.setTitle("Recovery selector");
		String current = rsel.getVersion();
		if (current.length()>0) {
			MyLogger.getLogger().info("Rebooting into recovery mode");
			Shell shell = new Shell("rebootrecoveryt");
			shell.setProperty("RECOV_VERSION", current);
			shell.runRoot();
			MyLogger.getLogger().info("Phone will reboot into recovery mode");
		}
		else {
			MyLogger.getLogger().info("Canceled");
		}
	}*/
	
	/*public void setDefaultRecovery() throws Exception {
		RecoveryBootSelectGUI rsel = new RecoveryBootSelectGUI();
		String current = rsel.getVersion();
		if (current.length()>0) {
			if (AdbUtility.Sysremountrw()) {
			MyLogger.getLogger().info("Setting default recovery");
			Shell shell = new Shell("setdefaultrecovery");
			shell.setProperty("RECOV_VERSION", current);
			shell.runRoot();
			MyLogger.getLogger().info("Done");
			}
		}
		else {
			MyLogger.getLogger().info("Canceled");
		}
	}*/
	
	private void setKernelVersion() {
		_entry.setProperty("kernel.version", AdbUtility.getKernelVersion(isBusyboxInstalled(false)));
	}
	
	public String getKernelVersion() {
		return _entry.getProperty("kernel.version");
	}
	
	public DeviceEntry(PropertiesFile entry) {
		_entry = entry;
	}
	
	public DeviceEntry(String Id) {
		_entry = new PropertiesFile();
		try {
			String path = OS.getFolderMyDevices()+File.separator+Id+File.separator+Id+".properties";
			if (new File(path).exists())
				_entry.open("",path);
			else {
				path = OS.getFolderDevices()+File.separator+Id+File.separator+Id+".properties";
				_entry.open("",path);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		                     
	}
	
	public String getId() {
		return _entry.getProperty("internalname");
	}
	
	public String getName() {
		return _entry.getProperty("realname");
	}
	
	public String getDeviceDir() {
		return OS.getFolderDevices()+File.separator+getId();
	}

	public String getMyDeviceDir() {
		return OS.getFolderMyDevices()+File.separator+getId();
	}
	
	public String getFolderRegisteted() {
		return OS.getFolderRegisteredDevices()+File.separator+this.getSerial();
	}
	public String getCleanDir() {
		return OS.getFolderRegisteredDevices()+File.separator+getSerial()+File.separator+"clean"+File.separator+getBuildId();
	}

	public String getAppsDir() {
		return OS.getFolderRegisteredDevices()+File.separator+getSerial()+File.separator+"apps"+File.separator+getBuildId();
	}
	
	public String getBuildProp() {
		return _entry.getProperty("buildprop");
	}
	
	public String getLoaderMD5() {
		return _entry.getProperty("loader").toUpperCase();
	}

	public String getLoaderUnlockedMD5() {
		return _entry.getProperty("loader_unlocked").toUpperCase();
	}

	public boolean hasUnlockedLoader() {
		if (_entry.getProperties().containsKey("loader_unlocked")) {
			return (_entry.getProperties().getProperty("loader_unlocked").length()>0);
		}
		else
			return false;
	}

	public String getBusyBoxInstallPath() {
		return _entry.getProperty("busyboxinstallpath");
	}
	
	public String getInstalledBusyboxVersion(boolean force) {
		if (Devices.getCurrent().isBusyboxInstalled(force)) {
			return AdbUtility.getBusyboxVersion(getBusyBoxInstallPath());
		}
		else 
			hasBusybox=false;
			return "N/A";

	}
	
	public HashSet<String> getRecognitionList() {
		String[] result = _entry.getProperty("recognition").split(",");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			set.add(result[i]);
		}
		return set;
	}

	public HashSet<String> getVariantList() {
		String[] result = getVariant().split(",");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			if (result[i].trim().length()>0)
				set.add(result[i]);
		}
		return set;
	}
	
	public String getRecognition() {
		return _entry.getProperty("recognition");
	}

	public String getVariant() {
		String variant = _entry.getProperty("variant");
		if (variant==null) return getId();
		return variant;
	}
	
	public void clearVariants() {
		_entry.setProperty("variant", "");
		_entry.write("ISO-8859-1");
	}

	public void addVariantToList(String model) {
		String current = _entry.getProperty("variant");
		if (current==null) current="";
		if (current.length()==0) _entry.setProperty("variant", model);
		else {
			current = current + ","+model;
			_entry.setProperty("variant", current);
		}
		_entry.write("ISO-8859-1");
	}
	
	public void addRecognitionToList(String recog) {
		String current = _entry.getProperty("recognition");
		current = current + ","+recog;
		_entry.setProperty("recognition", current);
		_entry.write("ISO-8859-1");
	}
	
	public String getLoader() {
		return this.getDeviceDir()+"/loader.sin";
	}

	public String getLoaderUnlocked() {
		return this.getDeviceDir()+"/loader_unlocked.sin";
	}
	
	private void setVersion () {
		_entry.setProperty("android.release",DeviceProperties.getProperty("ro.build.version.release"));
		_entry.setProperty("android.build",DeviceProperties.getProperty("ro.build.id"));
	}
	
	public String getVersion() {
		return _entry.getProperty("android.release");
	}

	public String getBuildId() {
		return _entry.getProperty("android.build");
	}

	public boolean canFlash() {
		return _entry.getProperty("canflash").equals("true");
	}
	
	public boolean canKernel() {
		return (_entry.getProperty("cankernel").equals("true"));
	}

	public boolean canRecovery() {
		return (new File(getDeviceDir()+File.separator+"bootkit").exists());
	}

	public boolean canFastboot() {
		return _entry.getProperty("canfastboot").equals("true");
	}

	public String getBusybox(boolean select) {
		String version="";
		if (!select) version = _entry.getProperty("busyboxhelper");
		else {
			version = WidgetTask.openBusyboxSelector(Display.getCurrent().getActiveShell());
			//BusyBoxSelectGUI sel = new BusyBoxSelectGUI(getId());
			//version = sel.getVersion();
		}
		if (version.length()==0) return "";
		else return OS.getFolderDevices()+File.separator+"busybox"+File.separator+version+File.separator+"busybox";
	}
	
	public String getOptimize() {
		return getDeviceDir()+"/optimize.tar";
	}
	
	public String getBuildMerge() {
		return getDeviceDir()+"/build.prop";
	}

	public String getCharger() {
		return getDeviceDir()+"/charger";
	}

	public boolean isBusyboxInstalled(boolean force) {
    	if (hasBusybox==null || force)
    		hasBusybox = (AdbUtility.getBusyboxVersion(getBusyBoxInstallPath()).length()>0);
    	return hasBusybox.booleanValue();
    }

    public void doBusyboxHelper() throws Exception {
    	if (!isBusyboxInstalled(false)) {
    		AdbUtility.push(getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
    		FTShell shell = new FTShell("busyhelper");
    		shell.run(true);
		}
    }

    public void reboot() throws Exception {
    	if (hasRoot()) {
	    	FTShell s = new FTShell("reboot");
	    	s.runRoot(false);
    	}
    	else {
    		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {OS.getPathAdb(),"reboot"},false);
    	}
    }
    
    public String getSerial() {
    	return AdbUtility.getDevices().nextElement();
    }
    
    /*public boolean canHandleUpdates() {
    	Iterator<DeviceEntryModel> i = getModels().iterator();
    	while (i.hasNext()) {
    		if (i.next().getTac().length()>0) return true;
    	}
    	return false;
    }
    
    public boolean canShowUpdates() {
    	Iterator<DeviceEntryModel> i = getModels().iterator();
    	while (i.hasNext()) {
    		if (i.next().getCDA().getProperties().size()>0) return true;
    	}
    	return false;
    }*/
    
    /*public HashSet<DeviceEntryModel> getModels() {
    	if (models.size()==0) {
			Iterator<String> ivariants = getVariantList().iterator();
			while (ivariants.hasNext()) {
				models.add(new DeviceEntryModel(this,ivariants.next()));
			}
    	}
    	return models;
    }*/
    
    public boolean hasFlashScript(String model, String version) {
    	
    	boolean official = new File(getDeviceDir()   + File.separator+model+"_"+version+".fsc").exists();
    	boolean custom   = new File(getMyDeviceDir() + File.separator+model+"_"+version+".fsc").exists();
    	
    	boolean genericofficial = new File(getDeviceDir()   + File.separator+model+".fsc").exists();
    	boolean genericcustom   = new File(getMyDeviceDir() + File.separator+model+".fsc").exists();
    	
    	boolean defaultofficial = new File(getDeviceDir()   + File.separator+"default.fsc").exists();
    	boolean defaultcustom   = new File(getMyDeviceDir() + File.separator+"default.fsc").exists();
    	
    	return (official || custom || genericofficial || genericcustom || defaultofficial || defaultcustom);
    }

    public String getFlashScript(String model, String version) {

    	boolean official = new File(getDeviceDir()   + File.separator+model+"_"+version+".fsc").exists();
    	boolean custom   = new File(getMyDeviceDir() + File.separator+model+"_"+version+".fsc").exists();
    	
    	boolean genericofficial = new File(getDeviceDir()   + File.separator+model+".fsc").exists();
    	boolean genericcustom   = new File(getMyDeviceDir() + File.separator+model+".fsc").exists();
    	
    	boolean defaultofficial = new File(getDeviceDir()   + File.separator+"default.fsc").exists();
    	boolean defaultcustom   = new File(getMyDeviceDir() + File.separator+"default.fsc").exists();

    	if (custom)   return getMyDeviceDir()+File.separator+model+"_"+version+".fsc";
    	if (official) return getDeviceDir()+File.separator+model+"_"+version+".fsc";
    	
    	if (genericcustom)   return getMyDeviceDir()+File.separator+model+".fsc";
    	if (genericofficial) return getDeviceDir()+File.separator+model+".fsc";
    	
    	if (defaultcustom)   return getMyDeviceDir()+File.separator+"default.fsc";
    	if (defaultofficial) return getDeviceDir()+File.separator+"default.fsc";

    	return "";
    }

}