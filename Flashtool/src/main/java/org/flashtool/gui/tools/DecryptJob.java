package org.flashtool.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.flashsystem.SeusSinTool;
import org.flashtool.gui.TARestore;
import org.flashtool.system.OS;
import org.flashtool.xmlcombiner.XmlCombiner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DecryptJob extends Job {

	boolean canceled = false;
	Vector files;
	static final Logger logger = LogManager.getLogger(DecryptJob.class);
	
	public DecryptJob(String name) {
		super(name);
	}
	
	public void setFiles(Vector f) {
		files=f;
	}
	
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		OS.deleteDirectory(new File(((File)files.get(0)).getParent()+File.separator+"decrypted"));
    		String decryptfolder="";
			for (int i=0;i<files.size();i++) {
				File f = (File)files.get(i);
				decryptfolder=f.getParentFile().getAbsolutePath()+File.separator+"decrypted";
				log.info("Decrypting "+f.getName());
				try {
					SeusSinTool.decryptAndExtract(f.getAbsolutePath());
				}
				catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			File update = new File(decryptfolder+File.separator+"update.xml");
			File update1 = new File(decryptfolder+File.separator+"update1.xml");
			File newupdate = new File(decryptfolder+File.separator+"update2.xml");
			if (update.exists() && update1.exists()) {
				XmlCombiner combiner = new XmlCombiner();
				FileInputStream fi1 = new FileInputStream(update);
				combiner.combine(fi1);
				FileInputStream fi2 = new FileInputStream(update1);
				combiner.combine(fi2);
				FileOutputStream fo = new FileOutputStream(newupdate);
				combiner.buildDocument(fo);
				fi1.close();
				fi2.close();
				fo.close();
				update.delete();
				update1.delete();
				newupdate.renameTo(update);
			}
			log.info("Decryption finished");
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
}
