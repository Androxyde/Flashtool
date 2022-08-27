package org.flashtool.gui.tools;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.system.FTDEntry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FTDExplodeJob extends Job {

	FTDEntry entry = null;
	boolean canceled = false;
	
	public FTDExplodeJob(String name) {
		super(name);
	}
	
	public void setFTD(FTDEntry f) {
		entry=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			log.info("Beginning import of "+entry.getName());
			if (entry.explode())
				log.info(entry.getName()+" imported successfully");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
