package org.system;

import java.io.File;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;

public class ULCodeFile extends TextFile {
	
	private static Logger logger = Logger.getLogger(ULCodeFile.class);

	public ULCodeFile(String serial) {
		super(OS.getFolderMyDevices()+File.separator+serial+File.separator+"ulcode.txt", "ISO-8859-1");
	}

	public String getULCode() {
		try {
			readLines();
			return (String)getLines().iterator().next();
		}
		catch (Exception e) {
			return "";
		}
	}

	public void setCode(String code) {
		try {
			open(true);
			write(code);
			close();
			logger.info("Unlock code saved to "+new File(fFileName).getAbsolutePath());
		}
		catch (Exception e) {
			logger.error("Error saving unlock code : " + e.getMessage());
		}
	}

}