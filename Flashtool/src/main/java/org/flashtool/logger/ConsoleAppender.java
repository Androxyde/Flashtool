package org.flashtool.logger;

import org.eclipse.swt.custom.StyledText;
import org.flashtool.gui.MainSWT;
import org.flashtool.libusb.LibUsbException;

import java.io.Serializable;  
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReadWriteLock;  
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Plugin(
    name = "ConsoleAppender",
    category = "Core",
    elementType = "appender",
    printObject = true)
public final class ConsoleAppender extends AbstractAppender {

  static private StyledText styledText = null;

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();


  protected ConsoleAppender(String name, Filter filter,
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
	if (!MainSWT.guimode && MyLogger.lastaction.equals("progress")) {
		System.out.println();
	}
	System.out.print(message);
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
  public static ConsoleAppender createAppender(
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
    return new ConsoleAppender(name, filter, layout, true);
  }
}