package org.flashtool.gui.tools;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.log.LogProgress;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;
import org.flashtool.system.OS;
import org.flashtool.system.TextFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CleanJob extends Job {

	DeviceApps _apps;
	
	public CleanJob(String name) {
		super(name);
	}
	
	public void setDeviceApps(DeviceApps apps) {
		_apps = apps;
	}
    protected IStatus run(IProgressMonitor monitor) {
    	try {
    		new File(Devices.getCurrent().getAppsDir()).mkdirs();
    		Vector<String> toberemoved = _apps.getToBeRemoved(true);
    		Vector<String> tobeinstalled = _apps.getToBeInstalled(true);
    		LogProgress.initProgress(toberemoved.size()+tobeinstalled.size());
    		TextFile listtoremove=new TextFile(Devices.getCurrent().getCleanDir()+File.separator+"listappsremove","ISO-8859-15");
    		TextFile listtoinstall=new TextFile(Devices.getCurrent().getCleanDir()+File.separator+"listappsadd","ISO-8859-15");
    		if (toberemoved.size()>0) {
    			log.info("Making a backup of removed apps.");
    			listtoremove.open(false);
    		}
    		if (tobeinstalled.size()>0) {
    			listtoinstall.open(false);
    		}
    		Iterator<String> ir = toberemoved.iterator();
			while (ir.hasNext()) {
				String app = _apps.getApkName(ir.next());
				listtoremove.writeln(app);
				LogProgress.updateProgress();
				try {
					AdbUtility.pull("/system/app/"+app, Devices.getCurrent().getAppsDir());
				}
				catch (Exception e) {}
			}
			if (toberemoved.size()>0) {
				log.info("Backup Finished.");
				listtoremove.close();
				AdbUtility.push(listtoremove.getFileName(), GlobalConfig.getProperty("deviceworkdir")+"/");
				FTShell s = new FTShell("sysremove");
				s.runRoot();
				log.info("Apps removed from device.");
			}
			if (tobeinstalled.size()>0) {
				Iterator<String> ii = tobeinstalled.iterator();
				while (ii.hasNext()) {
					String app = _apps.getApkName(ii.next());
					listtoinstall.writeln(app);
					LogProgress.updateProgress();
					try {
						AdbUtility.push(Devices.getCurrent().getAppsDir()+File.separator+app, GlobalConfig.getProperty("deviceworkdir")+"/");
					}
					catch (Exception e) {}
				}
				listtoinstall.close();
				AdbUtility.push(listtoinstall.getFileName(), GlobalConfig.getProperty("deviceworkdir")+"/");
				FTShell s = new FTShell("sysadd");
				s.runRoot();
				log.info("Installation Finished");
			}
			LogProgress.initProgress(0);
			listtoinstall.delete();
			listtoremove.delete();
			return Status.OK_STATUS;
		}
    	catch (Exception e) {
    		e.printStackTrace();
			log.error(e.getMessage());
			LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }

}