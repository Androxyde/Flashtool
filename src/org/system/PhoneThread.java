package org.system;


public class PhoneThread extends Thread {

	boolean done = false;
	boolean paused = false;
	boolean forced = false;
	String status = "none";
	String pid = "";
	
	StatusListener listener;

	public void run() {
		this.setName("Phonne-connect-watchdog");
		int count = 0;
		DeviceIdent id=null;
		int nbstatustocount=0;
		String lstatus="";
		while (!done) {
			if (!paused) {
				id = Devices.getConnectedDevice();
				if (!pid.equals(id.getPid())) {
					nbstatustocount=0;
					pid=id.getPid();
				}
				else {
					if (nbstatustocount<Integer.parseInt(GlobalConfig.getProperty("usbdetectthresold"))) nbstatustocount++;
					else lstatus=id.getStatus();
				}
				if (!status.equals(lstatus) && (nbstatustocount==Integer.parseInt(GlobalConfig.getProperty("usbdetectthresold")))) {
					status=lstatus;
					if (status.equals("adb")) {
						DeviceProperties.reload();
					}
					fireStatusChanged(new StatusEvent(lstatus,id.isDriverOk()));
				}
			}
			try {
				while ((count<50) && (!done)) {
					sleep(10);
					count++;
				}
				count = 0;
			} catch (Exception e) {}
		}
		Devices.clean();
	}

	public void pause(boolean ppaused) {
		paused = ppaused;
		status="paused";
	}

	public void end() {
		done = true;
	}

	public void addStatusListener(StatusListener listener) {
		this.listener = listener;
    }
    
    public void removeStatusListener(StatusListener listener) {
    	this.listener = null;
    }
    
    public StatusListener getStatusListeners() {
        return listener;
    }
    
    protected void fireStatusChanged(StatusEvent e) {
    	if (listener!=null)
		    listener.statusChanged(e);
    }
    
    public void forceDetection() {
    	if (!forced) forced = true;
    }
    
    public void doSleep(int time) {
    	try {
    		sleep(time);
    	}
    	catch (Exception e) {}
    }
}
