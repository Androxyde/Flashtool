package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import flashsystem.TaEntry;
import flashsystem.X10flash;

public class WriteTAJob extends Job {

	X10flash flash = null;
	TaEntry ta = null;
	boolean canceled = false;
	boolean success = false;

	public boolean writeSuccess() {
		return success;
	}
	
	public WriteTAJob(String name) {
		super(name);
	}
	
	public void setFlash(X10flash f) {
		flash=f;
	}

	public void setTA(TaEntry t) {
		ta=t;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			flash.openTA(2);
			flash.sendTAUnit(ta);
			flash.closeTA();
			success=true;
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
