package org.flashtool.gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Flasher;
import org.flashtool.gui.TARestore;
import org.flashtool.logger.LogProgress;
import org.flashtool.parsers.ta.TAUnit;
import org.flashtool.system.ULCodeFile;
import org.flashtool.util.HexDump;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	boolean relockable = false;


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
	//907437D662121E6F
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.open();
			flash.sendLoader();
			blstatus = flash.getRootingStatus();
			imei = flash.getIMEI();
			if (flash.getCurrentDevice().contains("X10") ||
				flash.getCurrentDevice().contains("E10") ||
				flash.getCurrentDevice().contains("E15") ||
				flash.getCurrentDevice().contains("U20")) {
				if (blstatus.equals("ROOTED")) {
					flash.close();
					LogProgress.initProgress(0);
					log.info("Phone already unlocked");
					log.info("You can safely reboot in normal mode");
				}
				else {
					LogProgress.initProgress(1);
		    		platform = flash.getCurrentDevice();
					TAUnit ta=flash.readTA(2,2129);
					flash.close();
					LogProgress.initProgress(0);
					if (ta!=null)
						phonecert = HexDump.toHex(ta.getUnitData());
				}
				if (phonecert.length()>874)
					phonecert = phonecert.substring(489,489+383);
			}
			else {
				TAUnit ta=flash.readTA(2,2226);
				serial = flash.getSerial();
				ULCodeFile uc = new ULCodeFile(serial);
				ulcode = uc.getULCode();
				if (ta!=null) {
					if (ta.getUnitData().length>2) {
						ulcode = new String(ta.getUnitData());
						uc.setCode(ulcode);
					}
				}
				
				alreadyunlocked=(ta==null && ulcode.length()>0 && blstatus.equals("ROOTABLE"));
				relockable=(blstatus.equals("ROOTED") && ta!=null);
				
				LogProgress.initProgress(0);
				//System.out.println("Rootable : " + this.isRootable());
				//System.out.println("Relockable : "+ this.isRelockable());
				//System.out.println("Already Unlocked : "+ this.alreadyUnlocked());
			}
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }
    
    public boolean isRootable() {
    	return (blstatus.equals("ROOTABLE"));
    }

    public boolean isRooted() {
    	return (blstatus.equals("ROOTED"));
    }
    
    public boolean isRelockable() {
    	return relockable;
    } 

    public boolean isRelocked() {
    	return alreadyunlocked && blstatus.equals("ROOTABLE");
    }
}
