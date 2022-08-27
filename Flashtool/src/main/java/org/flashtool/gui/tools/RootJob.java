package org.flashtool.gui.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RootJob extends Job {

	boolean canceled = false;
	String _action = "";
	String pck = "";
	Shell _parent;
	static final Logger logger = LogManager.getLogger(RootJob.class);

	public RootJob(String name) {
		super(name);
	}


	public void setParentShell(Shell s) {
		_parent = s;
	}
	
	public void setAction(String action) {
		_action = action;
	}
    
	public void setRootPackage(String ppck) {
		if (ppck == null) pck="";
		else
			pck = ppck;
	}
	
	protected IStatus run(IProgressMonitor monitor) {
    	try {

    		if (_action.equals("doRootpsneuter"))
    			doRootpsneuter();
    		if (_action.equals("doRootzergRush"))
    			doRootzergRush();
    		if (_action.equals("doRootEmulator"))
    			doRootEmulator();
    		if (_action.equals("doRootAdbRestore"))
    			doRootAdbRestore();
    		if (_action.equals("doRootServiceMenu"))
    			doRootServiceMenu();
    		if (_action.equals("doRootRunRootShell"))
    			doRootRunRootShell();
    		if (_action.equals("doRootTowelroot"))
    			doRootTowelroot();
    		return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }
	
	public void doRootzergRush() {
		if (pck.length()>0) {
			try {
				OS.decrypt(new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"ZergRush"+File.separator+"zergrush.tar.uue.enc"));
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ZergRush"+File.separator+"zergrush.tar.uue",GlobalConfig.getProperty("deviceworkdir"));
				new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"ZergRush"+File.separator+"zergrush.tar.uue").delete();
				doPushRootFiles(pck,false);
				FTShell shell = new FTShell("rootit");
				logger.info("Running part1 of Root Exploit, please wait");
				shell.run(true);
				Devices.waitForReboot(true);
				if (AdbUtility.hasRootNative(true)) {
					logger.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					logger.info("Finished!.");
					logger.info("Root should be available after reboot!");
				}
				else {
					logger.error("The part1 exploit did not work");
					logger.info("Cleaning files");
					AdbUtility.run("rm /data/local/tmp/zergrush");
					AdbUtility.run("rm /data/local/tmp/busybox");
					AdbUtility.run("rm /data/local/tmp/Superuser.apk");
					AdbUtility.run("rm /data/local/tmp/su");
					AdbUtility.run("rm /data/local/tmp/rootit");
					AdbUtility.run("rm /data/local/tmp/rootit2");
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void doRootpsneuter() {
		try {
			if (pck.length()>0) {
				OS.decrypt(new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"PsNeuter"+File.separator+"psneuter.tar.uue.enc"));						
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"PsNeuter"+File.separator+"psneuter.tar.uue",GlobalConfig.getProperty("deviceworkdir"));
				new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"PsNeuter"+File.separator+"psneuter.tar.uue").delete();
				doPushRootFiles(pck,false);
				FTShell shell = new FTShell("rootit");
				logger.info("Running part1 of Root Exploit, please wait");
				shell.run(false);
				Devices.waitForReboot(true);
				if (AdbUtility.hasRootNative(true)) {
					logger.info("Running part2 of Root Exploit");
					shell = new FTShell("rootit2");
					shell.run(false);
					logger.info("Finished!.");
					logger.info("Root should be available after reboot!");		
				}
				else {
					logger.error("The part1 exploit did not work");
					logger.info("Cleaning files");
					AdbUtility.run("rm /data/local/tmp/psneuter");
					AdbUtility.run("rm /data/local/tmp/busybox");
					AdbUtility.run("rm /data/local/tmp/Superuser.apk");
					AdbUtility.run("rm /data/local/tmp/su");
					AdbUtility.run("rm /data/local/tmp/rootit");
					AdbUtility.run("rm /data/local/tmp/rootit2");
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void doRootEmulator() {
		try {
			if (pck.length()>0) {
				logger.info("Preparing first part of the hack");
				AdbUtility.run("cd /data/local && mkdir tmp");
				AdbUtility.run("cd /data/local/tmp/ && rm *");
				AdbUtility.run("mv /data/local/tmp /data/local/tmp.bak");
				AdbUtility.run("ln -s /data /data/local/tmp");
				logger.info("Rebooting device. Please wait");
				Devices.getCurrent().reboot();
				Devices.waitForReboot(false);
				logger.info("Preparing second part of the hack");
				AdbUtility.run("rm /data/local.prop");
				AdbUtility.run("echo \"ro.kernel.qemu=1\" > /data/local.prop");
				logger.info("Rebooting device. Please wait");
				Devices.getCurrent().reboot();
				Devices.waitForReboot(false);
				if (AdbUtility.hasRootNative(true)) {
					logger.info("Now you have root");
					logger.info("Remounting system r/w");
					AdbUtility.run("mount -o remount,rw /system");
					logger.info("Installing root package");
					doPushRootFiles(pck,true);
					logger.info("Cleaning hack");
					AdbUtility.run("rm /data/local.prop");
					AdbUtility.run("rm /data/local/tmp");
					AdbUtility.run("mv /data/local/tmp.bak /data/local/tmp");
					logger.info("Rebooting device. Please wait. Your device is now rooted");
					Devices.getCurrent().reboot();
				}
				else {
					AdbUtility.run("rm /data/local.prop");
					AdbUtility.run("rm /data/local/tmp");
					AdbUtility.run("mv /data/local/tmp.bak /data/local/tmp");
					logger.info("Hack did not work. Cleaning and rebooting");
					Devices.getCurrent().reboot();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doRootAdbRestore() {
		try {
			if (pck.length()>0) {
				doPushRootFiles(pck,false);
				String backuppackage = AdbUtility.run("pm list package -f com.sonyericsson.backuprestore");
				if (backuppackage.contains("backuprestore")) {
					AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"AdbRestore"+File.separator+"RootMe.tar", GlobalConfig.getProperty("deviceworkdir")+"/RootMe.tar");
					AdbUtility.run("mkdir /mnt/sdcard/.semc-fullbackup > /dev/null 2>&1");
					AdbUtility.run("rm -r /mnt/sdcard/.semc-fullbackup/RootMe* > /dev/null 2>&1");
					AdbUtility.run("cd /mnt/sdcard/.semc-fullbackup;/data/local/tmp/busybox tar xf /data/local/tmp/RootMe.tar");
					AdbUtility.run("am start com.sonyericsson.vendor.backuprestore/.ui.BackupActivity");
					logger.info("Now open your device and restore \"RootMe\" backup. Waiting ...");
				}
				else {
					AdbUtility.restore(OS.getFolderCustom()+File.separator+"root"+File.separator+"AdbRestore"+File.separator+"fakebackup.ab");
					logger.info("Please look at your device and click RESTORE!");
				}
				String delay = "60";
				logger.info("You have "+delay+" seconds to follow the restore advice");
				FTShell exploit = new FTShell("adbrestoreexploit");
				exploit.setProperty("DELAY", delay);
				String result = exploit.run(false);
				if (result.contains("Success")) {
					logger.info("Restore worked fine. Rebooting device. Please wait ...");
					Devices.getCurrent().reboot();
					Devices.waitForReboot(false);
					if (AdbUtility.hasRootNative(true)) {
						logger.info("Root achieved. Installing root files. Device will reboot. Please wait.");
						doInstallRootFiles();
						Devices.waitForReboot(false);
						logger.info("Cleaning hack files");
						AdbUtility.run("rm /data/local/tmp/busybox;rm -r /mnt/sdcard/.semc-fullbackup/RootMe;rm /data/local/tmp/RootMe.tar;rm /data/local/tmp/su;rm /data/local/tmp/Superuser.apk;rm /data/local/tmp/adbrestoreexploit");
						logger.info("Finished.");
					}
					else {
						logger.info("Root hack did not work.");
						logger.info("Cleaning hack files");
						AdbUtility.run("rm /data/local/tmp/busybox;rm -r /mnt/sdcard/.semc-fullbackup/RootMe;rm /data/local/tmp/RootMe.tar;rm /data/local/tmp/su;rm /data/local/tmp/Superuser.apk;rm /data/local/tmp/adbrestoreexploit");
					}
					logger.info("Rebooting device. Please wait.");
					Devices.getCurrent().reboot();
				}
				else {
					logger.info("Root hack did not work. Cleaning hack files");
					AdbUtility.run("rm /data/local/tmp/busybox;rm -r /mnt/sdcard/.semc-fullbackup/RootMe;rm /data/local/tmp/RootMe.tar;rm /data/local/tmp/su;rm /data/local/tmp/Superuser.apk;rm /data/local/tmp/adbrestoreexploit");
				}
			}
			else {
				logger.info("Canceled");
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void doRootServiceMenu() {
		try {
			if (pck.length()>0) {
				doPushRootFiles(pck,false);
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"onload.sh", "/data/local/tmp/");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"getroot.sh", "/data/local/tmp/");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"install-recovery.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/onload.sh");
				AdbUtility.run("chmod 755 /data/local/tmp/getroot.sh");
				String semcpath = "";
				if (AdbUtility.exists("/mnt/ext_card/default-capability.xml")) 
					semcpath = "/mnt/ext_card/.semc-fullbackup";
				else 
					semcpath = "/mnt/ext_card/.semc-fullbackup";
				String backuppackage = AdbUtility.run("pm list package -f com.sonyericsson.backuprestore");
				if (backuppackage.contains("backuprestore")) {
					AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"RootMe.tar", GlobalConfig.getProperty("deviceworkdir")+"/RootMe.tar");
					AdbUtility.run("mkdir "+semcpath+" > /dev/null 2>&1");
					AdbUtility.run("rm -r "+semcpath+"/RootMe* > /dev/null 2>&1");
					AdbUtility.run("cd "+semcpath+" && /data/local/tmp/busybox tar xf /data/local/tmp/RootMe.tar");
					AdbUtility.run("am start com.sonyericsson.vendor.backuprestore/.ui.BackupActivity");
					AdbUtility.run("am start com.sonyericsson.vendor.backuprestore/.ui.phone.PhoneMainActivity");
					WidgetTask.openOKBox(_parent, "Please look at your device and goto restore tab.\nChoose RootMe backup.\nRestore and Press OK when done!");
					AdbUtility.run("rm -r "+semcpath+"/RootMe* > /dev/null 2>&1");
				}
				else {
					AdbUtility.restore(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"usbux.ab");
					WidgetTask.openOKBox(_parent, "Please look at your device and click RESTORE. Press OK when done!");
				}				
				AdbUtility.run("am start -a android.intent.action.MAIN -n com.sonyericsson.android.servicemenu/.ServiceMainMenu");
				logger.info("Look at your phone. Choose Service Test, then Display.");
				logger.info("Waiting for uevent_helper rw");
				AdbUtility.run("while : ; do [ -w /sys/kernel/uevent_helper ] && exit; done");
				logger.info("Waiting for rooted shell");
				AdbUtility.run("echo /data/local/tmp/getroot.sh > /sys/kernel/uevent_helper");
				AdbUtility.run("while : ; do [ -f /dev/sh ] && exit; done");
				logger.info("Root achieved. Installing root files. Device will reboot. Please wait.");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"ServiceMenu"+File.separator+"installsu.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/installsu.sh");
				AdbUtility.run("/dev/sh /data/local/tmp/installsu.sh");
				if (AdbUtility.hasRootPerms()) {
					logger.info("Device rooted. Now cleaning and rebooting. Please wait");
					FTShell shell = new FTShell("rebootservicemenu");
					shell.runRoot();
					shell.clean();
				}
			}
			else {
				logger.info("Canceled");
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void doRootRunRootShell() {
		try {
			if (pck.length()>0) {
				doPushRootFiles(pck,false);
				String device = AdbUtility.run("/system/bin/getprop "+ "ro.product.model");
				String buildid = AdbUtility.run("/system/bin/getprop "+ "ro.build.display.id");
				String config = device + "_" + buildid;
				File run_root_shell_enc = new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"run_root_shell"+File.separator+"run_root_shell.enc");
				File run_root_shell = new File(OS.getFolderCustom()+File.separator+"root"+File.separator+"run_root_shell"+File.separator+"run_root_shell");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"run_root_shell"+File.separator+"install_root.sh", "/data/local/tmp/");
				OS.decrypt(run_root_shell_enc);
				AdbUtility.push(run_root_shell.getAbsolutePath(), "/data/local/tmp/");
				run_root_shell.delete();
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"run_root_shell"+File.separator+"device.db", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/install_root.sh");
				AdbUtility.run("chmod 755 /data/local/tmp/run_root_shell");
				logger.info("Trying to apply root exploit. It can be very long. Please wait ...");
				AdbUtility.run("/data/local/tmp/run_root_shell -c /data/local/tmp/install_root.sh");
				if (AdbUtility.hasRootPerms()) {
					logger.info("Device rooted.");
					AdbUtility.pull("/data/local/tmp/device.db", OS.getFolderCustom()+File.separator+"root"+File.separator+"run_root_shell"+File.separator+"device.db");
				}
				else {
					logger.info("Root failed");;
				}
				logger.info("Cleaning workdir");
				AdbUtility.run("rm /data/local/tmp/su");
				AdbUtility.run("rm /data/local/tmp/Superuser.apk");
				AdbUtility.run("rm /data/local/tmp/busybox");
				AdbUtility.run("rm /data/local/tmp/run_root_shell");
				AdbUtility.run("rm /data/local/tmp/install_root.sh");
				AdbUtility.run("rm /data/local/tmp/99SuperSUDaemon");
				AdbUtility.run("rm /data/local/tmp/chattr");
				AdbUtility.run("rm /data/local/tmp/device.db");
				AdbUtility.run("rm /data/local/tmp/install-recovery.sh");
			}
			else {
				logger.info("Canceled");
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void doRootTowelroot() {
		try {
			if (pck.length()>0) {
				doPushRootFiles(pck,false);
				String tr=OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"tr_mod.apk";
				OS.decrypt(new File(tr+".enc"));
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"zxz.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/zxz.sh");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"wp_mod.ko", "/data/local/tmp/");
				AdbUtility.run("chmod 644 /data/local/tmp/wp_mod.ko");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"writekmem", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/writekmem");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"findricaddr", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/findricaddr");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"modulecrcpatch", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/modulecrcpatch");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"install_root.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/install_root.sh");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"install_root.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/install_root.sh");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"towelroot"+File.separator+"antiric", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/antiric");
				AdbUtility.install(tr);
				AdbUtility.run("am start -n com.geohot.towelroot/.TowelRoot");
				logger.info("Click the Make it ra1n button on your phone. Waiting for root");
				while (!AdbUtility.hasRootPerms()) {
					Thread.sleep(1000);
				}
				AdbUtility.uninstall("com.geohot.towelroot", false);
				logger.info("Installing latest su binaries");
				AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
				AdbUtility.run("chown shell.shell "+GlobalConfig.getProperty("deviceworkdir")+"/busybox && chmod 755 " + GlobalConfig.getProperty("deviceworkdir")+"/busybox",true);
				AdbUtility.run("/system/xbin/su -c '/data/local/tmp/install_root.sh'");
				logger.info("Root done. Cleaning files");
				AdbUtility.run("rm /data/local/tmp/99SuperSUDaemon");
				AdbUtility.run("rm /data/local/tmp/Superuser.apk");
				AdbUtility.run("rm /data/local/tmp/chattr");
				AdbUtility.run("rm /data/local/tmp/install-recovery.sh");
				AdbUtility.run("rm /data/local/tmp/install_root.sh");
				AdbUtility.run("rm /data/local/tmp/kernelmodule_patch.sh");
				AdbUtility.run("rm /data/local/tmp/su");
				AdbUtility.run("rm /data/local/tmp/sugote-mksh");
				AdbUtility.run("rm /data/local/tmp/wp_mod.ko");
				AdbUtility.run("rm /data/local/tmp/zxz.sh");
				AdbUtility.run("rm /data/local/tmp/writekmem");
				AdbUtility.run("rm /data/local/tmp/findricaddr");
				AdbUtility.run("rm /data/local/tmp/busybox");
				AdbUtility.run("rm /data/local/tmp/ricaddr");
				AdbUtility.run("rm /data/local/tmp/zxz_run");
				AdbUtility.run("rm /data/local/tmp/antiric");
				AdbUtility.run("rm /data/local/tmp/modulecrcpatch");
				logger.info("Rebooting device");
				Devices.getCurrent().reboot();
			}
			else {
				logger.info("Canceled");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void doPushRootFiles(String rootpackage, boolean direct) throws Exception {
		if (!direct) {
			AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox");
			AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"su", GlobalConfig.getProperty("deviceworkdir")+"/su");
			AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"Superuser.apk", GlobalConfig.getProperty("deviceworkdir")+"/Superuser.apk");
			if (rootpackage.toLowerCase().equals("supersu")) {
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"install-recovery.sh", GlobalConfig.getProperty("deviceworkdir")+"/install-recovery.sh");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"99SuperSUDaemon", GlobalConfig.getProperty("deviceworkdir")+"/99SuperSUDaemon");
			}
			AdbUtility.run("chown shell.shell "+GlobalConfig.getProperty("deviceworkdir")+"/busybox && chmod 755 " + GlobalConfig.getProperty("deviceworkdir")+"/busybox",true);
			
		}
		else {
			AdbUtility.push(Devices.getCurrent().getBusybox(false), "/system/xbin");
			AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"su", "/system/xbin");
			AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"subin"+File.separator+rootpackage+File.separator+"Superuser.apk", "/system/app");
			AdbUtility.run("chown root.shell /system/xbin/su");
			AdbUtility.run("chmod 06755 /system/xbin/su");
			AdbUtility.run("chown root.shell /system/xbin/busybox");
			AdbUtility.run("chmod 755 /system/xbin/busybox");			
		}
	}

	public void doInstallRootFiles() throws Exception {
		AdbUtility.run(GlobalConfig.getProperty("deviceworkdir")+"/busybox mount -o remount,rw /system && /data/local/tmp/busybox mv /data/local/tmp/su /system/xbin/su && /data/local/tmp/busybox mv /data/local/tmp/Superuser.apk /system/app/Superuser.apk && /data/local/tmp/busybox cp /data/local/tmp/busybox /system/xbin/busybox && chown root.root /system/xbin/su && chmod 06755 /system/xbin/su && chmod 655 /system/app/Superuser.apk && chmod 755 /system/xbin/busybox && rm /data/local.prop && reboot");
	}

	public void doInstallRootFilesServiceMenu() throws Exception {
		AdbUtility.run("/dev/sh /system/bin/mount -o remount,rw -t ext4 /dev/block/platform/msm_sdcc.1/by-name/system /system && /dev/sh /data/local/tmp/busybox mv /data/local/tmp/su /system/xbin/su && /dev/sh /data/local/tmp/busybox mv /data/local/tmp/Superuser.apk /system/app/Superuser.apk && /dev/sh /data/local/tmp/busybox cp /data/local/tmp/busybox /system/xbin/busybox && /dev/sh chown root.root /system/xbin/su && chmod 06755 /system/xbin/su && /dev/sh chmod 655 /system/app/Superuser.apk && /dev/sh chmod 755 /system/xbin/busybox");
	}

}