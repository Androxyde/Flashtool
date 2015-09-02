package flashsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sinfile.parsers.SinFileException;

public class Category implements Comparable {
		  
	private String id;
	private List<BundleEntry> entries = new ArrayList<BundleEntry>();
	private boolean enabled = false;
	private boolean issin = false;
	private boolean ista = false;
	private boolean ispartition = false;
	private boolean issecro = false;
	private boolean ispreload = false;
	private boolean iselabel = false;
	private boolean issystemuser = false;
	private boolean issw = false;

		  public String getId() {
		    return id;
		  }

		  public void setId(String id) {
		    this.id = id;
		  }

		  public List<BundleEntry> getEntries() {
		    return entries;
		  }
		  
		  public void addEntry(BundleEntry f) throws SinFileException {
			  entries.add(f);
			  if (f.getName().endsWith(".sin")) issin=true;
			  if (f.getName().endsWith(".ta")) ista=true;
			  if (issin) {
				  if (f.getName().toUpperCase().contains("PARTITION")) {
						ispartition = true;
				  }
				  else if (f.getName().toUpperCase().contains("SECRO")) {
						issecro = true;
				  }
				  else if (f.getName().toUpperCase().contains("PRELOAD")) {
					  	ispreload = true;
				  }
				  else if (f.getName().toUpperCase().contains("ELABEL")) {
						iselabel = true;
				  }
				  else if (f.getName().toUpperCase().contains("SYSTEM") || f.getName().toUpperCase().contains("USER") || f.getName().toUpperCase().contains("OEM") || f.getName().toUpperCase().contains("VENDOR") || f.getName().toUpperCase().contains("B2B") || f.getName().toUpperCase().contains("SSD")) {
						issystemuser = true;
				  }
				  else
					  issw = true;
			  }
		  }

		  public boolean isPartition() {
			  return ispartition;
		  }

		  public boolean isSecro() {
			  return issecro;
		  }

		  public boolean isPreload() {
			  return ispreload;
		  }

		  public boolean isElabel() {
			  return iselabel;
		  }

		  public boolean isSystem() {
			  return issystemuser;
		  }

		  public boolean isSoftware() {
			  return issw;
		  }

		  public String toString() {
			  return id;
		  }

		  public boolean isTa() {
			  return ista;
		  }
		  
		  public boolean isSin() {
			  return issin;
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
