package libusb;

import java.util.Vector;
import libusb.jna.libusb_endpoint_descriptor;
import libusb.jna.libusb_interface_descriptor;

public class UsbInterfaceDescriptor
{
  private byte alternateSetting;
  private Vector<UsbEndpointDescriptor> endpoints = new Vector();

  public UsbInterfaceDescriptor(libusb_interface_descriptor iface_desc) {
    this.alternateSetting = iface_desc.bAlternateSetting;
    if (iface_desc.bNumEndpoints > 0) {
      libusb_endpoint_descriptor[] endpoints = (libusb_endpoint_descriptor[])iface_desc.endpoint.toArray(iface_desc.bNumEndpoints);
      for (libusb_endpoint_descriptor endpoint : endpoints)
        this.endpoints.add(new UsbEndpointDescriptor(endpoint));
    }
  }

  int getId()
  {
    return this.alternateSetting;
  }

  public int getNbEndpointDescriptors() {
    return this.endpoints.size();
  }

  public Vector<UsbEndpointDescriptor> getEndpointDescriptors() {
    return this.endpoints;
  }
}