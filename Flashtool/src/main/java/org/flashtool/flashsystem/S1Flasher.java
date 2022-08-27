package org.flashtool.flashsystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.flashsystem.io.USBFlash;
import org.flashtool.gui.tools.WidgetTask;
import org.flashtool.gui.tools.XMLBootConfig;
import org.flashtool.gui.tools.XMLBootDelivery;
import org.flashtool.log.LogProgress;
import org.flashtool.parsers.sin.SinFile;
import org.flashtool.parsers.sin.SinFileException;
import org.flashtool.parsers.ta.TAFileParseException;
import org.flashtool.parsers.ta.TAFileParser;
import org.flashtool.parsers.ta.TAUnit;
import org.flashtool.system.DeviceEntry;
import org.flashtool.system.Devices;
import org.flashtool.system.OS;
import org.flashtool.system.TextFile;
import org.flashtool.util.BytesUtil;
import org.flashtool.util.HexDump;
import org.jdom2.JDOMException;

import com.google.common.primitives.Bytes;

import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

@Slf4j
public class S1Flasher implements Flasher {

    private Bundle _bundle;
    private S1Command cmd;
    private LoaderInfo phoneprops = null;
    private String firstRead = "";
    private String cmd01string = "";
    private boolean taopen = false;
    private boolean modded_loader=false;
    private String currentdevice = "";
    private int maxS1packetsize = 0;
    private String serial = "";
    private Shell _curshell;
    private HashMap<Long,TAUnit> TaPartition2 = new HashMap<Long,TAUnit>();
    int loaderConfig = 0;
    private XMLBootConfig bc=null;

    public S1Flasher(Bundle bundle, Shell shell) {
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
    	log.info("Enabling final verification");
    	setLoaderConfiguration();
    }
    
    public void disableFinalVerification() throws X10FlashException,IOException {
    	loaderConfig |= 0x1;
    	log.info("Disabling final verification");
    	setLoaderConfiguration();
    }
    
    public void enableEraseBeforeWrite() throws X10FlashException,IOException {
    	loaderConfig &= 0xFFFFFFFD;
    	log.info("Enabling erase before write");
    	setLoaderConfiguration();
    }

    public void disableEraseBeforeWrite() throws X10FlashException,IOException {
    	loaderConfig |= 0x2;
    	log.info("Disabling erase before write");
    	setLoaderConfiguration();
    }
    
    public void setLoaderConfiguration() throws X10FlashException,IOException {
    	byte[] data = BytesUtil.concatAll(BytesUtil.intToBytes(1, 2, false), BytesUtil.intToBytes(loaderConfig, 4, false));
    	if (!_bundle.simulate()) {
    		cmd.send(S1Command.CMD25,data,false);
    	}
    }

    public void setLoaderConfiguration(String param) throws X10FlashException,IOException {
    	String[] bytes = param.split(",");
    	if (bytes.length==1) bytes = param.split(" ");
    	byte[] data = new byte[bytes.length];
    	for (int i=0;i<bytes.length;i++) {
    		data[i]=(byte)Integer.parseInt(bytes[i],16);
    	}
    	log.info("Set loader configuration : ["+HexDump.toHex(data)+"]");
    	if (!_bundle.simulate()) {
    		cmd.send(S1Command.CMD25,data,false);
    	}
    }
    
