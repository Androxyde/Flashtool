package gui.tools;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.simpleusblogger.Parser;
import org.simpleusblogger.S1Packet;

import flashsystem.Bundle;

public class USBParseJob extends Job {

	String logfile="";
	String sindir="";
	private static Logger logger = Logger.getLogger(USBParseJob.class);
	private Vector<S1Packet> session = null;
	
	public USBParseJob(String name) {
		super(name);
	}
	
	public void setFilename(String file) {
		logfile=file;
	}

	public void setSinDir(String dir) {
		sindir=dir;
	}

    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		session = Parser.parse(logfile, sindir);
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
    
    public Vector<S1Packet> getSession() {
    	return session;
    }
}
