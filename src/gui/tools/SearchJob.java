package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.system.Devices;

import flashsystem.Flasher;

public class SearchJob extends Job {

	boolean canceled = false;

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
			found = Devices.getLastConnected(true).getPid().equals("ADDE") || Devices.getLastConnected(true).getPid().equals("B00B");
		}
		catch (Exception e) {
	    	found = false;
		}
    	return found;
    }

}
