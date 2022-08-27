package org.flashtool.gui.tools;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.logger.LogProgress;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class APKFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.toUpperCase().endsWith(".APK"));
    }
}


public class APKInstallJob extends Job {

	String instpath;
	static final Logger logger = LogManager.getLogger(APKInstallJob.class);

	public APKInstallJob(String name) {
		super(name);
	}
	
	public void setFolder(String path) {
		instpath = path;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			File files = new File(instpath);
			File[] chld = files.listFiles(new APKFilter());
			LogProgress.initProgress(chld.length);
			for(int i = 0; i < chld.length; i++){
				if (chld[i].getName().toUpperCase().endsWith(".APK"))
					AdbUtility.install(chld[i].getPath());
				LogProgress.updateProgress();
			}
			LogProgress.initProgress(0);
			logger.info("APK Installation finished");
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