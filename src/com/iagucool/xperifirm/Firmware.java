package com.iagucool.xperifirm;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class Firmware {
	
	String version = "";
	TreeMap<Integer, FileSet> map = new TreeMap<Integer, FileSet>();
	
	public Firmware(String lversion) {
		version = lversion;
	}
	
	public String getRelease() {
		return version;
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
}
