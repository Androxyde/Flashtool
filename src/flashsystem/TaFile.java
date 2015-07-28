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
	int _partition = 0;
	
	public TaFile(File f) throws TaParseException, FileNotFoundException {
		_taf = f;
		TaEntry entry = new TaEntry();
		_in = new FileInputStream(f);
	    _scanner = new Scanner (_in);
	    String partition="";
	    boolean beginentry=false;
	    while (_scanner.hasNextLine()) {
	    	String line = _scanner.nextLine().trim();
	    	if (line.startsWith("/")) continue;
	    	if (!(line.length()<=4)) {
	    		Scanner scanline = new Scanner(line);
	    		scanline.useDelimiter(" ");
	    		while (scanline.hasNext()) {
	    			String elem = scanline.next();
	    			if (elem.length()==8) {
	    				if (entry.getUnit().length()==0) {
	    					entry.setUnit(elem);
	    					String size=scanline.next();
	    					if (size.length()==4) entry.setSize(size);
	    					else throw new TaParseException("Next to unit should be the size on 4 digits");
	    				}
	    				else {
	    					entry.close();
	    					entries.add(entry);
	    					entry = new TaEntry();
	    					entry.setUnit(elem);
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
	    	if(_partition == 0 && line.length() == 2){
                try {
                    _partition = Integer.parseInt(line);
                    if (_partition !=1 && _partition != 2) {
                    	throw new TaParseException("TA partition should be 1 or 2");
                    }
                } catch (NumberFormatException  e) {

                }
            }
	    }

	    if(_partition == 0){
            _partition = 2;
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

    public int getPartition() {
        return _partition;
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