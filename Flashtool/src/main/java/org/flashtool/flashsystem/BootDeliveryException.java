package org.flashtool.flashsystem;

public class BootDeliveryException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BootDeliveryException(String msg){
      super(msg);
    }

    public BootDeliveryException(String msg, Throwable t){
      super(msg,t);
    }
}