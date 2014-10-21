package gui.tools;

import org.adb.FastbootUtility;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.system.RunOutputs;

public class BLUnlockJob extends Job {

	String ulcode;
	boolean canceled = false;
	boolean unlocksuccess = true;
	private static Logger logger = Logger.getLogger(BLUnlockJob.class);

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
					logger.info("Device will reboot into system now");
					FastbootUtility.rebootDevice();
				}
			}
			else {
				logger.error("Your device must be in fastboot mode");
				logger.error("Please restart it in fastboot mode");							
			}
			return Status.OK_STATUS;
		}
		catch (Exception exc) {
			logger.error(exc.getMessage());
    		exc.printStackTrace();
    		return Status.CANCEL_STATUS;
		}    	
    }
}
