package org.flashtool.jna.libusb;

import java.util.Arrays;
import java.util.List;

import org.flashtool.jna.adb.APKUtility;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class libusb_interface_descriptor extends Structure
{
  public byte bLength;
  public byte bDescriptorType;
  public byte bInterfaceNumber;
  public byte bAlternateSetting;
  public byte bNumEndpoints;
  public byte bInterfaceClass;
  public byte bInterfaceSubClass;
  public byte bInterfaceProtocol;
  public byte iInterface;
  public libusb_endpoint_descriptor.ByReference endpoint;
  public Pointer extra;
  public int extra_length;

  public byte getBLength()
  {
    return this.bLength;
  }
  public void setBLength(byte bLength) {
    this.bLength = bLength;
  }

  public byte getBDescriptorType()
  {
    return this.bDescriptorType;
  }
  public void setBDescriptorType(byte bDescriptorType) {
    this.bDescriptorType = bDescriptorType;
  }

  public byte getBInterfaceNumber()
  {
    return this.bInterfaceNumber;
  }
  public void setBInterfaceNumber(byte bInterfaceNumber) {
    this.bInterfaceNumber = bInterfaceNumber;
  }

  public byte getBAlternateSetting()
  {
    return this.bAlternateSetting;
  }
  public void setBAlternateSetting(byte bAlternateSetting) {
    this.bAlternateSetting = bAlternateSetting;
  }

  public byte getBNumEndpoints()
  {
    return this.bNumEndpoints;
  }
  public void setBNumEndpoints(byte bNumEndpoints) {
    this.bNumEndpoints = bNumEndpoints;
  }

  public byte getBInterfaceClass()
  {
    return this.bInterfaceClass;
  }
  public void setBInterfaceClass(byte bInterfaceClass) {
    this.bInterfaceClass = bInterfaceClass;
  }

  public byte getBInterfaceSubClass()
  {
    return this.bInterfaceSubClass;
  }
  public void setBInterfaceSubClass(byte bInterfaceSubClass) {
    this.bInterfaceSubClass = bInterfaceSubClass;
  }

  public byte getBInterfaceProtocol()
  {
    return this.bInterfaceProtocol;
  }
  public void setBInterfaceProtocol(byte bInterfaceProtocol) {
    this.bInterfaceProtocol = bInterfaceProtocol;
  }

  public byte getIInterface()
  {
    return this.iInterface;
  }
  public void setIInterface(byte iInterface) {
    this.iInterface = iInterface;
  }

  public libusb_endpoint_descriptor.ByReference getEndpoint()
  {
    return this.endpoint;
  }
  public void setEndpoint(libusb_endpoint_descriptor.ByReference endpoint) {
    this.endpoint = endpoint;
  }

  public Pointer getExtra()
  {
    return this.extra;
  }
  public void setExtra(Pointer extra) {
    this.extra = extra;
  }

  public int getExtra_length()
  {
    return this.extra_length;
  }
  public void setExtra_length(int extra_length) {
    this.extra_length = extra_length;
  }

  public libusb_interface_descriptor() {
  }
  
  protected List getFieldOrder() {
	  	return Arrays.asList("bLength", "bDescriptorType", "bInterfaceNumber", "bAlternateSetting", "bNumEndpoints", "bInterfaceClass", "bInterfaceSubClass", "bInterfaceProtocol", "iInterface", "endpoint", "extra", "extra_length");
  }

  public static class ByReference extends libusb_interface_descriptor
    implements Structure.ByReference
  {
  }

  public static class ByValue extends libusb_interface_descriptor
    implements Structure.ByValue
  {
  }
}