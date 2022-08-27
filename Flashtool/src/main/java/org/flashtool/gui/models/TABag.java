package org.flashtool.gui.models;

import java.io.File;
import java.util.Vector;

import org.flashtool.gui.TARestore;
import org.flashtool.parsers.ta.TAFileParser;
import org.flashtool.parsers.ta.TAUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public TABag(int partition) {
		this.partition = partition;
		available = new Vector<TAUnit>();
		toflash = new Vector<TAUnit>();
	}

	public void addUnit(TAUnit unit) {
		available.add(unit);
	}
}