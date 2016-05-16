package gui.models;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class TreeDeviceCustomizationRelease {

	String release="";
	Firmware firm=null;

	public TreeDeviceCustomizationRelease(String rel, File f, JarFile jf) throws IOException, Exception {
		release=rel;
		firm=new Firmware(f.getAbsolutePath(), jf.getManifest().getMainAttributes().getValue("device"), jf.getManifest().getMainAttributes().getValue("version"), jf.getManifest().getMainAttributes().getValue("branding"));
	}

	public String getRelease() {
		return release;
	}

	public Firmware getFirmware()  {
		return firm;
	}
}
