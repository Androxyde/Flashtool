package org.flashtool.logger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.flashtool.system.OS;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.FlashtoolAppender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyLogger {

	private static String logmode="CONSOLE";
	public static String lastaction = "";
	public static final String CONSOLE_MODE="CONSOLE";
	public static final String GUI_MODE="GUI";

	
	public static String writeFile() {
		String fname = MyLogger.getTimeStamp();
		FlashtoolAppender<?> fa = getAppender();
		fa.writeFile(OS.getFolderUserFlashtool()+File.separator+"flashtool_"+fname+".log");
		return fname;
	}
	
	public static FlashtoolAppender<?> getAppender() {
		Logger root = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		FlashtoolAppender<?> sa = (FlashtoolAppender<?>)root.getAppender("Flashtool");
		return sa;
	}

	public static void setMode(String mode) {

		logmode=mode;
		FlashtoolAppender<?> fa = getAppender();
		fa.setMode(logmode);

	}

	public static String getMode() {
		return logmode.toLowerCase();
	}

	public static void setLevel(String level) {
		if (level.toLowerCase().equals("warn")) setLevel(Level.WARN);
		if (level.toLowerCase().equals("error")) setLevel(Level.ERROR);
		if (level.toLowerCase().equals("debug")) setLevel(Level.DEBUG);
		if (level.toLowerCase().equals("info")) setLevel(Level.INFO);
	}

	public static Level getLevel() {
		Logger root = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		return root.getLevel();
	}
	
	public static void setLevel(Level level) {
		Logger root = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		root.setLevel(level);
		if (level == Level.ERROR) {
			log.error("<- This level is successfully initialized");
		}
		if (level == Level.WARN) {
			log.warn("<- This level is successfully initialized");
		}
		if (level == Level.DEBUG) {
			log.debug("<- This level is successfully initialized");
		}
		if (level == Level.INFO) {
			log.info("<- This level is successfully initialized");
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