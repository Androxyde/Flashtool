package org.logger;

import gui.MainSWT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.system.OS;

public class ConsoleAppender extends WriterAppender {

	private static Logger logger = Logger.getLogger(ConsoleAppender.class);
	

	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		if (!MainSWT.guimode && MyLogger.lastaction.equals("progress")) {
			System.out.println();
		}
		System.out.print(message);
		MyLogger.lastaction="log";
	}

}
