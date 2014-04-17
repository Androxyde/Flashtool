package libusb;

import libusb.jna.libusb_endpoint_descriptor;

public class UsbEndpointDescriptor
{
  private byte endpoint;
  private String direction;

  public UsbEndpointDescriptor(libusb_endpoint_descriptor endpoint)
  {
    this.endpoint = endpoint.bEndpointAddress;
    if ((endpoint.bEndpointAddress & 0x80) > 0) {
      this.direction = "in";
    }
    else
      this.direction = "out";
  }

  public boolean isIn()
  {
    return this.direction.equals("in");
  }

  public boolean isOut() {
    return this.direction.equals("out");
  }

  public byte getEndpoint() {
    return this.endpoint;
  }
}