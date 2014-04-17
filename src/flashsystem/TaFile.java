package flashsystem;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

/** Demonstrate using Scanner to read a file. **/
public class TaFile {
  
	File _taf;
	FileInputStream _in;
	Scanner _scanner;
	Vector<TaEntry> entries = new Vector<TaEntry>();
	
	public TaFile(File f) throws TaParseException, FileNotFoundException {
		_taf = f;
		TaEntry entry = new TaEntry();
		_in = new FileInputStream(f);
	    _scanner = new Scanner (_in);
	    String partition="";
	    boolean beginentry=false;
	    while (_scanner.hasNextLine()) {
	    	String line = _scanner.nextLine().trim();
	    	if (!line.startsWith("/") && !(line.length()<=4)) {
	    		Scanner scanline = new Scanner(line);
	    		scanline.useDelimiter(" ");
	    		while (scanline.hasNext()) {
	    			String elem = scanline.next();
	    			if (elem.length()==8) {
	    				if (entry.getPartition().length()==0) {
	    					entry.setPartition(elem);
	    					String size=scanline.next();
	    					if (size.length()==4) entry.setSize(size);
	    					else throw new TaParseException("Next to unit should be the size on 4 digits");
	    				}
	    				else {
	    					entry.close();
	    					entries.add(entry);
	    					entry = new TaEntry();
	    					entry.setPartition(elem);
	    					String size=scanline.next();
	    					if (size.length()==4) entry.setSize(size);
	    					else throw new TaParseException("Next to unit should be the size on 4 digits");
	    				}
	    			}
	    			else {
	    				if (elem.length()==2)
	    					entry.addData(elem);
	    			}
	    		}
	    	}
	    }
	    entry.close();
	    entries.add(entry);
	    try {
	    	_in.close();
	    }
	    catch (Exception e) {}
	}

	public Vector<TaEntry> entries() {
		return entries;
	}
	
	public String getName() {
		return _taf.getName();
	}
	
	public String toString() {
		String result = "";
		Iterator<TaEntry> i = entries().iterator();
		while (i.hasNext()) {
			TaEntry entry = i.next();
			result=result + entry+"\n"; 
		}
		return result;
	}
}