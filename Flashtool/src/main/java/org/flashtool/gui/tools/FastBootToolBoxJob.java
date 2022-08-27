package org.flashtool.gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.jna.adb.FastbootUtility;
import org.flashtool.system.Devices;
import org.flashtool.system.RunOutputs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastBootToolBoxJob extends Job {

	boolean canceled = false;
	String _action = "";
	String _image = "";
	static final Logger logger = LogManager.getLogger(FastBootToolBoxJob.class);
	
	public FastBootToolBoxJob(String name) {
		super(name);
	}

	public void setAction(String action) {
		_action = action;
	}
    
	protected IStatus run(IProgressMonitor monitor) {
    	try {
    		if (_action.equals("doRebootFastbootViaAdb"))
    			doRebootFastbootViaAdb();
    		if (_action.equals("doCheckDeviceStatus"))
    			doCheckDeviceStatus();
    		if (_action.equals("doGetConnectedDeviceInfo"))
    			doGetConnectedDeviceInfo();
    		if (_action.equals("doGetFastbootVerInfo"))
    			doGetFastbootVerInfo();
    		if (_action.equals("doRebootBackIntoFastbootMode"))
    			doRebootBackIntoFastbootMode();
    		if (_action.equals("doFastbootReboot"))
    			doFastbootReboot();
    		if (_action.equals("doHotbootKernel"))
    			doHotbootKernel();
    		if (_action.equals("doFlashKernel"))
    			doFlashKernel();
    		if (_action.equals("doFlashSystem"))
    			doFlashSystem();

    		return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
	
	protected void doRebootFastbootViaAdb() {
		if (Devices.HasOneAdbConnected()) {
			if (Devices.getCurrent().canFastboot()) {
				log.info("Please wait device is rebooting into fastboot mode (via ADB)");
				try {
					FastbootUtility.adbRebootFastboot();
					log.info("Device will soon enter fastboot mode");
				}
				catch (Exception e1) {
					log.error(e1.getMessage());
				}
			}
			else
				log.error("This action can be done only if the connected phone has fastboot mode");
		}
		else
			log.error("This action needs a connected device in ADB mode");
	}

	public void doCheckDeviceStatus() {
		String deviceStatus="NOT FOUND";
		Devices.HasOneFastbootConnected();
		if (AdbUtility.isConnected()){
			deviceStatus="ADB mode";
		}
		else
			if (FastbootUtility.getDevices().hasMoreElements())
				deviceStatus="FASTBOOT mode";	
		log.info("Device Status: " + deviceStatus);
	}

	public void doGetConnectedDeviceInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		log.info("Fetching connected device info");
		try {
			RunOutputs outputsRun = FastbootUtility.getDeviceInfo();
			log.info("Connected device info: [ " + outputsRun.getStdOut().split("fastboot")[0].trim() + " ]");
		}
		catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}

	public void doGetFastbootVerInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		log.info("Fetching fastboot version info from connected device");
		try {
			RunOutputs outputsRun = FastbootUtility.getFastbootVerInfo();
			log.info("FASTBOOT version info: [ " + outputsRun.getStdErr().split("\n")[0].trim() + " ]");
			
		}
		catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}

	public void doRebootBackIntoFastbootMode(){
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		log.info("Please wait device is rebooting into fastboot mode (via Fastboot)");
		try {
			FastbootUtility.rebootFastboot();
			log.info("Device will soon reboot back into fastboot mode");
		}
		catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}

	public void doFastbootReboot(){
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		log.info("Device will now exit fastboot mode and start booting into system");
		try {
			FastbootUtility.rebootDevice();
		}
		catch (Exception e1) {
			log.error(e1.getMessage());
		}
	}

	public void setImage(String image) {
		_image = image;
	}
	
	public void doHotbootKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			log.error("no kernel (boot.img or kernel.sin) selected!");
		} 
		else {						
			log.info("Selected kernel (boot.img or kernel.sin): " + _image);
			// this wont wait for reply and will move on to next command
			log.info("HotBooting selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.hotBoot(_image);
				log.info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				if (!outputsRun.getStdErr().trim().contains("FAILED"))
					log.info("Device should now start booting with this kernel");
			}
			catch (Exception e1) {
				log.error(e1.getMessage());
			}
			_image="";
		}
	}

	public void doFlashKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			log.error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			log.info("Selected kernel (boot.img or kernel.sin): " + _image);
			log.info("Flashing selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.flashBoot(_image);
				log.info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				log.info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
		_image="";
	}

	public void doFlashSystem() {
		if (!Devices.HasOneFastbootConnected()) {
			log.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			log.error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			log.info("Selected system (system.img or system.sin): " + _image);
			log.info("Flashing selected system");
			try {
				RunOutputs outputsRun = FastbootUtility.flashSystem(_image);
				log.info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
		_image="";
	}

}