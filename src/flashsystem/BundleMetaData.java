package flashsystem;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.util.MyTreeSet;

public class BundleMetaData {

	MyTreeSet<Category> _categwipe = new MyTreeSet<Category>();
	MyTreeSet<Category> _categex = new MyTreeSet<Category>();
	Category loader=null;
	Category fsc=null;
	Vector<String> noerase = new Vector<String>();
	
	public BundleMetaData() {
	}

	public String getNoEraseAsString() {
		return noerase.toString().replace("[", "").replace("]", "").replace(" ", "");
	}
	public void setNoErase(String noerase) {
		if (noerase!=null) {
			if (noerase.toUpperCase().equals(noerase)) {
				String tovect[] = noerase.split(",");
				for (int i=0;i<tovect.length;i++)
					this.noerase.addElement(tovect[i]);
			}
			else {
				String noerasecateg="";
				String lnoerase[]=noerase.split(",");
				for (int i=0;i<lnoerase.length;i++) {
					this.noerase.addElement(BundleEntry.getShortName(lnoerase[i]).toUpperCase());
				}
			}
		}
		if (this.noerase.size()==0) {
			this.noerase.addElement("APPS_LOG");
			this.noerase.addElement("USERDATA");
			this.noerase.addElement("SSD");
			this.noerase.addElement("DIAG");
			this.noerase.addElement("B2B");
		}
		this.noerase.addElement("SIMLOCK");
	}
	
	public Category getLoader() {
		return loader;
	}
	
	public Category getFsc() {
		return fsc;
	}
	
	public Set<Category> getWipe() {
		return _categwipe;
	}

	public Set<Category> getExclude() {
		return _categex;
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

	public void setLoader(BundleEntry f) throws Exception {
		Category cat = new Category();
		cat.setId(f.getCategory());
		cat.addEntry(f);
		loader=cat;
	}

	public void setFsc(BundleEntry f) throws Exception {
		Category cat = new Category();
		cat.setId(f.getCategory());
		cat.addEntry(f);
		fsc=cat;
	}

	public void process(BundleEntry f) throws Exception {
		if (f.getName().equals("fwinfo.xml")) return;
		Category cat = new Category();
		cat.setId(f.getCategory());
		cat.addEntry(f);

		if (cat.getId().equals("LOADER")) {
			loader=cat;
			return;
		}

		if (cat.getId().equals("FSC")) {
			fsc=cat;
			return;
		}

		if (noerase.contains(cat.getId())){
			cat.setEnabled(false);
			_categwipe.add(cat);
		}
		else {
			cat.setEnabled(true);
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
		if (f.getCategory().equals("FSC")) {
			fsc=null;
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
		Category c = _categex.get(categ);
		if (c==null) c = _categwipe.get(categ);
		return c;
	}
	
	public void clear() {
		_categex.clear();
		_categwipe.clear();
	}
}