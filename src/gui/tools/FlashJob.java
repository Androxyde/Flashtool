package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.system.DeviceChangedListener;

import flashsystem.Bundle;
import flashsystem.Flasher;
import flashsystem.FlasherFactory;

public class FlashJob extends Job {

	Flasher flash = null;
	Bundle _bundle;
	boolean canceled = false;
	Shell _shell;
	static final Logger logger = LogManager.getLogger(FlashJob.class);
	
	public FlashJob(String name) {
		super(name);
	}
	
	public void setBundle(Bundle b) {
		_bundle = b;
	}
	
	public void setShell(Shell shell) {
		_shell = shell;
	}
	
	public Flasher getFlasher() {
		return flash;
	}

    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		if (_bundle.open()) {
    			logger.info("Please connect your device into flashmode.");
    			String result = "";
    			result = (String)WidgetTask.openWaitDeviceForFlashmode(_shell);
    			if (result.equals("OK")) {
    				flash = FlasherFactory.getFlasher(_bundle, _shell);
    				if (flash.open())
    					flash.flash();
    				_bundle.close();
    			}
    			else {
    				_bundle.close();
    				logger.info("Flash canceled");
    			}
    		}
    		else {
    			logger.info("Cannot open bundle. Flash operation canceled");
    		}
    		DeviceChangedListener.enableDetection();
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		logger.error(e.getMessage());
    		DeviceChangedListener.enableDetection();
    		return Status.CANCEL_STATUS;
    	}
    }
}
