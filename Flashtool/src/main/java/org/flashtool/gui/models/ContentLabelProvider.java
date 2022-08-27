package org.flashtool.gui.models;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContentLabelProvider implements ITableLabelProvider {

	  // Constructs a PlayerLabelProvider
	  public ContentLabelProvider() {
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
	    Content content = (Content) arg0;
	    String text = "";
	    switch (arg1) {
	    case 0:
	      text = content.getEntry();
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
