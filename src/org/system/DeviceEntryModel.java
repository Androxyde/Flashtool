package org.system;

import java.io.File;

public class DeviceEntryModel {

	DeviceEntry _parent;
	String modelId;
	DeviceEntryModelUpdater mu = null;
	
	public DeviceEntryModel(DeviceEntry p, String pmodelId) {
		_parent = p;
		modelId = pmodelId;
		mu = new DeviceEntryModelUpdater(_parent, modelId);
	}
	
	public String getTac() {
		return mu.tac8;
	}
	
	public String getId() {
		return modelId;
	}
	
	public PropertiesFile getCDA() {
		return mu._custid;
	}
	
	public DeviceEntryModelUpdater getUpdater() {
		return mu;
	}
	
	public boolean canShowUpdates() {
		return mu.tac8.length()>0;
	}

}