package org.flashtool.libusb;

import org.flashtool.jna.libusb.LibUsbLibrary;
import org.flashtool.jna.libusb.libusb_version;

import com.sun.jna.Pointer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsbSystem {
  Pointer context = null;
  byte endpoint_in;
  byte endpoint_out;
  String version;

  public UsbSystem() throws LibUsbException
  {
    initSystem();
  }

  public void initSystem() throws LibUsbException
  {
    Pointer[] p = new Pointer[1];
    try {
	    int result = LibUsbLibrary.libUsb.libusb_init(p);
	    checkError("init", result);
	    this.context = p[0];
	    //LibUsbLibrary.libUsb.libusb_set_debug(this.context, 4);
    }
    catch (UnsatisfiedLinkError e) {
    	throw new LibUsbException("Libusb not found. Minimum libusb version is 1.0.15. It can be downloaded on http://www.libusbx.org");
    }
	try {
		libusb_version v = LibUsbLibrary.libUsb.libusb_get_version();
		if (v.major!=(short)1 || v.minor!=(short)0 || v.micro<(short)15) {
			throw new LibUsbException("Minimum libusb version is 1.0.15. Found "+v.major + "." + v.minor + "." + v.micro);
		}
		version=v.major+"."+v.minor+"."+v.micro;
	}
	catch (UnsatisfiedLinkError e) {
		throw new LibUsbException("A libusb was found but not with the right version. Minimum libusb version is 1.0.15. It can be downloaded on http://www.libusbx.org");
	}
  }

  public String getVersion() {
	  return version;
  }
  
  public UsbDevList getDevices(String vendorid) throws LibUsbException{
	  UsbDevList list = new UsbDevList();
    Pointer[] devs = new Pointer[1];
    int result = LibUsbLibrary.libUsb.libusb_get_device_list(null, devs);
    checkError("libusb_get_device_list",result);
    Pointer[] devices = devs[0].getPointerArray(0L);
    UsbDevice device = null;
    for (Pointer usb_device : devices) {
      if (usb_device != null) {
        device = new UsbDevice(usb_device);
        if (device.getVendor().equals(vendorid)) {
          list.addDevice(device);
        }
      }
    }
    LibUsbLibrary.libUsb.libusb_free_device_list(devs[0], 1);
    return list;
    /*if (device != null) {
      System.out.println(device.getVendor() + ":" + device.getProduct());
      System.out.println("Serial : " + device.getSerial());
      System.out.println("Device Configuration :");
      System.out.println("Number of configurations " + device.getNbConfig());
      Iterator iconfs = device.getConfigurations().iterator();
      while (iconfs.hasNext()) {
    	  UsbConfiguration c = (UsbConfiguration)iconfs.next();
    	  System.out.println("   Configuration number " + c.getId() + " with " + c.getNbInterfaces() + " interfaces");
    	  Iterator ifaces = c.getInterfaces().iterator();
    	  while (ifaces.hasNext()) {
    		  UsbInterface iface = (UsbInterface)ifaces.next();
    		  System.out.println("      Number of alternate settings for interface " + iface.getId() + " : " + iface.getNbInterfaceDescriptors());
    		  Iterator<UsbInterfaceDescriptor>ifdescs = iface.getInterfaceDescriptors().iterator();
    		  while (ifdescs.hasNext()) {
    			  UsbInterfaceDescriptor ifdesc = ifdescs.next();
    			  System.out.println("         Number of endpoints for alternate setting " + ifdesc.getId() + " : " + ifdesc.getNbEndpointDescriptors());
    			  Iterator endpoints = ifdesc.getEndpointDescriptors().iterator();
    			  while (endpoints.hasNext()) {
    				  UsbEndpointDescriptor endpoint = (UsbEndpointDescriptor)endpoints.next();
    				  if (endpoint.isIn()) this.endpoint_in = endpoint.getEndpoint(); else
    					  this.endpoint_out = endpoint.getEndpoint();
    				  System.out.print("            " + (endpoint.isIn() ? "In  :" : "Out :"));
    				  System.out.println("0x" + Integer.toHexString(endpoint.getEndpoint() & 0xFF));
    			  }
    		  }
    	  }
      }
      byte[] data = null;
      System.out.println("Reading from device :");
      System.out.println("First read");
      data = device.bulkRead();
      System.out.println("Second read");
      data = device.bulkRead();
      if (data != null) {
        System.out.println("Reply :");
        System.out.println(new String(data));
      }
      System.out.println("Third read");
      data = device.bulkRead();

      S1Packet cmd1 = new S1Packet(1, new byte[0], false);
      System.out.println("Writing to device : " + HexDump.toHex(cmd1.getByteArray()));
      device.bulkWrite(cmd1.getByteArray());

      System.out.println("Reading from device :");
      System.out.print("First read : ");
      data = device.bulkRead();
      System.out.print("Second read : ");
      data = device.bulkRead();
      if (data != null) {
    	  System.out.println(data.length);
    	  System.out.println("Reply :");
    	  String dataS = new String(data);
    	  if (dataS.indexOf(";IMEI=")>0)
    		  System.out.println(dataS.substring(0, dataS.indexOf(";IMEI=")) + dataS.substring(dataS.indexOf(";MSN=")));
    	  else
    		  System.out.println(dataS);
      }
      System.out.println("Third read : ");
      data = device.bulkRead();
      device.close();
      device.destroy();
    }*/
  }

  public void endSystem() {
    LibUsbLibrary.libUsb.libusb_exit(this.context);
  }

  public static void checkError(String action, int error) throws LibUsbException {
    switch (error) {
    case -1:
        throw new LibUsbException(action + " : I/O Errors");
    case -2:
    	throw new LibUsbException(action + " : Invalid parameters");
    case -3:
    	throw new LibUsbException(action + " : Access error. No permission on device");
    case -4:
    	throw new LibUsbException(action + " : No device");
    case -5:
    	throw new LibUsbException(action + " : Device Not found");
    case -6:
    	throw new LibUsbException(action + " : Device busy");
    case -7:
    	throw new LibUsbException(action + " : Timeout");
    case -8:
    	throw new LibUsbException(action + " : Overflow");
    case -9:
    	throw new LibUsbException(action + " : Pipe error");
    case -10:
    	throw new LibUsbException(action + " : Interrupted by user");
    case -11:
    	throw new LibUsbException(action + " : No memory");
    case -12:
    	throw new LibUsbException(action + " : Not supported");
    case -99:
    	throw new LibUsbException(action + " : Other");
    }
  }
}