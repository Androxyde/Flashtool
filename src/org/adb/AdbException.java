package org.adb;

public class AdbException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdbException(String msg){
      super(msg);
    }

    public AdbException(String msg, Throwable t){
      super(msg,t);
    }
}

