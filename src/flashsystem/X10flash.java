package flashsystem;

import flashsystem.io.USBFlash;
import gui.tools.WidgetTask;
import gui.tools.XMLBootConfig;
import gui.tools.XMLBootDelivery;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bouncycastle.util.io.Streams;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jdom.JDOMException;
import org.logger.LogProgress;
import org.sinfile.parsers.SinFileException;
import org.system.DeviceChangedListener;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.TextFile;
import org.util.BytesUtil;
import org.util.HexDump;
import com.google.common.primitives.Bytes;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
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
    private static Logger logger = Logger.getLogger(X10flash.class);
    private HashMap<Integer,TaEntry> TaPartition2 = new HashMap<Integer,TaEntry>();
    int loaderConfig = 0;
    private String model;
    private XMLBootConfig bc=null;

    public X10flash(Bundle bundle, Shell shell) {
    	_bundle=bundle;
    	_curshell = shell;
    }

    public String getCurrentDevice() {
    	if (!_bundle.simulate())
    		return currentdevice;
    	return _bundle.getDevice();
    }
    
    public void enableFinalVerification() throws X10FlashException,IOException {
    	loaderConfig &= 0xFFFFFFFE;
    	logger.info("Enabling final verification");
    	setLoaderConfiguration();
    }
    
    public void disableFinalVerification() throws X10FlashException,IOException {
    	loaderConfig |= 0x1;
    	logger.info("Disabling final verification");
    	setLoaderConfiguration();
    }
    
    public void enableEraseBeforeWrite() throws X10FlashException,IOException {
    	loaderConfig &= 0xFFFFFFFD;
    	logger.info("Enabling erase before write");
    	setLoaderConfiguration();
    }

    public void disableEraseBeforeWrite() throws X10FlashException,IOException {
    	loaderConfig |= 0x2;
    	logger.info("Disabling erase before write");
    	setLoaderConfiguration();
    }
    
    public void setLoaderConfiguration() throws X10FlashException,IOException {
    	byte[] data = BytesUtil.concatAll(BytesUtil.intToBytes(1, 2, false), BytesUtil.intToBytes(loaderConfig, 4, false));
    	if (!_bundle.simulate()) {
    		cmd.send(Command.CMD25,data,false);
    	}
    }

    public void setLoaderConfiguration(String param) throws X10FlashException,IOException {
    	String[] bytes = param.split(",");
    	byte[] data = new byte[bytes.length];
    	for (int i=0;i<bytes.length;i++) {
    		data[i]=(byte)Integer.parseInt(bytes[i]);
    	}
    	logger.info("Set loader configuration : "+HexDump.toHex(data));
    	if (!_bundle.simulate()) {
    		cmd.send(Command.CMD25,data,false);
    	}
    }
    
    public void setFlashTimestamp() throws IOException,X10FlashException {
	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	  	TaEntry tau = new TaEntry();
	  	tau.setUnit(0x00002725);
	  	tau.setData(BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
	  	sendTAUnit(tau);
    }
    
    public void setFlashState(boolean ongoing) throws IOException,X10FlashException
    {
	    	if (ongoing) {
	    		openTA(2);
	    		TaEntry ent = new TaEntry();
	    		ent.setUnit(0x00002774);
	    		ent.setData(new byte[] {0x01});
	    		sendTAUnit(ent);
	    		closeTA();
	    	}
	    	else {
	    		openTA(2);
	    	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    	  	TaEntry tau = new TaEntry();
	    	  	tau.setUnit(0x00002725);
	    	  	tau.setData(BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
			  	sendTAUnit(tau);
    			TaEntry ent = new TaEntry();
	    		ent.setUnit(0x00002774);
	    		ent.setData(new byte[] {0x00});
    			sendTAUnit(ent);
	    		closeTA();
	    	}
    }

    public void setFlashStat(byte state)  throws IOException,X10FlashException {
		TaEntry ent = new TaEntry();
		ent.setUnit(0x00002774);
		ent.setData(new byte[] {state});
		sendTAUnit(ent);
    }
    
    private void sendTA(File f) throws FileNotFoundException, IOException,X10FlashException {
    	try {
    		TaFile ta = new TaFile(f);
    		logger.info("Flashing "+ta.getName());
			Vector<TaEntry> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				TaEntry tent = entries.get(i);
				sendTAUnit(tent);
			}
    	}
    	catch (TaParseException tae) {
    		logger.error("Error parsing TA file. Skipping");
    	}
    }

    private void sendTA(TaFile ta) throws FileNotFoundException, IOException,X10FlashException {
    		logger.info("Flashing "+ta.getName()+" to partition "+ta.getPartition());
			Vector<TaEntry> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				sendTAUnit(entries.get(i));
			}
    }

    private void sendTA2(TaFile ta) throws FileNotFoundException, IOException,X10FlashException {
    		logger.info("Flashing "+ta.getName()+" to partition "+ta.getPartition());
			Vector<TaEntry> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				TaEntry tent = entries.get(i);
				sendTAUnit(tent);
			}
    }

    public void sendTAUnit(TaEntry ta) throws X10FlashException, IOException {
		logger.info("Writing TA unit "+ta.getUnit()+". Value : "+ta.getDataHex());
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
        		ta.setUnit(HexDump.toHex(unit));
	        	String reply = cmd.getLastReplyHex();
	        	reply = reply.replace("[", "");
	        	reply = reply.replace("]", "");
	        	reply = reply.replace(",", "");
        		ta.addData(reply.trim());
        		return ta;
    		}
			return null;
    }

    public void BackupTA() throws IOException, X10FlashException {
    	String timeStamp = OS.getTimeStamp();
    	BackupTA(1, timeStamp);
    	BackupTA(2, timeStamp);
    }

    public void BackupTA(int partition, String timeStamp) throws IOException, X10FlashException {
    	openTA(partition);
    	String folder = OS.getFolderRegisteredDevices()+File.separator+getPhoneProperty("MSN")+File.separator+"s1ta"+File.separator+timeStamp;
    	new File(folder).mkdirs();
    	TextFile tazone = new TextFile(folder+File.separator+partition+".ta","ISO8859-1");
        tazone.open(false);
    	try {
		    tazone.writeln(String.format("%02d", partition));
		    try {
		    	logger.info("Start Dumping TA partition "+partition);
		    	cmd.send(Command.CMD18, Command.VALNULL, false);
		    	logger.info("Finished Dumping TA partition "+partition);
		    	ByteArrayInputStream inputStream = new ByteArrayInputStream(cmd.getLastReply());
		    	TreeMap<Integer, byte[]> treeMap = new  TreeMap<Integer, byte[]>();
		    	int i = 0;
		    	while(i == 0) {
		    		int j = inputStream.read();
		    		if (j == -1) {
		    			i = 1;
		    		}
		    		else {
		    			byte[] buff = new byte[3];
		    			if(Streams.readFully(inputStream, buff)!=3){
		    				throw new X10FlashException("Not enough data to read Uint32 when decoding command");
		    			}
		    			
		    			byte[] unitbuff = Bytes.concat(new byte[] { (byte)j }, buff);
		    			long unit = ByteBuffer.wrap(unitbuff).getInt() & 0xFFFFFFFF;
		    			long unitdatalen = decodeUint32(inputStream);
		    			if (unitdatalen > 1000000L) {
		    				throw new X10FlashException("Maximum unit size exceeded, application will handle units of a maximum size of 0x"
		    			              + Long.toHexString(1000000L) + ". Got a unit of size 0x" + Long.toHexString(unitdatalen) + ".");
		    			}
		    			byte[] databuff = new byte[(int)unitdatalen];
		    			if (Streams.readFully(inputStream, databuff) != unitdatalen) {
		    				throw new X10FlashException("Not enough data to read unit data decoding command");
		    			}
			        	treeMap.put((int)unit, databuff);
		    		}
		    	}
		    	for (Map.Entry<Integer, byte[]> entry : treeMap.entrySet())
		    	{
		    	    int unit = entry.getKey();
		    	    byte[] unitdate = entry.getValue();
	    			String dataStr = HexDump.toHex(unitdate);
	    			dataStr = dataStr.replace("[", "");
		        	dataStr = dataStr.replace("]", "");
		        	dataStr = dataStr.replace(",", "");
		        	tazone.writeln(HexDump.toHex((int)unit) + " " + HexDump.toHex((short)unitdate.length) + " " + dataStr);
		    	}
		    	
		    }catch (X10FlashException e) {
		    	throw e;
		    }
	        tazone.close();
	        logger.info("TA partition "+partition+" saved to "+folder+File.separator+partition+".ta");
	        closeTA();
	    }
    	catch (Exception ioe) {
	        tazone.close();
	        closeTA();
    		logger.error(ioe.getMessage());
    		logger.error("Error dumping TA. Aborted");
    	}
    }
    
    private long decodeUint32(InputStream inputStream) throws IOException, X10FlashException {
    	byte[] buff = new byte[4];
    	if (Streams.readFully(inputStream, buff) != 4)
    	{
    		throw new X10FlashException("Not enough data to read Uint32 when decoding command");
    	}
    	long longval = ByteBuffer.wrap(buff).getInt();
    	return  longval & 0xFFFFFFFF;
    }
    
    public void RestoreTA(String tafile) throws FileNotFoundException, IOException, X10FlashException {
    	File f = new File(tafile);
    	boolean taopened = false;
    	try {
    		TaFile ta = new TaFile(f);
        	openTA(ta.getPartition());
        	taopened = true;
        	sendTA(ta);
    	}catch (TaParseException tae) {
    		logger.error("Error parsing TA file. Skipping");
    	}
    	sendTA(new File(tafile));
    	if (taopened) {
    		closeTA();
    	}
		LogProgress.initProgress(0);	    
    }
    
    private void processHeader(SinFile sin) throws X10FlashException {
    	try {
    		logger.info("    Checking header");
    		SinFileHeader header = sin.getSinHeader();
			for (int j=0;j<header.getNbChunks();j++) {
				int cur = j+1;
				logger.debug("Sending part "+cur+" of "+header.getNbChunks());
				if (!_bundle.simulate()) {
				cmd.send(Command.CMD05, header.getChunckBytes(j), !((j+1)==header.getNbChunks()));
				if (USBFlash.getLastFlags() == 0)
					getLastError();
				}
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
				if (!_bundle.simulate()) {
				cmd.send(Command.CMD06, sin.getChunckBytes(j), !((j+1)==sin.getNbChunks()));
				if (USBFlash.getLastFlags() == 0)
					getLastError();
				}
			}
			//logger.info("Processing of "+sin.getShortFileName()+" finished.");
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
    		if (current.getRecognition().contains(getCurrentDevice())) {
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
    	return loader;
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
		if (!_bundle.simulate()) {
			USBFlash.readS1Reply();
			hookDevice(true);
		}
	    if (!_bundle.simulate()) {
	    	maxpacketsize=Integer.parseInt(phoneprops.getProperty("MAX_PKT_SZ"),16);
	    }
	    else
	    	maxpacketsize=128000;
	    LogProgress.initProgress(_bundle.getMaxProgress(maxpacketsize));
    }
    
/*    public void sendImages(int group) throws FileNotFoundException, IOException,X10FlashException {
    	Iterator<Integer> orderlist = _bundle.getMeta().getOrder();
    	if (orderlist.hasNext()) {
    		openTA(2);
	    	while (orderlist.hasNext()) {
	    		int place = orderlist.next();
	    		if (place>=(group*100) && place<(group*100+99)) {
	    			String categ = _bundle.getMeta().getCagorie(place);
	    			if (_bundle.getMeta().isCategEnabled(categ)) {
	    				Enumeration entries = _bundle.getMeta().getEntriesOf(categ,true);
	    				while (entries.hasMoreElements()) {
	    					String entry = (String)entries.nextElement();
	    					BundleEntry bent = _bundle.getEntry(_bundle.getMeta().getExternal(entry));
	    					SinFile sin = new SinFile(bent.getAbsolutePath());
	    					sin.setChunkSize(maxpacketsize);
	    					uploadImage(sin);
	    				}
	    			}    			
	    		}
	    	}
	    	closeTA();
    	}
    }*/

    public String getPhoneProperty(String property) {
    	return phoneprops.getProperty(property);
    }

    public void openTA(int partition) throws X10FlashException, IOException{
    	if (!taopen) {
    		logger.info("Opening TA partition "+partition);
    		if (!_bundle.simulate())
    			cmd.send(Command.CMD09, BytesUtil.getBytesWord(partition, 1), false);
    	}
    	taopen = true;
    }
    
    public void closeTA() throws X10FlashException, IOException{
    	if (taopen) {
    		logger.info("Closing TA partition");
    		if (!_bundle.simulate())
    			cmd.send(Command.CMD10, Command.VALNULL, false);
    	}
    	taopen = false;
    }

    public XMLBootConfig getBootConfig() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TaParseException, BootDeliveryException  {
		if (!_bundle.hasBootDelivery()) return null;
		logger.info("Parsing boot delivery");
		XMLBootDelivery xml = _bundle.getXMLBootDelivery();
		Vector<XMLBootConfig> found = new Vector<XMLBootConfig>();
		if (!_bundle.simulate()) {    			
    		Enumeration<XMLBootConfig> e = xml.getBootConfigs();
    		while (e.hasMoreElements()) {
    			// We get matching bootconfig from all configs
    			XMLBootConfig bc=e.nextElement();
    			if (bc.matches(phoneprops.getProperty("OTP_LOCK_STATUS_1"), phoneprops.getProperty("OTP_DATA_1"), phoneprops.getProperty("IDCODE_1")))
    				found.add(bc);
    		}
		}
		else {
			Enumeration<XMLBootConfig> e = xml.getBootConfigs();
    		while (e.hasMoreElements()) {
    			// We get matching bootconfig from all configs
    			XMLBootConfig bc=e.nextElement();
    			if (bc.getName().startsWith("COMMERCIAL")) {
    				found.add(bc);
    				break;
    			}
    		}
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
		found.get(found.size()-1).setFolder(_bundle.getBootDelivery().getFolder());
		return found.get(found.size()-1);
    }
    
    public void sendBootDelivery() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TaParseException {
    	try {
    		if (_bundle.hasBootDelivery()) {
    			XMLBootDelivery xmlboot = _bundle.getXMLBootDelivery();
    			if (!_bundle.simulate())
    				if (!xmlboot.mustUpdate(phoneprops.getProperty("BOOTVER"))) throw new BootDeliveryException("Boot delivery up to date. Nothing to do");
	    		logger.info("Going to flash boot delivery");
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
    		}
    	} catch (BootDeliveryException e) {
    		logger.info(e.getMessage());
    	}
    }


    public void loadTAFiles() throws FileNotFoundException, IOException,X10FlashException {
		Iterator<Category> entries = _bundle.getMeta().getTAEntries(true).iterator();
			while (entries.hasNext()) {
				Category categ = entries.next();
				Iterator<BundleEntry> icateg = categ.getEntries().iterator();
				while (icateg.hasNext()) {
					BundleEntry bent = icateg.next();
					if (bent.getName().toUpperCase().endsWith(".TA")) {
						if (!bent.getName().toUpperCase().contains("SIM"))
						try {
							TaFile ta = new TaFile(new File(bent.getAbsolutePath()));
							Iterator<TaEntry> i = ta.entries().iterator();
							while (i.hasNext()) {
								TaEntry ent = i.next();
								TaPartition2.put(BytesUtil.getInt(ent.getUnitbytes()),ent);
							}
						}
						catch (TaParseException tae) {
				    		logger.error("Error parsing TA file. Skipping");
				    	}
						else {
							logger.warn("File "+bent.getName()+" is ignored");
						}
					}
				}
			}
		try {
			if (bc!=null) {
				TaFile taf = new TaFile(new File(bc.getTA()));
				Iterator<TaEntry> i = taf.entries().iterator();
				while (i.hasNext()) {
					TaEntry ent = i.next();
					TaPartition2.put(BytesUtil.getInt(ent.getUnitbytes()),ent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void getDevInfo() throws IOException, X10FlashException {
    	openTA(2);
    	cmd.send(Command.CMD12, Command.TA_MODEL, false);
    	currentdevice = cmd.getLastReplyString();
    	String info = "Current device : "+getCurrentDevice();
    	cmd.send(Command.CMD12, Command.TA_SERIAL, false);
    	serial = cmd.getLastReplyString();
    	info = info + " - "+serial;
    	cmd.send(Command.CMD12, Command.TA_DEVID3, false);
    	info = info + " - "+cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID4, false);
    	info = info + " - "+cmd.getLastReplyString();
    	cmd.send(Command.CMD12, Command.TA_DEVID5, false);
    	info = info + " - "+cmd.getLastReplyString();
    	logger.info(info);
    	closeTA();
    }
    
    
    public boolean hasScript() {
    	try {
    		Vector<String> ignored = new Vector<String>();
    		TextFile tf = new TextFile(getFlashScript(),"ISO8859-1");
    		Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    		while (e.hasNext()) {
    			Category cat = e.next();
    			Iterator<BundleEntry> icat = cat.getEntries().iterator();
    			while (icat.hasNext()) {
    				BundleEntry ent = icat.next();
    				if (ent.getName().equals("loader.sin")) continue;
    				if (ent.getName().endsWith(".sin")) {
    	        		Map<Integer,String> map =  tf.getMap();
    	        		Iterator<Integer> keys = map.keySet().iterator();
    	        		boolean intemplate = false;
    	        		while (keys.hasNext()) {
    	        			String line = map.get(keys.next());
    	        			String param="";
    	        			String[] parsed = line.split(":");
    	        			String action = parsed[0];
    	        			if (action.equals("uploadImage")) {
    	        				param = parsed[1];
    	            			if (ent.getName().contains(param)) {
    	            				intemplate=true;
    	            				break; 
    	            			}
    	        			}
    	        		}
    	        		if (!intemplate) ignored.add(ent.getName());
    				}
    				if (ent.getName().endsWith(".ta")) {
    					TaFile taf = new TaFile(new File(ent.getAbsolutePath()));
    					Iterator<TaEntry> itaent = taf.entries().iterator();
    					while (itaent.hasNext()) {
    						TaEntry taent = itaent.next();
        	        		Map<Integer,String> map =  tf.getMap();
        	        		Iterator<Integer> keys = map.keySet().iterator();
        	        		boolean intemplate = false;
        	        		while (keys.hasNext()) {
        	        			String line = map.get(keys.next());
        	        			String param="";
        	        			String[] parsed = line.split(":");
        	        			String action = parsed[0];
        	        			if (action.equals("writeTA")) {
        	        				param = parsed[1];
        	            			if (Integer.parseInt(taent.getUnit(),16) == Integer.parseInt(param)) {
        	            				intemplate=true;
        	            				break; 
        	            			}
        	        			}
        	        		}
        	        		if (!intemplate) ignored.add("TA unit "+taent.getUnit());
    					}
    				}
    			}
    		}
    		if (ignored.size()>0) {
    			Enumeration eignored = ignored.elements();
    			String dynmsg = "";
    			while (eignored.hasMoreElements()) {
    				dynmsg=dynmsg+eignored.nextElement();
    				if (eignored.hasMoreElements()) dynmsg = dynmsg + ",";
    			}
    			String result = WidgetTask.openYESNOBox(_curshell, "Those data will not be flashed : "+dynmsg+". Do you want to continue ?");
    			if (Integer.parseInt(result) == SWT.YES) {
    				return true;
    			}
    			return false;
    		}
    		return true;

    	} catch (Exception e) {
    		return false;
    	}
    }

    public String getFlashScript() {
      	String devid = Devices.getIdFromVariant(getCurrentDevice());
    	DeviceEntry dev = Devices.getDevice(devid);
    	return dev.getFlashScript(getCurrentDevice(),_bundle.getVersion());
    }
  
    public void runScript() {
    	try {
    		TextFile tf = new TextFile(getFlashScript(),"ISO8859-1");
    		logger.info("Found a template session. Using it : "+tf.getFileName());
    		Map<Integer,String> map =  tf.getMap();
    		Iterator<Integer> keys = map.keySet().iterator();
    		while (keys.hasNext()) {
    			String param="";
    			String line = map.get(keys.next());
    			String[] parsed = line.split(":");
    			String action = parsed[0];
    			if (parsed.length>1)
    				param = parsed[1];
    			if (action.equals("openTA")) {
    				this.openTA(Integer.parseInt(param));
    			}
    			else if (action.equals("closeTA")) {
    				this.closeTA();
    			}
    			else if (action.equals("setFlashState")) {
    				this.setFlashStat((byte)Integer.parseInt(param));
    			}
    			else if (action.equals("setLoaderConfig")) {
    				this.setLoaderConfiguration(param);
    			}
    			else if (action.equals("uploadImage")) {
    				BundleEntry b = _bundle.searchEntry(param);
    				if (b!=null) {
    					SinFile sin =new SinFile(b.getAbsolutePath());
    					sin.setChunkSize(maxpacketsize);
    					this.uploadImage(sin);
    				}
    				else {
    					if (bc!=null) {
    						String file = bc.getMatchingFile(param);
    						if (file!=null) {
    	    					SinFile sin =new SinFile(file);
    	    					sin.setChunkSize(maxpacketsize);
    	    					this.uploadImage(sin);						
    						}
        					else {
        						logger.warn(param + " is excluded from bundle");
        					}
    					}
    					else {
    						logger.warn(param + " is excluded from bundle");
    					}
    				}
    			}
    			else if (action.equals("writeTA")) {
    				TaEntry unit = TaPartition2.get(Integer.parseInt(param));
    				if (unit != null)
    					this.sendTAUnit(unit);
    				else logger.warn("Unit "+param+" not found in bundle");
    			}
    			else if (action.equals("setFlashTimestamp")) {
    				this.setFlashTimestamp();
    			}
    			else if (action.equals("End flashing")) {
    				this.endSession();
    			}
    		}
    	} catch (Exception e) {e.printStackTrace();}
    }

    public void flashDevice() {
    	try {
		    logger.info("Start Flashing");
		    sendLoader();
		    if (!_bundle.simulate())
		    	USBFlash.setUSBBuffer(maxpacketsize/2);
		    bc = getBootConfig();
		    loadTAFiles();
		    if (hasScript()) {
		    	runScript();
		    }
		    else {
		    	logger.info("No flash script found. Using 0.9.18 flash engine");
		    	oldFlashEngine();
		    }
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

    public void sendPartition() throws FileNotFoundException, IOException, X10FlashException {		
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isPartition()) {
    			BundleEntry entry = c.getEntries().iterator().next();
    			SinFile sin = new SinFile(entry.getAbsolutePath());
    			sin.setChunkSize(maxpacketsize);
    			uploadImage(sin);
    		}
    	}
    }

    public void sendBoot() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isSoftware()) {
				BundleEntry entry = c.getEntries().iterator().next();
				if (isBoot(entry.getAbsolutePath())) {
					SinFile sin = new SinFile(entry.getAbsolutePath());
					sin.setChunkSize(maxpacketsize);
					uploadImage(sin);
				}
    		}
    	}
    	closeTA();
    }

    public void sendSecro() throws X10FlashException, IOException {
    	BundleEntry preload = null;
    	BundleEntry secro = null;
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isPreload()) preload = c.getEntries().iterator().next();
    		if (c.isSecro()) secro = c.getEntries().iterator().next();
    	}
    	if (preload!=null && secro!=null) {
    		setLoaderConfiguration("00,01,00,00,00,01");
    		setLoaderConfiguration("00,01,00,00,00,03");
    		SinFile sinpreload = new SinFile(preload.getAbsolutePath());
    		sinpreload.setChunkSize(maxpacketsize);
    		uploadImage(sinpreload);
    		setLoaderConfiguration("00,01,00,00,00,01");
    		SinFile sinsecro = new SinFile(secro.getAbsolutePath());
    		sinsecro.setChunkSize(maxpacketsize);
    		uploadImage(sinsecro);    		
    		setLoaderConfiguration("00,01,00,00,00,00");
    	}
    }
    
    public boolean isBoot(String sinfile) throws SinFileException {
		org.sinfile.parsers.SinFile sin = new org.sinfile.parsers.SinFile(new File(sinfile));
		if (sin.getName().toUpperCase().contains("BOOT")) return true;
		return sin.getType()=="BOOT";
    }
    
    public void sendSoftware() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isSoftware()) {
				BundleEntry entry = c.getEntries().iterator().next();
				if (isBoot(entry.getAbsolutePath())) continue;
				SinFile sin = new SinFile(entry.getAbsolutePath());
				sin.setChunkSize(maxpacketsize);
				uploadImage(sin);
    		}
    	}
    	closeTA();
    }

    public void sendElabel() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isElabel()) {
				BundleEntry entry = c.getEntries().iterator().next();
				if (isBoot(entry.getAbsolutePath())) continue;
				SinFile sin = new SinFile(entry.getAbsolutePath());
				sin.setChunkSize(maxpacketsize);
				uploadImage(sin);
    		}
    	}
    	closeTA();
    }

    public void sendSystem() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isSystem()) {
				BundleEntry entry = c.getEntries().iterator().next();
				if (isBoot(entry.getAbsolutePath())) continue;
				SinFile sin = new SinFile(entry.getAbsolutePath());
				sin.setChunkSize(maxpacketsize);
				uploadImage(sin);
    		}
    	}
    	closeTA();
    }

    public void sendTAFiles()  throws FileNotFoundException, IOException, X10FlashException, TaParseException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isTa()) {
    			BundleEntry entry = c.getEntries().iterator().next();
    			TaFile taf = new TaFile(new File(entry.getAbsolutePath()));
    			sendTA(taf);
    		}
    	}
    	closeTA();
    }
 
    public void oldFlashEngine() {
    	try {
		    if (_bundle.hasCmd25()) {
		    	logger.info("Disabling final data verification check");
		    	this.disableFinalVerification();
		    }
		    setFlashState(true);
		    sendPartition();
		    sendSecro();
		    sendBootDelivery();
		    sendBoot();
			sendSoftware();
			sendSystem();
			sendTAFiles();
			sendElabel();
        	setFlashState(false);
        	closeDevice(0x01);
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
			found = Devices.getLastConnected(false).getPid().equals("ADDE");
		}
		catch (Exception e) {
	    	found = false;
		}
    	return found;
    }

    public void endSession() throws X10FlashException,IOException {
    	logger.info("Ending flash session");
    	if (!_bundle.simulate())
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