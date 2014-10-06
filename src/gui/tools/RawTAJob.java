package gui.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;

import org.adb.AdbUtility;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.system.Devices;
import org.system.OS;

public class RawTAJob extends Job {

	String _action = "";
	Shell _shell;
	private static Logger logger = Logger.getLogger(RawTAJob.class);
	
	public void setAction(String action) {
		_action = action;
	}
	
	public void setShell(Shell shell) {
		_shell = shell;
	}
	
	public RawTAJob(String name) {
		super(name);
	}
	
    protected IStatus run(IProgressMonitor monitor) {
    	try {

    		if (_action.equals("doBackup"))
    			doBackup();
    		if (_action.equals("doRestore"))
    			doRestore();
    		return Status.OK_STATUS;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return Status.CANCEL_STATUS;
    	}
    }

    public void doBackup() {
		String serial = Devices.getCurrent().getSerial();
    	String folder = OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+serial+File.separator+"rawta";
		try {
			if (!Devices.getCurrent().isBusyboxInstalled(false))
				Devices.getCurrent().doBusyboxHelper();
			new File(folder).mkdirs();
			String partition = "/dev/block/platform/msm_sdcc.1/by-name/TA";
			if (!AdbUtility.exists(partition)) {
				partition = AdbUtility.run("export PATH=$PATH:/data/local/tmp;busybox cat /proc/partitions|busybox grep -w 2048|busybox awk '{print $4}'");
				System.out.println(partition);
				if (partition.length()==0)
					throw new Exception("Your phone is not compatible");
				partition = "/dev/block/"+partition;
			}
			logger.info("Begin backup of "+partition);
			long transferred = AdbUtility.rawBackup(partition, "/mnt/sdcard/ta.dd");
			if (transferred == 0L)
				throw new Exception("Erreur when doing raw backup");
			Properties hash = new Properties();
			hash.setProperty("partition", AdbUtility.getMD5(partition));
			AdbUtility.pull("/mnt/sdcard/ta.dd", folder);
			AdbUtility.run("rm -f /mnt/sdcard/ta.dd");
			hash.setProperty("local", OS.getMD5(new File(folder+File.separator+"ta.dd")).toUpperCase());
			logger.info("End of backup");
			if (hash.getProperty("local").equals(hash.getProperty("partition"))) {
				logger.info("Backup is OK");
				createFTA(partition, folder);
			}
			else throw new Exception("Backup is not OK");
		} catch (Exception ex) {
			new File(folder+"ta.dd").delete();
			try {
				AdbUtility.run("rm -f /mnt/sdcard/ta.dd");
			} catch (Exception ex1) {}
			logger.error(ex.getMessage()); 
		}
    }
    
