package org.util;

import flashsystem.Bundle;
import flashsystem.BundleMetaData;
import gui.DeviceUpdates;
import gui.models.CategoriesModel;
import gui.tools.WidgetTask;
import gui.tools.createFTFJob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.ProcessBuilderWrapper;
import org.system.TextFile;
import org.system.XMLFwInfo;

public class XperiFirm {

	private static Logger logger = Logger.getLogger(XperiFirm.class);

	public static void run() throws Exception {
		try {
			String version = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/version"));
			String downloadurl = IOUtils.toString(new URL("http://www.iagucool.com/xperifirm/download"));
			TextFile tf = new TextFile(OS.getFolderUserFlashtool()+File.separator+"XperiFirm.version","ISO8859-15");
			try {
				tf.readLines();
				if (!version.equals(tf.getLines().iterator().next())) {
					tf.open(false);
					logger.info("Downloading latest XperiFirm");
					OS.unpackArchive(new URL(downloadurl), new File(OS.getFolderUserFlashtool()));
					tf.write(version);
					tf.close();
				}
			} catch (FileNotFoundException fne) {
				tf.open(false);
				logger.info("Downloading latest XperiFirm");
				OS.unpackArchive(new URL(downloadurl), new File(OS.getFolderUserFlashtool()));			tf.write(version);
				tf.write(version);
				tf.close();
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
					cmdargs.add(OS.getPathXperiFirmWrapper());
					cmdargs.add(OS.getPathXperiFirm());
					cmdargs.add(OS.getFolderFirmwaresDownloaded());
				}
				command = new ProcessBuilderWrapper(cmdargs);
			}
			catch (Exception e) {
				throw new Exception(command.getStdOut()+" / "+command.getStdErr());
			}
			String[] downloaded = new File(OS.getFolderFirmwaresDownloaded()).list();
			for (int i = 0; i<downloaded.length;i++) {
				File bundled = new File(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i]+File.separator+"bundled");
				if (bundled.exists()) continue;
				File fwinfo = new File(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i]+File.separator+"fwinfo.xml");
				if (fwinfo.exists()) {
					XMLFwInfo info = new XMLFwInfo(fwinfo);
					logger.info("Creating bundle for "+info.getProduct()+" "+info.getOperator()+" "+info.getVersion());
					try {
						createBundle(OS.getFolderFirmwaresDownloaded()+File.separator+downloaded[i],info);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public static void createBundle(String sourcefolder,XMLFwInfo info) throws Exception {
		BundleMetaData meta = new BundleMetaData();
		CategoriesModel model = new CategoriesModel(meta);
		meta.clear();
		File srcdir = new File(sourcefolder);
		File[] chld = srcdir.listFiles();
		for(int i = 0; i < chld.length; i++) {
			if (chld[i].getName().toUpperCase().endsWith("SIN") || (chld[i].getName().toUpperCase().endsWith("TA") && !chld[i].getName().toUpperCase().contains("SIMLOCK")) || (chld[i].getName().toUpperCase().endsWith("XML") && (!chld[i].getName().toUpperCase().contains("UPDATE") && !chld[i].getName().toUpperCase().contains("FWINFO")))) {
				meta.process(chld[i].getName(), chld[i].getAbsolutePath());
				model.refresh(meta);
			}
		}
		srcdir = new File(sourcefolder+File.separator+"boot");
		if (srcdir.exists()) {
			chld = srcdir.listFiles();
			for(int i = 0; i < chld.length; i++) {
				if (chld[i].getName().toUpperCase().endsWith("XML")) {
					meta.process(chld[i].getName(), chld[i].getAbsolutePath());
					model.refresh(meta);
				}
			}
		}
		model.refresh(meta);
		Bundle b = new Bundle();
		b.setMeta(meta);
		b.setDevice(info.getModel());
		b.setVersion(info.getVersion());
		b.setBranding(info.getOperator());
		b.setCDA(info.getCDA());
		b.setRevision(info.getRevision());
		b.setCmd25("false");
		b.createFTF();
		TextFile tf = new TextFile(sourcefolder+File.separator+"bundled","ISO8859-15");
		tf.open(true);
		tf.close();
	}

}