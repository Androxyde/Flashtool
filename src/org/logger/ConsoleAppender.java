package org.logger;

import gui.MainSWT;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.system.OS;

public class ConsoleAppender extends WriterAppender {
	/**
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
	private static StringBuilder content = new StringBuilder();
	private static String timestamp=getTimeStamp();
	
    public static String getTimeStamp() {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
    	df.setTimeZone( TimeZone.getTimeZone("PST"));  
    	String date = ( df.format(new Date()));    
    	DateFormat df1 = new SimpleDateFormat("hh-mm-ss") ;    
    	df1.setTimeZone( TimeZone.getDefault()) ;  
    	String time = ( df1.format(new Date()));
    	return date+"_"+time;
    }

	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		if (!MainSWT.guimode && MyLogger.lastaction.equals("progress")) {
			System.out.println();
		}
		System.out.print(message);
		content.append(message);
		MyLogger.lastaction="log";
	}

	public static void writeFile() {
		String logname=OS.getWorkDir()+OS.getFileSeparator()+"flashtool_"+timestamp+".log";
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(logname));
			writer.write(content.toString());
		}
		catch (IOException exception) {}
		finally {
			if (writer != null) {
				try {
					MyLogger.getLogger().info("Log written to "+logname);
					writer.close();
				}
				catch (Exception exception) {}
			}
		}
	
	}

}
