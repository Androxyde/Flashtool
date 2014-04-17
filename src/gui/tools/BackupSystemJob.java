package gui.tools;

import java.io.File;
import java.util.Iterator;
import org.adb.AdbUtility;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.Devices;
import org.system.OS;

public class BackupSystemJob extends Job {

	public BackupSystemJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		new File(OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+Devices.getCurrent().getSerial()+File.separator+"apps"+File.separator+Devices.getCurrent().getBuildId()).mkdirs();
			DeviceApps apps = new DeviceApps();
			MyLogger.initProgress(apps.getCurrent().size());
			Iterator<String> ic = apps.getCurrent().iterator();
			while (ic.hasNext()) {
				String app = ic.next();
				MyLogger.updateProgress();
				try {
					AdbUtility.pull("/system/app/"+app, "."+File.separator+"custom"+File.separator+"mydevices"+File.separator+Devices.getCurrent().getSerial()+File.separator+"apps"+File.separator+Devices.getCurrent().getBuildId());
				}
				catch (Exception e) {}
			}
			MyLogger.getLogger().info("Backup Finished");
			MyLogger.initProgress(0);
			return Status.OK_STATUS;
		}
    	catch (Exception e) {
    		e.printStackTrace();
			MyLogger.getLogger().error(e.getMessage());
			MyLogger.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }

}