package org.flashtool.jna.libusb;

import org.flashtool.jna.adb.APKUtility;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public abstract interface LibUsbLibrary extends Library
{
  public static final LibUsbLibrary libUsb = (LibUsbLibrary)Native.loadLibrary("usbx-1.0", LibUsbLibrary.class);
  public static final int LIBUSB_ISO_SYNC_TYPE_MASK = 12;
  public static final int LIBUSB_ISO_USAGE_TYPE_MASK = 48;
  public static final int LIBUSBX_API_VERSION = 16777471;
  public static final int LIBUSB_DT_DEVICE_SIZE = 18;
  public static final int LIBUSB_ENDPOINT_ADDRESS_MASK = 15;
  public static final int LIBUSB_DT_CONFIG_SIZE = 9;
  public static final int LIBUSB_TRANSFER_TYPE_MASK = 3;
  public static final int LIBUSB_ENDPOINT_DIR_MASK = 128;
  public static final int LIBUSB_DT_ENDPOINT_AUDIO_SIZE = 9;
  public static final int LIBUSB_DT_ENDPOINT_SIZE = 7;
  public static final int LIBUSB_DT_HUB_NONVAR_SIZE = 7;
  public static final int LIBUSB_DT_INTERFACE_SIZE = 9;

  public abstract int libusb_init(Pointer[] paramArrayOfPointer);

  public abstract void libusb_exit(Pointer paramPointer);

  public abstract int libusb_get_device_list(Pointer paramPointer, Pointer[] paramArrayOfPointer);

  public abstract void libusb_free_device_list(Pointer paramPointer, int paramInt);

  public abstract Pointer libusb_get_device(Pointer paramPointer);

  public abstract int libusb_get_device_descriptor(Pointer paramPointer, libusb_device_descriptor[] paramArrayOflibusb_device_descriptor);

  public abstract int libusb_get_string_descriptor_ascii(Pointer paramPointer, byte paramByte, byte[] paramArrayOfByte, int paramInt);

  public abstract int libusb_open(Pointer paramPointer, Pointer[] paramArrayOfPointer);

  public abstract void libusb_close(Pointer paramPointer);

  public abstract int libusb_get_config_descriptor(Pointer paramPointer, int paramInt, PointerByReference paramPointerByReference);

  public abstract void libusb_free_config_descriptor(Pointer paramPointer);

  public abstract void libusb_ref_device(Pointer paramPointer);
  
  public abstract void libusb_unref_device(Pointer paramPointer);

  public abstract int libusb_detach_kernel_driver(Pointer paramPointer, int paramInt);

  public abstract int libusb_attach_kernel_driver(Pointer paramPointer, int paramInt);

  public abstract int libusb_claim_interface(Pointer paramPointer, int paramInt);

  public abstract int libusb_release_interface(Pointer paramPointer, int paramInt);

  public abstract int libusb_bulk_transfer(Pointer paramPointer, byte paramByte, byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2);

  public abstract void libusb_set_debug(Pointer paramPointer, int paramInt);

  public abstract libusb_version libusb_get_version();

  public static abstract interface libusb_class_code
  {
    public static final int LIBUSB_CLASS_PER_INTERFACE = 0;
    public static final int LIBUSB_CLASS_AUDIO = 1;
    public static final int LIBUSB_CLASS_COMM = 2;
    public static final int LIBUSB_CLASS_HID = 3;
    public static final int LIBUSB_CLASS_PHYSICAL = 5;
    public static final int LIBUSB_CLASS_PRINTER = 7;
    public static final int LIBUSB_CLASS_PTP = 6;
    public static final int LIBUSB_CLASS_IMAGE = 6;
    public static final int LIBUSB_CLASS_MASS_STORAGE = 8;
    public static final int LIBUSB_CLASS_HUB = 9;
    public static final int LIBUSB_CLASS_DATA = 10;
    public static final int LIBUSB_CLASS_SMART_CARD = 11;
    public static final int LIBUSB_CLASS_CONTENT_SECURITY = 13;
    public static final int LIBUSB_CLASS_VIDEO = 14;
    public static final int LIBUSB_CLASS_PERSONAL_HEALTHCARE = 15;
    public static final int LIBUSB_CLASS_DIAGNOSTIC_DEVICE = 220;
    public static final int LIBUSB_CLASS_WIRELESS = 224;
    public static final int LIBUSB_CLASS_APPLICATION = 254;
    public static final int LIBUSB_CLASS_VENDOR_SPEC = 255;
  }

  public static abstract interface libusb_descriptor_type
  {
    public static final int LIBUSB_DT_DEVICE = 1;
    public static final int LIBUSB_DT_CONFIG = 2;
    public static final int LIBUSB_DT_STRING = 3;
    public static final int LIBUSB_DT_INTERFACE = 4;
    public static final int LIBUSB_DT_ENDPOINT = 5;
    public static final int LIBUSB_DT_HID = 33;
    public static final int LIBUSB_DT_REPORT = 34;
    public static final int LIBUSB_DT_PHYSICAL = 35;
    public static final int LIBUSB_DT_HUB = 41;
    public static final int LIBUSB_DT_SUPERSPEED_HUB = 42;
  }

  public static abstract interface libusb_endpoint_direction
  {
    public static final int LIBUSB_ENDPOINT_IN = 128;
    public static final int LIBUSB_ENDPOINT_OUT = 0;
  }

  public static abstract interface libusb_error
  {
    public static final int LIBUSB_SUCCESS = 0;
    public static final int LIBUSB_ERROR_IO = -1;
    public static final int LIBUSB_ERROR_INVALID_PARAM = -2;
    public static final int LIBUSB_ERROR_ACCESS = -3;
    public static final int LIBUSB_ERROR_NO_DEVICE = -4;
    public static final int LIBUSB_ERROR_NOT_FOUND = -5;
    public static final int LIBUSB_ERROR_BUSY = -6;
    public static final int LIBUSB_ERROR_TIMEOUT = -7;
    public static final int LIBUSB_ERROR_OVERFLOW = -8;
    public static final int LIBUSB_ERROR_PIPE = -9;
    public static final int LIBUSB_ERROR_INTERRUPTED = -10;
    public static final int LIBUSB_ERROR_NO_MEM = -11;
    public static final int LIBUSB_ERROR_NOT_SUPPORTED = -12;
    public static final int LIBUSB_ERROR_OTHER = -99;
  }

  public static abstract interface libusb_iso_sync_type
  {
    public static final int LIBUSB_ISO_SYNC_TYPE_NONE = 0;
    public static final int LIBUSB_ISO_SYNC_TYPE_ASYNC = 1;
    public static final int LIBUSB_ISO_SYNC_TYPE_ADAPTIVE = 2;
    public static final int LIBUSB_ISO_SYNC_TYPE_SYNC = 3;
  }

  public static abstract interface libusb_iso_usage_type
  {
    public static final int LIBUSB_ISO_USAGE_TYPE_DATA = 0;
    public static final int LIBUSB_ISO_USAGE_TYPE_FEEDBACK = 1;
    public static final int LIBUSB_ISO_USAGE_TYPE_IMPLICIT = 2;
  }

  public static abstract interface libusb_log_level
  {
    public static final int LIBUSB_LOG_LEVEL_NONE = 0;
    public static final int LIBUSB_LOG_LEVEL_ERROR = 1;
    public static final int LIBUSB_LOG_LEVEL_WARNING = 2;
    public static final int LIBUSB_LOG_LEVEL_INFO = 3;
    public static final int LIBUSB_LOG_LEVEL_DEBUG = 4;
  }

  public static abstract interface libusb_request_recipient
  {
    public static final int LIBUSB_RECIPIENT_DEVICE = 0;
    public static final int LIBUSB_RECIPIENT_INTERFACE = 1;
    public static final int LIBUSB_RECIPIENT_ENDPOINT = 2;
    public static final int LIBUSB_RECIPIENT_OTHER = 3;
  }

  public static abstract interface libusb_request_type
  {
    public static final int LIBUSB_REQUEST_TYPE_STANDARD = 0;
    public static final int LIBUSB_REQUEST_TYPE_CLASS = 32;
    public static final int LIBUSB_REQUEST_TYPE_VENDOR = 64;
    public static final int LIBUSB_REQUEST_TYPE_RESERVED = 96;
  }

  public static abstract interface libusb_speed
  {
    public static final int LIBUSB_SPEED_UNKNOWN = 0;
    public static final int LIBUSB_SPEED_LOW = 1;
    public static final int LIBUSB_SPEED_FULL = 2;
    public static final int LIBUSB_SPEED_HIGH = 3;
    public static final int LIBUSB_SPEED_SUPER = 4;
  }

  public static abstract interface libusb_standard_request
  {
    public static final int LIBUSB_REQUEST_GET_STATUS = 0;
    public static final int LIBUSB_REQUEST_CLEAR_FEATURE = 1;
    public static final int LIBUSB_REQUEST_SET_FEATURE = 3;
    public static final int LIBUSB_REQUEST_SET_ADDRESS = 5;
    public static final int LIBUSB_REQUEST_GET_DESCRIPTOR = 6;
    public static final int LIBUSB_REQUEST_SET_DESCRIPTOR = 7;
    public static final int LIBUSB_REQUEST_GET_CONFIGURATION = 8;
    public static final int LIBUSB_REQUEST_SET_CONFIGURATION = 9;
    public static final int LIBUSB_REQUEST_GET_INTERFACE = 10;
    public static final int LIBUSB_REQUEST_SET_INTERFACE = 11;
    public static final int LIBUSB_REQUEST_SYNCH_FRAME = 12;
    public static final int LIBUSB_REQUEST_SET_SEL = 48;
    public static final int LIBUSB_SET_ISOCH_DELAY = 49;
  }

  public static abstract interface libusb_transfer_flags
  {
    public static final int LIBUSB_TRANSFER_SHORT_NOT_OK = 1;
    public static final int LIBUSB_TRANSFER_FREE_BUFFER = 2;
    public static final int LIBUSB_TRANSFER_FREE_TRANSFER = 4;
    public static final int LIBUSB_TRANSFER_ADD_ZERO_PACKET = 8;
  }

  public static abstract interface libusb_transfer_status
  {
    public static final int LIBUSB_TRANSFER_COMPLETED = 0;
    public static final int LIBUSB_TRANSFER_ERROR = 1;
    public static final int LIBUSB_TRANSFER_TIMED_OUT = 2;
    public static final int LIBUSB_TRANSFER_CANCELLED = 3;
    public static final int LIBUSB_TRANSFER_STALL = 4;
    public static final int LIBUSB_TRANSFER_NO_DEVICE = 5;
    public static final int LIBUSB_TRANSFER_OVERFLOW = 6;
  }

  public static abstract interface libusb_transfer_type
  {
    public static final int LIBUSB_TRANSFER_TYPE_CONTROL = 0;
    public static final int LIBUSB_TRANSFER_TYPE_ISOCHRONOUS = 1;
    public static final int LIBUSB_TRANSFER_TYPE_BULK = 2;
    public static final int LIBUSB_TRANSFER_TYPE_INTERRUPT = 3;
  }
}