package org.flashtool.gui.tools;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Bundle;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class createFTFJob extends Job {

	Bundle bundle;
	
	public createFTFJob(String name) {
		super(name);
	}
	
	public void setBundle(Bundle b) {
		bundle=b;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		bundle.createFTF();
    		log.info("Bundle creation finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
