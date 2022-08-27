package org.flashtool.gui.tools;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.FastbootUtility;
import org.flashtool.system.RunOutputs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BLUnlockJob extends Job {

	String ulcode;
	boolean canceled = false;
	boolean unlocksuccess = true;

	public boolean unlockSuccess() {
		return unlocksuccess;
	}
	
	public BLUnlockJob(String name) {
		super(name);
	}
	
	public void setULCode(String code) {
		ulcode = code;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		try {
			if (FastbootUtility.getDevices().hasMoreElements()) {
				RunOutputs out = FastbootUtility.unlock(ulcode);
				if (out.getStdErr().contains("FAIL") || out.getStdOut().contains("FAIL"))
						unlocksuccess = false;
				if (unlocksuccess) {
					log.info("Device will reboot into system now");
					FastbootUtility.rebootDevice();
				}
			}
			else {
				log.error("Your device must be in fastboot mode");
				log.error("Please restart it in fastboot mode");							
			}
			return Status.OK_STATUS;
		}
		catch (Exception exc) {
			log.error(exc.getMessage());
    		exc.printStackTrace();
    		return Status.CANCEL_STATUS;
		}    	
    }
}
