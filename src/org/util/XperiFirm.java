package org.util;

import flashsystem.Bundle;
import flashsystem.BundleEntry;
import flashsystem.BundleMetaData;
import flashsystem.SeusSinTool;
import gui.tools.WidgetTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.xmlcombiner.XmlCombiner;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jdom.JDOMException;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.ProcessBuilderWrapper;
import org.system.TextFile;
import org.system.XMLFwInfo;

public class XperiFirm {

	static final Logger logger = LogManager.getLogger(XperiFirm.class);
	static Shell _parent;

	public static void run(Shell parent) throws IOException,JDOMException {
			_parent = parent;
			TextFile tf=null;
			String version = null;
			String downloadurl=null;
			try {
				File f = new File(OS.getFolderUserFlashtool()+File.separator+"XperiFirm.exe.config");
				if (f.exists()) f.delete();
				version = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/version"));
				version = version.substring(0,version.indexOf("|"));
				downloadurl = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/download"));
				tf = new TextFile(OS.getFolderUserFlashtool()+File.separator+"XperiFirm.version","ISO8859-15");
				tf.readLines();
				if (!version.equals(tf.getLines().iterator().next())) {
					tf.open(false);
					logger.info("Downloading latest XperiFirm");
					OS.unpackArchive(new URL(downloadurl), new File(OS.getFolderUserFlashtool()));
					tf.write(version);
					tf.close();
				}
			} catch (Exception fne) {
				if (tf!=null) {
					tf.open(false);
					logger.info("Downloading latest XperiFirm");
					OS.unpackArchive(new URL(downloadurl), new File(OS.getFolderUserFlashtool()));			tf.write(version);
					tf.write(version);
					tf.close();
				}
			}
			ProcessBuilderWrapper command=null;
			try {
				List<String> cmdargs = new ArrayList<String>();
				if (OS.getName().equals("windows")) {
					cmdargs.add(OS.getPathXperiFirm());
					cmdargs.add("-o");
					cmdargs.add("\""+OS.getFolderFirmwaresDownloaded()+"\"");
				}
				else {
					cmdargs.add("sh");
					cmdargs.add(OS.getPathXperiFirmWrapper());
					cmdargs.add(OS.getPathXperiFirm());
					cmdargs.add(OS.getFolderFirmwaresDownloaded());
				}
				command = new ProcessBuilderWrapper(cmdargs);
			}
			catch (Exception e) {
				logger.warn(command.getStdOut()+" / "+command.getStdErr());
			}
			String[] downloaded = new File(OS.getFolderFirmwaresDownloaded()).list();
			for (int i = 0; i<downloaded.length;i++) {
				File bundled = new File(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i]+File.separator+"bundled");
				if (bundled.exists()) continue;
				File fwinfo = new File(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i]+File.separator+"fwinfo.xml");
				if (fwinfo.exists()) {
					XMLFwInfo info = null;
					try {
						info = new XMLFwInfo(fwinfo);
					} catch (Exception e) {}
					if (info!=null) {
						logger.info("Creating bundle for "+info.getProduct()+" "+info.getOperator()+" "+info.getVersion());
						try {
							createBundle(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i],info);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}
				}
			}
	}

	public static void createBundle(String sourcefolder,XMLFwInfo info) throws Exception {
		BundleMetaData meta = new BundleMetaData();
		meta.clear();
		File srcdir = new File(sourcefolder);
		File[] chld = srcdir.listFiles();
		boolean xperifirmdecrypted=true;
		String decryptfolder="";
		String updatexml="";
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().toUpperCase().startsWith("FILE")) {
				decryptfolder=chld[i].getParentFile().getAbsolutePath()+File.separator+"decrypted";
				SeusSinTool.decryptAndExtract(chld[i].getAbsolutePath());
				xperifirmdecrypted=false;
			}
		}
		if (!xperifirmdecrypted) {
			File update = new File(decryptfolder+File.separator+"update.xml");
			File update1 = new File(decryptfolder+File.separator+"update1.xml");
			File newupdate = new File(decryptfolder+File.separator+"update2.xml");
			if (update.exists() && update1.exists()) {
				XmlCombiner combiner = new XmlCombiner();
				FileInputStream fi1 = new FileInputStream(update);
				combiner.combine(fi1);
				FileInputStream fi2 = new FileInputStream(update1);
				combiner.combine(fi2);
				FileOutputStream fo = new FileOutputStream(newupdate);
				combiner.buildDocument(fo);
				fi1.close();
				fi2.close();
				fo.close();
				update.delete();
				update1.delete();
				newupdate.renameTo(update);
			}
		}
		
		if (!xperifirmdecrypted) {
			srcdir = new File(sourcefolder+File.separator+"decrypted");
			chld = srcdir.listFiles();
		}
		
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().toUpperCase().endsWith("FSC") || chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA") && !chld[i].getName().toUpperCase().contains("SIMLOCK")) || (chld[i].getName().toUpperCase().endsWith("XML") && (!chld[i].getName().toUpperCase().contains("UPDATE") && !chld[i].getName().toUpperCase().contains("FWINFO")))) {
				meta.process(new BundleEntry(chld[i]));
			}
			if (chld[i].getName().toUpperCase().contains("UPDATE")) {
				updatexml=chld[i].getAbsolutePath();
			}
		}
		File srcbootdir = new File(srcdir.getAbsolutePath()+File.separator+"boot");
		if (srcbootdir.exists()) {
			chld = srcbootdir.listFiles();
			for(int i = 0; i < chld.length; i++) {
				if (chld[i].getName().toUpperCase().endsWith("XML")) {
					meta.process(new BundleEntry(chld[i]));
				}
			}
		}
		Bundle b = new Bundle();
		b.setMeta(meta);
		b.setNoErase(updatexml);
		b.setDevice(info.getModel());
		b.setVersion(info.getVersion());
		b.setBranding(info.getOperator());
		b.setCDA(info.getCDA());
		b.setRevision(info.getRevision());
		b.setCmd25("false");
		if (!b.hasFsc()) {
			DeviceEntry dev = Devices.getDeviceFromVariant(info.getModel());
			if (dev!=null) {
		    	String fscpath = dev.getFlashScript(info.getModel(),info.getVersion());
		    	File fsc = new File(fscpath);
		    	if (fsc.exists()) {
	    			String result = WidgetTask.openYESNOBox(_parent, "A FSC script is found : "+fsc.getName()+". Do you want to add it ?");
	    			if (Integer.parseInt(result)==SWT.YES) {
	    				b.setFsc(fsc);
	    			}
		    	}
			}
		}
		b.createFTF();
		TextFile tf = new TextFile(sourcefolder+File.separator+"bundled","ISO8859-15");
		tf.open(true);
		tf.close();
	}

}