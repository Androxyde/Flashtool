package org.system;

public class DeviceChangedListener {

	public static PhoneThread usbwatch;
	
	public static void start() {
		usbwatch = new PhoneThread();
		usbwatch.start();
	}
	
	public static void stop() {
		try {
			usbwatch.end();
			usbwatch.join();
		}
		catch (Exception e) {}
	}
	
	public static void pause(boolean paused) {
		usbwatch.pause(paused);
	}

	public static void addStatusListener(StatusListener listener) {
		usbwatch.addStatusListener(listener);
	}
	
	public static void forceDetection() {
		usbwatch.forceDetection();
	}
}