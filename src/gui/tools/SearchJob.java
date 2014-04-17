package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import flashsystem.X10flash;

public class SearchJob extends Job {

	X10flash flash = null;
	boolean canceled = false;

	public SearchJob(String name) {
		super(name);
	}
	
	public void setFlash(X10flash f) {
		flash=f;
	}
	
	public void stopSearch() {
		canceled=true;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
		    while (!canceled) {
		    	if (flash!=null) {
					if (flash.deviceFound()) {
						return Status.OK_STATUS;
					}
		    	}
		    }
		    return Status.CANCEL_STATUS;
    }
}
