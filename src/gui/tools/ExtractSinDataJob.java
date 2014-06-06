package gui.tools;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;

import flashsystem.SinFile;

public class ExtractSinDataJob extends Job {

	boolean canceled = false;
	SinFile sin;
	private String mode="raw";
	private static Logger logger = Logger.getLogger(ExtractSinDataJob.class);
	
	public ExtractSinDataJob(String name) {
		super(name);
	}
	
	public void setSin(SinFile f) {
		sin=f;
	}
	
	public void setMode(String m) {
		mode = m;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		if (mode.equals("data")) {
    			logger.info("Starting data extraction");
    			sin.dumpImage();
    		}
    		else
    			if (mode.equals("raw"))
    				sin.dumpRaw();
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
