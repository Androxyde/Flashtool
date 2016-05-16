package gui.models;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

public class TreeDeviceVariant {

	private List<TreeDeviceCustomization> devicevariantcust = new LinkedList<TreeDeviceCustomization>();
	String devicevariant;
	
	public TreeDeviceVariant(String variantname, File f, JarFile jf) throws IOException, Exception  {
		devicevariant=variantname;
		addCustomization(f,jf);
	}

	public String getVariant() {
		return devicevariant;
	}
	
	public void addCustomization(File f, JarFile jf) throws IOException, Exception {
		String cust = jf.getManifest().getMainAttributes().getValue("branding");
		if (contains(cust)) {
			TreeDeviceCustomization v = get(cust);
			v.addRelease(f, jf);
		}
		else {
			TreeDeviceCustomization v = new TreeDeviceCustomization(cust, f, jf);
			devicevariantcust.add(v);
		}
	}

	public boolean contains(String customization) {
		  Iterator<TreeDeviceCustomization> icust = devicevariantcust.iterator();
		  while (icust.hasNext()) {
			  TreeDeviceCustomization cust = icust.next();
			  if (cust.getCustomization().equals(customization)) return true; 
		  }
		  return false;
	}

	public TreeDeviceCustomization get(String customization) {
		  Iterator<TreeDeviceCustomization> icust = devicevariantcust.iterator();
		  while (icust.hasNext()) {
			  TreeDeviceCustomization cust = icust.next();
			  if (cust.getCustomization().equals(customization)) return cust; 
		  }
		  return null;
	}

	public List<TreeDeviceCustomization> getCustomizations() {
		return devicevariantcust;
	}
}
