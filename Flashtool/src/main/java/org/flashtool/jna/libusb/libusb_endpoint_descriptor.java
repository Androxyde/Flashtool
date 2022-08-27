package org.flashtool.jna.libusb;

import java.util.Arrays;
import java.util.List;

import org.flashtool.jna.adb.APKUtility;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class libusb_endpoint_descriptor extends Structure
{
  public byte bLength;
  public byte bDescriptorType;
  public byte bEndpointAddress;
  public byte bmAttributes;
  public short wMaxPacketSize;
  public byte bInterval;
  public byte bRefresh;
  public byte bSynchAddress;
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

  public byte getBEndpointAddress()
  {
    return this.bEndpointAddress;
  }
  public void setBEndpointAddress(byte bEndpointAddress) {
    this.bEndpointAddress = bEndpointAddress;
  }

  public byte getBmAttributes()
  {
    return this.bmAttributes;
  }
  public void setBmAttributes(byte bmAttributes) {
    this.bmAttributes = bmAttributes;
  }

  public short getWMaxPacketSize()
  {
    return this.wMaxPacketSize;
  }
  public void setWMaxPacketSize(short wMaxPacketSize) {
    this.wMaxPacketSize = wMaxPacketSize;
  }

  public byte getBInterval()
  {
    return this.bInterval;
  }
  public void setBInterval(byte bInterval) {
    this.bInterval = bInterval;
  }

  public byte getBRefresh()
  {
    return this.bRefresh;
  }
  public void setBRefresh(byte bRefresh) {
    this.bRefresh = bRefresh;
  }

  public byte getBSynchAddress()
  {
    return this.bSynchAddress;
  }
  public void setBSynchAddress(byte bSynchAddress) {
    this.bSynchAddress = bSynchAddress;
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

  public libusb_endpoint_descriptor() {
  }

  protected List getFieldOrder() {
	  	return Arrays.asList("bLength", "bDescriptorType", "bEndpointAddress", "bmAttributes", "wMaxPacketSize", "bInterval", "bRefresh", "bSynchAddress", "extra", "extra_length");
  }

  public static class ByReference extends libusb_endpoint_descriptor
    implements Structure.ByReference
  {
  }

  public static class ByValue extends libusb_endpoint_descriptor
    implements Structure.ByValue
  {
  }
}