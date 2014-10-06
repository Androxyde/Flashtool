package flashsystem;

import flashsystem.HexDump;
import flashsystem.io.USBFlash;
import gui.tools.WidgetTask;
import gui.tools.XMLBootConfig;
import gui.tools.XMLBootDelivery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;
import org.jdom.JDOMException;
import org.logger.LogProgress;
import org.system.Device;
import org.system.DeviceChangedListener;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.TextFile;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class X10flash {

    private Bundle _bundle;
    private Command cmd;
    private LoaderInfo phoneprops = null;
    private String firstRead = "";
    private String cmd01string = "";
    private boolean taopen = false;
    private boolean modded_loader=false;
    private String currentdevice = "";
    private int maxpacketsize = 0;
    private String serial = "";
    private Shell _curshell;
    private TaEntry _8fdunit = null;
    private TaEntry _8a4unit = null;
    private static Logger logger = Logger.getLogger(X10flash.class);

    public X10flash(Bundle bundle, Shell shell) {
    	_bundle=bundle;
    	_curshell = shell;
    }

    public String getCurrentDevice() {
    	return currentdevice;
    }

    public void setFlashState(boolean ongoing) throws IOException,X10FlashException
    {
	    	if (ongoing) {
	    		openTA(2);
	    		cmd.send(Command.CMD13,Command.TA_FLASH_STARTUP_SHUTDOWN_RESULT_ONGOING,false);
	    		closeTA();
	    	}
	    	else {
	    		openTA(2);
	    		cmd.send(Command.CMD13, Command.TA_FLASH_STARTUP_SHUTDOWN_RESULT_FINISHED,false);
	    		closeTA();
	    	}
    }

    private void sendTA(File f) throws FileNotFoundException, IOException,X10FlashException {
    	try {
    		TaFile ta = new TaFile(f);
    		logger.info("Flashing "+ta.getName());
			Vector<TaEntry> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				TaEntry tent = entries.get(i);
				if (tent.getPartition().equals(_8a4unit.getPartition()) && tent.getData().equals(_8a4unit.getData())) {
					logger.info("Custumization reset is now based on UI choice");
				}
				else
					sendTAUnit(tent);
			}
    	}
    	catch (TaParseException tae) {
    		logger.error("Error parsing TA file. Skipping");
    	}
    }

    private void sendTA(TaFile ta) throws FileNotFoundException, IOException,X10FlashException {
    		logger.info("Flashing "+ta.getName());
			Vector<TaEntry> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				sendTAUnit(entries.get(i));
			}
    }

    public void sendTAUnit(TaEntry ta) throws X10FlashException, IOException {
		logger.info("Writing TA unit : "+HexDump.toHex(ta.getWordbyte()));
		if (!_bundle.simulate()) {
			cmd.send(Command.CMD13, ta.getWordbyte(),false);  
		} 	
    }

    public TaEntry dumpProperty(int unit) throws IOException, X10FlashException
    {
    		String sunit = HexDump.toHex(BytesUtil.getBytesWord(unit, 4));
    		sunit = sunit.replace("[", "");
    		sunit = sunit.replace("]", "");
    		sunit = sunit.replace(",", "");
    		sunit = sunit.replace(" ", "");
    		logger.info("Start Reading unit "+sunit);
	        logger.debug((new StringBuilder("%%% read TA property id=")).append(unit).toString());
	        try {
	        	cmd.send(Command.CMD12, BytesUtil.getBytesWord(unit, 4),false);
	        	logger.info("Reading TA finished.");
	        }
	        catch (X10FlashException e) {
	        	logger.info("Reading TA finished.");
	        	return null;
	        }
	        if (cmd.getLastReplyLength()>0) {
        		TaEntry ta = new TaEntry();
        		ta.setPartition(HexDump.toHex(unit));
	        	String reply = cmd.getLastReplyHex();
	        	reply = reply.replace("[", "");
	        	reply = reply.replace("]", "");
	        	reply = reply.replace(",", "");
        		ta.addData(reply.trim());
        		return ta;
    		}
			return null;
    }

    public Vector<TaEntry> dumpProperties()
    {
    	Vector<TaEntry> v = new Vector();
    	try {
		    logger.info("Start Dumping TA");
		    LogProgress.initProgress(9789);
	        for(int i = 0; i < 4920; i++) {
	        	try {
	        		cmd.send(Command.CMD12, BytesUtil.getBytesWord(i, 4),false);
		        	String reply = cmd.getLastReplyHex();
		        	reply = reply.replace("[", "");
		        	reply = reply.replace("]", "");
		        	reply = reply.replace(",", "");
		        	if (cmd.getLastReplyLength()>0) {
		        		TaEntry ta = new TaEntry();
		        		ta.setPartition(HexDump.toHex(i));
		        		ta.addData(reply.trim());
		        		v.add(ta);
		        	}

	        	}
	        	catch (X10FlashException e) {
	        	}
	        }
	        LogProgress.initProgress(0);
	        logger.info("Dumping TA finished.");
	    }
    	catch (Exception ioe) {
    		LogProgress.initProgress(0);
    		logger.error(ioe.getMessage());
    		logger.error("Error dumping TA. Aborted");
    		closeDevice();
    	}
    	return v;
    }

    public void BackupTA() throws IOException, X10FlashException {
    	int partition = 2;
    	openTA(partition);
    	String folder = OS.getWorkDir()+File.separator+"custom"+File.separator+"mydevices"+File.separator+getPhoneProperty("MSN")+File.separator+"s1ta"+File.separator+OS.getTimeStamp();
    	new File(folder).mkdirs();
    	TextFile tazone = new TextFile(folder+File.separator+partition+".ta","ISO8859-1");
    	logger.info("TA partition "+partition+" saved to "+folder+File.separator+partition+".ta");
        tazone.open(false);
    	try {
		    logger.info("Start Dumping TA");
		    LogProgress.initProgress(4920);
	        for(int i = 0; i < 4920; i++) {
	        	try {
		        	logger.debug((new StringBuilder("%%% read TA property id=")).append(i).toString());
		        	cmd.send(Command.CMD12, BytesUtil.getBytesWord(i, 4),false);
		        	String reply = cmd.getLastReplyHex();
		        	reply = reply.replace("[", "");
		        	reply = reply.replace("]", "");
		        	reply = reply.replace(",", "");
		        	if (cmd.getLastReplyLength()>0) {
		        		tazone.writeln(HexDump.toHex(i) + " " + HexDump.toHex(cmd.getLastReplyLength()) + " " + reply.trim());
		        	}
	        	}
	        	catch (X10FlashException e) {
	        	}
	        }
	        LogProgress.initProgress(0);
	        tazone.close();
	        closeTA();
	    }
    	catch (Exception ioe) {
	        tazone.close();
	        closeTA();
	        LogProgress.initProgress(0);
    		logger.error(ioe.getMessage());
    		logger.error("Error dumping TA. Aborted");
    	}
    }
    
    public void RestoreTA(String tafile) throws FileNotFoundException, IOException, X10FlashException {
    	openTA(2);
    	sendTA(new File(tafile));
		closeTA();
		LogProgress.initProgress(0);	    
    }
    
    private void processHeader(SinFile sin) throws X10FlashException {
    	try {
    		logger.info("    Checking header");
    		SinFileHeader header = sin.getSinHeader();
			for (int j=0;j<header.getNbChunks();j++) {
				int cur = j+1;
				logger.debug("Sending part "+cur+" of "+header.getNbChunks());
				cmd.send(Command.CMD05, header.getChunckBytes(j), !((j+1)==header.getNbChunks()));
				if (USBFlash.getLastFlags() == 0)
					getLastError();
			}
	    }
    	catch (IOException ioe) {
    		throw new X10FlashException("Error in processHeader : "+ioe.getMessage());
    	}
    }
 
    public void getLastError() throws IOException, X10FlashException {
            cmd.send(Command.CMD07,Command.VALNULL,false);    	
    }
    
    private void uploadImage(SinFile sin) throws X10FlashException {
    	try {
    		logger.info("Processing "+sin.getShortFileName());
    		logger.debug(sin);
	    	processHeader(sin);
	    	logger.info("    Flashing data");
	    	logger.debug("Number of parts to send : "+sin.getNbChunks()+" / Part size : "+sin.getChunkSize());
			for (int j=0;j<sin.getNbChunks();j++) {
				int cur = j+1;
				logger.debug("Sending part "+cur+" of "+sin.getNbChunks());
				cmd.send(Command.CMD06, sin.getChunckBytes(j), !((j+1)==sin.getNbChunks()));
				if (USBFlash.getLastFlags() == 0)
					getLastError();
			}
			logger.info("Processing of "+sin.getShortFileName()+" finished.");
    	}
    	catch (Exception e) {
    		logger.error("Processing of "+sin.getShortFileName()+" finished with errors.");
    		e.printStackTrace();
    		throw new X10FlashException (e.getMessage());
    	}
    }

    private String getDefaultLoader() {
    	int nbfound = 0;
    	String loader = "";
    	Enumeration<Object> e = Devices.listDevices(true);
    	while (e.hasMoreElements()) {
    		DeviceEntry current = Devices.getDevice((String)e.nextElement());
    		if (current.getRecognition().contains(currentdevice)) {
    			nbfound++;
    			if (modded_loader) {
    				loader = current.getLoaderUnlocked();
    			}
    			else {
    				loader=current.getLoader();
    			}
    		}
    	}
    	if ((nbfound == 0) || (nbfound > 1)) 
    		return "";
    	if (modded_loader)
			logger.info("Using an unofficial loader");
    	return OS.getWorkDir()+loader.substring(1, loader.length());
    }

    public void sendLoader() throws FileNotFoundException, IOException, X10FlashException {
    	String loader = "";
		if (!modded_loader) {
			if (_bundle.hasLoader()) {
				loader = _bundle.getLoader().getAbsolutePath();
			}
			else {
				loader = getDefaultLoader();
			}
		}
		else {
			loader = getDefaultLoader();
			if (!new File(loader).exists()) loader="";
			if (loader.length()==0)
				if (_bundle.hasLoader()) {
					logger.info("Device loader has not been identified. Using the one from the bundle");
					loader = _bundle.getLoader().getAbsolutePath();
				}
		}
		if (loader.length()==0) {
			String device = WidgetTask.openDeviceSelector(_curshell);
			if (device.length()==0)
				throw new X10FlashException("No loader found for this device");
			else {
				DeviceEntry ent = new DeviceEntry(device);
				loader = ent.getLoader();				
			}
		}
		SinFile sin = new SinFile(loader);
		if (sin.getSinHeader().getVersion()>=2)
			sin.setChunkSize(0x10000);
		else
			sin.setChunkSize(0x1000);
		uploadImage(sin);
		USBFlash.readS1Reply();
		hookDevice(true);
    }

    public void sendPartition() throws FileNotFoundException, IOException, X10FlashException {		
		if (_bundle.hasPartition()) {
			BundleEntry entry = _bundle.getPartition();
			SinFile sin = new SinFile(entry.getAbsolutePath());
			sin.setChunkSize(maxpacketsize);
			uploadImage(sin);
		}
    }

    public void sendBoot() throws FileNotFoundException, IOException,X10FlashException {
    	if (_bundle.hasBoot()) {
    		openTA(2);
    		Enumeration<String> e = _bundle.getMeta().getEntriesOf("BOOT", true);
    		while (e.hasMoreElements()) {
				String entry = (String)e.nextElement();
				BundleEntry bent = _bundle.getEntry(entry);
				SinFile sin = new SinFile(bent.getAbsolutePath());
				sin.setChunkSize(maxpacketsize);
				uploadImage(sin);
    		}
    		closeTA();
    	}
    }
    
    public void sendImages() throws FileNotFoundException, IOException,X10FlashException {
    	Iterator<Integer> orderlist = _bundle.getMeta().getOrder();
    	if (orderlist.hasNext()) {
    		openTA(2);
	    	while (orderlist.hasNext()) {
	    		int place = orderlist.next();
	    		if (place>0) {
	    			String categ = _bundle.getMeta().getCagorie(place);
	    			if (_bundle.getMeta().isCategEnabled(categ)) {
	    				Enumeration entries = _bundle.getMeta().getEntriesOf(categ,true);
	    				while (entries.hasMoreElements()) {
	    					String entry = (String)entries.nextElement();
	    					BundleEntry bent = _bundle.getEntry(entry);
	    					SinFile sin = new SinFile(bent.getAbsolutePath());
	    					sin.setChunkSize(maxpacketsize);
	    					uploadImage(sin);
	    				}
	    			}    			
	    		}
	    	}
	    	closeTA();
    	}
    }

    public String getPhoneProperty(String property) {
    	return phoneprops.getProperty(property);
    }

    public void openTA(int partition) throws X10FlashException, IOException{
    	if (!taopen)
    		cmd.send(Command.CMD09, BytesUtil.getBytesWord(partition, 1), false);
    	taopen = true;
    }
    
    public void closeTA() throws X10FlashException, IOException{
    	if (taopen)
    		cmd.send(Command.CMD10, Command.VALNULL, false);
    	taopen = false;
    }

    public void sendBootDelivery() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TaParseException {
    	try {
    		if (!_bundle.hasBootDelivery()) throw new BootDeliveryException ("No bootdelivery to send");
    		logger.info("Parsing boot delivery");
    		XMLBootDelivery xml = _bundle.getXMLBootDelivery();
    		if (!xml.mustUpdate(phoneprops.getProperty("BOOTVER"))) throw new BootDeliveryException("Boot delivery up to date. Nothing to do");    			
    		Enumeration<XMLBootConfig> e = xml.getBootConfigs();
    		Vector<XMLBootConfig> found = new Vector<XMLBootConfig>();
    		while (e.hasMoreElements()) {
    			// We get matching bootconfig from all configs
    			XMLBootConfig bc=e.nextElement();
    			if (bc.matches(phoneprops.getProperty("OTP_LOCK_STATUS_1"), phoneprops.getProperty("OTP_DATA_1"), phoneprops.getProperty("IDCODE_1")))
    				found.add(bc);
    		}
    		if (found.size()==0)
    			throw new BootDeliveryException ("Found no matching config. Skipping boot delivery");
    		// if found more thant 1 config
    		boolean same = true;
    		if (found.size()>1) {
    			// Check if all found configs have the same fileset
    			Iterator<XMLBootConfig> masterlist = found.iterator();
				while (masterlist.hasNext()) {
					XMLBootConfig masterconfig = masterlist.next();
					Iterator<XMLBootConfig> slavelist = found.iterator();
					while (slavelist.hasNext()) {
						XMLBootConfig slaveconfig = slavelist.next();
						if (slaveconfig.compare(masterconfig)==2)
							throw new BootDeliveryException ("Cannot decide among found configurations. Skipping boot delivery");
					}
				}
    		}
    		logger.info("Going to flash boot delivery");
    		XMLBootConfig bc=found.get(found.size()-1);
			bc.setFolder(_bundle.getBootDelivery().getFolder());
			if (!bc.isComplete()) throw new BootDeliveryException ("Some files are missing from your boot delivery");
			TaFile taf = new TaFile(new File(bc.getTA()));
			openTA(2);
			SinFile sin = new SinFile(bc.getAppsBootFile());
			sin.setChunkSize(maxpacketsize);
			uploadImage(sin);
			closeTA();
			openTA(2);
			sendTA(taf);
			closeTA();
			openTA(2);
			Iterator<String> otherfiles = bc.getOtherFiles().iterator();
			while (otherfiles.hasNext()) {
				SinFile sin1 = new SinFile(otherfiles.next());
				sin1.setChunkSize(maxpacketsize);
				uploadImage(sin1);
			}
			closeTA();
			_bundle.setBootDeliveryFlashed(true);
    	} catch (BootDeliveryException e) {
    		logger.info(e.getMessage());
    	}
    }

    public void sendTAFiles() throws FileNotFoundException, IOException,X10FlashException {
		Enumeration entries = _bundle.getMeta().getEntriesOf("TA",true);
		if (entries.hasMoreElements()) {
			openTA(2);
			while (entries.hasMoreElements()) {
				String entry = (String)entries.nextElement();
				BundleEntry bent = _bundle.getEntry(entry);
				if (!bent.getName().toUpperCase().contains("SIM"))
					sendTA(new File(bent.getAbsolutePath()));
				else {
					logger.warn("This file is ignored : "+bent.getName());
				}
			}
			closeTA();
		}
    }
    
    public void getDevInfo() throws IOException, X10FlashException {
    	openTA(2);
    	cmd.send(Command.CMD12, Command.TA_DEVID1, false);
    	String info = "Current device : "+cmd.getLastReplyString();
    	currentdevice = cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID2, false);
    	info = info + " - "+cmd.getLastReplyString();
    	serial = cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID3, false);
    	info = info + " - "+cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID4, false);
    	info = info + " - "+cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID5, false);
    	info = info + " - "+cmd.getLastReplyString();
    	logger.info(info);
    	closeTA();
    }
    
    public void resetStats() throws IOException, X10FlashException {
		openTA(2);
		sendTAUnit(_8a4unit);
		closeTA();    	
    }
    
    public void flashDevice() {
    	try {
		    logger.info("Start Flashing");
		    sendLoader();
		    maxpacketsize=Integer.parseInt(phoneprops.getProperty("MAX_PKT_SZ"),16);
		    LogProgress.initProgress(_bundle.getMaxProgress(maxpacketsize));
		    if (_bundle.hasCmd25()) {
		    	logger.info("Disabling final data verification check");
		    	cmd.send(Command.CMD25, Command.DISABLEFINALVERIF, false);
		    }
		    setFlashState(true);
		    sendPartition();
		    sendBootDelivery();
		    sendBoot();
			sendImages();
			if (_bundle.isBootDeliveryFlashed()) {
				openTA(2);
				sendTAUnit(_8fdunit);
				closeTA();
			}
        	setFlashState(false);
        	sendTAFiles();
		    if (_bundle.hasResetStats()) {
		    	logger.info("Resetting customizations");
		    	resetStats();
		    }
        	closeDevice(0x01);
			logger.info("Flashing finished.");
			logger.info("Please unplug and start your phone");
			logger.info("For flashtool, Unknown Sources and Debugging must be checked in phone settings");
			LogProgress.initProgress(0);
    	}
    	catch (Exception ioe) {
    		ioe.printStackTrace();
    		closeDevice();
    		logger.error(ioe.getMessage());
    		logger.error("Error flashing. Aborted");
    		LogProgress.initProgress(0);
    	}
    }

    public Bundle getBundle() {
    	return _bundle;
    }
    
    public boolean openDevice() {
    	return openDevice(_bundle.simulate());
    }

    public boolean deviceFound() {
    	boolean found = false;
    	try {
			Thread.sleep(500);
			found = Device.getLastConnected(false).getPid().equals("ADDE");
		}
		catch (Exception e) {
	    	found = false;
		}
    	return found;
    }

    public void endSession() throws X10FlashException,IOException {
    	logger.info("Ending flash session");
    	cmd.send(Command.CMD04,Command.VALNULL,false);
    }

    public void endSession(int param) throws X10FlashException,IOException {
    	logger.info("Ending flash session");
    	cmd.send(Command.CMD04,BytesUtil.getBytesWord(param, 1),false);
    }

    public void closeDevice() {
    	try {
    		endSession();
    	}
    	catch (Exception e) {}
    	USBFlash.close();
    	DeviceChangedListener.pause(false);
    }

    public void closeDevice(int par) {
    	try {
    		endSession(par);
    	}
    	catch (Exception e) {}
    	USBFlash.close();
    	DeviceChangedListener.pause(false);
    }

    public void hookDevice(boolean printProps) throws X10FlashException,IOException {
		cmd.send(Command.CMD01, Command.VALNULL, false);
		cmd01string = cmd.getLastReplyString();
		logger.debug(cmd01string);
		phoneprops.update(cmd01string);
		if (getPhoneProperty("ROOTING_STATUS")==null) phoneprops.setProperty("ROOTING_STATUS", "UNROOTABLE"); 
		if (phoneprops.getProperty("VER").startsWith("r"))
			phoneprops.setProperty("ROOTING_STATUS", "ROOTED");
		if (printProps) {
			logger.debug("After loader command reply (hook) : "+cmd01string);
			logger.info("Loader : "+phoneprops.getProperty("LOADER_ROOT")+" - Version : "+phoneprops.getProperty("VER")+" / Boot version : "+phoneprops.getProperty("BOOTVER")+" / Bootloader status : "+phoneprops.getProperty("ROOTING_STATUS"));
		}
		else
			logger.debug("First command reply (hook) : "+cmd01string);
    }

    public boolean openDevice(boolean simulate) {
    	if (simulate) return true;
    	LogProgress.initProgress(_bundle.getMaxLoaderProgress());
    	boolean found=false;
    	try {
    		USBFlash.open("ADDE");
    		try {
				logger.info("Reading device information");
				USBFlash.readS1Reply();
				firstRead = new String (USBFlash.getLastReply());
				phoneprops = new LoaderInfo(firstRead);
				phoneprops.setProperty("BOOTVER", phoneprops.getProperty("VER"));
				if (phoneprops.getProperty("VER").startsWith("r"))
					modded_loader=true;
				logger.debug(firstRead);
				
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			logger.info("Unable to read from phone after having opened it.");
    			logger.info("trying to continue anyway");
    		}
    	    cmd = new Command(_bundle.simulate());
    	    hookDevice(false);
    	    logger.info("Phone ready for flashmode operations.");
		    getDevInfo();
	    	_8fdunit = new TaEntry();
	    	_8fdunit.setPartition("000008FD");
	    	_8fdunit.addData("01");
	    	_8fdunit.setSize("1");
	    	_8a4unit = new TaEntry();
	    	_8a4unit.setPartition("000008A4");
	    	_8a4unit.addData("00 00 00 00 00 00 00 00 00 00 00 00 00 00");
	    	_8a4unit.setSize("0E");
	    	found = true;
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		found=false;
    	}
    	return found;
    }

    public String getSerial() {
    	return serial;
    }
}