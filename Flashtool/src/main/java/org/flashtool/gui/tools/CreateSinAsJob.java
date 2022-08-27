package org.flashtool.gui.tools;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.system.OS;
import org.flashtool.system.ProcessBuilderWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateSinAsJob extends Job {

	boolean canceled = false;
	String file;
	String partition;
	String spareinfo;
	
	public CreateSinAsJob(String name) {
		super(name);
	}
	
	public void setFile(String f) {
		file=f;
	}
	
	public void setPartition(String part) {
		partition = part;
	}
	
	public void setSpare(String spare) {
		spareinfo = spare;
	}
    
	protected IStatus run(IProgressMonitor monitor) {
        if (file != null) {
			try {
				log.info("Generating sin file to "+file+".sin");
				log.info("Please wait");
				if (spareinfo.equals("09")) {
					ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {OS.getPathBin2Sin(),file, partition, "0x"+spareinfo,"0x20000"},false);
				}
				else {
					ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {OS.getPathBin2Sin(),file, partition, "0x"+spareinfo,"0x20000", "0x1000"},false);
				}
				log.info("Sin file creation finished");
	    		return Status.OK_STATUS;
			}
			catch (Exception ex) {
				log.error(ex.getMessage());
	    		return Status.CANCEL_STATUS;
			}
        }
        else {
        	log.info("Create SIN As canceled (no selected data input");
    		return Status.CANCEL_STATUS;
        }
    }
}
