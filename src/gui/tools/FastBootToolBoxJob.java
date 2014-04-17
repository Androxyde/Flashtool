package gui.tools;

import org.adb.AdbUtility;
import org.adb.FastbootUtility;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.MyLogger;
import org.system.Devices;
import org.system.RunOutputs;

public class FastBootToolBoxJob extends Job {

	boolean canceled = false;
	String _action = "";
	String _image = "";

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
				MyLogger.getLogger().info("Please wait device is rebooting into fastboot mode (via ADB)");
				try {
					FastbootUtility.adbRebootFastboot();
					MyLogger.getLogger().info("Device will soon enter fastboot mode");
				}
				catch (Exception e1) {
					MyLogger.getLogger().error(e1.getMessage());
				}
			}
			else
				MyLogger.getLogger().error("This action can be done only if the connected phone has fastboot mode");
		}
		else
			MyLogger.getLogger().error("This action needs a connected device in ADB mode");
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
		MyLogger.getLogger().info("Device Status: " + deviceStatus);
	}

	public void doGetConnectedDeviceInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		MyLogger.getLogger().info("Fetching connected device info");
		try {
			RunOutputs outputsRun = FastbootUtility.getDeviceInfo();
			MyLogger.getLogger().info("Connected device info: [ " + outputsRun.getStdOut().split("fastboot")[0].trim() + " ]");
		}
		catch (Exception e1) {
			MyLogger.getLogger().error(e1.getMessage());
		}
	}

	public void doGetFastbootVerInfo(){
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		MyLogger.getLogger().info("Fetching fastboot version info from connected device");
		try {
			RunOutputs outputsRun = FastbootUtility.getFastbootVerInfo();
			MyLogger.getLogger().info("FASTBOOT version info: [ " + outputsRun.getStdErr().split("\n")[0].trim() + " ]");
			
		}
		catch (Exception e1) {
			MyLogger.getLogger().error(e1.getMessage());
		}
	}

	public void doRebootBackIntoFastbootMode(){
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		MyLogger.getLogger().info("Please wait device is rebooting into fastboot mode (via Fastboot)");
		try {
			FastbootUtility.rebootFastboot();
			MyLogger.getLogger().info("Device will soon reboot back into fastboot mode");
		}
		catch (Exception e1) {
			MyLogger.getLogger().error(e1.getMessage());
		}
	}

	public void doFastbootReboot(){
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		MyLogger.getLogger().info("Device will now exit fastboot mode and start booting into system");
		try {
			FastbootUtility.rebootDevice();
		}
		catch (Exception e1) {
			MyLogger.getLogger().error(e1.getMessage());
		}
	}

	public void setImage(String image) {
		_image = image;
	}
	
	public void doHotbootKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			MyLogger.getLogger().error("no kernel (boot.img or kernel.sin) selected!");
		} 
		else {						
			MyLogger.getLogger().info("Selected kernel (boot.img or kernel.sin): " + _image);
			// this wont wait for reply and will move on to next command
			MyLogger.getLogger().info("HotBooting selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.hotBoot(_image);
				MyLogger.getLogger().info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				if (!outputsRun.getStdErr().trim().contains("FAILED"))
					MyLogger.getLogger().info("Device should now start booting with this kernel");
			}
			catch (Exception e1) {
				MyLogger.getLogger().error(e1.getMessage());
			}
			_image="";
		}
	}

	public void doFlashKernel() {
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			MyLogger.getLogger().error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			MyLogger.getLogger().info("Selected kernel (boot.img or kernel.sin): " + _image);
			MyLogger.getLogger().info("Flashing selected kernel");
			try {
				RunOutputs outputsRun = FastbootUtility.flashBoot(_image);
				MyLogger.getLogger().info("FASTBOOT Output: \n " + outputsRun.getStdErr().trim() + "\n");
				MyLogger.getLogger().info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				MyLogger.getLogger().error(e1.getMessage());
			}
		}
		_image="";
	}

	public void doFlashSystem() {
		if (!Devices.HasOneFastbootConnected()) {
			MyLogger.getLogger().error("This action can only be done in fastboot mode");
			return;
		}
		if (_image.length()==0) {
			MyLogger.getLogger().error("no kernel (boot.img or kernel.sin) selected!");
		}
		else {						
			MyLogger.getLogger().info("Selected system (system.img or system.sin): " + _image);
			MyLogger.getLogger().info("Flashing selected system");
			try {
				RunOutputs outputsRun = FastbootUtility.flashSystem(_image);
				MyLogger.getLogger().info("Please check the log before rebooting into system");
			}
			catch (Exception e1) {
				MyLogger.getLogger().error(e1.getMessage());
			}
		}
		_image="";
	}

}