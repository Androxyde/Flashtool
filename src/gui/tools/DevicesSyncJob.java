package gui.tools;

import flashsystem.SeusSinTool;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.DevicesGit;

public class DevicesSyncJob extends Job {

	boolean canceled = false;
	Vector files;
	private static Logger logger = Logger.getLogger(DevicesSyncJob.class);

	public DevicesSyncJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		try {
			DevicesGit.gitSync();
			return Status.OK_STATUS;
		}
		catch (Exception e) {
			logger.error("Cannot sync devices : "+e.getMessage());
			return Status.CANCEL_STATUS;
		}
    }
}
