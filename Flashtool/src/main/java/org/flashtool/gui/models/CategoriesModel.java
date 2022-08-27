package org.flashtool.gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.flashtool.flashsystem.BundleMetaData;
import org.flashtool.flashsystem.Category;
import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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