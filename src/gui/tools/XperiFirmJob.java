package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.util.XperiFirm;

public class XperiFirmJob extends Job {

	static final Logger logger = LogManager.getLogger(DecryptJob.class);
	
	public XperiFirmJob(String name) {
		super(name);
	}
	
		
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		XperiFirm.run();
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
