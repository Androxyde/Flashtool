package org.flashtool.jna.libusb;

import java.util.Arrays;
import java.util.List;

import org.flashtool.jna.adb.APKUtility;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class libusb_version extends Structure
{
  public short major;
  public short minor;
  public short micro;
  public short nano;
  public Pointer rc;
  public Pointer describe;

  public short getMajor()
  {
    return this.major;
  }
  public void setMajor(short major) {
    this.major = major;
  }

  public short getMinor()
  {
    return this.minor;
  }
  public void setMinor(short minor) {
    this.minor = minor;
  }

  public short getMicro()
  {
    return this.micro;
  }
  public void setMicro(short micro) {
    this.micro = micro;
  }

  public short getNano()
  {
    return this.nano;
  }
  public void setNano(short nano) {
    this.nano = nano;
  }

  public Pointer getRc()
  {
    return this.rc;
  }
  public void setRc(Pointer rc) {
    this.rc = rc;
  }

  public Pointer getDescribe()
  {
    return this.describe;
  }
  public void setDescribe(Pointer describe) {
    this.describe = describe;
  }

  public libusb_version() {
  }

  protected List getFieldOrder() {
	  	return Arrays.asList("major", "minor", "micro", "nano", "rc", "describe");
  }

  public libusb_version(short major, short minor, short micro, short nano, Pointer rc, Pointer describe)
  {
    this.major = major;
    this.minor = minor;
    this.micro = micro;
    this.nano = nano;
    this.rc = rc;
    this.describe = describe;
  }

  public static class ByReference extends libusb_version
    implements Structure.ByReference
  {
  }

  public static class ByValue extends libusb_version
    implements Structure.ByValue
  {
  }
}