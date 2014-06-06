package gui.tools;

import flashsystem.SeusSinTool;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;

public class DecryptJob extends Job {

	boolean canceled = false;
	Vector files;
	private static Logger logger = Logger.getLogger(DecryptJob.class);
	
	public DecryptJob(String name) {
		super(name);
	}
	
	public void setFiles(Vector f) {
		files=f;
	}
	
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			for (int i=0;i<files.size();i++) {
				File f = (File)files.get(i);
				logger.info("Decrypting "+f.getName());
        		SeusSinTool.decrypt(f.getAbsolutePath());
			}
			logger.info("Decryption finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
