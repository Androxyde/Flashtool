package gui.tools;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;

import flashsystem.X10flash;
import gui.models.TABag;


public class RestoreTAJob extends Job {

	X10flash flash = null;
	Vector<TABag> bag = null;
	boolean canceled = false;
	private static Logger logger = Logger.getLogger(RestoreTAJob.class);
	

	public RestoreTAJob(String name) {
		super(name);
	}
	
	public void setFlash(X10flash f) {
		flash=f;
	}
	
	public void setTA(Vector<TABag> bag) {
		this.bag = bag;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.openDevice();
			flash.sendLoader();
			for (int i=0;i<bag.size();i++) {
				TABag b = bag.get(i);
				if (b.toflash.size()>0) {
					flash.openTA(b.partition);
					for (int j=0;j<b.toflash.size();j++) {
						flash.sendTAUnit(b.toflash.get(j));
					}
					flash.closeTA();
				}
			}
			flash.closeDevice();
			logger.info("Restoring TA finished.");
			LogProgress.initProgress(0);
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }
}