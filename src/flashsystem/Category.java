package flashsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Category implements Comparable {
		  
	private String id;
	private String fname;
	private String fpath;
	private List<BundleEntry> entries = new ArrayList<BundleEntry>();
	boolean enabled = false;

		  public String getId() {
		    return id;
		  }

		  public void setId(String id) {
		    this.id = id;
		  }

		  public List<BundleEntry> getEntries() {
		    return entries;
		  }
		  
		  public void addEntry(BundleEntry f) {
			  entries.add(f);
		  }
		  
		  public String toString() {
			  return id;
		  }

		  public boolean equals(Category c) {
			  return c.getId().equals(id);
		  }
		  
		  @Override
		  public int hashCode() { 
			  return id.hashCode();
		  }
		  
		  public boolean isEnabled() {
			  return enabled;
		  }
		  
		  @Override
		  public boolean equals(Object o) {
			  if (o instanceof String)
				  return id.equals((String)o);
			  if (o instanceof Category)
				  return ((Category)o).getId().equals(id);
			  return false;
		  }

		@Override
		public int compareTo(Object o) {
			return this.id.compareTo(((Category)o).getId());
		}
		
		public void setEnabled(boolean enabled) {
			this.enabled=enabled;
		}
		
		public static String getCategoryFromName(String name) {
			return BundleEntry.getShortName(name).toUpperCase();
		}
}
