package org.flashtool.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.swt.SWT;
import org.flashtool.gui.tools.MsgBox;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.util.BytesUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FTDEntry {

	File ftdfile;
	Properties entry = new Properties();
	static final Logger logger = LogManager.getLogger(FTDEntry.class);

	public FTDEntry(String id) throws FileNotFoundException, IOException {
		ftdfile = new File(OS.getFolderMyDevices()+File.separator+id+".ftd");
		if (!ftdfile.exists()) throw new FileNotFoundException();
    	JarFile jar = new JarFile(ftdfile);
    	Enumeration<JarEntry> e = jar.entries();
    	while (e.hasMoreElements()) {
    		JarEntry j = e.nextElement();
    		if (j.getName().contains(id.toUpperCase()+".properties"))
    			entry.load(jar.getInputStream(j));
    	}    	
    	jar.close();
	}

	public String getId() {
		return entry.getProperty("internalname");
	}

	public String getName() {
		return entry.getProperty("realname");
	}

	public boolean explode() throws Exception {
    	String destDir = OS.getFolderMyDevices();
    	int reply = SWT.YES;
    	if (new  File(destDir).exists()) 
    		reply = MsgBox.question("This device already exists. Overwrite it ?");
    	if (reply == SWT.YES) {
	    	new File(destDir+File.separator+getId()).mkdir();
	    	JarFile jar = new JarFile(ftdfile);
	    	boolean alldirs=false;
	    	Enumeration<JarEntry> e;
	    	while (!alldirs) {
		    	e = jar.entries();
		    	alldirs=true;
		    	while (e.hasMoreElements()) {
		    	    JarEntry file = e.nextElement();
		    	    File f = new File(destDir + File.separator + file.getName());
		    	    if (file.isDirectory()) { // if its a directory, create it
		    	    	if (!f.exists())
		    	    		if (!f.mkdir()) alldirs=false;
		    	    }
		    	}
	    	}
	    	e = jar.entries();
	    	while (e.hasMoreElements()) {
	    	    JarEntry file = (JarEntry) e.nextElement();
	    	    File f = new File(destDir + File.separator + file.getName());
	    	    if (!file.isDirectory()) { // if its a directory, create it
		    	    InputStream is = jar.getInputStream(file); // get the input stream
		    	    FileOutputStream fos = new FileOutputStream(f);
		    	    byte[] array = new byte[10240];
		    	    while (is.available() > 0) {  // write contents of 'is' to 'fos'
		    	    	int read = is.read(array);
		    	        fos.write((read<array.length)?BytesUtil.getReply(array, read):array);
		    	    }
		    	    fos.close();
		    	    is.close();
	    	    }
	    	}
	    	jar.close();
	    	return true;
    	}
    	else {
    		logger.info("Import canceled");
    		return false;
    	}
    }

}