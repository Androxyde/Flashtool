package org.flashtool.gui.models;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.flashtool.gui.TARestore;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Firmwares {

	  private List firmwares;
	  String devid = "";

	  public Firmwares() {
	    firmwares = new LinkedList();
	  }


	  public void setDevice(String id) {
		  devid = id;
	  }
	 
	  public boolean add(Firmware firm) {
	    boolean added = firmwares.add(firm);
	    if (added)
	      firm.setFirmwares(this);
	    return added;
	  }

	  /**
	   * Gets the players
	   * 
	   * @return List
	   */
	  public List<Firmware> getContent() {
		  LinkedList filteredfirmwares = new LinkedList();
		  if (devid.length()>0) {
			  DeviceEntry entry = Devices.getDevice(devid);
			  Iterator i = firmwares.listIterator();
			  while (i.hasNext()) {
				  Firmware f = (Firmware)i.next();
				  if (entry.getVariantList().contains(f.getDevice()))
					  filteredfirmwares.add(f);
			  }
			  return filteredfirmwares;
		  }
		  else
			  return Collections.unmodifiableList(firmwares);
	  }

	  public boolean hasFirmwares() {
		  return !getContent().isEmpty();
	  }
	  
}
