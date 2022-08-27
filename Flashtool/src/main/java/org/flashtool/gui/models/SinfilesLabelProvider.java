package org.flashtool.gui.models;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.flashtool.flashsystem.BundleEntry;
import org.flashtool.flashsystem.Category;
import org.flashtool.gui.TARestore;
import org.flashtool.windowbuilder.swt.SWTResourceManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SinfilesLabelProvider extends LabelProvider {
	
	private static final Image FOLDER = SWTResourceManager.getImage(SinfilesLabelProvider.class,"/gui/ressources/folder.gif");
	private static final Image FILE = SWTResourceManager.getImage(SinfilesLabelProvider.class,"/gui/ressources/file.gif");

	@Override
	  public String getText(Object element) {
	    if (element instanceof Category) {
	      Category category = (Category) element;
	      return category.getId();
	    }
	    return ((BundleEntry) element).getInternal();
	  }

	  @Override
	  public Image getImage(Object element) {
	    if (element instanceof Category) {
	      return FOLDER;
	    }
	    return FILE;
	  }

	} 