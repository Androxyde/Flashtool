package flashsystem;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.sinfile.parsers.SinFile;
import org.system.TextFile;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;
import gui.tools.XMLBootConfig;
import gui.tools.XMLPartitionDelivery;

public class FlashScript {

	Vector<String> categories = new Vector<String>();
	Vector<Long> units = new Vector<Long>();
	private XMLBootConfig bc=null;
	private XMLPartitionDelivery pd=null;
	
	public FlashScript(String fsc) {
		TextFile flashscript = new TextFile(fsc,"ISO8859-1");
		try {
			Map<Integer,String> map =  flashscript.getMap();
			Iterator<Integer> keys = map.keySet().iterator();
    		while (keys.hasNext()) {
    			String line = map.get(keys.next());
    			String param1="";
    			String param2="";
    			String[] parsed = line.split(":");
    			String action = parsed[0];
    			if (parsed.length>1) param1=parsed[1];
    			if (parsed.length>2) param2=parsed[2];
    			if (action.equals("uploadImage")) {
    				if (param1.toUpperCase().equals("PARTITION")) {
    					categories.add("PARTITION-IMAGE");
    				}
    				categories.add(Category.getCategoryFromName(param1));
    			}
    			if (action.equals("writeTA")) units.add(Long.parseLong(param1));
    			if (action.equals("flash")) categories.add(Category.getCategoryFromName(param2));
    			if (action.equals("Repartition")) {
    				categories.add(Category.getCategoryFromName(param2));
    			}
    			if (action.equals("Write-TA")) units.add(Long.parseLong(param2));
    		}
		} catch (Exception e) {}
	}

	public boolean hasCategory(Category category) {
		
		if (category.isPartitionDelivery()) {
			if (pd==null) return false;
			Iterator ifiles = pd.getFiles().asIterator();
			while (ifiles.hasNext()) {
				String file = (String)ifiles.next();
				if (!categories.contains(Category.getCategoryFromName(file)))
						return false;
			}
			return true;
		}
		if (category.isBootDelivery()) {
			if (bc==null) return false;
			Iterator ifiles = bc.getFiles().iterator();
			while (ifiles.hasNext()) {
				String file = (String)ifiles.next();
				if (!categories.contains(Category.getCategoryFromName(file)))
						return false;
			}
			try {
				TAFileParser tf = new TAFileParser(new File(bc.getTA()));
				Iterator<TAUnit> taul = tf.entries().iterator();
				while (taul.hasNext()) {
					TAUnit u = taul.next();
					if (!units.contains(u.getUnitNumber()))
						return false;
				}
			}
			catch (Exception e) {
				return false;
			}
			return true;
		}
		if (!category.isTa()) {
			if (categories.contains(category.getId())) return true;
			Enumeration ecategs = categories.elements();
			while (ecategs.hasMoreElements()) {
				String elem = (String)ecategs.nextElement();
				if (category.getId().equals(SinFile.getShortName(elem))) return true;
			}
			return false;
		}
		try {
			TAFileParser tf = new TAFileParser(new File(category.getEntries().iterator().next().getAbsolutePath()));
			Iterator<TAUnit> taul = tf.entries().iterator();
			while (taul.hasNext()) {
				TAUnit u = taul.next();
				if (!units.contains(u.getUnitNumber())) {
					return false;
				}
			}
			return true;
		}
		catch (Exception e) {
			
			return false;
		}
	}
	
	public void setBootConfig(XMLBootConfig bc) {
		this.bc=bc;
	}

	public void setPartitionDelivery(XMLPartitionDelivery pd) {
		this.pd=pd;
	}

}