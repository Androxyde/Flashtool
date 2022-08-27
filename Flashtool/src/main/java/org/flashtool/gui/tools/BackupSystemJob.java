package org.flashtool.gui.tools;

import java.io.File;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.log.LogProgress;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackupSystemJob extends Job {

	static final Logger logger = LogManager.getLogger(BackupSystemJob.class);
	
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
			log.info("Backup Finished");
			LogProgress.initProgress(0);
			return Status.OK_STATUS;
		}
    	catch (Exception e) {
    		e.printStackTrace();
			log.error(e.getMessage());
			LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }

}