package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.DeviceChangedListener;
import org.system.ULCodeFile;

import flashsystem.TaEntry;
import flashsystem.X10flash;

public class GetULCodeJob extends Job {

	X10flash flash = null;
	boolean canceled = false;
	String blstatus = "";
	String ulcode = "";
	String imei = "";
	String serial = "";
	String phonecert = "";
	String platform = "";
	boolean alreadyunlocked = false;
	boolean relocked = false;

	
	public String getBLStatus() {
		return blstatus;
	}
	
	public String getULCode() {
		return ulcode;
	}

	public String getPhoneCert() {
		return phonecert;
	}

	public String getPlatform() {
		return platform.replace("i", "").replace("a", "").trim();
	}

	public String getSerial() {
		return serial;
	}
	
	public String getIMEI() {
		return imei;
	}
	
	public boolean alreadyUnlocked() {
		return alreadyunlocked;
	}

	public GetULCodeJob(String name) {
		super(name);
	}
	
	public void setFlash(X10flash f) {
		flash=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.openDevice();
			flash.sendLoader();
			blstatus = flash.getPhoneProperty("ROOTING_STATUS");
			imei = flash.getPhoneProperty("IMEI");
			if (flash.getCurrentDevice().contains("X10") ||
				flash.getCurrentDevice().contains("E10") ||
				flash.getCurrentDevice().contains("E15") ||
				flash.getCurrentDevice().contains("U20")) {
				if (blstatus.equals("ROOTED")) {
					flash.closeDevice();
					MyLogger.initProgress(0);
					DeviceChangedListener.pause(false);
					MyLogger.getLogger().info("Phone already unlocked");
					MyLogger.getLogger().info("You can safely reboot in normal mode");
				}
				else {
		    		MyLogger.initProgress(1);
		    		platform = flash.getCurrentDevice();
					flash.openTA(2);
					TaEntry ta=flash.dumpProperty(2129);
					flash.closeTA();
					flash.closeDevice();
					MyLogger.initProgress(0);
					DeviceChangedListener.pause(false);
					if (ta!=null)
						phonecert = ta.getDataHex().replace(",","").replace("[", "").replace("[", "").trim();
				}
				if (phonecert.length()>874)
					phonecert = phonecert.substring(489,489+383);
			}
			else {
				flash.openTA(2);
				TaEntry ta=flash.dumpProperty(2226);
				flash.closeTA();
				serial = flash.getSerial();
				if (ta==null) {
					ULCodeFile uc = new ULCodeFile(serial);
					if (uc.getULCode().length()>0) {
						ulcode = uc.getULCode();
						alreadyunlocked=true;
						relocked=true;
					}
					else {
						ulcode="";
						alreadyunlocked=false;
						flash.closeDevice();
						MyLogger.initProgress(0);
						DeviceChangedListener.pause(false);
					}
				}
				else {
					alreadyunlocked=true;
					if (ta.getDataSize()<=2) {
						relocked = true;
						ULCodeFile uc = new ULCodeFile(serial);
						ulcode = uc.getULCode();
					}
					else {
						ulcode = ta.getDataString();
						ULCodeFile uc = new ULCodeFile(serial);
						uc.setCode(ulcode);
					}
				}
			}
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		MyLogger.getLogger().error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }
    
    public boolean isRelocked() {
    	return relocked;
    }

}
