package gui.models;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import org.system.DeviceEntry;
import org.system.Devices;
import gui.tools.FtfFilter;

public class TreeDevices {

	  private List<TreeDevice> devices;
	  String devicefilter = "";

	  public TreeDevices(String srcFolder) {
		  devices = new LinkedList<TreeDevice>();
		  File dir = new File(srcFolder);
		  File[] chld = dir.listFiles(new FtfFilter(""));
		  for(int i = 0; i < chld.length; i++) {
				try {
					JarFile jf = new JarFile(chld[i]);
					String model = jf.getManifest().getMainAttributes().getValue("device");
					DeviceEntry ent = Devices.getDeviceFromVariant(model);
					if (ent!=null) {
						if (contains(ent.getId())) {
							TreeDevice dev = get(ent.getId());
							dev.addVariant(chld[i],jf);
						}
						else {
							TreeDevice dev=new TreeDevice(ent.getId(),ent.getName(),chld[i],jf);
							devices.add(dev);							
						}
					}
					jf.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
	  }

	  public void setDeviceFilter(String id) {
		  devicefilter = id;
	  }
	 
	  public List<TreeDevice> getContent() {
		  LinkedList<TreeDevice> filtereddevices = new LinkedList<TreeDevice>();
		  if (devicefilter.length()>0) {
			  DeviceEntry entry = Devices.getDevice(devicefilter);
			  Iterator<TreeDevice> i = devices.listIterator();
			  while (i.hasNext()) {
				  TreeDevice f = i.next();
				  if (f.getDevice().equals(devicefilter))
					  filtereddevices.add(f);
			  }
			  return filtereddevices;
		  }
		  else
			  return devices;
	  }

	  public boolean hasDevices() {
		  return !getContent().isEmpty();
	  }

	  public boolean contains(String devid) {
		  Iterator<TreeDevice> idev = devices.iterator();
		  while (idev.hasNext()) {
			  TreeDevice dev = idev.next();
			  if (dev.getDevice().equals(devid)) return true; 
		  }
		  return false;
	  }
	  
	  public TreeDevice get(String devid) {
		  Iterator<TreeDevice> idev = devices.iterator();
		  while (idev.hasNext()) {
			  TreeDevice dev = idev.next();
			  if (dev.getDevice().equals(devid)) return dev; 
		  }
		  return null;		  
	  }
	  
}