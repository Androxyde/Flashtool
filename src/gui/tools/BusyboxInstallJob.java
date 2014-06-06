package gui.tools;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.Devices;
import org.system.GlobalConfig;
import org.system.FTShell;

public class BusyboxInstallJob extends Job {

	String bbpath;
	private static Logger logger = Logger.getLogger(BusyboxInstallJob.class);
	
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
	        logger.info("Installed version of busybox : " + Devices.getCurrent().getInstalledBusyboxVersion(true));
	        logger.info("Finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }
}
