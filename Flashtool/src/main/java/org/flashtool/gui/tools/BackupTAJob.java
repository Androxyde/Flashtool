package org.flashtool.gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Flasher;
import org.flashtool.gui.TARestore;
import org.flashtool.logger.LogProgress;
import org.flashtool.system.DeviceChangedListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackupTAJob extends Job {

	Flasher flash = null;
	boolean canceled = false;
	static final Logger logger = LogManager.getLogger(BackupTAJob.class);

	public BackupTAJob(String name) {
		super(name);
	}
	
	public void setFlash(Flasher f) {
		flash=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.open();
			flash.sendLoader();
			flash.backupTA();
			flash.close();
			logger.info("Dumping TA finished.");
			LogProgress.initProgress(0);
			DeviceChangedListener.enableDetection();
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		LogProgress.initProgress(0);
    		DeviceChangedListener.enableDetection();
    		return Status.CANCEL_STATUS;
    	}
    }
}