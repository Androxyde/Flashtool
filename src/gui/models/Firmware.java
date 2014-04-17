package gui.models;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import flashsystem.Bundle;

public class Firmware {
	  private String fileName;
	  private String device;
	  private String version;
	  private String branding;
	  private Firmwares firmwares;
	  private Bundle bundle;

	  private List content;

	  public Firmware() {
		  content = new LinkedList();
	  }
	  public Firmware(String pfilename, String pdevice, String pversion, String pbranding) {
	    fileName = pfilename;
	    device = pdevice;
	    version = pversion;
	    branding = pbranding;
	    content = new LinkedList();
	    bundle = new Bundle(pfilename,Bundle.JARTYPE);
		Enumeration<String> e = bundle.getMeta().getAllEntries(true);
		while (e.hasMoreElements()) {
			String elem = e.nextElement();
			add(new Content(elem));
	    }
	  }

	  public String getFilename() {
	    return (new File(fileName)).getName();
	  }

	  public String getDevice() {
	    return device;
	  }

	  public String getVersion() {
		    return version;
	  }
	  
	  public String getBranding() {
		    return branding;
	  }

	  public Bundle getBundle() {
		  return bundle;
	  }
	  
	  public boolean add(Content fcontent) {
	    boolean added = content.add(fcontent);
	    if (added)
	      fcontent.setFirmware(this);
	    return added;
	  }

	  public void disableCateg(String categ) {
		  bundle.getMeta().setCategEnabled(categ, false);
		  content.clear();
			Enumeration<String> e = bundle.getMeta().getAllEntries(true);
			while (e.hasMoreElements()) {
				String elem = e.nextElement();
				add(new Content(elem));
		    }

	  }
	  
	  public void enableCateg(String categ) {
		  bundle.getMeta().setCategEnabled(categ, true);
		  content.clear();
			Enumeration<String> e = bundle.getMeta().getAllEntries(true);
			while (e.hasMoreElements()) {
				String elem = e.nextElement();
				add(new Content(elem));
		    }
	  }
	  /**
	   * Gets the players
	   * 
	   * @return List
	   */
	  public List getContent() {
	    return Collections.unmodifiableList(content);
	  }

	  public void setFirmwares(Firmwares f) {
		  firmwares=f;
	  }
}
