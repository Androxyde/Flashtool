package gui.tools;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.util.XperiFirm;

public class XperiFirmJob extends Job {

	private static Logger logger = Logger.getLogger(DecryptJob.class);
	
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
