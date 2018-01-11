package flashsystem;

public class CommandPacket {

	public static int OKAY=1;
	public static int FAIL=0;
	public static int DATA=2;

	private String status="";
	private String message="";
	private long length=-1L;
	
	public void setStatus(String status) {
		this.status=status;
	}

	public void setMessage(String message) {
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		if (status.equals("OKAY")) return 1;
		if (status.equals("FAIL")) return 0;
		if (status.equals("DATA")) return 2;
		return -1;
	}

}