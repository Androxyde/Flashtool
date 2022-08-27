package org.flashtool.libusb;

import java.util.Vector;

import org.flashtool.jna.libusb.libusb_endpoint_descriptor;
import org.flashtool.jna.libusb.libusb_interface_descriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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