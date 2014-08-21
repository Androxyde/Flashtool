package com.iagucool.xperifirm;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.logger.LogProgress;

public class Firmware {
	
	String version = "";
	String revision = "";
	TreeMap<Integer, FileSet> map = new TreeMap<Integer, FileSet>();
	private static Logger logger = Logger.getLogger(FileSet.class);
	boolean isDownloaded = false;
	boolean isDownloadCanceled = false;
	FileSet currentFileset = null;
	
	public Firmware(String lversion, String lrevision) {
		version = lversion;
		revision = lrevision;
	}
	
	public String getRelease() {
		return version;
	}
	
	public String getRevision() {
		return revision;
	}
	
	public void addFileSet(FileSet f) {
		map.put(f.getId(), f);
	}
	
	public Collection<FileSet> getFileSets() {
		return map.values();
	}
	
	public int getId() {
		Iterator<FileSet> i=getFileSets().iterator();
		while (i.hasNext()) {
			FileSet fs = i.next();
			if (fs.getNbFiles()>1) return fs.getId();
		}
		return -1;
	}
	
	public Vector download(String path) {
    	Iterator<FileSet> i = getFileSets().iterator();
		Vector result = new Vector();
		boolean localDownloaded = true;
		while (i.hasNext() && !isDownloadCanceled) {
			currentFileset = i.next();
			currentFileset.setFolder(path);
			if (currentFileset.download()) result.add(new File(path+File.separator+currentFileset.getName()));
			else {
				logger.error("Error downloading "+currentFileset.getName());
				localDownloaded = false;
			}
			LogProgress.initProgress(0);
		}
		isDownloaded = localDownloaded;
		return result;
	}
	
	public void cancelDownload() {
		currentFileset.cancelDownload();
		isDownloadCanceled = true;
	}
	
	public boolean isDownloaded() {
		return isDownloaded;
	}

}