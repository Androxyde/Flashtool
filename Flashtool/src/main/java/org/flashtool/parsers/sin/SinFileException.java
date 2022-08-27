package org.flashtool.parsers.sin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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