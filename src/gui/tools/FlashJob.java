package gui.tools;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import flashsystem.X10flash;

public class FlashJob extends Job {

	X10flash flash = null;
	boolean canceled = false;
	Shell _shell;
	private static Logger logger = Logger.getLogger(FlashJob.class);
	
	public FlashJob(String name) {
		super(name);
	}
	
	public void setFlash(X10flash f) {
		flash=f;
	}
	
	public void setShell(Shell shell) {
		_shell = shell;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		if (flash.getBundle().open()) {
    			logger.info("Please connect your device into flashmode.");
    			String result = "";
    			if (!flash.getBundle().simulate()) {
    				result = (String)WidgetTask.openWaitDeviceForFlashmode(_shell,flash);
    			} result="OK";
    			if (result.equals("OK")) {
    				flash.openDevice();
    				flash.flashDevice();
    				flash.getBundle().close();
    			}
    			else {
    				flash.getBundle().close();
    				logger.info("Flash canceled");
    			}
    		}
    		else {
    			logger.info("Cannot open bundle. Flash operation canceled");
    		}
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
