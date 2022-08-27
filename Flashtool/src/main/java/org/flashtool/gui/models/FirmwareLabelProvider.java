package org.flashtool.gui.models;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirmwareLabelProvider implements ITableLabelProvider {

	  // Constructs a PlayerLabelProvider
	  public FirmwareLabelProvider() {
	  }

	  public Image getColumnImage(Object arg0, int arg1) {
		  return null;
	  }
	  /**
	   * Gets the text for the specified column
	   * 
	   * @param arg0
	   *            the player
	   * @param arg1
	   *            the column
	   * @return String
	   */
	  public String getColumnText(Object arg0, int arg1) {
	    Firmware firmware = (Firmware) arg0;
	    String text = "";
	    switch (arg1) {
	    case 0:
	      text = firmware.getFilename();
	      break;
	    case 1:
	      text = firmware.getDeviceName();
	      break;
	    case 2:
	      text = firmware.getVersion();
	      break;
	    case 3:
	      text = firmware.getBranding();
	      break;
	    }
	    return text;
	  }

	  /**
	   * Adds a listener
	   * 
	   * @param arg0
	   *            the listener
	   */
	  public void addListener(ILabelProviderListener arg0) {
	    // Throw it away
	  }

	  /**
	   * Dispose any created resources
	   */
	  public void dispose() {
	  }

	  /**
	   * Returns whether the specified property, if changed, would affect the
	   * label
	   * 
	   * @param arg0
	   *            the player
	   * @param arg1
	   *            the property
	   * @return boolean
	   */
	  public boolean isLabelProperty(Object arg0, String arg1) {
	    return false;
	  }

	  /**
	   * Removes the specified listener
	   * 
	   * @param arg0
	   *            the listener
	   */
	  public void removeListener(ILabelProviderListener arg0) {
	    // Do nothing
	  }
	}
