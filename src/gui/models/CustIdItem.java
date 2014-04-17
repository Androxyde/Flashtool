package gui.models;

import org.system.DeviceEntry;
import org.system.Devices;

public class CustIdItem {

	private String model;
	TableLine iddef;
	
	public CustIdItem(String pmodel, TableLine piddef) {
		model = pmodel;
		iddef = piddef;
	}
	
	public String getModel() {
		return model;
	}
	
	public TableLine getDef() {
		return iddef;
	}

	public DeviceEntry getDevice() {
			String id = Devices.getIdFromVariant(model);
			return Devices.getDevice(id);
	}
}
