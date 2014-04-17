package org.logger;

import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.system.OS;

public class TextAreaAppender extends WriterAppender {
	
	static private StyledText styledText = null;
	static private StringBuilder builder = new StringBuilder();
	public static String timestamp=OS.getTimeStamp();

	public static void writeFile() {
		String logname=OS.getWorkDir()+OS.getFileSeparator()+"flashtool_"+timestamp+".log";
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(logname));
			writer.write(builder.toString());
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

	/** Set the target TextArea for the logging information to appear. */
	static public void setTextArea(StyledText styledText) {
		TextAreaAppender.styledText = styledText;
	}

	/**
	 * Format and then append the loggingEvent to the stored
	 * TextArea.
	 */
	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		final Level level = loggingEvent.getLevel();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				// Append formatted message to textarea.
				builder.append(message);
				if (level==Level.ERROR) {
					append(styledText.getDisplay().getSystemColor(SWT.COLOR_RED),message);
				}
				else if (level==Level.WARN) {
					append(styledText.getDisplay().getSystemColor(SWT.COLOR_BLUE),message);
				}
				else {
					append(styledText.getDisplay().getSystemColor(SWT.COLOR_BLACK),message);
				}
			}
		});

	}
	
	public static void append(final Color color, final String message) {
		if (styledText != null) {
					styledText.append(message);
					StyleRange styleRange = new StyleRange();
					styleRange.start = styledText.getCharCount()-message.length();
					styleRange.length = message.length();
					styleRange.fontStyle = SWT.NORMAL;
					styleRange.foreground = color;
					styledText.setStyleRange(styleRange);
					styledText.setSelection(styledText.getCharCount());
		}
    }

	public String getString() {
		return builder.toString();
	}
}