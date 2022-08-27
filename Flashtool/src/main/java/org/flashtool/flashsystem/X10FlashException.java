package org.flashtool.flashsystem;

public class X10FlashException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public X10FlashException(String msg){
      super(msg);
    }

    public X10FlashException(String msg, Throwable t){
      super(msg,t);
    }
}