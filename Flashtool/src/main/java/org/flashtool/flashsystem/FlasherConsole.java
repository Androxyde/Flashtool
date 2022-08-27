package org.flashtool.flashsystem;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import org.flashtool.gui.About;
import org.flashtool.gui.MainSWT;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.log.MyLogger;
import org.flashtool.parsers.sin.SinFile;
import org.flashtool.system.DeviceChangedListener;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.DeviceProperties;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;
import org.flashtool.system.OS;
import org.flashtool.system.StatusEvent;
import org.flashtool.system.StatusListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlasherConsole {
	
	private static String fsep = OS.getFileSeparator();
	
	public static void init(boolean withadb) {
			log.info("Flashtool "+About.getVersion());
			MainSWT.guimode=false;
			StatusListener phoneStatus = new StatusListener() {
				public void statusChanged(StatusEvent e) {
					if (!e.isDriverOk()) {
						log.error("Drivers need to be installed for connected device.");
						log.error("You can find them in the drivers folder of Flashtool.");
					}
					else {
						if (e.getNew().equals("adb")) {
							log.info("Device connected with USB debugging on");
							log.debug("Device connected, continuing with identification");
							doIdent();
						}
						if (e.getNew().equals("none")) {
							log.info("Device disconnected");
						}
						if (e.getNew().equals("flash")) {
							log.info("Device connected in flash mode");
						}
						if (e.getNew().equals("fastboot")) {
							log.info("Device connected in fastboot mode");
						}
						if (e.getNew().equals("normal")) {
							log.info("Device connected with USB debugging off");
							log.info("For 2011 devices line, be sure you are not in MTP mode");
						}
					}
				}
			};
			DeviceChangedListener.starts(null);
	}

	public static void exit() {
		DeviceChangedListener.stop();
		Mylog.writeFile();
		System.exit(0);
	}
	
	public static void doRoot() {
		Devices.waitForReboot(false);
		if (Devices.getCurrent().getVersion().contains("2.3")) {
			if (!Devices.getCurrent().hasRoot())
				doRootzergRush();
			else log.error("Your device is already rooted");
		}
		else 
			if (!Devices.getCurrent().hasRoot())
				doRootpsneuter();
			else log.error("Your device is already rooted");
		exit();
	}

	public static void doRootzergRush() {
				try {
					AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
					FTShell shell = new FTShell("busyhelper");
					shell.run(true);
					AdbUtility.push(new File("."+fsep+"custom"+fsep+"root"+fsep+"zergrush.tar.uue").getAbsolutePath(),GlobalConfig.getProperty("deviceworkdir"));
					shell = new FTShell("rootit");
					log.info("Running part1 of Root Exploit, please wait");
					shell.run(true);
					Devices.waitForReboot(true);
					log.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					log.info("Finished!.");
					log.info("Root should be available after reboot!");		
				}
				catch (Exception e) {
					log.error(e.getMessage());
				}
	}

	public static void doRootpsneuter() {
				try {
					AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
					FTShell shell = new FTShell("busyhelper");
					shell.run(true);
					AdbUtility.push("."+fsep+"custom"+fsep+"root"+fsep+"psneuter.tar.uue",GlobalConfig.getProperty("deviceworkdir"));
					shell = new FTShell("rootit");
					log.info("Running part1 of Root Exploit, please wait");
					shell.run(false);
					Devices.waitForReboot(true);
					log.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					log.info("Finished!.");
					log.info("Root should be available after reboot!");		
				}
				catch (Exception e) {
					log.error(e.getMessage());
				}
	}
	
	public static void doGetIMEI() throws Exception {
		Flasher f=null;
		try {
			Bundle b = new Bundle();
			b.setSimulate(false);
			f = new S1Flasher(b,null);
			log.info("Please connect your phone in flash mode");
			while (!f.flashmode());
			f.open(false);
			log.info("IMEI : "+f.getPhoneProperty("IMEI"));
			f.close();
			exit();
		}
		catch (Exception e) {
			if (f!=null) f.close();
			throw e;
		}		
	}
	
	public static void doExtract(String file) {
		try {
			SinFile sin = new SinFile(new File(file));
			sin.dumpImage();
		}
		catch (Exception e) {
		}
	}

	public static void doFlash(String file,boolean wipedata,boolean wipecache,boolean excludebb,boolean excludekrnl, boolean excludesys) throws Exception {
		Flasher f=null;
		try {
			File bf = new File(file);
			if (!bf.exists()) {
				log.error("File "+bf.getAbsolutePath()+" does not exist");
				exit();
			}
			log.info("Choosed "+bf.getAbsolutePath());
			Bundle b = new Bundle(bf.getAbsolutePath(),Bundle.JARTYPE);
			b.setSimulate(false);
			b.getMeta().setCategEnabled("DATA", wipedata);
			b.getMeta().setCategEnabled("CACHE", wipecache);
			b.getMeta().setCategEnabled("BASEBAND", excludebb);
			b.getMeta().setCategEnabled("SYSTEM", excludesys);
			b.getMeta().setCategEnabled("KERNEL", excludekrnl);
			log.info("Preparing files for flashing");
			b.open();
			f = new S1Flasher(b,null);
			log.info("Please connect your phone in flash mode");
			while (!f.flashmode());
			f.open(false);
			f.flash();
			b.close();
			exit();
		}
		catch (Exception e) {
			if (f!=null) f.close();
			throw e;
		}		
	}

	public static void doIdent() {
    		Enumeration<Object> e = Devices.listDevices(true);
    		if (!e.hasMoreElements()) {
    			log.error("No device is registered in Flashtool.");
    			log.error("You can only flash devices.");
    			return;
    		}
    		boolean found = false;
    		Properties founditems = new Properties();
    		founditems.clear();
    		Properties buildprop = new Properties();
    		buildprop.clear();
    		while (e.hasMoreElements()) {
    			DeviceEntry current = Devices.getDevice((String)e.nextElement());
    			String prop = current.getBuildProp();
    			if (!buildprop.containsKey(prop)) {
    				String readprop = DeviceProperties.getProperty(prop);
    				buildprop.setProperty(prop,readprop);
    			}
    			Iterator<String> i = current.getRecognitionList().iterator();
    			String localdev = buildprop.getProperty(prop);
    			while (i.hasNext()) {
    				String pattern = i.next().toUpperCase();
    				if (localdev.toUpperCase().contains(pattern)) {
    					founditems.put(current.getId(), current.getName());
    				}
    			}
    		}
    		if (founditems.size()==1) {
    			found = true;
    			Devices.setCurrent((String)founditems.keys().nextElement());
    			if (!Devices.isWaitingForReboot())
    				log.info("Connected device : " + Devices.getCurrent().getId());
    		}
    		else {
    			log.error("Cannot identify your device.");
        		log.error("You can only flash devices.");
    		}
    		if (found) {
    			if (!Devices.isWaitingForReboot()) {
    				log.info("Installed version of busybox : " + Devices.getCurrent().getInstalledBusyboxVersion(false));
    				log.info("Android version : "+Devices.getCurrent().getVersion()+" / kernel version : "+Devices.getCurrent().getKernelVersion());
    			}
    			if (Devices.getCurrent().isRecovery()) {
    				log.info("Phone in recovery mode");
    				if (!Devices.isWaitingForReboot())
    					log.info("Root Access Allowed");
    			}
    			else {
    				boolean hasSU = Devices.getCurrent().hasSU();
    				if (hasSU) {
    					boolean hasRoot = Devices.getCurrent().hasRoot();
    					if (hasRoot)
    						if (!Devices.isWaitingForReboot())
    							log.info("Root Access Allowed");
    				}
    			}
    			log.debug("Stop waiting for device");
    			if (Devices.isWaitingForReboot())
    				Devices.stopWaitForReboot();
    			log.debug("End of identification");
    		}
	}

}