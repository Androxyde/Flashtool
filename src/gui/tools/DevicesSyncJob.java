package gui.tools;


import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.system.DevicesGit;

public class DevicesSyncJob extends Job {

	boolean canceled = false;
	
	private static Logger logger = Logger.getLogger(DevicesSyncJob.class);

	public DevicesSyncJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		try {
			logger.info("Syncing devices from github");
			DevicesGit.gitSync();
	    	logger.info("Devices sync finished.");
			return Status.OK_STATUS;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Cannot sync devices : "+e.getMessage());
			return Status.CANCEL_STATUS;
		}
    }
}
