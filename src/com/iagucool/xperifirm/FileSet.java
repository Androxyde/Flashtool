package com.iagucool.xperifirm;


import gui.DeviceUpdates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.system.OS;
import org.system.URLDownloader;

public class FileSet {

	private int nbparts=0;
	private String FSName="";
	private String destFolder = "";
	private int id;
	private TreeMap<Integer, String> map = new TreeMap<Integer, String>();
	private static Logger logger = Logger.getLogger(FileSet.class);
	private long FSChecksum = 0;
	URLDownloader ud=null;
	
	public String getName() {
		return FSName;
	}
	
	public void addUrl(String url) {
		nbparts++;
		map.put(nbparts, url);
	}

	public void setId(int lid) {
		id=lid;
		FSName="FILESET_"+lid;
	}
	
	public int getId() {
		return id;
	}
	
	public void setFolder(String folder) {
		destFolder=folder;
	}
	
	public void setCheckSum(long lsum) {
		FSChecksum=lsum;
	}

	public int getNbFiles() {
		return nbparts;
	}
	
	public void cancelDownload() {
		try {
			ud.Cancel();
		} catch (Exception e) {}
	}
	
	public boolean download() {
		try {
			if (new File(destFolder+File.separator+FSName).exists()) {
				logger.info("This FILESET is already downloaded");
				return true;
			}
			logger.info("Downloading "+FSName);
			new File(destFolder).mkdirs();
			Iterator<Integer> i = map.keySet().iterator();
			ud = new URLDownloader();
			while (i.hasNext()) {
				Integer key = i.next();
				String url = map.get(key);
				String f = url.substring(url.lastIndexOf("/")+1);
				if (map.size()>1)
				logger.info("   Downloading part "+key+" of "+map.size());
				ud.Download(url,destFolder+File.separator+FSName+"_temp");
			}
			FileUtils.moveFile(new File(destFolder+File.separator+FSName+"_temp"), new File(destFolder+File.separator+FSName));
			long checksum = OS.getAlder32(new File(destFolder+File.separator+FSName));
			if (checksum!=FSChecksum) new File(destFolder+File.separator+FSName).delete();
			return (checksum==FSChecksum);
		}
		catch (Exception e) {
			return false;
		}
	}
}