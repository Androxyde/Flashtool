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
import org.system.URLDownloader;

public class FileSet {

	private int nbparts=0;
	private String FSName="";
	private String destFolder = "";
	private int id;
	private TreeMap<Integer, String> map = new TreeMap<Integer, String>();
	private static Logger logger = Logger.getLogger(FileSet.class);
	
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
	
	public int getNbFiles() {
		return nbparts;
	}
	
	public void download() throws IOException {
		if (new File(destFolder+File.separator+FSName).exists()) {
			logger.info("This FILESET is already downloaded");
			return;
		}
		logger.info("Downloading "+FSName);
		new File(destFolder).mkdirs();
		new File(destFolder+File.separator+FSName).delete();
		new File(destFolder+File.separator+FSName+"_1").delete();
		new File(destFolder+File.separator+FSName+"_2").delete();
		new File(destFolder+File.separator+FSName+"_3").delete();
		new File(destFolder+File.separator+FSName+"_4").delete();
		new File(destFolder+File.separator+FSName+"_5").delete();
		new File(destFolder+File.separator+FSName+"_6").delete();
		Iterator<Integer> i = map.keySet().iterator();
		while (i.hasNext()) {
			Integer key = i.next();
			String url = map.get(key);
			URLDownloader ud = new URLDownloader();
			String f = url.substring(url.lastIndexOf("/")+1);
			if (map.size()>1)
			logger.info("   Downloading part "+key+" of "+map.size());
			ud.Download(url,destFolder+File.separator+FSName+"_"+key);
			map.put(key, destFolder+File.separator+FSName+"_"+key);
		}
		if (map.size()>1)
			mergeFiles();
		else {
			FileUtils.moveFile(new File(map.get(1)), new File(destFolder+File.separator+FSName));
		}
	}
	
	public void mergeFiles() {
		 
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(destFolder+File.separator+FSName, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Iterator<Integer> i = map.keySet().iterator();
		while (i.hasNext()) {
			FileInputStream fis;
			try {
				File f = new File(map.get(i.next()));
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
 
				in.close();
				f.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
}