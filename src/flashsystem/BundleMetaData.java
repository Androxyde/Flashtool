package flashsystem;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.util.MyTreeSet;
//import org.lang.Language;

public class BundleMetaData {

	MyTreeSet<Category> _categwipe = new MyTreeSet<Category>();
	MyTreeSet<Category> _categex = new MyTreeSet<Category>();
	MyTreeSet<Category> _categta = new MyTreeSet<Category>();
	Category loader=null;
	
	public BundleMetaData() {
	}

	public Category getLoader() {
		return loader;
	}
	
	public Set<Category> getWipe() {
		return _categwipe;
	}

	public Set<Category> getExclude() {
		return _categex;
	}

	public Set<Category> getTA() {
		return _categta;
	}
	
	public Set<Category> getAllEntries(boolean checked) {
		MyTreeSet<Category> _categ = new MyTreeSet<Category>();
		Iterator<Category> i = getExclude().iterator();
		while (i.hasNext()) {
			Category c = i.next();
			if (checked) {
				if (c.isEnabled()) _categ.add(c);
			}
			else _categ.add(c);
		}
		i = getWipe().iterator();
		while (i.hasNext()) {
			Category c = i.next();
			if (checked) {
				if (c.isEnabled()) _categ.add(c);
			}
			else _categ.add(c);
		}
		return _categ;
	}

	public Set<Category> getTAEntries(boolean checked) {
		MyTreeSet<Category> _categ = new MyTreeSet<Category>();
		Iterator<Category> i = getExclude().iterator();
		while (i.hasNext()) {
			Category c = i.next();
			if (checked) {
				if (c.isEnabled()) _categ.add(c);
			}
			else _categ.add(c);
		}
		i = getWipe().iterator();
		while (i.hasNext()) {
			Category c = i.next();
			if (checked) {
				if (c.isEnabled()) _categ.add(c);
			}
			else _categ.add(c);
		}
		return _categ;
	}

	public void process(BundleEntry f) throws Exception {
		if (f.getName().equals("fwinfo.xml")) return;
		Category cat = new Category();
		cat.setId(f.getCategory());
		cat.addEntry(f);
		if (cat.getId().startsWith("APPS_LOG") ||
			cat.getId().startsWith("USERDATA") ||
			cat.getId().startsWith("SSD") ||
			cat.getId().startsWith("DIAG") ||
			cat.getId().startsWith("B2B")
		   ) {
			cat.setEnabled(false);
			_categwipe.add(cat);
		}
		else {
			cat.setEnabled(true);
			if (cat.getId().equals("LOADER")) loader=cat;
			else
				_categex.add(cat);
		}
	}
	
	public void setCategEnabled(String categ, boolean enabled) {
		Category c = _categex.get(categ);
		if (c==null) c = _categwipe.get(categ);
		if (c!=null) c.setEnabled(enabled);
	}

	public void remove(BundleEntry f) {
		if (f.getCategory().equals("LOADER")) {
			loader=null;
			return;
		}
		try {
			_categex.remove(get(f.getCategory()));
		} catch (Exception e) {}
		try {
			_categwipe.remove(get(f.getCategory()));
		} catch (Exception e) {}
	}
	
	public Category get(String categ) {
		//if (categ.equals("LOADER")) return loader;
		Category c = _categex.get(categ);
		if (c==null) c = _categwipe.get(categ);
		return c;
	}
	
	public void clear() {
		_categex.clear();
		_categwipe.clear();
		_categta.clear();
	}
}