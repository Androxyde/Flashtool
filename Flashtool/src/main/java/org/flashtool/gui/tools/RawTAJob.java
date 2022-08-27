package org.flashtool.gui.tools;

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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.gui.TARestore;
import org.flashtool.jna.adb.AdbUtility;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RawTAJob extends Job {

	String _action = "";
	Shell _shell;
	String phonetemp = "/data/local/tmp";
	String tafilename = "ta.dd";
	String tafilenamebefore="tabefore.dd";
	
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
    	String folder = OS.getFolderRegisteredDevices()+File.separator+serial+File.separator+"rawta";
		try {
			if (Devices.getCurrent().hasRoot()) {
			if (!Devices.getCurrent().isBusyboxInstalled(false))
				Devices.getCurrent().doBusyboxHelper();
			new File(folder).mkdirs();
			String partition = AdbUtility.run("su -c 'export PATH=$PATH:/data/local/tmp;busybox find /dev/block/platform -name TA'");
			if (!AdbUtility.existsRoot(partition)) {
				partition = AdbUtility.run("export PATH=$PATH:/data/local/tmp;busybox cat /proc/partitions|busybox grep -w 2048|busybox awk '{print $4}'");
				if (partition.length()==0)
					throw new Exception("Your phone is not compatible");
				partition = "/dev/block/"+partition;
			}
			log.info("Begin backup of "+partition);
			long transferred = AdbUtility.rawBackup(partition, phonetemp+File.separator+tafilename);
			if (transferred == 0L)
				throw new Exception("Erreur when doing raw backup");
			Properties hash = new Properties();
			hash.setProperty("partition", AdbUtility.getMD5(partition));
			AdbUtility.pull(phonetemp+File.separator+tafilename, folder);
			AdbUtility.run("rm -f "+phonetemp+File.separator+tafilename);
			hash.setProperty("local", OS.getMD5(new File(folder+File.separator+tafilename)).toUpperCase());
			log.info("End of backup");
			if (hash.getProperty("local").equals(hash.getProperty("partition"))) {
				log.info("Backup is OK");
				createFTA(partition, folder);
			}
			else throw new Exception("Backup is not OK");
			}
			else {
				log.info("Device not rooted. Trying to backup ta using dirtycow exploit");
				AdbUtility.run("rm -f /data/local/tmp/*");
				String platform = Devices.getCurrent().getArch();
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"dirtycow"+File.separator+"backupTA.sh", "/data/local/tmp/");
				AdbUtility.run("chmod 755 /data/local/tmp/backupTA.sh");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"dirtycow"+File.separator+"dirtycow"+platform, "/data/local/tmp/dirtycow");
				AdbUtility.run("chmod 755 /data/local/tmp/dirtycow");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"dirtycow"+File.separator+"dumpta"+platform, "/data/local/tmp/dumpta");
				AdbUtility.run("chmod 755 /data/local/tmp/dumpta");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"dirtycow"+File.separator+"exploitta"+platform, "/data/local/tmp/exploitta");
				AdbUtility.run("chmod 755 /data/local/tmp/exploitta");
				AdbUtility.push(OS.getFolderCustom()+File.separator+"root"+File.separator+"dirtycow"+File.separator+"run-as"+platform, "/data/local/tmp/run-as");
				AdbUtility.run("chmod 755 /data/local/tmp/run-as");
				AdbUtility.run("cd /data/local/tmp && ./backupTA.sh "+tafilename);
				if (AdbUtility.exists("/data/local/tmp/"+tafilename)) {
					new File(folder).mkdirs();
					AdbUtility.pull("/data/local/tmp/"+tafilename, folder);
				}
				AdbUtility.run("rm -f /data/local/tmp/*");
				AdbUtility.run("rm -f /sdcard/dumpta");
				File tafile=new File(folder+File.separator+tafilename);
				if (tafile.exists()) {
					if (tafile.length()==(2*1024*1024)) {
						createFTA("", folder);
					}
					else {
						log.error("dirtycow expoit failed. No ta backup done");
					}
				}
				else {
					log.error("dirtycow expoit failed. No ta backup done");
				}	
			}
		} catch (Exception ex) {
			new File(folder+tafilename).delete();
			try {
				AdbUtility.run("rm -f "+phonetemp+File.separator+tafilename);
			} catch (Exception ex1) {}
			log.error(ex.getMessage()); 
		}
    }
    
    public void doRestore() {
		String serial = Devices.getCurrent().getSerial();
		String folder = OS.getFolderRegisteredDevices()+File.separator+serial+File.separator+"rawta";
		String folderprepared = folder+File.separator+"prepared";
    	try {
			if (!Devices.getCurrent().isBusyboxInstalled(false))
				Devices.getCurrent().doBusyboxHelper();
			
			File srcdir = new File(folder);
			String backupset="";
			if (srcdir.exists()) {
				File[] chld = srcdir.listFiles();
				if (chld.length>0)
					backupset = WidgetTask.openTABackupSelector(_shell);
				else log.info("No backup");
			}
			else log.info("No backup");
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
				OS.deleteDirectory(prepared);
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
			
			if (!new File(folderprepared+File.separator+tafilename).exists())
				throw new Exception(folderprepared+File.separator+tafilename+" does not exist");
			hash.setProperty("local", OS.getMD5(new File(folderprepared+File.separator+tafilename)).toUpperCase());
			if (!hash.getProperty("stored").equals(hash.getProperty("local")))
				throw new Exception("Error during extraction. Bundle is corrupted");

			AdbUtility.push(folderprepared+File.separator+tafilename,phonetemp);
			hash.setProperty("remote", AdbUtility.getMD5(phonetemp+"/"+tafilename));
			if (!hash.getProperty("local").equals(hash.getProperty("remote")))
				throw new Exception("Local file and remote file do not match");
			hash.setProperty("partitionbefore", AdbUtility.getMD5(partition));
			if (hash.getProperty("remote").equals(hash.getProperty("partitionbefore")))
				throw new Exception("Backup and current partition match. Nothing to be done. Aborting");
			log.info("Making a backup on device before flashing.");
			long transferred = AdbUtility.rawBackup(partition, phonetemp+"/"+tafilenamebefore);
			if (transferred == 0)
				throw new Exception("Failed to take a backup before flashing new TA. Aborting");
			log.info("Flashing new TA.");
			transferred = AdbUtility.rawBackup(phonetemp+"/"+tafilename, partition);
			hash.setProperty("partitionafter", AdbUtility.getMD5(partition));
			if (!hash.getProperty("remote").equals(hash.getProperty("partitionafter"))) {
				log.error("Error flashing new TA. Reverting back to the previous TA.");
				transferred = AdbUtility.rawBackup(phonetemp+"/"+tafilenamebefore, partition);
				if (transferred == 0L)
					throw new Exception("Failed to restore previous TA");
				log.info("Restore previous TA OK");
			}
			else {
				log.info("Restore is OK");
				Devices.getCurrent().reboot();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
    }

    public void createFTA(String partition, String folder) {
    	File tadd = new File(folder+File.separator+tafilename);
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
			log.info("Creating backupset bundle");
		    JarEntry jarAdd = new JarEntry(tafilename);
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
		    log.info("Bundle "+fta.getAbsolutePath()+" creation finished");
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
    }

    private void saveEntry(JarFile jar, JarEntry entry, String folder) throws IOException {
			log.debug("Saving entry "+entry.getName()+" to disk");
			InputStream in = jar.getInputStream(entry);
			String outname = folder+File.separator+entry.getName();
			log.debug("Writing Entry to "+outname);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(outname));
			byte[] buffer = new byte[10240];
			int len;
			while((len = in.read(buffer)) >= 0)
				out.write(buffer, 0, len);
			in.close();
			out.close();
	}

}