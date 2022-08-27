package org.flashtool.gui.models;

import java.util.Iterator;
import java.util.Vector;

import org.flashtool.gui.TARestore;
import org.flashtool.parsers.ta.TAUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TADevice {

	String model = "";
	String serial = "";
	Vector<TABag> tabags=null;
	
	public TADevice() {
	}
	
	public void addBags(Vector<TABag> bags) {
		tabags = bags;
		for (int i=0;i<tabags.size();i++) {
			if (tabags.get(i).partition==2) {
				Iterator<TAUnit> iu = bags.get(i).available.iterator();
				while (iu.hasNext()) {
					TAUnit u = iu.next();
					if (u.getUnitHex().equals("000008A2")) model = new String(u.getUnitData());
					if (u.getUnitHex().equals("00001324")) serial = new String(u.getUnitData());
				}				
			}
		}
	}

	public Vector<TABag> getBags() {
		return tabags;
	}

	public String getModel() {
		return model;
	}

	public String getSerial() {
		return serial;
	}
}