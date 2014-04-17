package org.system;

import java.io.File;

import org.logger.MyLogger;

public class ULCodeFile extends TextFile {


	public ULCodeFile(String serial) {
		super(OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+serial+File.separator+"ulcode.txt", "ISO-8859-1");
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
			MyLogger.getLogger().info("Unlock code saved to "+new File(fFileName).getAbsolutePath());
		}
		catch (Exception e) {
			MyLogger.getLogger().error("Error saving unlock code : " + e.getMessage());
		}
	}

}