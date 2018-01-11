package flashsystem;

import org.eclipse.swt.widgets.Shell;
import org.system.DeviceEntry;
import org.system.Devices;

public class FlasherFactory {

	public static Flasher getFlasher(Bundle b, Shell sh) {
		DeviceEntry ent = Devices.getDeviceFromVariant(b.getDevice());
		if (ent.getProtocol().equals("S1")) return new S1Flasher(b, sh);
		if (ent.getProtocol().equals("Command")) return new CommandFlasher(b, sh);
		return null;
	}

}