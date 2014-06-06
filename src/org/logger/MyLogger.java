package org.logger;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.system.GlobalConfig;

public class MyLogger {

	public static String curlevel;
	static String lastaction = "";
	static String logdest ="";
	static Properties proplog=null;
	static Logger logger = Logger.getLogger(MyLogger.class);
	
	public static void writeFile() {
		StringBuilderAppender.writeFile(MyLogger.getTimeStamp());
	}

	public static void setLogDest(String dest) {
		logdest = dest;
		if (!initProperties()) {
			proplog.setProperty("log4j.rootLogger",curlevel+", memory, "+logdest);
			PropertyConfigurator.configure(proplog);
		}
	}

	public static boolean initProperties() {
		if (proplog==null) {
			curlevel=GlobalConfig.getProperty("loglevel").toLowerCase();
			try {
			InputStream in = MyLogger.class.getClassLoader().getResourceAsStream("org/logger/config/" + curlevel + ".properties");
				proplog = new Properties();
	            proplog.load(in);
	            if (logdest.length()==0) logdest="console";
	            proplog.setProperty("log4j.rootLogger",curlevel+", memory, "+logdest);
	            PropertyConfigurator.configure(proplog);
	            return true;
			}
			catch (Exception e) {
	            System.err.println("Error loading log4j properties file");
	            System.exit(31);
			}
		}
		return false;
	}

	public static void setLevel(String level) {
		curlevel = level.toLowerCase();
		if (!initProperties()) {
			if (curlevel.equals("err"))
				Logger.getRootLogger().setLevel(Level.ERROR);
			if (curlevel.equals("warn"))
				Logger.getRootLogger().setLevel(Level.WARN);
			if (curlevel.equals("debug"))
				Logger.getRootLogger().setLevel(Level.DEBUG);
			if (curlevel.equals("info"))
				Logger.getRootLogger().setLevel(Level.INFO);
		}
		try {
			if (curlevel.equals("err"))
				logger.error("<- This level is successfully initialized");
			if (curlevel.equals("warn"))
				logger.warn("<- This level is successfully initialized");
			if (curlevel.equals("debug"))
				logger.debug("<- This level is successfully initialized");
			if (curlevel.equals("info"))
				logger.info("<- This level is successfully initialized");
		}
		catch (Exception e) {
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