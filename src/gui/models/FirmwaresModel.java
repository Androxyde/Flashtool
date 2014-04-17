package gui.models;

import gui.tools.FtfFilter;
import java.io.File;
import java.util.jar.JarFile;

public class FirmwaresModel {
	  
	public Firmwares firmwares;

	  public FirmwaresModel(String srcFolder) {
			File dir = new File(srcFolder);
			File[] chld = dir.listFiles(new FtfFilter(""));
			firmwares = new Firmwares();
			for(int i = 0; i < chld.length; i++) {
				try {
					JarFile jf = new JarFile(chld[i]);
					firmwares.add(new Firmware(chld[i].getAbsolutePath(),jf.getManifest().getMainAttributes().getValue("device"), jf.getManifest().getMainAttributes().getValue("version"), jf.getManifest().getMainAttributes().getValue("branding")));
				}
				catch (Exception e) {e.printStackTrace();}
			}
	  }

	  public Firmware getFirstFirmware() {
		  if (!firmwares.hasFirmwares()) return new Firmware();
		  else {
			  return firmwares.getContent().get(0);
		  }
		  
	  }

}