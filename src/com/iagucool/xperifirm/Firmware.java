package com.iagucool.xperifirm;

import java.util.Collection;
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
}
