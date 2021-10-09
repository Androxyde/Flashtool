package libusb;

import java.util.Iterator;
import java.util.Vector;

public class UsbDevList {

	Vector<UsbDevice> list = new Vector<UsbDevice>();
	
	public void addDevice(UsbDevice d) {
		try {
			d.setConfiguration();
			d.ref();
			list.add(d);
		} catch (Exception e) {};
	}

	public Iterator<UsbDevice> getDevices() {
		return list.iterator();
	}

	public void destroyDevices() {
		Iterator<UsbDevice> i = getDevices();
		while (i.hasNext()) {
			try {
				i.next().destroy();
			} catch (LibUsbException e) {}
		}
		list.clear();
	}

	public int size() {
		return list.size();
	}

	public UsbDevice get(int index) {
		return list.get(index);
	}

}