package org.flashtool.libusb;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibUsbException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LibUsbException(String msg){
      super(msg);
    }

    public LibUsbException(String msg, Throwable t){
      super(msg,t);
    }
}
