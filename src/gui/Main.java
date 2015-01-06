package gui;

import java.io.File;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import libusb.LibUsbException;
import linuxlib.JUsb;

import org.logger.MyLogger;
import org.system.AWTKillerThread;
import org.system.OS;

import flashsystem.FlasherConsole;

public class Main {

	public static void main(String[] args) {
		new File(System.getProperty("user.home")+File.separator+"FlashTool"+File.separator+"Firmwares"+File.separator+"Downloads").mkdirs();
		new File(System.getProperty("user.home")+File.separator+"FlashTool"+File.separator+"devices").mkdirs();
		new File(System.getProperty("user.home")+File.separator+"FlashTool"+File.separator+"registeredDevices").mkdirs();
		AWTKillerThread k = new AWTKillerThread();
		k.start();
		try {
			OptionSet options = parseCmdLine(args);
			Main.initLinuxUsb();
			if (options.has("console")) {
				MyLogger.setLogDest("console");
				MyLogger.setLevel("info");
				processConsole(options);
			}
			else {
				MyLogger.setLogDest("textarea");
				MainSWT window = new MainSWT();
				window.open();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		k.done();
	}

	
	public static void initLinuxUsb() throws LibUsbException {
			if (OS.getName()!="windows") JUsb.init();
	}

	private static OptionSet parseCmdLine(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSet options;
		parser.accepts( "console" );
		try {
			options = parser.parse(args);
		}
		catch (Exception e) {
			parser.accepts("action").withRequiredArg().required();
			parser.accepts("file").withOptionalArg().defaultsTo("");
			parser.accepts("method").withOptionalArg().defaultsTo("auto");
			parser.accepts("wipedata").withOptionalArg().defaultsTo("yes");
			parser.accepts("wipecache").withOptionalArg().defaultsTo("yes");
			parser.accepts("baseband").withOptionalArg().defaultsTo("yes");
			parser.accepts("system").withOptionalArg().defaultsTo("yes");
			parser.accepts("kernel").withOptionalArg().defaultsTo("yes");
			options = parser.parse(args);        	
		}
		return options;
	}

	public static void processConsole(OptionSet options) throws Exception {
		String action=(String)options.valueOf("action");
		
		if (action.toLowerCase().equals("flash")) {
			FlasherConsole.init(false);
			FlasherConsole.doFlash((String)options.valueOf("file"), options.valueOf("wipedata").equals("yes"), options.valueOf("wipecache").equals("yes"), options.valueOf("baseband").equals("no"), options.valueOf("kernel").equals("no"), options.valueOf("system").equals("no"));
		}
		if (action.toLowerCase().equals("imei")) {
			FlasherConsole.init(false);
			FlasherConsole.doGetIMEI();
		}
		if (action.toLowerCase().equals("root")) {
			FlasherConsole.init(true);
			FlasherConsole.doRoot();
		}
		if (action.toLowerCase().equals("extract")) {
			FlasherConsole.init(true);
			FlasherConsole.doExtract((String)options.valueOf("file"));
		}
		/*		if (action.toLowerCase().equals("blunlock")) {
			FlasherConsole.init(true);
			FlasherConsole.doBLUnlock();        		
		}*/
		FlasherConsole.exit();		
	}

}
