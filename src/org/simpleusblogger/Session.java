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
	private Vector<FlashCommand> commands = new Vector<FlashCommand>();
	private String readUnit="";
	private String model="";
	private String version="";

	public void addCommand(FlashCommand c) {
		commands.add(c);
		if (c.getCommand().startsWith("Read-TA")) {
			if (c.getUnit()==2210) model=new String(c.getReply());
			if (c.getUnit()==2202) {
				try {
				version = new String(c.getReply()).split("_")[1];
				} catch (ArrayIndexOutOfBoundsException npe1 ) {
					version = new String(c.getReply());	
				}
			}
		}
	}

	public void addPacket(S1Packet p) {
		if (p.getCommandName().equals("readTA")) {
			if (p.getDirection().equals("WRITE"))
				readUnit = Integer.toString(BytesUtil.getInt(p.data));
			else {
				if (readUnit.equals("2210")) {
					model = new String(p.data);
				}
				if (readUnit.equals("2206")) {
					version = new String(p.data).split("_")[1];
				}
				}
		}
		if (p.getCommandName().equals("closeTA") && packets.get(packets.size()-1).getCommandName().equals("openTA")) {
			packets.remove(packets.size()-1);
		}
		else if (!p.getCommandName().equals("readTA") && !p.getCommandName().equals("Get Error"))
			packets.add(p);
	}

	public String getModel() {
		return model;
	}

	public String getVersion() {
		return version;
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
				tf.writeln(tl.getValueOf(0)+(tl.getValueOf(1).length()>0?":":"")+tl.getValueOf(1));
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
		if (!packets.isEmpty()) {
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
		}
		if (!commands.isEmpty()) {
			Iterator<FlashCommand> ic = commands.iterator();
			while (ic.hasNext()) {
				FlashCommand c = ic.next();
				TableLine t1 = new TableLine();
				t1.add(c.getFinalCommand());
				t1.add(c.getParameters());
				v.add(t1);
			}
			
		}
		return v;
	}

}
