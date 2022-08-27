package org.flashtool.gui.tools;



import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.system.DevicesGit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DevicesSyncJob extends Job {

	boolean canceled = false;
	

	public DevicesSyncJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		try {
			log.info("Syncing devices from github");
			DevicesGit.gitSync();
	    	log.info("Devices sync finished.");
			return Status.OK_STATUS;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("Cannot sync devices : "+e.getMessage());
			return Status.CANCEL_STATUS;
		}
    }
}
