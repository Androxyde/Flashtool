package gui.tools;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.system.FTDEntry;

public class FTDExplodeJob extends Job {

	FTDEntry entry = null;
	boolean canceled = false;
	private static Logger logger = Logger.getLogger(FTDExplodeJob.class);
	
	public FTDExplodeJob(String name) {
		super(name);
	}
	
	public void setFTD(FTDEntry f) {
		entry=f;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			logger.info("Beginning import of "+entry.getName());
			if (entry.explode())
				logger.info(entry.getName()+" imported successfully");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
