package flashsystem;

public class BundleException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BundleException(String msg){
      super(msg);
    }

    public BundleException(String msg, Throwable t){
      super(msg,t);
    }
}

