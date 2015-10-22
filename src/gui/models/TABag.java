package gui.models;

import java.io.File;
import java.util.Vector;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;

public class TABag {
	public Vector<TAUnit> available;
	public Vector<TAUnit> toflash;
	public int partition=0;
	public TABag(File file) {
		try {
			TAFileParser taf = new TAFileParser(file);
			available = taf.entries();
			toflash = new Vector<TAUnit>();
			partition = taf.getPartition();
		} catch (Exception e) {}
	}
}
