package flashsystem;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import gui.About;
import gui.MainSWT;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;
import org.logger.MyLogger;
import org.system.AdbPhoneThread;
import org.system.DeviceChangedListener;
import org.system.DeviceEntry;
import org.system.DeviceProperties;
import org.system.Devices;
import org.system.GlobalConfig;
import org.system.OS;
import org.system.FTShell;
import org.system.StatusEvent;
import org.system.StatusListener;


public class FlasherConsole {
	
	private static AdbPhoneThread phoneWatchdog;
	private static String fsep = OS.getFileSeparator();
	private static Logger logger = Logger.getLogger(FlasherConsole.class);
	
	public static void init(boolean withadb) {
			logger.info("Flashtool "+About.getVersion());
			MainSWT.guimode=false;
			if (withadb) {
			StatusListener phoneStatus = new StatusListener() {
				public void statusChanged(StatusEvent e) {
					if (!e.isDriverOk()) {
						logger.error("Drivers need to be installed for connected device.");
						logger.error("You can find them in the drivers folder of Flashtool.");
					}
					else {
						if (e.getNew().equals("adb")) {
							logger.info("Device connected with USB debugging on");
							logger.debug("Device connected, continuing with identification");
							doIdent();
						}
						if (e.getNew().equals("none")) {
							logger.info("Device disconnected");
						}
						if (e.getNew().equals("flash")) {
							logger.info("Device connected in flash mode");
						}
						if (e.getNew().equals("fastboot")) {
							logger.info("Device connected in fastboot mode");
						}
						if (e.getNew().equals("normal")) {
							logger.info("Device connected with USB debugging off");
							logger.info("For 2011 devices line, be sure you are not in MTP mode");
						}
					}
				}
			};
			phoneWatchdog = new AdbPhoneThread();
			phoneWatchdog.start();
			phoneWatchdog.addStatusListener(phoneStatus);
			}
			else DeviceChangedListener.start();
	}

	public static void exit() {
		DeviceChangedListener.stop();
		if (phoneWatchdog!=null) {
			phoneWatchdog.done();
			try {
				phoneWatchdog.join();
			}
			catch (Exception e) {
			}
		}
		MyLogger.writeFile();
		System.exit(0);
	}
	
	public static void doRoot() {
		Devices.waitForReboot(false);
		if (Devices.getCurrent().getVersion().contains("2.3")) {
			if (!Devices.getCurrent().hasRoot())
				doRootzergRush();
			else logger.error("Your device is already rooted");
		}
		else 
			if (!Devices.getCurrent().hasRoot())
				doRootpsneuter();
			else logger.error("Your device is already rooted");
		exit();
	}

	public static void doRootzergRush() {
				try {
					AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
					FTShell shell = new FTShell("busyhelper");
					shell.run(true);
					AdbUtility.push(new File("."+fsep+"custom"+fsep+"root"+fsep+"zergrush.tar.uue").getAbsolutePath(),GlobalConfig.getProperty("deviceworkdir"));
					shell = new FTShell("rootit");
					logger.info("Running part1 of Root Exploit, please wait");
					shell.run(true);
					Devices.waitForReboot(true);
					logger.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					logger.info("Finished!.");
					logger.info("Root should be available after reboot!");		
				}
				catch (Exception e) {
					logger.error(e.getMessage());
				}
	}

	public static void doRootpsneuter() {
				try {
					AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
					FTShell shell = new FTShell("busyhelper");
					shell.run(true);
					AdbUtility.push("."+fsep+"custom"+fsep+"root"+fsep+"psneuter.tar.uue",GlobalConfig.getProperty("deviceworkdir"));
					shell = new FTShell("rootit");
					logger.info("Running part1 of Root Exploit, please wait");
					shell.run(false);
					Devices.waitForReboot(true);
					logger.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					logger.info("Finished!.");
					logger.info("Root should be available after reboot!");		
				}
				catch (Exception e) {
					logger.error(e.getMessage());
				}
	}
	
	public static void doGetIMEI() throws Exception {
		X10flash f=null;
		try {
			Bundle b = new Bundle();
			b.setSimulate(false);
			f = new X10flash(b,null);
			logger.info("Please connect your phone in flash mode");
			while (!f.deviceFound());
			f.openDevice(false);
			logger.info("IMEI : "+f.getPhoneProperty("IMEI"));
			f.closeDevice();
			exit();
		}
		catch (Exception e) {
			if (f!=null) f.closeDevice();
			throw e;
		}		
	}
	
	public static void doExtract(String file) {
		try {
			SinFile sin = new SinFile(file);
			sin.dumpImage();
		}
		catch (Exception e) {
		}
	}

	public static void doFlash(String file,boolean wipedata,boolean wipecache,boolean excludebb,boolean excludekrnl, boolean excludesys) throws Exception {
		X10flash f=null;
		try {
			File bf = new File(file);
			if (!bf.exists()) {
				logger.error("File "+bf.getAbsolutePath()+" does not exist");
				exit();
			}
			logger.info("Choosed "+bf.getAbsolutePath());
			Bundle b = new Bundle(bf.getAbsolutePath(),Bundle.JARTYPE);
			b.setSimulate(false);
			b.getMeta().setCategEnabled("DATA", wipedata);
			b.getMeta().setCategEnabled("CACHE", wipecache);
			b.getMeta().setCategEnabled("BASEBAND", excludebb);
			b.getMeta().setCategEnabled("SYSTEM", excludesys);
			b.getMeta().setCategEnabled("KERNEL", excludekrnl);
			logger.info("Preparing files for flashing");
			b.open();
			f = new X10flash(b,null);
			logger.info("Please connect your phone in flash mode");
			while (!f.deviceFound());
			f.openDevice(false);
			f.flashDevice();
			b.close();
			exit();
		}
		catch (Exception e) {
			if (f!=null) f.closeDevice();
			throw e;
		}		
	}

	public static void doIdent() {
    		Enumeration<Object> e = Devices.listDevices(true);
    		if (!e.hasMoreElements()) {
    			logger.error("No device is registered in Flashtool.");
    			logger.error("You can only flash devices.");
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
    				logger.info("Connected device : " + Devices.getCurrent().getId());
    		}
    		else {
    			logger.error("Cannot identify your device.");
        		logger.error("You can only flash devices.");
    		}
    		if (found) {
    			if (!Devices.isWaitingForReboot()) {
    				logger.info("Installed version of busybox : " + Devices.getCurrent().getInstalledBusyboxVersion(false));
    				logger.info("Android version : "+Devices.getCurrent().getVersion()+" / kernel version : "+Devices.getCurrent().getKernelVersion());
    			}
    			if (Devices.getCurrent().isRecovery()) {
    				logger.info("Phone in recovery mode");
    				if (!Devices.isWaitingForReboot())
    					logger.info("Root Access Allowed");
    			}
    			else {
    				boolean hasSU = Devices.getCurrent().hasSU();
    				if (hasSU) {
    					boolean hasRoot = Devices.getCurrent().hasRoot();
    					if (hasRoot)
    						if (!Devices.isWaitingForReboot())
    							logger.info("Root Access Allowed");
    				}
    			}
    			logger.debug("Stop waiting for device");
    			if (Devices.isWaitingForReboot())
    				Devices.stopWaitForReboot();
    			logger.debug("End of identification");
    		}
	}

}