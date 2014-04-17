package gui.tools;

import java.io.File;
import java.io.FilenameFilter;

import org.adb.AdbUtility;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;

class APKFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.toUpperCase().endsWith(".APK"));
    }
}


public class APKInstallJob extends Job {

	String instpath;

	public APKInstallJob(String name) {
		super(name);
	}
	
	public void setFolder(String path) {
		instpath = path;
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
			File files = new File(instpath);
			File[] chld = files.listFiles(new APKFilter());
			MyLogger.initProgress(chld.length);
			for(int i = 0; i < chld.length; i++){
				if (chld[i].getName().toUpperCase().endsWith(".APK"))
					AdbUtility.install(chld[i].getPath());
				MyLogger.updateProgress();
			}
			MyLogger.initProgress(0);
			MyLogger.getLogger().info("APK Installation finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		MyLogger.getLogger().error(e.getMessage());
    		MyLogger.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }
}