package org.flashtool.gui.tools;

import java.util.Vector;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Flasher;
import org.flashtool.gui.TARestore;
import org.flashtool.gui.models.TABag;
import org.flashtool.gui.models.TADevice;
import org.flashtool.log.LogProgress;
import org.flashtool.system.DeviceChangedListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestoreTAJob extends Job {

	Flasher flash = null;
	TADevice tadev = null;
	boolean canceled = false;
	

	public RestoreTAJob(String name) {
		super(name);
	}
	
	public void setFlash(Flasher f) {
		flash=f;
	}
	
	public void setTA(TADevice tad) {
		this.tadev = tad;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.open();
			if (flash.getCurrentDevice().equals(tadev.getModel()) || tadev.getModel().length()==0) {
				flash.sendLoader();
				flash.setFlashState(true);
				for (int i=0;i<tadev.getBags().size();i++) {
					TABag b = tadev.getBags().get(i);
					if (b.toflash.size()>0) {
						for (int j=0;j<b.toflash.size();j++) {
							flash.writeTA(b.partition,b.toflash.get(j));
						}
					}
				}
				flash.setFlashState(false);
				flash.close();
				log.info("Restoring TA finished.");
				LogProgress.initProgress(0);
			}
			else {
				log.info("Those TA units are not for your device");
				flash.close();
				log.info("Restoring TA finished.");
				LogProgress.initProgress(0);
			}
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