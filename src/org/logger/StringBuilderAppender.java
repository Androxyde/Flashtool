package org.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.system.OS;

public class StringBuilderAppender extends WriterAppender {

	private static StringBuilder content = new StringBuilder();
	
	public static void writeFile(String timestamp) {
		String logname=OS.getWorkDir()+File.separator+"flashtool_"+timestamp+".log";
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(logname));
			writer.write(content.toString());
		}
		catch (IOException exception) {}
		finally {
			if (writer != null) {
				try {
					writer.close();
				}
				catch (Exception exception) {}
			}
		}
	}

	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		content.append(message);
		MyLogger.lastaction="log";
	}

}