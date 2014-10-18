package linuxlib;

import java.io.ByteArrayInputStream;

import org.util.BytesUtil;

import libusb.LibUsbException;
import libusb.UsbDevList;
import libusb.UsbDevice;
import libusb.UsbSystem;

public class JUsb {
	
	private static byte[] data = new byte[512];
	private static UsbSystem us=null;
	private static UsbDevice dev=null;
	private static String VendorId = "";
	private static String DeviceId = "";
	private static String Serial = "";
	
	public static void init() throws LibUsbException {
		us = new UsbSystem();
	}
	
	public static String getVersion() {
		if (us==null) return "";
		return "libusb version " + us.getVersion();
	}
	
	public static void fillDevice(boolean destroy) {
		dev = getDevice();
		if (dev!=null) {
			VendorId = dev.getVendor().toUpperCase();
			DeviceId = dev.getProduct().toUpperCase();
			Serial = "";
			if (destroy) {
				try {
				dev.open();
				Serial = dev.getSerial();
				dev.destroy();
				} catch (Exception e) {
					VendorId = "";
					DeviceId = "";
					Serial = "";
					dev.destroy();
				}
			}
		}
		else {
			VendorId = "";
			DeviceId = "";
			Serial = "";
		}
	}

	public static String getVendorId() {
		return VendorId;
	}
	
	public static String getProductId() {
		return DeviceId;
	}
	
	public static String getSerial() {
		return Serial;
	}
	
	public static UsbDevice getDevice() {
		UsbDevice device=null;
		UsbDevList ulist = us.getDevices("0fce");
	    if (ulist.size()> 0 ) {
	    	device = ulist.get(0);
	    }
	    return device;
	}
	
	public static void open() throws Exception {
  	  	dev.openAndClaim(0);
	}
	
	public static void writeBytes(byte[] towrite) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(towrite);		
  	  	boolean hasData = true;
  	  	int loop = 0;
  	  	while (hasData) {
				int read = in.read(data);
				if (read > 0) {
	  	  			dev.bulkWrite(BytesUtil.getReply(data, read));
				}
				else hasData=false;
  	  	}
  	  	in.close();
	}

	public static void close() {
		if (dev!=null)
			dev.releaseAndClose();
	}
	
	public static byte[] readBytes(int count) {
		return dev.bulkRead(count);
	}

	public static byte[] readBytes(int count, int timeout) {
		return dev.bulkRead(count);
	}

	public static void cleanup() throws Exception {
		us.endSystem();
	}

}