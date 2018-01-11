package gui.tools;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;

import flashsystem.Flasher;
import gui.models.TABag;
import gui.models.TADevice;


public class RestoreTAJob extends Job {

	Flasher flash = null;
	TADevice tadev = null;
	boolean canceled = false;
	static final Logger logger = LogManager.getLogger(RestoreTAJob.class);
	

	public RestoreTAJob(String name) {
		super(name);
	}
	
	public void setFlash(Flasher f) {
		flash=f;
	}
	
	public void setTA(TADevice tad) {
		this.tadev = tad;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.open();
			if (flash.getCurrentDevice().equals(tadev.getModel()) || tadev.getModel().length()==0) {
				flash.sendLoader();
				for (int i=0;i<tadev.getBags().size();i++) {
					TABag b = tadev.getBags().get(i);
					if (b.toflash.size()>0) {
						for (int j=0;j<b.toflash.size();j++) {
							flash.writeTA(b.partition,b.toflash.get(j));
						}
					}
				}
				flash.close();
				logger.info("Restoring TA finished.");
				LogProgress.initProgress(0);
			}
			else {
				logger.info("Those TA units are not for your device");
				flash.close();
				logger.info("Restoring TA finished.");
				LogProgress.initProgress(0);
			}
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }
}