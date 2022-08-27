package org.flashtool.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.flashtool.libusb.LibUsbException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyLogger {

	private static int logmode=1;
	public static String lastaction = "";
	public static final int CONSOLE_MODE=1;
	public static final int GUI_MODE=2;
	
	public static String writeFile() {
		String fname = MyLogger.getTimeStamp();
		StringBuilderAppender.writeFile(fname);
		return fname;
	}
	
	public static void setMode(int mode) {
		logmode=mode;
	}

	public static String getMode() {
		if (logmode==CONSOLE_MODE) return "console";
		return "gui";
	}

	public static void setLevel(String level) {
		if (level.toLowerCase().equals("warn")) setLevel(Level.WARN);
		if (level.toLowerCase().equals("error")) setLevel(Level.ERROR);
		if (level.toLowerCase().equals("debug")) setLevel(Level.DEBUG);
		if (level.toLowerCase().equals("info")) setLevel(Level.INFO);
	}

	public static Level getLevel() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger logger = loggerContext.getLogger("root");
		return logger.getLevel();
	}
	
	public static void setLevel(Level level) {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger logger = loggerContext.getLogger("root");
		logger.setLevel(level);
		if (level == Level.ERROR) {
			logger.error("<- This level is successfully initialized");
		}
		if (level == Level.WARN) {
			logger.warn("<- This level is successfully initialized");
		}
		if (level == Level.DEBUG) {
			logger.debug("<- This level is successfully initialized");
		}
		if (level == Level.INFO) {
			logger.info("<- This level is successfully initialized");
		}
	}

    public static String getTimeStamp() {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
    	df.setTimeZone( TimeZone.getTimeZone("PST"));  
    	String date = ( df.format(new Date()));    
    	DateFormat df1 = new SimpleDateFormat("hh-mm-ss") ;    
    	df1.setTimeZone( TimeZone.getDefault()) ;  
    	String time = ( df1.format(new Date()));
    	return date+"_"+time;
    }

}