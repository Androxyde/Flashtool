package libusb.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class libusb_device_descriptor extends Structure
{
  public byte bLength;
  public byte bDescriptorType;
  public short bcdUSB;
  public byte bDeviceClass;
  public byte bDeviceSubClass;
  public byte bDeviceProtocol;
  public byte bMaxPacketSize0;
  public short idVendor;
  public short idProduct;
  public short bcdDevice;
  public byte iManufacturer;
  public byte iProduct;
  public byte iSerialNumber;
  public byte bNumConfigurations;

  public libusb_device_descriptor() {}

  protected List getFieldOrder() {
	  	return Arrays.asList("bLength", "bDescriptorType", "bcdUSB", "bDeviceClass", "bDeviceSubClass", "bDeviceProtocol", "bMaxPacketSize0", "idVendor", "idProduct", "bcdDevice", "iManufacturer", "iProduct", "iSerialNumber", "bNumConfigurations");
  }
  
  public String toString()
  {
    return "libusb_device_descriptor { bLength=" + 
      this.bLength + 
      " bDescriptorType=" + this.bDescriptorType + 
      " bcdUSB=" + this.bcdUSB + 
      " bDeviceClass=" + this.bDeviceClass + 
      " bDeviceSubClass=" + this.bDeviceSubClass + 
      " bDeviceProtocol=" + this.bDeviceProtocol + 
      " bMaxPacketSize0=" + this.bMaxPacketSize0 + 
      " idVendor=" + this.idVendor + 
      " idProduct=" + this.idProduct + 
      " bcdDevice=" + this.bcdDevice + 
      " iManufacturer=" + this.iManufacturer + 
      " iProduct=" + this.iProduct + 
      " iSerialNumber=" + this.iSerialNumber + 
      " bNumConfigurations=" + this.bNumConfigurations + 
      "}";
  }

  public static class ByReference extends libusb_device_descriptor
    implements Structure.ByReference
  {
  }

  public static class ByValue extends libusb_device_descriptor
    implements Structure.ByValue
  {
  }
}