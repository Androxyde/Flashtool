package gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import flashsystem.BundleMetaData;
import flashsystem.Category;

public class CategoriesModel  {

	List<Category> categories;

	public CategoriesModel(BundleMetaData meta) {
		refresh(meta);
	}

	public void refresh(BundleMetaData meta) {
		categories = new ArrayList<Category>();
		Iterator<Category> c = meta.getAllEntries(false).iterator();
		while (c.hasNext()) {
		    Category category = c.next();
		    categories.add(category);
		}
		if (meta.getLoader()!=null) categories.add(meta.getLoader());
	}
	
	public List<Category> getCategories() {
		return categories;
	}

} 