package org.simpleusblogger;

import java.util.Arrays;

public class FlashCommand {

	String command = "";
	String parameters="";
	String lastsubcommand="";
	String lastsubparameters="";
	byte[] signdata;
	byte[] reply;
	String sinfile = "";
	int unit = 0;
	int partition = 0;
	
	public FlashCommand(String command) {
		String[] parsed = command.split(":");
		this.command = parsed[0];
		if (this.command.equals("Read-TA")) {
			partition = Integer.parseInt(parsed[1]);
			unit = Integer.parseInt(parsed[2]);
		}
		if (parsed.length>1) {
			for (int i=1;i<parsed.length;i++)
				parameters=parameters+(parameters.length()==0?"":":")+parsed[i];
		}
	}

	public int getUnit() {
		return unit;
	}
	
	public int getPartition() {
		return partition;
	}
	
	public byte[] getReply() {
		return reply;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getFinalCommand() {
		if (command.equals("signature"))
			return lastsubcommand;
		return command;
	}
	public String getParameters() {
		if (command.equals("signature"))
			return lastsubparameters+":"+sinfile;
		else return parameters;
	}

	public void setSubCommand(String command) {
		lastsubparameters="";
		String[] parsed = command.split(":");
		this.lastsubcommand = parsed[0];
		if (parsed.length>1) {
			for (int i=1;i<parsed.length;i++)
				lastsubparameters=lastsubparameters+(lastsubparameters.length()==0?"":":")+parsed[i];
		}
	}

	public String getLastSubCommand() {
		return lastsubcommand;
	}

	public void setFile(String file) {
		sinfile=file;
	}
	
	public void addSignData (byte[] content) {
		signdata = Arrays.copyOf(content, content.length);
	}

	public void addReply (byte[] d) {
		reply = d;
	}
	
	public String toString() {
		return command + " " + lastsubcommand + " " + (signdata!=null?sinfile:""+" "+(reply!=null?new String(reply):""));
	}

}