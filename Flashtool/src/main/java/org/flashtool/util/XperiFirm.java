package org.flashtool.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.flashsystem.Bundle;
import org.flashtool.flashsystem.BundleEntry;
import org.flashtool.flashsystem.BundleMetaData;
import org.flashtool.flashsystem.SeusSinTool;
import org.flashtool.gui.tools.WidgetTask;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;
import org.flashtool.system.ProcessBuilderWrapper;
import org.flashtool.system.TextFile;
import org.flashtool.system.XMLFwInfo;
import org.flashtool.xmlcombiner.XmlCombiner;
import org.jdom2.JDOMException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XperiFirm {

	static Shell _parent;

	public static void run(Shell parent) throws IOException,JDOMException {
			_parent = parent;
			TextFile tf=null;
			String version = null;
			String downloadurl=null;
			try {
				File f = new File(OS.getFolderUserFlashtool()+File.separator+"XperiFirm.exe.config");
				if (f.exists()) f.delete();
				version = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/version"),Charset.forName("UTF-8"));
				version = version.substring(0,version.indexOf("|"));
				downloadurl = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/download"),Charset.forName("UTF-8"));
				tf = new TextFile(OS.getFolderUserFlashtool()+File.separator+"XperiFirm.version","ISO8859-15");
				tf.readLines();
				if (!version.equals(tf.getLines().iterator().next())) {
					tf.open(false);
					log.info("Downloading latest XperiFirm");
					OS.unpackArchive(new URL(downloadurl), new File(OS.getFolderUserFlashtool()));
					tf.write(version);
					tf.close();
				}
			} catch (Exception fne) {
				if (tf!=null) {
					tf.open(false);
					log.info("Downloading latest XperiFirm");
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
				log.warn(command.getStdOut()+" / "+command.getStdErr());
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
						log.info("Creating bundle for "+info.getProduct()+" "+info.getOperator()+" "+info.getVersion());
						try {
							createBundle(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i],info);
						} catch (Exception e) {
							log.error(e.getMessage());
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
		
		File srcpartzip = new File(srcdir.getAbsolutePath()+File.separator+"partition.zip");
		
		if (srcpartzip.exists()) {
			ZipFile zip = new ZipFile(srcpartzip);
    		log.info("Extracting "+zip.getName());
			String subfolder = srcdir.getAbsolutePath()+File.separator+"partition";
			new File(subfolder).mkdirs();
			File xmlpartition = new File(subfolder+File.separator+"partition_delivery.xml");
			PrintWriter fw = new PrintWriter(xmlpartition);
			fw.println("<PARTITION_DELIVERY FORMAT=\"1\">");
			fw.println(" <PARTITION_IMAGES>");
			 Enumeration<? extends ZipEntry> entries = zip.entries();
			 while ( entries.hasMoreElements() ) {
				 ZipEntry entry = entries.nextElement();
				 fw.println("   <FILE PATH=\""+entry.getName()+"\"/>");
	    		 InputStream entryStream = zip.getInputStream(entry);
	    		 File out = new File(subfolder+File.separator+entry.getName());
	    		 OS.writeToFile(entryStream, out);
	    		 entryStream.close();
			 }
			fw.println(" </PARTITION_IMAGES>");
			fw.println("</PARTITION_DELIVERY>");
			fw.flush();
			fw.close();
			 zip.close();
			 srcpartzip.delete();
		}
		
		File srcpartdir = new File(srcdir.getAbsolutePath()+File.separator+"partition");
		if (srcpartdir.exists()) {
			chld = srcpartdir.listFiles();
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
		    	String fscpath = dev.getFlashScript(info.getVersion(),info.getModel());
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