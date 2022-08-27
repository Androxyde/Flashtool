package org.flashtool.gui.tools;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusyboxInstallJob extends Job {

	String bbpath;
	
	public BusyboxInstallJob(String name) {
		super(name);
	}
	
	public void setBusybox(String path) {
		bbpath = path;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		AdbUtility.push(bbpath, GlobalConfig.getProperty("deviceworkdir"));
    		FTShell shell = new FTShell("busyhelper");
    		shell.run(false);
    		shell = new FTShell("instbusybox");
			shell.setProperty("BUSYBOXINSTALLPATH", Devices.getCurrent().getBusyBoxInstallPath());
			shell.runRoot();
	        log.info("Installed version of busybox : " + Devices.getCurrent().getInstalledBusyboxVersion(true));
	        log.info("Finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		log.error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }
}
