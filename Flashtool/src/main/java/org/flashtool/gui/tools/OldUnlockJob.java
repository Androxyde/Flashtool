package org.flashtool.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OldUnlockJob extends Job {

	boolean canceled = false;
	String blstatus = "";
	String phonecert = "";
	String platform = "";
	static final Logger logger = LogManager.getLogger(OldUnlockJob.class);
	
	public void setStatus(String status) {
		blstatus = status;
	}

	public void setPhoneCert(String cert) {
		phonecert = cert;
	}

	public void setPlatform(String pf) {
		platform = pf;
	}

	public String getBLStatus() {
		return blstatus;
	}

	public String getPhoneCert() {
		return phonecert;
	}

	public String getPlatform() {
		return platform;
	}

	public OldUnlockJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try   {
			DeviceEntry ent = new DeviceEntry(platform);
			if (!new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"cert.properties").exists())
				throw new Exception("cert.properties is missing");
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"cert.properties")));
			} catch (Exception ex) {}
			String bootwrite = "";
			Enumeration<Object> e = p.keys();
			while (e.hasMoreElements()) {
				String key = (String)e.nextElement();
				if (p.getProperty(key).equals(phonecert)) {
					bootwrite = key;
					break;
				}
			}
			if (bootwrite.length()==0) throw new Exception("Phone certificate not identified");
			bootwrite = "bootwrite_"+bootwrite+"SL";
			if (!new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+bootwrite).exists())
				throw new Exception(bootwrite+" is missing");			
			if (!new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"fixPart").exists())
				throw new Exception("fixPart is missing");
			if (!new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"mapper_2.6.29.ko").exists())
				throw new Exception("mapper_2.6.29.ko is missing");
			if (platform.equals("X10"))
				if (!new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"mapper_2.6.29-00054-g5f01537.ko").exists())
					throw new Exception("mapper_2.6.29-00054-g5f01537.ko is missing");
			logger.info("Waiting for device to reboot");
			Devices.waitForReboot(false);
			if (!Devices.getCurrent().getKernelVersion().equals("2.6.29-00054-g5f01537") && !Devices.getCurrent().getKernelVersion().equals("2.6.29"))
				throw new Exception("Kernel does not match a compatible one");
			if (!Devices.getCurrent().hasRoot())
				throw new Exception("Device must be rooted first");
			String mapper = "mapper_"+Devices.getCurrent().getKernelVersion()+".ko";
			AdbUtility.push(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+"fixPart", GlobalConfig.getProperty("deviceworkdir"));
			FTShell fixpart = new FTShell(new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"shells"+File.separator+"runfixPart"));
			String output = fixpart.runRoot();
			if (!output.contains("success"))
				throw new Exception("Error applying fixpart: "+output);
			logger.info("Successfully applied fixPart. Rebooting");
			Devices.getCurrent().reboot();
			Devices.waitForReboot(false);
			AdbUtility.push(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+mapper, GlobalConfig.getProperty("deviceworkdir"));
			AdbUtility.push(ent.getDeviceDir()+File.separator+"blu"+File.separator+"files"+File.separator+bootwrite, GlobalConfig.getProperty("deviceworkdir"));
			FTShell runbootwrite = new FTShell(new File(ent.getDeviceDir()+File.separator+"blu"+File.separator+"shells"+File.separator+"runbootwrite"));
			runbootwrite.setProperty("KVER", Devices.getCurrent().getKernelVersion());
			runbootwrite.setProperty("BOOTWRITEBIN", bootwrite);
			output = runbootwrite.runRoot();
			if (!output.contains("success"))
				throw new Exception("Error applying fixpart: "+output);
			logger.info("Successfully applied bootwrite. Bootloader should be unlocked. Rebooting");
			Devices.getCurrent().reboot();
			return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		 logger.error(e.getMessage());
    		return Status.CANCEL_STATUS;
    	}
    }

}
