package org.flashtool.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;  
import org.apache.logging.log4j.core.Layout;  
import org.apache.logging.log4j.core.LogEvent;  
import org.apache.logging.log4j.core.appender.AbstractAppender;  
import org.apache.logging.log4j.core.config.plugins.Plugin;  
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;  
import org.apache.logging.log4j.core.config.plugins.PluginElement;  
import org.apache.logging.log4j.core.config.plugins.PluginFactory;  
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.flashtool.libusb.LibUsbException;

import java.io.Serializable;  
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReadWriteLock;  
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Plugin(
    name = "TextAreaAppender",
    category = "Core",
    elementType = "appender",
    printObject = true)
public final class TextAreaAppender extends AbstractAppender {

  static private StyledText styledText = null;
  static private Color cblack = null;
  static private Color cred = null;
  static private Color cblue = null;

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();


  protected TextAreaAppender(String name, Filter filter,
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
    Level l = event.getLevel();
	if (styledText!=null) {
		StyleRange styleRange = new StyleRange();
		if (l==Level.ERROR) {
			styleRange.length = message.length();
			styleRange.fontStyle = SWT.NORMAL;
			styleRange.foreground = cred;
			
		}
		else if (l==Level.WARN) {
			styleRange.length = message.length();
			styleRange.fontStyle = SWT.NORMAL;
			styleRange.foreground = cblue;
		}
		else {
			
			styleRange.length = message.length();
			styleRange.fontStyle = SWT.NORMAL;
			styleRange.foreground = cblack;
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// Append formatted message to textarea.
				styleRange.start = styledText.getCharCount();
				styledText.append(message);
				styledText.setStyleRange(styleRange);
				styledText.setSelection(styledText.getCharCount());
			}
		});
	}
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
  public static TextAreaAppender createAppender(
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
    return new TextAreaAppender(name, filter, layout, true);
  }


  /**
   * Set TextArea to append
   *
   * @param textArea TextArea to append
   */
  public static void setTextArea(StyledText styledText) {
    TextAreaAppender.styledText = styledText;
    cred = styledText.getDisplay().getSystemColor(SWT.COLOR_RED);
    cblack = styledText.getDisplay().getSystemColor(SWT.COLOR_BLACK);
    cblue = styledText.getDisplay().getSystemColor(SWT.COLOR_BLUE);
  }
}