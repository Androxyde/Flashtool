package gui.models;

import java.util.TreeMap;

import org.system.DeviceEntry;

public class Models extends TreeMap<String,ModelUpdater> {
	
	DeviceEntry _entry;
	
	public Models(DeviceEntry entry) {
		_entry = entry;
	}

	public DeviceEntry getDevice() {
		return _entry;
	}

}