    public void setFlashTimestamp() throws IOException,X10FlashException {
	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	  	TAUnit tau = new TAUnit(0x00002725, BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
	  	sendTAUnit(tau);
    }
    
    public void setFlashState(boolean ongoing) throws IOException,X10FlashException
    {
	    	if (ongoing) {
	    		openTA(2);
	    		TAUnit ent = new TAUnit(0x00002774, new byte[] {0x01});
	    		sendTAUnit(ent);
	    		closeTA();
	    	}
	    	else {
	    		openTA(2);
	    	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    	  	TAUnit tau = new TAUnit(0x00002725, BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
			  	sendTAUnit(tau);
    			TAUnit ent = new TAUnit(0x00002774, new byte[] {0x00});
    			sendTAUnit(ent);
	    		closeTA();
	    	}
    }

    public void setFlashStat(byte state)  throws IOException,X10FlashException {
		TAUnit ent = new TAUnit(0x00002774, new byte[] {state});
		sendTAUnit(ent);
    }

    private void sendTA(TAFileParser ta) throws FileNotFoundException, IOException,X10FlashException {
    		log.info("Flashing "+ta.getName()+" to partition "+ta.getPartition());
			Vector<TAUnit> entries = ta.entries();
			for (int i=0;i<entries.size();i++) {
				sendTAUnit(entries.get(i));
			}
    }

    public void sendTAUnit(TAUnit ta) throws X10FlashException, IOException {
    	if (ta.getUnitHex().equals("000007DA")) {
    		String result = WidgetTask.openYESNOBox(_curshell, "This unit ("+ta.getUnitHex() + ") is very sensitive and can brick the device. Do you really want to flash it ?");
    		if (Integer.parseInt(result)==SWT.NO) {
    			log.warn("HWConfig unit skipped : "+ta.getUnitHex());
    			return;
    		}
    	}
		log.info("Writing TA unit "+ta.getUnitHex()+". Value : "+HexDump.toHex(ta.getUnitData()));
		if (!_bundle.simulate()) {
			cmd.send(S1Command.CMD13, ta.getFlashBytes(),false);
		}
    }

    public TAUnit readTA(int unit) throws IOException, X10FlashException
    {
    	String sunit = HexDump.toHex(BytesUtil.getBytesWord(unit, 4));
    	log.info("Start Reading unit "+sunit);
	    log.debug((new StringBuilder("%%% read TA property id=")).append(unit).toString());
	    cmd.send(S1Command.CMD12, BytesUtil.getBytesWord(unit, 4),false);
	    log.info("Reading TA finished.");
	    if (cmd.getLastReply().getDataLength()>0) {
	    	TAUnit ta = new TAUnit(unit, cmd.getLastReply().getDataArray());
        	return ta;
    	}
		return null;
    }

    public void backupTA() {
    	log.info("Making a TA backup");
    	String timeStamp = OS.getTimeStamp();
    	try {
    		BackupTA(1, timeStamp);
    	} catch (Exception e) {
    	}
    	try {
    		BackupTA(2, timeStamp);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public void BackupTA(int partition, String timeStamp) throws IOException, X10FlashException {
    	openTA(partition);
    	String folder = OS.getFolderRegisteredDevices()+File.separator+getPhoneProperty("MSN")+File.separator+"s1ta"+File.separator+timeStamp;
    	new File(folder).mkdirs();
    	TextFile tazone = new TextFile(folder+File.separator+partition+".ta","ISO8859-1");
    	tazone.open(false);
    	try {
		    tazone.writeln(HexDump.toHex((byte)partition));
		    try {
		    	log.info("Start Dumping TA partition "+partition);
		    	cmd.send(S1Command.CMD18, S1Command.VALNULL, false);
		    	if (cmd.getLastReply().getDataLength()>0) {
		    	log.info("Finished Dumping TA partition "+partition);
		    	ByteArrayInputStream inputStream = new ByteArrayInputStream(cmd.getLastReply().getDataArray());
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
		    		TAUnit tau = new TAUnit(entry.getKey(), entry.getValue());
		    		if (tau.getUnitNumber()>0)
		    			tazone.write(tau.toString());
		    	    if (treeMap.lastEntry().getKey()!=entry.getKey()) tazone.write("\n");
		    	}
		        tazone.close();
		        log.info("TA partition "+partition+" saved to "+folder+File.separator+partition+".ta");
		    	} else {
			    	log.warn("This partition is not readable");
			    }
		        closeTA();
		    } catch (X10FlashException e) {
		    	closeTA();
		    	throw e;
		    }
	    }
    	catch (Exception ioe) {
	        tazone.close();
	        closeTA();
    		log.error(ioe.getMessage());
    		log.error("Error dumping TA. Aborted");
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
    
    
    private void processHeader(SinFile sin) throws X10FlashException {
    	try {
    		log.info("    Checking header");
				if (!_bundle.simulate()) {
					cmd.send(S1Command.CMD05, sin.getHeader(), false);
				}
	    }
    	catch (IOException ioe) {
    		throw new X10FlashException("Error in processHeader : "+ioe.getMessage());
    	}
    }
     
    private void uploadImage(SinFile sin) throws X10FlashException {
    	try {
    		log.info("Processing "+sin.getName());
	    	processHeader(sin);
	    	log.info("    Flashing data");
	    	log.debug("Number of parts to send : "+sin.getNbChunks()+" / Part size : "+sin.getChunkSize());
	    	sin.openForSending();
	    	int nbparts=1;
	    	while (sin.hasData()) {
				log.debug("Sending part "+nbparts+" of "+sin.getNbChunks());
				byte[] part = sin.getNextChunk();
				if (!_bundle.simulate()) {
					cmd.send(S1Command.CMD06, part, sin.hasData());
				}
				nbparts++;
			}
	    	sin.closeFromSending();
			//log.info("Processing of "+sin.getShortFileName()+" finished.");
    	}
    	catch (Exception e) {
    		log.error("Processing of "+sin.getName()+" finished with errors.");
    		sin.closeFromSending();
    		e.printStackTrace();
    		throw new X10FlashException (e.getMessage());
    	}
    }

    private String getDefaultLoader() {
    	DeviceEntry ent = Devices.getDeviceFromVariant(getCurrentDevice());
    	String loader = "";
    	if (ent!=null) {
    		if (modded_loader)
    			loader=ent.getLoaderUnlocked();
    		else
    			loader=ent.getLoader();
    	}
    	if (modded_loader)
			log.info("Using an unofficial loader");
		if (loader.length()==0) {
			String device = WidgetTask.openDeviceSelector(_curshell);
			if (device.length()>0) {
				ent = new DeviceEntry(device);
				loader = ent.getLoader();				
			}
		}
    	return loader;
    }

    public void sendLoader() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
	    if (!_bundle.hasLoader() || modded_loader) {
	    	if (modded_loader)
	    		log.info("Searching for a modded loader");
	    	else
	    		log.info("No loader in the bundle. Searching for one");
	    	String loader = getDefaultLoader();
	    	if (new File(loader).exists()) {
	    		_bundle.setLoader(new File(loader));
	    	}
	    	else log.info("No matching loader found");
	    }
    	if (_bundle.hasLoader()) {
			SinFile sin = new SinFile(new File(_bundle.getLoader().getAbsolutePath()));
			if (sin.getVersion()>=2)
				sin.setChunkSize(0x10000);
			else
				sin.setChunkSize(0x1000);
			uploadImage(sin);
			if (!_bundle.simulate()) {
				USBFlash.readS1Reply();		
			}
		}
    	else log.warn("No loader found or set manually. Skipping loader");
	    if (!_bundle.simulate()) {
	    	hookDevice(true);
	    	maxS1packetsize=Integer.parseInt(phoneprops.getProperty("MAX_PKT_SZ"),16);
	    }
	    else
	    	maxS1packetsize=0x080000;
    	if ((maxS1packetsize/1024)<1024)
    		log.info("Max packet size set to "+maxS1packetsize/1024+"K");
    	else
    		log.info("Max packet size set to "+maxS1packetsize/1024/1024+"M");
    	if (_bundle.getMaxBuffer()==0) {
    			USBFlash.setUSBBufferSize(maxS1packetsize);
		    	if ((maxS1packetsize/1024)<1024)
		    		log.info("USB buffer size set to "+maxS1packetsize/1024+"K");
		    	else
		    		log.info("USB buffer size set to "+maxS1packetsize/1024/1024+"M");
    	}
    	if (_bundle.getMaxBuffer()==1) {
    		USBFlash.setUSBBufferSize(2048*1024);
    		log.info("USB buffer size set to 2048K");
    	}
    	if (_bundle.getMaxBuffer()==2) {
    		USBFlash.setUSBBufferSize(1024*1024);
	    	log.info("USB buffer size set to 1024K");
    	}
    	if (_bundle.getMaxBuffer()==3) {
    		USBFlash.setUSBBufferSize(512*1024);
	    	log.info("USB buffer size set to 512K");
    	}
    	if (_bundle.getMaxBuffer()==4) {
    		USBFlash.setUSBBufferSize(256*1024);
    		log.info("USB buffer size set to 256K");
    	}
    	if (_bundle.getMaxBuffer()==5) {
    		USBFlash.setUSBBufferSize(128*1024);
    		log.info("USB buffer size set to 128K");
    	}
    	if (_bundle.getMaxBuffer()==6) {
    		USBFlash.setUSBBufferSize(64*1024);
    		log.info("USB buffer size set to 64K");
    	}
    	if (_bundle.getMaxBuffer()==7) {
    		USBFlash.setUSBBufferSize(32*1024);
    		log.info("USB buffer size set to 32K");
    	}
	    LogProgress.initProgress(_bundle.getMaxProgress(maxS1packetsize));
    }

    public String getPhoneProperty(String property) {
    	return phoneprops.getProperty(property);
    }

    public void openTA(int partition) throws X10FlashException, IOException{
    	if (!taopen) {
    		log.info("Opening TA partition "+partition);
    		if (!_bundle.simulate())
    			cmd.send(S1Command.CMD09, BytesUtil.getBytesWord(partition, 1), false);
    	}
    	taopen = true;
    }
    
    public void closeTA() throws X10FlashException, IOException{
    	if (taopen) {
    		log.info("Closing TA partition");
    		if (!_bundle.simulate())
    			cmd.send(S1Command.CMD10, S1Command.VALNULL, false);
    	}
    	taopen = false;
    }

    public XMLBootConfig getBootConfig() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TAFileParseException, BootDeliveryException  {
		if (!_bundle.hasBootDelivery()) return null;
		log.info("Parsing boot delivery");
		XMLBootDelivery xml = _bundle.getXMLBootDelivery();
		Vector<XMLBootConfig> found = new Vector<XMLBootConfig>();
		if (!_bundle.simulate()) {    			
    		Enumeration<XMLBootConfig> e = xml.getBootConfigs();
    		while (e.hasMoreElements()) {
    			// We get matching bootconfig from all configs
    			XMLBootConfig bc=e.nextElement();
    			if (bc.matches(phoneprops.getProperty("OTP_LOCK_STATUS_1"), phoneprops.getProperty("OTP_DATA_1"), phoneprops.getProperty("IDCODE_1"), phoneprops.getProperty("PLF_ROOT_1")))
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
    
    public void sendBootDelivery() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TAFileParseException, SinFileException {
    	try {
    		if (bc!=null) {
    			XMLBootDelivery xmlboot = _bundle.getXMLBootDelivery();
    			if (!_bundle.simulate())
    				if (!xmlboot.mustUpdate(phoneprops.getProperty("BOOTVER"))) throw new BootDeliveryException("Boot delivery up to date. Nothing to do");
	    		log.info("Going to flash boot delivery");
				if (!bc.isComplete()) throw new BootDeliveryException ("Some files are missing from your boot delivery");
				TAFileParser taf = new TAFileParser(new File(bc.getTA()));
				if (bc.hasAppsBootFile()) {
					openTA(2);
					SinFile sin = new SinFile(new File(bc.getAppsBootFile()));
					sin.setChunkSize(maxS1packetsize);
					uploadImage(sin);
					closeTA();
				}
				openTA(2);
				sendTA(taf);
				closeTA();
				openTA(2);
				Iterator<String> otherfiles = bc.getOtherFiles().iterator();
				while (otherfiles.hasNext()) {
					SinFile sin1 = new SinFile(new File(otherfiles.next()));
					sin1.setChunkSize(maxS1packetsize);
					uploadImage(sin1);
				}
				closeTA();
				_bundle.setBootDeliveryFlashed(true);
    		}
    	} catch (BootDeliveryException e) {
    		log.info(e.getMessage());
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
							TAFileParser ta = new TAFileParser(new File(bent.getAbsolutePath()));
							Iterator<TAUnit> i = ta.entries().iterator();
							while (i.hasNext()) {
								TAUnit ent = i.next();
								TaPartition2.put(ent.getUnitNumber(),ent);
							}
						}
						catch (TAFileParseException tae) {
				    		log.error("Error parsing TA file. Skipping");
				    	}
						else {
							log.warn("File "+bent.getName()+" is ignored");
						}
					}
				}
			}
		try {
			if (bc!=null) {
				TAFileParser taf = new TAFileParser(new File(bc.getTA()));
				Iterator<TAUnit> i = taf.entries().iterator();
				while (i.hasNext()) {
					TAUnit ent = i.next();
					TaPartition2.put(ent.getUnitNumber(),ent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void getDevInfo() throws IOException, X10FlashException {
    	openTA(2);
    	cmd.send(S1Command.CMD12, S1Command.TA_MODEL, false);
    	currentdevice = cmd.getLastReply().getDataString();
    	String info = "Current device : "+getCurrentDevice();
    	cmd.send(S1Command.CMD12, S1Command.TA_SERIAL, false);
    	serial = cmd.getLastReply().getDataString();
    	info = info + " - "+serial;
    	cmd.send(S1Command.CMD12, S1Command.TA_DEVID3, false);
    	info = info + " - "+cmd.getLastReply().getDataString();
    	cmd.send(S1Command.CMD12, S1Command.TA_DEVID4, false);
    	info = info + " - "+cmd.getLastReply().getDataString();
    	cmd.send(S1Command.CMD12, S1Command.TA_DEVID5, false);
    	info = info + " - "+cmd.getLastReply().getDataString();
    	log.info(info);
    	closeTA();
    }
    

    public boolean checkScript() {
    	try {
    		Vector<String> ignored = new Vector<String>();
    		FlashScript flashscript = new FlashScript(getFlashScript());
    		flashscript.setBootConfig(bc);
    		Iterator<Category> icategs = _bundle.getMeta().getAllEntries(true).iterator();
    		while (icategs.hasNext()) {
    			Category cat = icategs.next();
    			if (!flashscript.hasCategory(cat)) ignored.add(cat.getId());
    		}
    		if (ignored.size()>0) {
    			Enumeration eignored = ignored.elements();
    			String dynmsg = "";
    			while (eignored.hasMoreElements()) {
    				dynmsg=dynmsg+eignored.nextElement();
    				if (eignored.hasMoreElements()) dynmsg = dynmsg + ",";
    			}
    			String result = WidgetTask.openYESNOBox(_curshell, "Those data are not in the FSC script and will be skipped : \n"+dynmsg+".\n Do you want to continue ?");
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
    	try {
    		if (_bundle.hasFsc()) {
    			return _bundle.getFsc().getAbsolutePath();
    		}
    	} catch (Exception e) {
    	}
    	DeviceEntry dev = Devices.getDeviceFromVariant(getCurrentDevice());
    	return dev.getFlashScript(_bundle.getVersion(), getCurrentDevice());
    }
  
    public void runScript() {
    	try {
    		TextFile tf = new TextFile(getFlashScript(),"ISO8859-1");
    		log.info("Found a template session. Using it : "+tf.getFileName());
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
    				if (b==null && param.toUpperCase().equals("PARTITION")) {
    					b = _bundle.searchEntry("partition-image");
    				}
    				if (b!=null) {
    					SinFile sin =new SinFile(new File(b.getAbsolutePath()));
    					sin.setChunkSize(maxS1packetsize);
    					this.uploadImage(sin);
    				}
    				else {
    					if (bc!=null) {
    						String file = bc.getMatchingFile(param);
    						if (file!=null) {
    	    					SinFile sin =new SinFile(new File(file));
    	    					sin.setChunkSize(maxS1packetsize);
    	    					this.uploadImage(sin);						
    						}
        					else {
        						log.warn(param + " is excluded from bundle");
        					}
    					}
    					else {
    						log.warn(param + " is excluded from bundle");
    					}
    				}
    			}
    			else if (action.equals("writeTA")) {
    				TAUnit unit = TaPartition2.get(Long.parseLong(param));
    				if (unit != null)
    					this.sendTAUnit(unit);
    				else log.warn("Unit "+param+" not found in bundle");
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

    public boolean hasScript() {
    	File fsc=null;
    	try {
    		fsc=new File(getFlashScript());
    	}
    	catch (Exception e) {
    		fsc=null;
    	}
    	if (fsc!=null) {
    		if (fsc.exists()) {
    			if (_bundle.hasFsc()) return true;
    			String result = WidgetTask.openYESNOBox(_curshell, "A FSC script is found : "+fsc.getName()+". Do you want to use it ?");
    			return Integer.parseInt(result)==SWT.YES;
    		}
    		else return false;
    	}
    	return false;
    }

    public void flash() throws X10FlashException, IOException {
    	try {
		    log.info("Start Flashing");
		    sendLoader();
		    bc = getBootConfig();
		    loadTAFiles();
		    if (hasScript()) {
		    	if (checkScript())
		    		runScript();
		    }
		    else {
		    	DeviceEntry dev = Devices.getDeviceFromVariant(getCurrentDevice());
		    	if (!dev.isFlashScriptMandatory()) {
			    	log.info("No flash script found. Using 0.9.18 flash engine");
			    	oldFlashEngine();
		    	}
		    	else {
		    		log.info("No flash script found.");
		    		log.info("Flash script is mandatory. Closing session");
		        	closeDevice(0x01);
		    	}
		    }
			log.info("Flashing finished.");
			log.info("Please unplug and start your phone");
			log.info("For flashtool, Unknown Sources and Debugging must be checked in phone settings");
			LogProgress.initProgress(0);
    	}
    	catch (Exception ioe) {
    		ioe.printStackTrace();
    		close();
    		log.error(ioe.getMessage());
    		log.error("Error flashing. Aborted");
    		LogProgress.initProgress(0);
    	}
    }

    public void sendPartition() throws FileNotFoundException, IOException, X10FlashException, SinFileException {		
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isPartition()) {
    			BundleEntry entry = c.getEntries().iterator().next();
    			SinFile sin = new SinFile(new File(entry.getAbsolutePath()));
    			sin.setChunkSize(maxS1packetsize);
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
					SinFile sin = new SinFile(new File(entry.getAbsolutePath()));
					sin.setChunkSize(maxS1packetsize);
					uploadImage(sin);
				}
    		}
    	}
    	closeTA();
    }

    public void sendSecro() throws X10FlashException, IOException, SinFileException {
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
    		SinFile sinpreload = new SinFile(new File(preload.getAbsolutePath()));
    		sinpreload.setChunkSize(maxS1packetsize);
    		uploadImage(sinpreload);
    		setLoaderConfiguration("00,01,00,00,00,01");
    		SinFile sinsecro = new SinFile(new File(secro.getAbsolutePath()));
    		sinsecro.setChunkSize(maxS1packetsize);
    		uploadImage(sinsecro);    		
    		setLoaderConfiguration("00,01,00,00,00,00");
    	}
    }
    
    public boolean isBoot(String sinfile) throws SinFileException {
		org.flashtool.parsers.sin.SinFile sin = new org.flashtool.parsers.sin.SinFile(new File(sinfile));
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
				SinFile sin = new SinFile(new File(entry.getAbsolutePath()));
				sin.setChunkSize(maxS1packetsize);
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
				SinFile sin = new SinFile(new File(entry.getAbsolutePath()));
				sin.setChunkSize(maxS1packetsize);
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
				SinFile sin = new SinFile(new File(entry.getAbsolutePath()));
				sin.setChunkSize(maxS1packetsize);
				uploadImage(sin);
    		}
    	}
    	closeTA();
    }

    public void sendTAFiles()  throws FileNotFoundException, IOException, X10FlashException, TAFileParseException {
    	openTA(2);
    	Iterator<Category> e = _bundle.getMeta().getAllEntries(true).iterator();
    	while (e.hasNext()) {
    		Category c = e.next();
    		if (c.isTa()) {
    			BundleEntry entry = c.getEntries().iterator().next();
    			TAFileParser taf = new TAFileParser(new File(entry.getAbsolutePath()));
    			sendTA(taf);
    		}
    	}
    	closeTA();
    }
 
    public void oldFlashEngine() {
    	try {
    		if (_bundle.hasCmd25()) {
		    	log.info("Disabling final data verification check");
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
    		close();
    		log.error(ioe.getMessage());
    		log.error("Error flashing. Aborted");
    		LogProgress.initProgress(0);
    	}
    }

    public Bundle getBundle() {
    	return _bundle;
    }
    
    public boolean open() {
    	return open(_bundle.simulate());
    }

    public boolean flashmode() {
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
    	log.info("Ending flash session");
    	if (!_bundle.simulate())
    		cmd.send(S1Command.CMD04,S1Command.VALNULL,false);
    }

    public void endSession(int param) throws X10FlashException,IOException {
    	log.info("Ending flash session");
    	cmd.send(S1Command.CMD04,BytesUtil.getBytesWord(param, 1),false);
    }

    public void close() {
    	try {
    		endSession();
    	}
    	catch (Exception e) {}
    	USBFlash.close();
    }

    public void closeDevice(int par) {
    	try {
    		endSession(par);
    	}
    	catch (Exception e) {}
    	USBFlash.close();
    }

    public void hookDevice(boolean printProps) throws X10FlashException,IOException {
    	if (printProps && _bundle.hasLoader()) {
			cmd.send(S1Command.CMD01, S1Command.VALNULL, false);
			cmd01string = cmd.getLastReply().getDataString();
			log.debug(cmd01string);
			phoneprops.update(cmd01string);
			if (getPhoneProperty("ROOTING_STATUS")==null) phoneprops.setProperty("ROOTING_STATUS", "UNROOTABLE"); 
			if (phoneprops.getProperty("VER").startsWith("r"))
				phoneprops.setProperty("ROOTING_STATUS", "ROOTED");
    	}
		if (printProps) {
			log.debug("After loader command reply (hook) : "+cmd01string);
			log.info("Loader : "+phoneprops.getProperty("LOADER_ROOT")+" - Version : "+phoneprops.getProperty("VER")+" / Boot version : "+phoneprops.getProperty("BOOTVER")+" / Bootloader status : "+phoneprops.getProperty("ROOTING_STATUS"));
		}
		else
			log.debug("First command reply (hook) : "+cmd01string);
    }

    public TAUnit readTA(int partition, int unit) throws X10FlashException, IOException {
    	TAUnit u = null;
    	this.openTA(partition);
    	try {
    		u = this.readTA(unit);
    	}
    	catch (X10FlashException x10e) {
    		log.warn(x10e.getMessage());
    		u=null;
    	}
    	this.closeTA();
    	return u;
    }

    public void writeTA(int partition, TAUnit unit) throws X10FlashException, IOException {
    	this.openTA(partition);
    	this.sendTAUnit(unit);
    	this.closeTA();
    }
    
    public boolean open(boolean simulate) {
    	if (simulate) return true;
    	LogProgress.initProgress(_bundle.getMaxLoaderProgress());
    	boolean found=false;
    	try {
    		USBFlash.open("ADDE");
    		try {
				log.info("Reading device information");
				;
				firstRead = new String (USBFlash.readS1Reply().getDataArray());
				phoneprops = new LoaderInfo(firstRead);
				phoneprops.setProperty("BOOTVER", phoneprops.getProperty("VER"));
				if (phoneprops.getProperty("VER").startsWith("r"))
					modded_loader=true;
				log.debug(firstRead);
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			log.info("Unable to read from phone after having opened it.");
    			log.info("trying to continue anyway");
    		}
    	    cmd = new S1Command(_bundle.simulate());
    	    hookDevice(false);
    	    log.info("Phone ready for flashmode operations.");
		    getDevInfo();
			if (_bundle.getDevice()!=null) {
				if (_bundle.getDevice().length()>0 && !currentdevice.equals(_bundle.getDevice())) {
						log.error("The bundle does not match the connected device");
						close();
						found = false;
				}
				else found=true;
			}
			else
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

    public String getIMEI() {
		return phoneprops.getProperty("IMEI");
	}
	
	public String getRootingStatus() {
		return phoneprops.getProperty("ROOTING_STATUS");
	}

}