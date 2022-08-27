package org.flashtool.jna.libusb;

import java.util.Arrays;
import java.util.List;

import org.flashtool.jna.adb.APKUtility;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class libusb_config_descriptor extends Structure
{
  public byte bLength;
  public byte bDescriptorType;
  public short wTotalLength;
  public byte bNumInterfaces;
  public byte bConfigurationValue;
  public byte iConfiguration;
  public byte bmAttributes;
  public byte MaxPower;
  public libusb_interface.ByReference iFaces;
  public Pointer extra;
  public int extra_length;

  protected List getFieldOrder() {
  	return Arrays.asList("bLength",
  				     	 "bDescriptorType",
  				     	 "wTotalLength", 
  				     	 "bNumInterfaces",
  				     	 "bConfigurationValue",
  				     	 "iConfiguration",
  				     	 "bmAttributes",
  				     	 "MaxPower",
  				     	 "iFaces",
  				     	 "extra",
  						 "extra_length");
  }

  public libusb_config_descriptor() {}

  public libusb_config_descriptor[] toArray(int size)
  {
    return (libusb_config_descriptor[])super.toArray(size);
  }

  public libusb_config_descriptor(Pointer p) {
    super(p);
    read();
  }

  public static class ByReference extends libusb_config_descriptor
    implements Structure.ByReference
  {
  }

  public static class ByValue extends libusb_config_descriptor
    implements Structure.ByValue
  {
  }

}