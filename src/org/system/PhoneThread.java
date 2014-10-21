package org.system;


public class PhoneThread extends Thread {

	boolean done = false;
	boolean paused = false;
	boolean forced = false;
	String status = "";
	
	StatusListener listener;

	public void run() {
		this.setName("Phonne-connect-watchdog");
		int count = 0;
		int nbnull = 0;
		DeviceIdent id=null;
		while (!done) {
			if (!paused) {
				id = Devices.getConnectedDevice();
				if (id.getPid().equals("ADDE"))
					GlobalState.setState(id.getSerial(), id.getPid(), "flash");
				else if (id.getPid().equals("0DDE"))
					GlobalState.setState(id.getSerial(), id.getPid(), "fastboot");
				if (id.getStatus().length()==0) {
					nbnull++;
					if (nbnull==5) GlobalState.setState(id.getSerial(), id.getPid(), "normal");
				}
				else {
					nbnull=0;
					String lstatus= id.getStatus();
					if (!lstatus.equals(status)) {
						if (!lstatus.equals("adb"))
							fireStatusChanged(new StatusEvent(lstatus,id.isDriverOk()));
						status = lstatus;
					}
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
