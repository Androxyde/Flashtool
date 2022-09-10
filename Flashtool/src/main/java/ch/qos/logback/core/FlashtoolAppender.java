/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core;

import static ch.qos.logback.core.CoreConstants.CODES_URL;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.flashtool.logger.MyLogger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;

/**
 * OutputStreamAppender appends events to a {@link OutputStream}. This class
 * provides basic services that other appenders build upon.
 * 
 * For more information about this appender, please refer to the online manual
 * at http://logback.qos.ch/manual/appenders.html#OutputStreamAppender
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class FlashtoolAppender<E> extends UnsynchronizedAppenderBase<E> {

    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;
    private final static String AnsiConsole_CLASS_NAME = "org.fusesource.jansi.AnsiConsole";
    private final static String wrapSystemOut_METHOD_NAME = "wrapSystemOut";
    private final static String wrapSystemErr_METHOD_NAME = "wrapSystemErr";
    private final static Class<?>[] ARGUMENT_TYPES = { PrintStream.class };
    private StyledText text = null;
    private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    private BufferedOutputStream boutputStream = new BufferedOutputStream(byteStream);

    /**
     * It is the encoder which is ultimately responsible for writing the event to an
     * {@link OutputStream}.
     */
    protected Encoder<E> encoder;

    /**
     * All synchronization in this class is done via the lock object.
     */
    protected final ReentrantLock lock = new ReentrantLock(false);

    /**
     * This is the {@link OutputStream outputStream} where output will be written.
     */
    private OutputStream outputStream;

    boolean immediateFlush = true;

    /**
     * Sets the value of the <b>Target</b> option. Recognized values are
     * "System.out" and "System.err". Any other value will be ignored.
     */
    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null) {
            targetWarn(value);
        } else {
            target = t;
        }
    }

    /**
     * Returns the current value of the <b>target</b> property. The default value of
     * the option is "System.out".
     * <p>
     * See also {@link #setTarget}.
     */
    public String getTarget() {
        return target.getName();
    }

    private void targetWarn(String val) {
        Status status = new WarnStatus("[" + val + "] should be one of " + Arrays.toString(ConsoleTarget.values()),
                this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        addStatus(status);
    }

    /**
     * The underlying output stream used by this appender.
     * 
     * @return
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Checks that requires parameters are set and if everything is in order,
     * activates this appender.
     */
    public void start() {
    	
        OutputStream targetStream = target.getStream();
        // enable jansi only if withJansi set to true
        if (withJansi) {
            targetStream = wrapWithJansi(targetStream);
        }
        setOutputStream(targetStream);

        int errors = 0;
        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
            errors++;
        }

        if (this.outputStream == null) {
            addStatus(new ErrorStatus("No output stream set for the appender named \"" + name + "\".", this));
            errors++;
        }
        // only error free appenders should be activated
        if (errors == 0) {
            super.start();
        }
    }

    private OutputStream wrapWithJansi(OutputStream targetStream) {
        try {
            addInfo("Enabling JANSI AnsiPrintStream for the console.");
            ClassLoader classLoader = Loader.getClassLoaderOfObject(context);
            Class<?> classObj = classLoader.loadClass(AnsiConsole_CLASS_NAME);
            String methodName = target == ConsoleTarget.SystemOut ? wrapSystemOut_METHOD_NAME
                    : wrapSystemErr_METHOD_NAME;
            Method method = classObj.getMethod(methodName, ARGUMENT_TYPES);
            return (OutputStream) method.invoke(null, new PrintStream(targetStream));
        } catch (Exception e) {
            addWarn("Failed to create AnsiPrintStream. Falling back on the default stream.", e);
        }
        return targetStream;
    }
    
    /**
     * @return whether to use JANSI or not.
     */
    public boolean isWithJansi() {
        return withJansi;
    }

    /**
     * If true, this appender will output to a stream provided by the JANSI library.
     *
     * @param withJansi whether to use JANSI or not.
     * @since 1.0.5
     */
    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
    
    public void setLayout(Layout<E> layout) {
        addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        addWarn("See also " + CODES_URL + "#layoutInsteadOfEncoder for details");
        LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder<E>();
        lwe.setLayout(layout);
        lwe.setContext(context);
        this.encoder = lwe;
    }

    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }

        subAppend(eventObject);
    }

    /**
     * Stop this appender instance. The underlying stream or writer is also closed.
     * 
     * <p>
     * Stopped appenders cannot be reused.
     */
    public void stop() {
        if(!isStarted())
            return;

        lock.lock();
        try {
            closeOutputStream();
            super.stop();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Close the underlying {@link OutputStream}.
     */
    protected void closeOutputStream() {
        if (this.outputStream != null) {
            try {
                // before closing we have to output out layout's footer
                encoderClose();
                this.outputStream.close();
                this.outputStream = null;
            } catch (IOException e) {
                addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
            }
        }
    }

    void encoderClose() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] footer = encoder.footerBytes();
                writeBytes(footer);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to write footer for appender named [" + name + "].", this, ioe));
            }
        }
    }

    /**
     * <p>
     * Sets the @link OutputStream} where the log output will go. The specified
     * <code>OutputStream</code> must be opened by the user and be writable. The
     * <code>OutputStream</code> will be closed when the appender instance is
     * closed.
     * 
     * @param outputStream An already opened OutputStream.
     */
    public void setOutputStream(OutputStream outputStream) {
        lock.lock();
        try {
            // close any previously opened output stream
            closeOutputStream();
            this.outputStream = outputStream;
            if (encoder == null) {
                addWarn("Encoder has not been set. Cannot invoke its init method.");
                return;
            }

            encoderInit();
        } finally {
            lock.unlock();
        }
    }

    void encoderInit() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] header = encoder.headerBytes();
                writeBytes(header);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(
                        new ErrorStatus("Failed to initialize encoder for appender named [" + name + "].", this, ioe));
            }
        }
    }

    protected void writeOut(E event) throws IOException {
        byte[] byteArray = this.encoder.encode(event);
        writeBytes(byteArray);
    	Display.getDefault().syncExec(new Runnable() {
			public void run() {
				// Append formatted message to textarea.
				if (MyLogger.getLevel()==Level.ERROR) {
					append(text.getDisplay().getSystemColor(SWT.COLOR_RED),new String(byteArray));
				}
				else if (MyLogger.getLevel()==Level.WARN) {
					append(text.getDisplay().getSystemColor(SWT.COLOR_BLUE),new String(byteArray));
				}
				else {
					append(text.getDisplay().getSystemColor(SWT.COLOR_BLACK),new String(byteArray));
				}
			}
		});
    }
    
	public void append(final Color color, final String message) {
					text.append(message);
					StyleRange styleRange = new StyleRange();
					styleRange.start = text.getCharCount()-message.length();
					styleRange.length = message.length();
					styleRange.fontStyle = SWT.NORMAL;
					styleRange.foreground = color;
					text.setStyleRange(styleRange);
					text.setSelection(text.getCharCount());
    }

    private void writeBytes(byte[] byteArray) throws IOException {
        if (byteArray == null || byteArray.length == 0)
            return;

        lock.lock();
        try {
            if (text==null) this.outputStream.write(byteArray);
            this.boutputStream.write(byteArray);
            if (immediateFlush) {
            	if (text==null) this.outputStream.flush();
                this.boutputStream.flush();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Actual writing occurs here.
     * <p>
     * Most subclasses of <code>WriterAppender</code> will need to override this
     * method.
     * 
     * @since 0.9.0
     */
    protected void subAppend(E event) {
        if (!isStarted()) {
            return;
        }
        try {
            // this step avoids LBCLASSIC-139
            if (event instanceof DeferredProcessingAware) {
                ((DeferredProcessingAware) event).prepareForDeferredProcessing();
            }
            writeOut(event);

        } catch (IOException ioe) {
            // as soon as an exception occurs, move to non-started state
            // and add a single ErrorStatus to the SM.
            this.started = false;
            addStatus(new ErrorStatus("IO failure in appender", this, ioe));
        }
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    public void setStyledText(StyledText text) {
        lock.lock();
        boolean first=false;
        if (this.text==null) first=true;
    	this.text=text;
    	if (first) {
    	Scanner sc = new Scanner(getContent());
    	while (sc.hasNextLine()) {
    		String line = sc.nextLine();
        	Display.getDefault().syncExec(new Runnable() {
    			public void run() {
    				// Append formatted message to textarea.
    	    		if (line.contains("ERROR")) {
    	    			append(text.getDisplay().getSystemColor(SWT.COLOR_RED),line+"\n");
    	    		}
    	    		else if (line.contains("WARN")) {
    	    			append(text.getDisplay().getSystemColor(SWT.COLOR_BLUE),line+"\n");
    	    		}
    	    		else {
    	    			append(text.getDisplay().getSystemColor(SWT.COLOR_BLACK),line+"\n");
    	    		}
    			}
    		});
    	}
    	}
    	lock.unlock();
    }

    public String getContent() {
    	try {
			return byteStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }
    
    public void writeFile(String fname) {
    	final File file = new File(fname);
    	try {
			FileUtils.writeStringToFile(file, getContent(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void setMode(String mode) {
    /*	if (!mode.equals(this.mode)) {
    		log.info("Changing mode from "+this.mode + " to "+mode);
    		this.mode=mode;
    	}*/
    }

}