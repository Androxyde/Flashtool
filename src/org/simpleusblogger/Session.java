package org.simpleusblogger;

import gui.models.TableLine;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.sinfile.parsers.SinFile;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.TextFile;
import org.ta.parsers.TAUnit;
import org.util.BytesUtil;
import org.util.HexDump;

public class Session {

	private Vector<S1Packet> packets = new Vector<S1Packet>();
	private String model;
	private String version="";

	public void addPacket(S1Packet p) {
		if (packets.size()>0) {
			S1Packet pp = packets.get(packets.size()-1);
			if ((pp.getCommand()==0x0C) && pp.getDirection().equals("WRITE")) {
				if (BytesUtil.getInt(pp.data)==0x000008A2)
					model = new String(p.data);
			}
		}
		if (p.getSin().startsWith("elabel")) {
			version = p.getSin().split("_")[2];
		}
		packets.add(p);
	}

	public String getModel() {
		return model;
	}

	public Iterator<S1Packet> getPackets() {
		return packets.iterator();
	}

	public String saveScript() {
		try {
			DeviceEntry ent = Devices.getDeviceFromVariant(model);
			
			String folder = "";
			
			if (ent!=null)
				folder = ent.getMyDeviceDir();
			else
				folder = OS.getFolderFirmwaresScript();
			
			String filename = folder+File.separator+model+(version.length()>0?"_"+version:"")+".fsc";
			TextFile tf = new TextFile(filename,"ISO8859-1");
			
			tf.open(false);
			Iterator<TableLine> i = getScript().iterator();
			while (i.hasNext()) {
				TableLine tl = i.next();
				tf.writeln(tl.getValueOf(0)+":"+tl.getValueOf(1));
			}
			tf.close();
			return filename;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public Vector<TableLine> getScript() {
		Vector<TableLine> v = new Vector<TableLine>();
		Iterator<S1Packet> i = packets.iterator();
		int count=0;
		boolean start = false;
		while (i.hasNext()) {
			S1Packet p = i.next();
				TableLine tl = new TableLine();
/*				if (p.getDirection().equals("READ REPLY")) {
					if (p.getCommand()==1) {
						tl.add("readLoaderInfos");
						tl.add("");
						v.add(tl);
					}
				}*/
				if (p.getDirection().equals("WRITE")) {
					if (p.getCommand()==0x0D) {
						if (p.getTA().getUnitNumber()==10100) {
							tl.add("setFlashState");
							tl.add(Integer.toString(BytesUtil.getInt(p.getTA().getUnitData())));
						}
						else if (p.getTA().getUnitNumber()==10021) {
							tl.add("setFlashTimestamp");
							tl.add("");
						}
						else {
							tl.add("writeTA");
							tl.add(Long.toString(p.getTA().getUnitNumber()));
						}
						v.add(tl);
					}
					else {
						tl.add(p.getCommandName());
						if (p.getCommand()==0x05) {
							tl.add(SinFile.getShortName(p.sinname));
						}
						else if (p.getCommand()==0x09)
							tl.add(Integer.toString(BytesUtil.getInt(p.getData())));
						else if (p.getCommand()==0x0C)
							tl.add(HexDump.toHex(p.getData()));
						else if (p.getCommand()==0x19)
							tl.add(HexDump.toHex(p.getData()));
						else tl.add("");
						v.add(tl);
					}
				}
			count++;
		}
		return v;
	}

}
