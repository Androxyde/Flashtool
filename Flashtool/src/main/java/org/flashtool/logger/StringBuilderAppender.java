package org.flashtool.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.flashtool.libusb.LibUsbException;
import org.flashtool.system.OS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Plugin(
	    name = "StringBuilderAppender",
	    category = "Core",
	    elementType = "appender",
	    printObject = true)
public final class StringBuilderAppender extends AbstractAppender {

	private static StringBuilder content = new StringBuilder();

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();

	public static void writeFile(String timestamp) {
		String logname=OS.getFolderUserFlashtool()+File.separator+"flashtool_"+timestamp+".log";
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

	  protected StringBuilderAppender(String name, Filter filter,
	                             Layout<? extends Serializable> layout,
	                             final boolean ignoreExceptions) {
	    super(name, filter, layout, ignoreExceptions);
	  }

	  /**
	   * This method is where the appender does the work.
	   *
	   * @param event Log event with log data
	   */
	  @Override
		public void append(LogEvent event) {
			readLock.lock();
			String message = new String(getLayout().toByteArray(event));
			content.append(message);
			MyLogger.lastaction="log";
			readLock.unlock();
		}

	  /**
	   * Factory method. Log4j will parse the configuration and call this factory 
	   * method to construct the appender with
	   * the configured attributes.
	   *
	   * @param name   Name of appender
	   * @param layout Log layout of appender
	   * @param filter Filter for appender
	   * @return The TextAreaAppender
	   */
	  @PluginFactory
	  public static StringBuilderAppender createAppender(
	      @PluginAttribute("name") String name,
	      @PluginElement("Layout") Layout<? extends Serializable> layout,
	      @PluginElement("Filter") final Filter filter) {
	    if (name == null) {
	      LOGGER.error("No name provided for TextAreaAppender2");
	      return null;
	    }
	    if (layout == null) {
	      layout = PatternLayout.createDefaultLayout();
	    }
	    return new StringBuilderAppender(name, filter, layout, true);
	  }


	}