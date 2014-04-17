package libusb;

import java.util.Vector;
import libusb.jna.libusb_config_descriptor;
import libusb.jna.libusb_interface;

public class UsbConfiguration
{
  Vector<UsbInterface> ifaces = new Vector();
  int id;

  public UsbConfiguration(libusb_config_descriptor cdesc)
  {
    this.id = cdesc.bConfigurationValue;
    if (cdesc.bNumInterfaces > 0) {
      libusb_interface[] ifaces = (libusb_interface[])cdesc.iFaces.toArray(cdesc.bNumInterfaces);
      int iid = 0;
      for (libusb_interface iface : ifaces)
        this.ifaces.add(new UsbInterface(iid++, iface));
    }
  }

  public int getNbInterfaces()
  {
    return this.ifaces.size();
  }

  public Vector<UsbInterface> getInterfaces() {
    return this.ifaces;
  }

  public int getId() {
    return this.id;
  }
}