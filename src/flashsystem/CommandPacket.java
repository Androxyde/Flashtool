package flashsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.util.BytesUtil;

public class CommandPacket {

	ByteArrayOutputStream bresponse = new ByteArrayOutputStream();
	String response="";
	
	ByteArrayOutputStream blength = new ByteArrayOutputStream();
	int length=-1;
	
	ByteArrayOutputStream bdata = new ByteArrayOutputStream();
	
	ByteArrayOutputStream tmpdata = new ByteArrayOutputStream();
	
	boolean withOK=true;

	public CommandPacket(byte[] datachunk, boolean withOk) {
		this.withOK=withOk;
		addData(datachunk);
	}

	
	public void addData(byte[] datachunk) {
		for (int i=0;i<datachunk.length;i++) {
			if (bresponse.toByteArray().length<4) {
				bresponse.write(datachunk[i]);
				if (response.length()==0 && bresponse.toByteArray().length==4) {
					response=new String(bresponse.toByteArray());
				}
			}
			else {
				if (response.equals("OKAY") || response.equals("FAIL")) {
					bdata.write(datachunk[i]);
				} else if (response.equals("DATA")) {
					if (blength.toByteArray().length<8) {
						blength.write(datachunk[i]);
						if (length==-1 && blength.toByteArray().length==8) {
							length=BytesUtil.getInt(blength.toByteArray());
						}
					} else {
						tmpdata.write(datachunk[i]);
						try {
						if (new String(tmpdata.toByteArray()).endsWith("FAIL")) {
							response="FAIL";
							bdata.write(Arrays.copyOf(tmpdata.toByteArray(), tmpdata.toByteArray().length-4));
						}
						if (new String(tmpdata.toByteArray()).endsWith("OKAY")) {
							response="OKAY";
							bdata.write(Arrays.copyOf(tmpdata.toByteArray(), tmpdata.toByteArray().length-4));
						}
						} catch (Exception e) {}
					}
				}
			}
		}
	}
	
	public boolean hasMoreToRead() {
		boolean isFinished1 = (response.equals("DATA") && withOK==false);
		boolean isFinished2 = ((response.equals("OKAY") || response.equals("FAIL")) && withOK);
		return !isFinished1 && !isFinished2; 
	}

	public byte[] getDataArray() {
		return bdata.toByteArray();
	}
	
	public String getMessage() {
		return new String(bdata.toByteArray());
	}
	
	public int getStatus() {
		return 0;
	}
	
	public String getResponse() {
		return response;
	}

}