    public void doRestore() {
		String serial = Devices.getCurrent().getSerial();
		String folder = OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+serial+File.separator+"rawta";
		String folderprepared = folder+File.separator+"prepared"; 
    	try {
			if (!Devices.getCurrent().isBusyboxInstalled(false))
				Devices.getCurrent().doBusyboxHelper();
			String backupset = WidgetTask.openTABackupSelector(_shell);
			if (backupset.length()==0) {
				throw new Exception("Operation canceled");
			} 
			backupset = backupset.split(":")[0].trim();
			backupset = folder+File.separator+backupset+".fta";
			JarFile jf = new JarFile(new File(backupset));
			Attributes attr = jf.getManifest().getMainAttributes();
			String partition = attr.getValue("partition");			
			File prepared = new File(folderprepared);
			if (prepared.exists()) {
				FileUtils.deleteDirectory(prepared);
				if (prepared.exists()) {
					jf.close();
					throw new Exception("Cannot delete previous folder : "+prepared.getAbsolutePath());
				}
			}
			prepared.mkdirs();
			Enumeration<JarEntry> ents = jf.entries();
			while (ents.hasMoreElements()) {
				JarEntry entry = ents.nextElement();
				if (!entry.getName().startsWith("META"))
					saveEntry(jf,entry, folderprepared);
			}
			Properties hash = new Properties();
			hash.setProperty("stored", attr.getValue("md5"));
			
			if (!new File(folderprepared+File.separator+"ta.dd").exists())
				throw new Exception(folderprepared+File.separator+"ta.dd"+" does not exist");
			hash.setProperty("local", OS.getMD5(new File(folderprepared+File.separator+"ta.dd")).toUpperCase());
			if (!hash.getProperty("stored").equals(hash.getProperty("local")))
				throw new Exception("Error during extraction. Bundle is corrupted");

			AdbUtility.push(folderprepared+File.separator+"ta.dd","/mnt/sdcard/");
			hash.setProperty("remote", AdbUtility.getMD5("/mnt/sdcard/ta.dd"));
			if (!hash.getProperty("local").equals(hash.getProperty("remote")))
				throw new Exception("Local file and remote file do not match");
			hash.setProperty("partitionbefore", AdbUtility.getMD5(partition));
			if (hash.getProperty("remote").equals(hash.getProperty("partitionbefore")))
				throw new Exception("Backup and current partition match. Nothing to be done. Aborting");
			logger.info("Making a backup on device before flashing.");
			long transferred = AdbUtility.rawBackup(partition, "/mnt/sdcard/tabefore.dd");
			if (transferred == 0)
				throw new Exception("Failed to take a backup before flashing new TA. Aborting");
			logger.info("Flashing new TA.");
			transferred = AdbUtility.rawBackup("/mnt/sdcard/ta.dd", partition);
			hash.setProperty("partitionafter", AdbUtility.getMD5(partition));
			if (!hash.getProperty("remote").equals(hash.getProperty("partitionafter"))) {
				logger.error("Error flashing new TA. Reverting back to the previous TA.");
				transferred = AdbUtility.rawBackup("/mnt/sdcard/tabefore.dd", partition);
				if (transferred == 0L)
					throw new Exception("Failed to restore previous TA");
				logger.info("Restore previous TA OK");
			}
			else {
				logger.info("Restore is OK");
				Devices.getCurrent().reboot();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    }

    public void createFTA(String partition, String folder) {
    	File tadd = new File(folder+File.separator+"ta.dd");
    	String timestamp = OS.getTimeStamp();
		File fta = new File(folder+File.separator+timestamp+".fta");
		byte buffer[] = new byte[10240];
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Manifest-Version: 1.0\n");
		sbuf.append("Created-By: FlashTool\n");
		sbuf.append("serial: "+Devices.getCurrent().getSerial()+"\n");
		sbuf.append("build: "+Devices.getCurrent().getBuildId()+"\n");
		sbuf.append("partition: "+partition+"\n");
		sbuf.append("md5: "+OS.getMD5(tadd).toUpperCase()+"\n");
		sbuf.append("timestamp: "+timestamp+"\n");
		try {
			Manifest manifest = new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8")));
		    FileOutputStream stream = new FileOutputStream(fta);
		    JarOutputStream out = new JarOutputStream(stream, manifest);
		    out.setLevel(Deflater.BEST_SPEED);
			logger.info("Creating backupset bundle");
		    JarEntry jarAdd = new JarEntry("ta.dd");
	        out.putNextEntry(jarAdd);
	        InputStream in = new FileInputStream(tadd);
	        while (true) {
	          int nRead = in.read(buffer, 0, buffer.length);
	          if (nRead <= 0)
	            break;
	          out.write(buffer, 0, nRead);
	        }
	        in.close();
	        out.flush();
	        out.close();
	        stream.flush();
		    stream.close();
		    tadd.delete();
		    logger.info("Bundle "+fta.getAbsolutePath()+" creation finished");
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
    }

    private void saveEntry(JarFile jar, JarEntry entry, String folder) throws IOException {
			logger.debug("Saving entry "+entry.getName()+" to disk");
			InputStream in = jar.getInputStream(entry);
			String outname = folder+File.separator+entry.getName();
			logger.debug("Writing Entry to "+outname);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(outname));
			byte[] buffer = new byte[10240];
			int len;
			while((len = in.read(buffer)) >= 0)
				out.write(buffer, 0, len);
			in.close();
			out.close();
	}

}