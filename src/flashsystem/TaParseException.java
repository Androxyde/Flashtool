package flashsystem;

public class TaParseException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaParseException(String msg){
      super(msg);
    }

    public TaParseException(String msg, Throwable t){
      super(msg,t);
    }
} 