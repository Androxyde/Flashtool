package gui.tools;

import java.io.File;
import java.util.Iterator;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;
import org.system.Devices;
import org.system.OS;

public class BackupSystemJob extends Job {

	private static Logger logger = Logger.getLogger(BackupSystemJob.class);
	
	public BackupSystemJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		new File(OS.getFolderRegisteredDevices()+File.separator+Devices.getCurrent().getSerial()+File.separator+"apps"+File.separator+Devices.getCurrent().getBuildId()).mkdirs();
			DeviceApps apps = new DeviceApps();
			LogProgress.initProgress(apps.getCurrent().size());
			Iterator<String> ic = apps.getCurrent().iterator();
			while (ic.hasNext()) {
				String app = ic.next();
				LogProgress.updateProgress();
				try {
					AdbUtility.pull("/system/app/"+app, OS.getFolderRegisteredDevices()+File.separator+Devices.getCurrent().getSerial()+File.separator+"apps"+File.separator+Devices.getCurrent().getBuildId());
				}
				catch (Exception e) {}
			}
			logger.info("Backup Finished");
			LogProgress.initProgress(0);
			return Status.OK_STATUS;
		}
    	catch (Exception e) {
    		e.printStackTrace();
			logger.error(e.getMessage());
			LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }

}