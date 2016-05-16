package org.logger;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import org.system.GlobalConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class MyLogger {

	//public static String curlevel;
	static String lastaction = "";
	static String logdest ="";
	static Properties proplog=null;
	static final Logger logger = LogManager.getLogger(MyLogger.class);
	public static final int CONSOLE_MODE=1;
	public static final int GUI_MODE=2;
	
	public static String writeFile() {
		String fname = MyLogger.getTimeStamp();
		StringBuilderAppender.writeFile(fname);
		return fname;
	}
	
	public static void setMode(int mode) {
		System.setProperty("enableConsole",(mode==CONSOLE_MODE)?"true":"false");
		System.setProperty("enableGUI",(mode==GUI_MODE)?"true":"false");
	}
/*	public static void initProperties() {
				
		LoggerContext context= (LoggerContext) LogManager.getContext();
        Configuration config= context.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern(PatternLayout.SIMPLE_CONVERSION_PATTERN).build();
        Appender appender=TextAreaAppender.createAppender("TEXTAREA_APPENDER", layout, null);
        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("File", Level.INFO, null);
        AppenderRef[] refs = new AppenderRef[] { ref };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("true", Level.INFO, LogManager.ROOT_LOGGER_NAME, "true",
                refs, null, config, null);
        loggerConfig.addAppender(appender, Level.INFO, null);
        config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
        context.updateLoggers();
	}*/

	public static void setLevel(String level) {
		if (level.toLowerCase().equals("warn")) setLevel(Level.WARN);
		if (level.toLowerCase().equals("error")) setLevel(Level.ERROR);
		if (level.toLowerCase().equals("debug")) setLevel(Level.DEBUG);
		if (level.toLowerCase().equals("info")) setLevel(Level.INFO);
	}

	public static Level getLevel() {
		return LogManager.getRootLogger().getLevel();
	}
	
	public static void setLevel(Level level) {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(level);
		ctx.updateLoggers();
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