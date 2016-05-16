package gui.models;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

public class TreeDevice {

	String deviceid="";
	String devicename="";
	private List<TreeDeviceVariant> devicevariants = new LinkedList<TreeDeviceVariant>();
	
	public TreeDevice(String id, String name, File f, JarFile jf) throws IOException, Exception {
		deviceid=id;
		devicename=name;
		addVariant(f,jf);
	}

	String getDevice() {
		return deviceid;
	}
	
	String getDeviceName() {
		return devicename;
	}
	
	public void addVariant(File f, JarFile jf) throws IOException, Exception {
		String model = jf.getManifest().getMainAttributes().getValue("device");
		if (contains(model)) {
			TreeDeviceVariant v = get(model);
			v.addCustomization(f,jf);
		}
		else {
			TreeDeviceVariant v = new TreeDeviceVariant(model,f,jf);
			devicevariants.add(v);
		}
	}

	public boolean contains(String variantname) {
		  Iterator<TreeDeviceVariant> ivariant = devicevariants.iterator();
		  while (ivariant.hasNext()) {
			  TreeDeviceVariant variant = ivariant.next();
			  if (variant.getVariant().equals(variantname)) return true; 
		  }
		  return false;
	}
	  
	public TreeDeviceVariant get(String variantname) {
		  Iterator<TreeDeviceVariant> ivariant = devicevariants.iterator();
		  while (ivariant.hasNext()) {
			  TreeDeviceVariant variant = ivariant.next();
			  if (variant.getVariant().equals(variantname)) return variant; 
		  }
		  return null;
	}
	
	public List<TreeDeviceVariant> getVariants() {
		return devicevariants;
	}

}