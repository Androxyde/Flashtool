package org.flashtool.gui.models;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContentContentProvider implements IStructuredContentProvider {

	  /**
	   * Gets the elements for the table
	   * 
	   * @param arg0
	   *            the model
	   * @return Object[]
	   */
	  public Object[] getElements(Object arg0) {
		  return ((Firmware)arg0).getContent().toArray();
	  }

	  /**
	   * Disposes any resources
	   */
	  public void dispose() {
	  }

	  /**
	   * Called when the input changes
	   * 
	   * @param arg0
	   *            the parent viewer
	   * @param arg1
	   *            the old input
	   * @param arg2
	   *            the new input
	   */
	  public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	  }
}