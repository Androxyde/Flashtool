package gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import flashsystem.BundleMetaData;

public class CategoriesModel  {

	List<Category> categories;

	public CategoriesModel(BundleMetaData meta) {
		refresh(meta);
	}

	public void refresh(BundleMetaData meta) {
		categories = new ArrayList<Category>();
		Enumeration<String> c = meta.getCategories();
		while (c.hasMoreElements()) {
		    Category category = new Category();
		    category.setId(c.nextElement());
		    category.setName(meta.getCategorie(category.getId()));
		    Enumeration<String> e = meta.getEntriesOf(category.getId(), true);
		    while (e.hasMoreElements()) {
		    	File f = new File(e.nextElement());
		    	category.addSinfile(f);
		    }
		    categories.add(category);
		}		
	}
	
	public List<Category> getCategories() {
		return categories;
	}

} 