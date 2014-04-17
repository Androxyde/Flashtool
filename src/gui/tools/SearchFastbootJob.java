package gui.tools;

import org.adb.FastbootUtility;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

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
