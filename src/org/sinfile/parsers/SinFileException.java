package org.sinfile.parsers;

public class SinFileException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SinFileException(String msg){
      super(msg);
    }

    public SinFileException(String msg, Throwable t){
      super(msg,t);
    }
}