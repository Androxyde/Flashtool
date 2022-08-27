package org.flashtool.gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.FastbootUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchFastbootJob extends Job {

	boolean canceled = false;

	public SearchFastbootJob(String name) {
		super(name);
	}

	public void stopSearch() {
		canceled=true;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		    while (!canceled) {
				try {
					Thread.sleep(1000);
				}
				catch (Exception e) {}
				if (FastbootUtility.getDevices().hasMoreElements()) {
					return Status.OK_STATUS;
				}
		    }
		    return Status.CANCEL_STATUS;
    }
}
