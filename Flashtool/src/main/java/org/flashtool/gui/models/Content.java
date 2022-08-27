package org.flashtool.gui.models;

import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a player
 */

@Slf4j
public class Content {
  
  private Firmware firmware;

  private String entry;

  /**
   * Constructs an empty Player
   */
  public Content() {
    this(null);
  }

  public Content(String entry) {
    setEntry(entry);
  }

  public void setFirmware(Firmware team) {
    this.firmware = firmware;
  }

  public Firmware getFirmware() {
	    return firmware;
	  }

  public void setEntry(String entry) {
	    this.entry = entry;
  }

  public String getEntry() {
    return entry;
  }

}
