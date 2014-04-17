package org.system;

public class StatusEvent {
	String newstatus;
	boolean driverok;
	
	public StatusEvent(String news, boolean dok) {
		newstatus = news;
		driverok = dok;
	}
	
	public String getNew() {
		return newstatus;
	}
	
	public boolean isDriverOk() {
		return driverok;
	}
}
