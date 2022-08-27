package org.flashtool.libusb;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.util.Iterator;
import java.util.Vector;

import org.flashtool.jna.libusb.LibUsbLibrary;
import org.flashtool.jna.libusb.libusb_config_descriptor;
import org.flashtool.jna.libusb.libusb_device_descriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsbDevice
{
  private Pointer usb_device = null;
  private Pointer handle = null;
  private String vendorid = ""; private String productid = "";
  private byte iSerialNumber;
  private byte iManufacturer;
  private Vector<UsbConfiguration> confs = new Vector();
  private int iface_detached = -1;
  private int iface_claimed = -1;
  private int refcount=0;
  private byte default_endpoint_in=0, default_endpoint_out=0;
  private int nbconfig=0;

  public UsbDevice(Pointer device) throws LibUsbException {
    this.usb_device = device;
    libusb_device_descriptor[] arr = new libusb_device_descriptor[1];
    int result = LibUsbLibrary.libUsb.libusb_get_device_descriptor(this.usb_device, arr);
    UsbSystem.checkError("get_device_descriptor", result);
    this.vendorid = String.format("%4s", new Object[] { Integer.toHexString(arr[0].idVendor & 0xFFFF) }).replace(' ', '0');
    this.productid = String.format("%4s", new Object[] { Integer.toHexString(arr[0].idProduct & 0xFFFF) }).replace(' ', '0');
    this.iSerialNumber = arr[0].iSerialNumber;
    this.iManufacturer = arr[0].iManufacturer;
    nbconfig = arr[0].bNumConfigurations;
  }

  public String getVendor() {
    return this.vendorid;
  }

  public String getProduct() {
    return this.productid;
  }

  public int getNbConfig() {
    return this.confs.size();
  }

  public void unref() {
    LibUsbLibrary.libUsb.libusb_unref_device(this.usb_device);
    refcount--;
  }

  public void ref() {
	    LibUsbLibrary.libUsb.libusb_ref_device(this.usb_device);
	    refcount++;
	  }

  public void close() throws LibUsbException {
	  if (handle != null) {
		  releaseInterface();
		  LibUsbLibrary.libUsb.libusb_close(this.handle);
		  this.handle = null;
		  refcount--;
	  }
  }
  
  public void open() throws Exception {
    Pointer[] dev_handle = new Pointer[1];
    int result = LibUsbLibrary.libUsb.libusb_open(this.usb_device, dev_handle);
	  int retries=0;
	  int maxretries=5;
	  if (result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_NO_DEVICE || result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_NOT_FOUND || result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_OTHER)
		  throw new Exception("Error while querying the device");
	  if (result <0) {
		  while (retries<maxretries) {
			  try {
			  Thread.sleep(500);
			  } catch (Exception e) {};
			  result = LibUsbLibrary.libUsb.libusb_open(this.usb_device, dev_handle);
			  if (result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_NO_DEVICE || result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_NOT_FOUND || result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_OTHER) {
				  this.handle=null;
				  throw new Exception("Error while querying the device");
			  }
			  if (result==0) retries=maxretries;
			  retries++;
		  }
	  }
    UsbSystem.checkError("Open", result);
    if (result == 0) {
    	this.handle = dev_handle[0];
    	refcount++;
    }
    else this.handle=null;
  }

  public void openAndClaim(int iface) throws Exception {
	  open();
	  claimInterface(iface);
  }
  
  public void releaseAndClose() throws LibUsbException {
	  releaseInterface();
	  close();
  }
  
  public String getSerial() throws LibUsbException {
    byte[] buffer = new byte[256];
    if (handle!=null) {
    	int result = LibUsbLibrary.libUsb.libusb_get_string_descriptor_ascii(this.handle, this.iSerialNumber, buffer, buffer.length);
    	if (result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_INVALID_PARAM) return "";
    	UsbSystem.checkError("libusb_get_string_descriptor_ascii", result);
    	return new String(buffer);
    }
    else return "";    
  }

  public String getManufacturer() throws LibUsbException {
    byte[] buffer = new byte[256];
    if (handle!=null) {
	    int result = LibUsbLibrary.libUsb.libusb_get_string_descriptor_ascii(this.handle, this.iManufacturer, buffer, buffer.length);
	    if (result == LibUsbLibrary.libusb_error.LIBUSB_ERROR_INVALID_PARAM) return "";
	    UsbSystem.checkError("libusb_get_string_descriptor_ascii", result);
	    return new String(buffer);
    }
    else return "";
  }

  public void setConfiguration() throws Exception {
    PointerByReference configRef = new PointerByReference();
    int result = LibUsbLibrary.libUsb.libusb_get_config_descriptor(this.usb_device, 0, configRef);
    int retries=0;
	  int maxretries=5;
	  if (result <0) {
		  while (retries<maxretries) {
			  try {
			  Thread.sleep(500);
			  } catch (Exception e) {};
			  result = LibUsbLibrary.libUsb.libusb_get_config_descriptor(this.usb_device, 0, configRef);
			  if (result==0) retries=maxretries;
			  retries++;
		  }			  
	  }
    UsbSystem.checkError("get_config", result);
    if (result<0) throw new Exception("setConfiguration error");
    libusb_config_descriptor cd = new libusb_config_descriptor(configRef.getValue());
    libusb_config_descriptor[] cdescs = cd.toArray(nbconfig);
    for (libusb_config_descriptor cdesc : cdescs) {
      this.confs.add(new UsbConfiguration(cdesc));
    }
    LibUsbLibrary.libUsb.libusb_free_config_descriptor(configRef.getValue());
    Iterator iconfs = getConfigurations().iterator();
    while (iconfs.hasNext()) {
  	  UsbConfiguration c = (UsbConfiguration)iconfs.next();
  	  Iterator ifaces = c.getInterfaces().iterator();
  	  while (ifaces.hasNext()) {
  		  UsbInterface iface = (UsbInterface)ifaces.next();
  		  Iterator<UsbInterfaceDescriptor>ifdescs = iface.getInterfaceDescriptors().iterator();
  		  while (ifdescs.hasNext()) {
  			  UsbInterfaceDescriptor ifdesc = ifdescs.next();
  			  Iterator endpoints = ifdesc.getEndpointDescriptors().iterator();
  			  while (endpoints.hasNext()) {
  				  UsbEndpointDescriptor endpoint = (UsbEndpointDescriptor)endpoints.next();
  				  if (endpoint.isIn()) default_endpoint_in = endpoint.getEndpoint(); else
  					  default_endpoint_out = endpoint.getEndpoint();
  			  }
  		  }
  	  }
    }
  }

  public Vector<UsbConfiguration> getConfigurations() {
    return this.confs;
  }

  public void claimInterface(int ifacenum) throws LibUsbException {
	  if (handle != null) {
		  int result = LibUsbLibrary.libUsb.libusb_claim_interface(this.handle, ifacenum);
		  UsbSystem.checkError("claim_interface", result);
		  if (result != 0) {
			  result = LibUsbLibrary.libUsb.libusb_detach_kernel_driver(this.handle, ifacenum);
			  UsbSystem.checkError("detach kernel", result);
			  this.iface_detached = ifacenum;
			  result = LibUsbLibrary.libUsb.libusb_claim_interface(this.handle, ifacenum);
			  UsbSystem.checkError("claim_interface", result);
		  }
		  iface_claimed = ifacenum;
	  }
  }

  public void releaseInterface() throws LibUsbException {
	  if (handle != null && iface_claimed>=0) {
		  int result = LibUsbLibrary.libUsb.libusb_release_interface(this.handle, iface_claimed);
		  UsbSystem.checkError("release_interface", result);
		  if (result==0) iface_claimed = -1;
		  if (this.iface_detached >= 0) {
			  result = LibUsbLibrary.libUsb.libusb_attach_kernel_driver(this.handle, this.iface_detached);
			  UsbSystem.checkError("attach kernel", result);
		  }
	  }
  }

  public byte[] bulkRead(int count) throws LibUsbException {
	  if (handle != null) {
		  byte[] data = new byte[count];
		  int[] actual_length = new int[1];
		  int result = LibUsbLibrary.libUsb.libusb_bulk_transfer(this.handle, default_endpoint_in, data, data.length, actual_length, 10000);
		  int retries=0;
		  if (result <0) {
			  while (retries<2) {
				  result = LibUsbLibrary.libUsb.libusb_bulk_transfer(this.handle, default_endpoint_in, data, data.length, actual_length, 10000);
				  if (result==0) retries=3;
				  retries++;
			  }			  
		  }
		  UsbSystem.checkError("libusb_bulk_transfer (read)", result);
		  if (result>=0)
			  return getReply(data, actual_length[0]);
		  else
			  return null;
	  }
	  else return null;
  }

  public void bulkWrite(byte[] data) throws LibUsbException {
	  if (handle != null) {
		  int[] actual_length = new int[1];
		  int result = LibUsbLibrary.libUsb.libusb_bulk_transfer(this.handle, default_endpoint_out, data, data.length, actual_length, 0);
		  UsbSystem.checkError("libusb_bulk_transfer (write)", result);
		  if (data.length != actual_length[0])
			  System.err.println("Error : did not write all data");
	  }
  }

  public byte[] getReply(byte[] reply, int nbread)
  {
    if (reply.length == nbread) return reply;
    byte[] newreply = null;
    if (nbread > 0) {
      newreply = new byte[nbread];
      System.arraycopy(reply, 0, newreply, 0, nbread);
    }
    return newreply;
  }
  
  public void destroy() throws LibUsbException {
	  close();
	  if (refcount>0) {
		  while (refcount>0)
			  unref();
	  }
  }

}