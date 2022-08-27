package org.flashtool.flashsystem;

import org.eclipse.swt.widgets.Shell;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;

public class FlasherFactory {

	public static Flasher getFlasher(Bundle b, Shell sh) {
		if (Devices.getConnectedDevice().getPid().equals("ADDE")) return new S1Flasher(b, sh);
		if (Devices.getConnectedDevice().getPid().equals("B00B")) return new CommandFlasher(b, sh);
		if (Devices.getDeviceFromVariant(b.getDevice()).getProtocol().equals("Command")) return new CommandFlasher(b, sh);
		return new S1Flasher(b, sh);
	}

}