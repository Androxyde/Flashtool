package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.FTDEntry;

public class FTDExplodeJob extends Job {

	FTDEntry entry = null;
	boolean canceled = false;

	public FTDExplodeJob(String name) {
		super(name);
	}
	
	public void setFTD(FTDEntry f) {
		entry=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			MyLogger.getLogger().info("Beginning import of "+entry.getName());
			if (entry.explode())
				MyLogger.getLogger().info(entry.getName()+" imported successfully");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
