package gui.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Category {
		  
	private String id;
	private String name;
	private int sort;
	private List<File> sinfiles = new ArrayList<File>();

		  public String getId() {
		    return id;
		  }

		  public void setId(String id) {
		    this.id = id;
		  }

		  public String getName() {
			    return name;
		  }

		  public void setName(String name) {
			    this.name = name;
		  }

		  public int getSort() {
		    return sort;
		  }

		  public void setSort(int sort) {
		    this.sort = sort;
		  }

		  public List<File> getSinfiles() {
		    return sinfiles;
		  }
		  
		  public void addSinfile(File f) {
			  sinfiles.add(f);
		  }
}
