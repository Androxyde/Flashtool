package flashsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
						if (( (char)datachunk[i]=='O' || (char)datachunk[i]=='F') && tmpdata.toByteArray().length==0) {
							tmpdata.write(datachunk[i]);
						} else if (( (char)datachunk[i]=='K' || (char)datachunk[i]=='A') && tmpdata.toByteArray().length==1) {
							tmpdata.write(datachunk[i]);
						}
					    else if (( (char)datachunk[i]=='A' || (char)datachunk[i]=='I') && tmpdata.toByteArray().length==2) {
					    	tmpdata.write(datachunk[i]);
					    }
					    else if (( (char)datachunk[i]=='Y' || (char)datachunk[i]=='L') && tmpdata.toByteArray().length==3) {
					    	tmpdata.write(datachunk[i]);
					    	response=new String(tmpdata.toByteArray());
					    }
					    else {
					    	if (tmpdata.toByteArray().length>0) {
					    		byte[] result=BytesUtil.concatAll(bdata.toByteArray(), tmpdata.toByteArray());
					    		tmpdata = new ByteArrayOutputStream();
					    		bdata = new ByteArrayOutputStream();
					    		try {
					    			bdata.write(result);
					    		} catch (IOException ioe) {}
					    	}
					    	bdata.write(datachunk[i]);
					    }
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