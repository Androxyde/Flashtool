package gui.tools;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;
import org.system.DeviceChangedListener;
import org.system.ULCodeFile;
import org.ta.parsers.TAUnit;
import org.util.HexDump;

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
	private static Logger logger = Logger.getLogger(GetULCodeJob.class);


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
					LogProgress.initProgress(0);
					DeviceChangedListener.pause(false);
					logger.info("Phone already unlocked");
					logger.info("You can safely reboot in normal mode");
				}
				else {
					LogProgress.initProgress(1);
		    		platform = flash.getCurrentDevice();
					flash.openTA(2);
					TAUnit ta=flash.readTA(2129);
					flash.closeTA();
					flash.closeDevice();
					LogProgress.initProgress(0);
					DeviceChangedListener.pause(false);
					if (ta!=null)
						phonecert = HexDump.toHex(ta.getUnitData());
				}
				if (phonecert.length()>874)
					phonecert = phonecert.substring(489,489+383);
			}
			else {
				flash.openTA(2);
				TAUnit ta=flash.readTA(2226);
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
