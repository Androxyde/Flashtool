package org.flashtool.gui.tools;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.Bundle;
import org.flashtool.gui.TARestore;
import org.flashtool.parsers.simpleusblogger.Parser;
import org.flashtool.parsers.simpleusblogger.S1Packet;
import org.flashtool.parsers.simpleusblogger.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class USBParseJob extends Job {

	String logfile="";
	String sindir="";
	static final Logger logger = LogManager.getLogger(USBParseJob.class);
	Session session;
	
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
    
    public Session getSession() {
    	return session;
    }
}
