package org.ta.parsers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;


import gui.models.TABag;

public class TARawParser {
	JBBPBitInputStream ddStream = null;
	FileInputStream fin = null;
	BufferedInputStream bin = null;
	File ddFile = null;
	Vector<TABag> bags = new Vector<TABag>();

	JBBPParser partblock = JBBPParser.prepare(
	            "          <int magic;"
	                    + "<int hash;"
	            		+ "byte unknown;"
	            		+ "byte partnumber;"
	            		+ "byte partition;"
	            		+ "byte numblocks;"
	            		+ "byte[131072-12] units;"
	);

	public TARawParser(File ddfile) throws FileNotFoundException, IOException {
		ddFile = ddfile;
		openStreams();
		while (ddStream.hasAvailableData()) {
			TARawBlock parsedblock = partblock.parse(ddStream).mapTo(TARawBlock.class);
			if (parsedblock.magic==0x3BF8E9C1) {
				parsedblock.parseUnits();
				Iterator<TABag> ib = bags.iterator();
				TABag b  = null;
				boolean found = false;
				while (ib.hasNext()) {
					b = ib.next();
					if (b.partition==parsedblock.partition) {
						found=true;
						break;
					}
				}
				if (!found) b = new TABag(parsedblock.partition);
				Iterator<TAUnit> iu = parsedblock.getUnits().iterator();
				while (iu.hasNext()) {
					b.addUnit(iu.next());
				}
				if (!found) bags.add(b);
			}
		}
		closeStreams();
	}
 
	public void closeStreams() {
		try {
			ddStream.close();
		} catch (Exception e) {}
		try {
			fin.close();
		} catch (Exception e) {}
		try {
			bin.close();
		} catch (Exception e) {}
	}
	
	public void openStreams() throws FileNotFoundException {
		closeStreams();
		fin=new FileInputStream(ddFile);
		ddStream = new JBBPBitInputStream(fin);
	}

	public Vector<TABag> getBags() {
		return bags;
	}

}