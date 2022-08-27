package org.flashtool.gui.models;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TreeDeviceCustomization {
	
	private List<TreeDeviceCustomizationRelease> devicevariantcustreleases = new LinkedList<TreeDeviceCustomizationRelease>();
	String customization="";

	public TreeDeviceCustomization(String cust, File f, JarFile jf) throws IOException, Exception {
		customization=cust;
		addRelease(f, jf);
	}

	public void addRelease(File f, JarFile jf) throws IOException, Exception {
		String version = jf.getManifest().getMainAttributes().getValue("version");
		TreeDeviceCustomizationRelease v = new TreeDeviceCustomizationRelease(version, f, jf);
		devicevariantcustreleases.add(v);
	}
	
	public String getCustomization() {
		return customization;
	}

	public boolean contains(String release) {
		  Iterator<TreeDeviceCustomizationRelease> irel = devicevariantcustreleases.iterator();
		  while (irel.hasNext()) {
			  TreeDeviceCustomizationRelease rel = irel.next();
			  if (rel.getRelease().equals(release)) return true; 
		  }
		  return false;
	}

	public TreeDeviceCustomizationRelease get(String release) {
		  Iterator<TreeDeviceCustomizationRelease> irel = devicevariantcustreleases.iterator();
		  while (irel.hasNext()) {
			  TreeDeviceCustomizationRelease rel = irel.next();
			  if (rel.getRelease().equals(release)) return rel; 
		  }
		  return null;
	}

	public List<TreeDeviceCustomizationRelease> getReleases() {
		return devicevariantcustreleases;
	}
}
