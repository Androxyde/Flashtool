package org.flashtool.gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Flasher;
import org.flashtool.gui.MainSWT;
import org.flashtool.gui.TARestore;
import org.flashtool.system.DeviceChangedListener;
import org.flashtool.system.DeviceIdent;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchJob extends Job {

	boolean canceled = false;
	boolean obsolete = false;
	
	static final Logger logger = LogManager.getLogger(SearchJob.class);

	public SearchJob(String name) {
		super(name);
	}
	
	public void stopSearch() {
		canceled=true;
	}

    protected IStatus run(IProgressMonitor monitor) {
		    while (!canceled) {
				if (flashmode()) {
					return Status.OK_STATUS;
				}
		    }
		    return Status.CANCEL_STATUS;
    }

    public boolean flashmode() {
    	boolean found = false;
    	try {
			Thread.sleep(500);
			DeviceIdent id = Devices.getConnectedDevice();
			found = id.getStatus().equals("flash");
	    	if (found && OS.getName().equals("windows")) {
	    		logger.info("Using Gordon gate drivers version "+id.getDriverMajor()+"."+id.getDriverMinor()+"."+id.getDriverMili()+"."+id.getDriverMicro());
	    	}
			if (id.getStatus().equals("flash_obsolete")) {
				if (!obsolete) {
					logger.error("Device connected in flash mode but driver is too old");
					obsolete=true;
				}
			}
		}
		catch (Exception e) {
	    	found = false;
		}
    	return found;
    }

}
