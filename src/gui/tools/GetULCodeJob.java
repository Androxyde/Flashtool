package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;
import org.system.DeviceChangedListener;
import org.system.ULCodeFile;
import org.ta.parsers.TAUnit;
import org.util.HexDump;

import flashsystem.Flasher;

public class GetULCodeJob extends Job {

	Flasher flash = null;
	boolean canceled = false;
	String blstatus = "";
	String ulcode = "";
	String imei = "";
	String serial = "";
	String phonecert = "";
	String platform = "";
	boolean alreadyunlocked = false;
	boolean relocked = false;
	static final Logger logger = LogManager.getLogger(GetULCodeJob.class);


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
	
	public void setFlash(Flasher f) {
		flash=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.open();
			flash.sendLoader();
			blstatus = flash.getPhoneProperty("ROOTING_STATUS");
			imei = flash.getPhoneProperty("IMEI");
			if (flash.getCurrentDevice().contains("X10") ||
				flash.getCurrentDevice().contains("E10") ||
				flash.getCurrentDevice().contains("E15") ||
				flash.getCurrentDevice().contains("U20")) {
				if (blstatus.equals("ROOTED")) {
					flash.close();
					LogProgress.initProgress(0);
					DeviceChangedListener.pause(false);
					logger.info("Phone already unlocked");
					logger.info("You can safely reboot in normal mode");
				}
				else {
					LogProgress.initProgress(1);
		    		platform = flash.getCurrentDevice();
					TAUnit ta=flash.readTA(2,2129);
					flash.close();
					LogProgress.initProgress(0);
					DeviceChangedListener.pause(false);
					if (ta!=null)
						phonecert = HexDump.toHex(ta.getUnitData());
				}
				if (phonecert.length()>874)
					phonecert = phonecert.substring(489,489+383);
			}
			else {
				TAUnit ta=flash.readTA(2,2226);
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
						flash.close();
						LogProgress.initProgress(0);
						DeviceChangedListener.pause(false);
					}
				}
				else {
					alreadyunlocked=true;
					if (ta.getDataLength()<=2) {
						relocked = true;
						ULCodeFile uc = new ULCodeFile(serial);
						ulcode = uc.getULCode();
					}
					else {
						ulcode = new String(ta.getUnitData());
						ULCodeFile uc = new ULCodeFile(serial);
						uc.setCode(ulcode);
					}
				}
			}
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }
    
    public boolean isRelocked() {
    	return relocked;
    } 

}
