package org.flashtool.system;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.jna.adb.AdbUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ULCodeFile extends TextFile {
	
	static final Logger logger = LogManager.getLogger(ULCodeFile.class);

	public ULCodeFile(String serial) {
		super(OS.getFolderRegisteredDevices()+File.separator+serial+File.separator+"ulcode.txt", "ISO-8859-1");
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
			log.info("Unlock code saved to "+new File(fFileName).getAbsolutePath());
		}
		catch (Exception e) {
			log.error("Error saving unlock code : " + e.getMessage());
		}
	}

}