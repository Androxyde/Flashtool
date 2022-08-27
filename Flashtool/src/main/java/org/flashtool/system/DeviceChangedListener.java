package org.flashtool.system;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceChangedListener {

	public static PhoneThread usbwatch;
	
	public static void starts(StatusListener listener) {
		usbwatch = new PhoneThread();
		usbwatch.addStatusListener(listener);
		usbwatch.start();
	}
	
	public static void stop() {
		try {
			usbwatch.end();
			usbwatch.join();
		}
		catch (Exception e) {}
	}
	
	public static void enableDetection() {
		usbwatch.pause(false);
	}

	public static void disableDetection() {
		usbwatch.pause(true);
	}
	
	public static void forceDetection() {
		usbwatch.forceDetection();
	}
}