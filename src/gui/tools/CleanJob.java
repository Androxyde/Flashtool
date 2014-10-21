package gui.tools;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.logger.LogProgress;
import org.system.Devices;
import org.system.FTShell;
import org.system.GlobalConfig;
import org.system.OS;
import org.system.TextFile;

public class CleanJob extends Job {

	DeviceApps _apps;
	private static Logger logger = Logger.getLogger(CleanJob.class);
	
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
    			logger.info("Making a backup of removed apps.");
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
				logger.info("Backup Finished.");
				listtoremove.close();
				AdbUtility.push(listtoremove.getFileName(), GlobalConfig.getProperty("deviceworkdir")+"/");
				FTShell s = new FTShell("sysremove");
				s.runRoot();
				logger.info("Apps removed from device.");
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
				logger.info("Installation Finished");
			}
			LogProgress.initProgress(0);
			listtoinstall.delete();
			listtoremove.delete();
			return Status.OK_STATUS;
		}
    	catch (Exception e) {
    		e.printStackTrace();
			logger.error(e.getMessage());
			LogProgress.initProgress(0);
    		return Status.CANCEL_STATUS;
    	}
    }

}