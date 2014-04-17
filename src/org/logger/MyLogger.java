package org.logger;

import gui.MainSWT;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class MyLogger {

	static Logger logger = Logger.getLogger(MyLogger.class);
	static boolean isinit = false;
	static ProgressBar _bar = null;
	static boolean hasTextArea = true;
	static long maxstepsconsole = 0;
	static long currentstepconsole = 0;
	static String lastaction = "";
	public static String curlevel;

	public static void registerProgressBar(ProgressBar bar) {
		_bar = bar;
	}
	
	public static void writeFile() {
		if (MainSWT.guimode)
			org.logger.TextAreaAppender.writeFile();
		else
			org.logger.ConsoleAppender.writeFile();
	}

	public static ProgressBar getProgressBar() {
		return _bar;
	}
	
	public static void initProgress(final long max) {
		MyLogger.initProgress((int) max);
	}

	public static void initProgress(final int max) {
					if (MainSWT.guimode) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								_bar.setMinimum(0);
								_bar.setMaximum((int)max);
								_bar.setSelection(0);
							}
						});
					}
					else {
						maxstepsconsole=max;
						currentstepconsole=0;
					}
	}

	public static void updateProgress() {
					if (MainSWT.guimode) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								_bar.setSelection(_bar.getSelection()+1);
							}
						});
					}
					else {
						currentstepconsole++;
						double result = (double)currentstepconsole/(double)maxstepsconsole*100.0;
						printProgBar((int)result);
					}
					lastaction="progress";
	}

	public static void setLevel(String level) {
		curlevel = level.toLowerCase();
		try {
			isinit = true;
			//redirectSystemStreams();
			InputStream in = MyLogger.class.getClassLoader().getResourceAsStream("org/logger/config/" + level.toLowerCase() + ".properties");
			if (in != null)
			{
				Properties pl = new Properties();
                pl.load(in);
                if (!hasTextArea) 
                	pl.setProperty("log4j.rootLogger",level+", console");
                else
                	pl.setProperty("log4j.rootLogger",level+", textarea");
                PropertyConfigurator.configure(pl);
                
			}
			else {
                System.err.println("Error loading log4j properties file");
                System.exit(31);
			}
			if (level.toLowerCase().equals("err"))
				logger.error("<- This level is successfully initialized");
			if (level.toLowerCase().equals("warn"))
				logger.warn("<- This level is successfully initialized");
			if (level.toLowerCase().equals("debug"))
				logger.debug("<- This level is successfully initialized");
			if (level.toLowerCase().equals("info"))
				logger.info("<- This level is successfully initialized");

		}
		catch (Exception e) {
		}
	}

	public static void appendTextArea(StyledText textArea) {
		org.logger.TextAreaAppender.setTextArea(textArea);
	}

	
	public static void disableTextArea() {
		hasTextArea = false;
	}
	

	public static Logger getLogger() {
		return logger;
	}

	public static void printProgBar(long percent){
		if (percent <=100) {
		    StringBuilder bar = new StringBuilder("[");
	
		    for(int i = 0; i < 50; i++){
		        if( i < (percent/2)){
		            bar.append("=");
		        }else if( i == (percent/2)){
		            bar.append(">");
		        }else{
		            bar.append(" ");
		        }
		    }
	
		    bar.append("]   " + percent + "%     ");
		    System.out.print("\r" + bar.toString());
		}
	}

}