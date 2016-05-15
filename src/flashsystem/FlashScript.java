package flashsystem;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.sinfile.parsers.SinFile;
import org.system.OS;
import org.system.TextFile;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;

import gui.tools.XMLBootConfig;

public class FlashScript {

	Vector<String> categories = new Vector<String>();
	Vector<Long> units = new Vector<Long>();
	private XMLBootConfig bc=null;
	
	public FlashScript(String fsc) {
		TextFile flashscript = new TextFile(fsc,"ISO8859-1");
		try {
			Map<Integer,String> map =  flashscript.getMap();
			Iterator<Integer> keys = map.keySet().iterator();
    		while (keys.hasNext()) {
    			String line = map.get(keys.next());
    			String param="";
    			String[] parsed = line.split(":");
    			String action = parsed[0];
    			if (parsed.length>1) param=parsed[1];
    			if (action.equals("uploadImage")) categories.add(param.toUpperCase());
    			if (action.equals("writeTA")) units.add(Long.parseLong(param));
    		}
		} catch (Exception e) {}
	}

	public boolean hasCategory(Category category) {
		if (category.isBootDelivery()) {
			if (bc==null) return false;
			Iterator ifiles = bc.getFiles().iterator();
			while (ifiles.hasNext()) {
				String file = (String)ifiles.next();
				if (!categories.contains(SinFile.getShortName(file).toUpperCase()))
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
		if (!category.isTa())
			return categories.contains(category.getId());
		try {
			TAFileParser tf = new TAFileParser(new File(category.getEntries().iterator().next().getAbsolutePath()));
			Iterator<TAUnit> taul = tf.entries().iterator();
			while (taul.hasNext()) {
				TAUnit u = taul.next();
				if (!units.contains(u.getUnitNumber()))
					return false;
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

}