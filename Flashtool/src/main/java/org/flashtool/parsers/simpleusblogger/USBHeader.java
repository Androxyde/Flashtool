package org.flashtool.parsers.simpleusblogger;

import org.flashtool.libusb.LibUsbException;
import org.flashtool.util.HexDump;

import com.igormaznitsa.jbbp.mapper.Bin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class USBHeader {
	@Bin short usb_Length;
	@Bin short usb_Function;
	@Bin int usb_Status;
	@Bin long usb_UsbDeviceHandle;
	@Bin long usb_UsbdFlags;
	@Bin long usb_PipeHandle;
	@Bin int usb_TransferFlags;
	@Bin int usb_TransferBufferLength;
	@Bin long usb_TransferBuffer;
	@Bin long usb_TransferBufferMDL;
	@Bin long usb_UrbLink;
	@Bin long usb_hcdendpoint;
	@Bin long usb_hcdirp;
	@Bin long usb_hcdlistentry;
	@Bin long usb_flink;
	@Bin long usb_blink;
	@Bin long usb_hcdlistentry2;
	@Bin long usb_hcdcurrentflushpointer;
	@Bin long usb_hcdextension;
	
	public String toString() {
		return "header length : "+usb_Length+" / function : "+usb_Function+" / status : "+usb_Status+" / usbdevicehandle "+HexDump.toHex(usb_UsbDeviceHandle)+" / usbdflags : "+usb_UsbdFlags +
		" / pipehandle : " + HexDump.toHex(usb_PipeHandle) +
		" / transferflag : " + usb_TransferFlags +
		" / transferbufferlength : " + usb_TransferBufferLength;
	}
}