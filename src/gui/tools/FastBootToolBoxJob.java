package gui.tools;

import org.adb.AdbUtility;
import org.adb.FastbootUtility;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.system.Devices;
import org.system.RunOutputs;

public class FastBootToolBoxJob extends Job {

	boolean canceled = false;
	String _action = "";
	String _image = "";
	private static Logger logger = Logger.getLogger(FastBootToolBoxJob.class);
	
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
				logger.info("Please wait device is rebooting into fastboot mode (via ADB)");
				try {
					FastbootUtility.adbRebootFastboot();
					logger.info("Device will soon enter fastboot mode");
				}
				catch (Exception e1) {
					logger.error(e1.getMessage());
				}
			}
			else
				logger.error("This action can be done only if the connected phone has fastboot mode");
		}
		else
			logger.error("This action needs a connected device in ADB mode");
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
		logger.info("Device Status: " + deviceStatus);
	}

	public void doGetConnectedDeviceInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		logger.info("Fetching connected device info");
		try {
			RunOutputs outputsRun = FastbootUtility.getDeviceInfo();
			logger.info("Connected device info: [ " + outputsRun.getStdOut().split("fastboot")[0].trim() + " ]");
		}
		catch (Exception e1) {
			logger.error(e1.getMessage());
		}
	}

	public void doGetFastbootVerInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		logger.info("Fetching fastboot version info from connected device");
		try {
			RunOutputs outputsRun = FastbootUtility.getFastbootVerInfo();
			logger.info("FASTBOOT version info: [ " + outputsRun.getStdErr().split("\n")[0].trim() + " ]");
			
		}
		catch (Exception e1) {
			logger.error(e1.getMessage());
		}
	}

	public void doRebootBackIntoFastbootMode(){
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		logger.info("Please wait device is rebooting into fastboot mode (via Fastboot)");
		try {
			FastbootUtility.rebootFastboot();
			logger.info("Device will soon reboot back into fastboot mode");
		}
		catch (Exception e1) {
			logger.error(e1.getMessage());
		}
	}

	public void doFastbootReboot(){
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		logger.info("Device will now exit fastboot mode and start booting into system");
		try {
			FastbootUtility.rebootDevice();
		}
		catch (Exception e1) {
			logger.error(e1.getMessage());
		}
	}

	public void setImage(String image) {
		_image = image;
	}
	
	public void doHotbootKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			logger.error("no kernel (boot.img or kernel.sin) selected!");
		} 
		else {						
			logger.info("Selected kernel (boot.img or kernel.sin): " + _image);
			// this wont wait for reply and will move on to next command
			logger.info("HotBooting selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.hotBoot(_image);
				logger.info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				if (!outputsRun.getStdErr().trim().contains("FAILED"))
					logger.info("Device should now start booting with this kernel");
			}
			catch (Exception e1) {
				logger.error(e1.getMessage());
			}
			_image="";
		}
	}

	public void doFlashKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			logger.error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			logger.info("Selected kernel (boot.img or kernel.sin): " + _image);
			logger.info("Flashing selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.flashBoot(_image);
				logger.info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				logger.info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				logger.error(e1.getMessage());
			}
		}
		_image="";
	}

	public void doFlashSystem() {
		if (!Devices.HasOneFastbootConnected()) {
			logger.error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			logger.error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			logger.info("Selected system (system.img or system.sin): " + _image);
			logger.info("Flashing selected system");
			try {
				RunOutputs outputsRun = FastbootUtility.flashSystem(_image);
				logger.info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				logger.error(e1.getMessage());
			}
		}
		_image="";
	}

}