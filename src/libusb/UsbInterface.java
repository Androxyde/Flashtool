package libusb;

import java.util.Vector;
import libusb.jna.libusb_interface;
import libusb.jna.libusb_interface_descriptor;

public class UsbInterface
{
  Vector<UsbInterfaceDescriptor> ifacesdesc = new Vector();
  int id;

  public UsbInterface(int id, libusb_interface iface)
  {
    this.id = id;
    if (iface.num_altsetting > 0) {
      libusb_interface_descriptor[] iface_descs = (libusb_interface_descriptor[])iface.altsetting.toArray(iface.num_altsetting);
      for (libusb_interface_descriptor iface_desc : iface_descs)
        this.ifacesdesc.add(new UsbInterfaceDescriptor(iface_desc));
    }
  }

  public int getId()
  {
    return this.id;
  }

  public int getNbInterfaceDescriptors() {
    return this.ifacesdesc.size();
  }

  public Vector<UsbInterfaceDescriptor> getInterfaceDescriptors() {
    return this.ifacesdesc;
  }
}