package gui.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Firmwares {

	  private List firmwares;

	  public Firmwares() {
	    firmwares = new LinkedList();
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
	    return Collections.unmodifiableList(firmwares);
	  }

	  public boolean hasFirmwares() {
		  return !firmwares.isEmpty();
	  }
	  
}
