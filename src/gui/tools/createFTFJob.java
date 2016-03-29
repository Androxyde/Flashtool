package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import flashsystem.Bundle;

public class createFTFJob extends Job {

	Bundle bundle;
	static final Logger logger = LogManager.getLogger(createFTFJob.class);
	
	public createFTFJob(String name) {
		super(name);
	}
	
	public void setBundle(Bundle b) {
		bundle=b;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		bundle.createFTF();
    		logger.info("Bundle creation finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
