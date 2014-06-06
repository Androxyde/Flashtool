package org.system;

import gui.models.TableLine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class PropertiesFile {

	String rname;
	String fname;
	Properties props;
	
	public PropertiesFile() {	
		props=new Properties();
	}
	
	public void setProperties(Properties p) {
		props=p;
	}
	
	public void mergeWith(PropertiesFile pf) {
		Iterator i = pf.keySet().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			props.setProperty(key, pf.getProperty(key));
		}
	}
	
	public void open(String arname, String afname) {
		rname = arname;
		fname = afname;
        try {
        	props = new Properties();
        	Reader in = new InputStreamReader(new FileInputStream(fname), "UTF-8"); 
        	props.load(in);
        }
        catch (Exception e) {
        	try {
        		props = new Properties();
        		if (rname.length()>0) {
        			Reader in = new InputStreamReader(PropertiesFile.class.getClassLoader().getResourceAsStream(rname), "UTF-8");
        			props.load(in);
        		}
        	}
        	catch (Exception e1) {
        	}
        }		
	}
	
	public PropertiesFile(String arname, String afname) {
		open(arname, afname);
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public Set<Object> keySet() {
		return props.keySet();
	}
	
	public void setProperty(String key, String value) {
		props.setProperty(key, value);
	}
	
	public void write(String filename,String encoding) {
		try {
			new File(filename).getParentFile().mkdirs();
			Writer out = new OutputStreamWriter(new FileOutputStream(filename), encoding);
			props.store(out, null);
			out.flush();
			out.close();
		}
		catch (Exception e) {
		}		
	}

	public void setFileName(String filename) {
		fname = filename;
	}
	
	public void write(String encoding) {
		write(fname,encoding);
	}
	
	public Properties getProperties() {
		return props;
	}
	
	public void load(InputStream is) throws Exception {
		props.load(is);
	}
	
	public void remove(String key) {
		props.remove(key);
	}
	
	public Object[] toArray() {
		Vector<TableLine> v = new Vector<TableLine>();
		Enumeration<Object> e = props.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			TableLine l = new TableLine();
			l.add(key);
			l.add(props.getProperty(key));
			v.add(l);
		}
		return v.toArray();
	}
	
	
}