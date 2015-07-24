package gui.models;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import flashsystem.BundleEntry;
import flashsystem.Category;

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