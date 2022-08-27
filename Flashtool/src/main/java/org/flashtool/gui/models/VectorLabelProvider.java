package org.flashtool.gui.models;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.flashtool.gui.TARestore;
import org.flashtool.system.PropertiesFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VectorLabelProvider implements ITableLabelProvider {

	  // Constructs a PlayerLabelProvider
	  public VectorLabelProvider() {
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
		  if (arg0 instanceof TableLine) {
			  TableLine line = (TableLine) arg0;
			  return line.getValueOf(arg1);
		  }
		  return "";
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
