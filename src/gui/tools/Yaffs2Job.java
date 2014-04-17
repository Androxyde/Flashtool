package gui.tools;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.OS;

public class Yaffs2Job extends Job {

	String _fname = "";
	
	public void setFilename(String fname) {
		_fname = fname;
	}
	
	public Yaffs2Job(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
				int index = _fname.lastIndexOf(".yaffs2");
				String folder = _fname.substring(0, index)+"_content";
				MyLogger.getLogger().info("Extracting " + _fname + " to " + folder);
				OS.unyaffs(_fname, folder);
				MyLogger.getLogger().info("Extraction finished");
				return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }


}