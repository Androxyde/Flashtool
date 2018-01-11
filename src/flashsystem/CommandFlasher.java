package flashsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.JDOMException;
import org.logger.LogProgress;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.IOUtils;
import org.sinfile.parsers.SinFile;
import org.sinfile.parsers.SinFileException;
import org.system.DeviceChangedListener;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.TextFile;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;
import org.util.BytesUtil;
import org.util.HexDump;

import com.Ostermiller.util.CircularByteBuffer;
import com.google.common.io.BaseEncoding;

import flashsystem.io.USBFlash;
import gui.tools.WidgetTask;
import gui.tools.XMLBootConfig;
import gui.tools.XMLBootDelivery;
import gui.tools.XMLPartitionDelivery;

public class CommandFlasher implements Flasher {

	private Bundle _bundle;
    private String currentdevice = "";
    private String serial = "";
    private Shell _curshell;
    static final Logger logger = LogManager.getLogger(CommandFlasher.class);
    private HashMap<Long,TAUnit> TaPartition2 = new HashMap<Long,TAUnit>();
    private XMLBootConfig bc=null;
    private XMLPartitionDelivery pd=null;
    private Properties  phoneprops = null;

    public CommandFlasher(Bundle bundle, Shell shell) {
    	_bundle=bundle;
    	_curshell = shell;
    }

    public boolean flashmode() {
    	boolean found = false;
    	try {
			Thread.sleep(500);
			found = Devices.getLastConnected(false).getPid().equals("B00B");
		}
		catch (Exception e) {
	    	found = false;
		}
    	return found;
    }


    public void loadProperties() {
    	phoneprops = new Properties();
    	try {
    		phoneprops.setProperty("max-download-size",getVar("max-download-size"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("version",getVar("version"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("version-bootloader",getVar("version-bootloader"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("version-baseband",getVar("version-baseband"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("product",getVar("product"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("serialno",getVar("serialno"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("secure",getVar("secure"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Sector-size",getVar("Sector-size"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Loader-version",getVar("Loader-version"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Phone-id",getVar("Phone-id"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Device-id",getVar("Device-id"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Rooting-status",getVar("Rooting-status"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Ufs-info",getVar("Ufs-info"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Emmc-info",getVar("Emmc-info"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Default-security",getVar("Default-security"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Platform-id",getVar("Platform-id"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Keystore-counter",getVar("Keystore-counter"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Security-state",getVar("Security-state"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("S1-root",getVar("S1-root"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Sake-root",getVar("Sake-root"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Version-sony",getVar("Version-sony"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("USB-version",getVar("USB-version"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Battery",getVar("Battery"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Frp-partition",getVar("Frp-partition"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Stored-security-state",getVar("Stored-security-state"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("Keystore-xcs",getVar("Keystore-xcs"));
    	} catch (Exception e) {}
    	try {
    		phoneprops.setProperty("root-key-hash",HexDump.toHex(getRootKeyHash().getBytes()).replaceAll(" ", ""));
    		logger.info("root-key-hash : "+phoneprops.getProperty("root-key-hash"));
    	} catch (Exception e) {}   	
    }

    public String getRootKeyHash() throws IOException, X10FlashException {
    	if (!_bundle.simulate()) {
    		USBFlash.write("Get-root-key-hash".getBytes());
    		CommandPacket reply = USBFlash.readCommandReply();
    		return reply.getMessage();
    		
    	}
    	return null;
    }
    
    public void sync() throws IOException, X10FlashException {
    	logger.info("Syncing device");
    	if (!_bundle.simulate()) {
    		USBFlash.write(("Sync").getBytes());
    		CommandPacket reply = USBFlash.readCommandReply();
    		if (reply.getStatus()!=CommandPacket.OKAY) throw new X10FlashException("Sync command not OK");
    	}
    }

    public void powerDown() throws IOException, X10FlashException {
    	logger.info("Powering down device");
    	if (!_bundle.simulate()) {
			USBFlash.write(("powerdown").getBytes());
			CommandPacket reply = USBFlash.readCommandReply();
			if (reply.getStatus()!=CommandPacket.OKAY) throw new X10FlashException("powerdown command not OK");
    	}
    }

    public boolean open(boolean simulate) {
    	if (_bundle.getMaxBuffer()==0) {
    		USBFlash.setUSBBufferSize(4096*1024);
    		logger.info("USB buffer size set to 4096K");
    	}
    	if (_bundle.getMaxBuffer()==1) {
    		USBFlash.setUSBBufferSize(2048*1024);
    		logger.info("USB buffer size set to 2048K");
    	}
    	if (_bundle.getMaxBuffer()==2) {
    		USBFlash.setUSBBufferSize(1024*1024);
	    	logger.info("USB buffer size set to 1024K");
    	}
    	if (_bundle.getMaxBuffer()==3) {
    		USBFlash.setUSBBufferSize(512*1024);
	    	logger.info("USB buffer size set to 512K");
    	}
    	if (_bundle.getMaxBuffer()==4) {
    		USBFlash.setUSBBufferSize(256*1024);
    		logger.info("USB buffer size set to 256K");
    	}
    	if (_bundle.getMaxBuffer()==5) {
    		USBFlash.setUSBBufferSize(128*1024);
    		logger.info("USB buffer size set to 128K");
    	}
    	if (_bundle.getMaxBuffer()==6) {
    		USBFlash.setUSBBufferSize(64*1024);
    		logger.info("USB buffer size set to 64K");
    	}
    	if (_bundle.getMaxBuffer()==7) {
    		USBFlash.setUSBBufferSize(32*1024);
    		logger.info("USB buffer size set to 32K");
    	}
    	if (simulate) {
    		currentdevice=_bundle.getDevice();
    		return true;
    	}
    	boolean found=false;
    	try {
    		USBFlash.open("B00B");
    		try {
				logger.info("Reading device information");
				currentdevice=getVar("product");
				logger.info("Connected device : "+currentdevice);
    		}
    		catch (Exception e) {
    			logger.info(e.getMessage());
    			logger.info("Unable to read from phone after having opened it.");
    			logger.info("trying to continue anyway");
    		}
    	    logger.info("Phone ready for flashmode operations.");
	    	found = true;
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		found=false;
    	}
    	return found;
    }

    public boolean open() {
    	return open(_bundle.simulate());
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

    public boolean checkScript() {
    	try {
    		Vector<String> ignored = new Vector<String>();
    		FlashScript flashscript = new FlashScript(getFlashScript());
    		flashscript.setBootConfig(bc);
    		flashscript.setPartitionDelivery(pd);
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

    public void runScript() {
    	try {
    		TextFile tf = new TextFile(getFlashScript(),"ISO8859-1");
    		logger.info("Found a template session. Using it : "+tf.getFileName());
    		Map<Integer,String> map =  tf.getMap();
    		Iterator<Integer> keys = map.keySet().iterator();
    		writeTA(2,new TAUnit(10100,new byte[] {0x01}));
    		while (keys.hasNext()) {
    			String param1="";
    			String param2="";
    			String line = map.get(keys.next());
    			String[] parsed = line.split(":");
    			String action = parsed[0];
    			if (parsed.length>1)
    				param1 = parsed[1];
    			if (parsed.length>2)
    				param2 = parsed[2];
    			if (action.equals("flash")) {
    				BundleEntry b = _bundle.searchEntry(param2);
    				if (b!=null) {
    					SinFile sin =new SinFile(new File(b.getAbsolutePath()));
    					flashImage(sin,param1);
    				}
    				else {
    					if (bc!=null) {
    						String file = bc.getMatchingFile(param2);
    						if (file!=null) {
    	    					SinFile sin =new SinFile(new File(file));
    	    					flashImage(sin,param1);
    						}
        					else {
        						logger.warn(param2 + " is excluded from bundle");
        					}
    					}
    					else {
    						logger.warn(param2 + " is excluded from bundle");
    					}
    				}
    			}
    			else if (action.equals("Repartition")) {
   					if (pd!=null) {
						String file = pd.getMatchingFile(param2);
						if (file!=null) {
	    					SinFile sin =new SinFile(new File(file));
	    					repartition(sin,Integer.parseInt(param1));
						}
    					else {
    						logger.warn(param2 + " is excluded from bundle");
    					}
					}
					else {
						logger.warn(param2 + " is excluded from bundle");
					}   				
    			}
 
    			else if (action.equals("Write-TA")) {
    				if (Integer.parseInt(param1) == 2)
    					if (TaPartition2.get(Long.parseLong(param2))!=null) {
    						writeTA(2,TaPartition2.get(Long.parseLong(param2)));
    					}
    					else {
    						if (!param2.equals("10100") && !param2.equals("10021"))
    							logger.warn("TA Unit "+param2 + " is excluded from bundle");
    					}
    			}
    		}
    	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    	  	TAUnit tau = new TAUnit(10021, BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
    	  	writeTA(2,tau);
    		writeTA(2,new TAUnit(10100,new byte[] {0x00}));
    	} catch (Exception e) {e.printStackTrace();}
    }

    
	public void flash() throws X10FlashException, IOException {
		try {
		//WidgetTask.openOKBox(_curshell, "This device protocol is not yet supported");
		logger.info("Start Flashing");
		loadProperties();
		bc = getBootConfig();
		pd = _bundle.getXMLPartitionDelivery();
		if (pd!=null)
			pd.setFolder(_bundle.getPartitionDelivery().getFolder());
		else {
			String result = WidgetTask.openYESNOBox(_curshell, "No partition delivery included.\nMaybe a bundle created with a previous release of Flashtool.\nDo you want to continue ?");
			if (Integer.parseInt(result)!=SWT.YES) throw new X10FlashException("No partition delivery");
		}
		loadTAFiles();
	    if (hasScript()) {
	    	if (checkScript())
	    		runScript();
	    }
	    else {
	    	logger.error("No flash script found. Flash script is mandatory");
	    }
		} catch (Exception e) {}
		LogProgress.initProgress(0);
		sync();
		powerDown();
		DeviceChangedListener.pause(false);
	}

	public XMLBootConfig getBootConfig() throws FileNotFoundException, IOException,X10FlashException, JDOMException, TAFileParseException, BootDeliveryException  {
		if (!_bundle.hasBootDelivery()) {
			logger.info("No boot delivery into the bundle");
			return null;
		}
		logger.info("Parsing boot delivery");
		XMLBootDelivery xml = _bundle.getXMLBootDelivery();
		Vector<XMLBootConfig> found = new Vector<XMLBootConfig>();
		if (!_bundle.simulate()) {    			
    		Enumeration<XMLBootConfig> e = xml.getBootConfigs();
    		while (e.hasMoreElements()) {
    			// We get matching bootconfig from all configs
    			XMLBootConfig bc=e.nextElement();
    			if (bc.matches(phoneprops.getProperty("root-key-hash"))) {
    				found.add(bc);
    			}
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
		logger.info("Found a boot delivery");
		return found.get(found.size()-1);
    }

	public void close() {
		
	}

	public String getPhoneProperty(String property) {
		return phoneprops.getProperty(property);
	}

	public Bundle getBundle() {
		return _bundle;
	}

	public TAUnit readTA(int partition, int unit) throws X10FlashException, IOException {
		logger.info("Reading TA unit "+unit+" from partition "+partition);
		if (!_bundle.simulate()) {
			String command = "Read-TA:"+partition+":"+unit;
			logger.info(command);
			USBFlash.write(command.getBytes());
			logger.info("get reply");
			CommandPacket reply = USBFlash.readCommandReply();
			logger.info("Reply : "+reply.getMessage());
		}
		return null;
	}

	public void repartition(SinFile sin, int partnumber) throws IOException, X10FlashException {
		String command="";
		logger.info("processing "+sin.getName());
		logger.info("   signature:"+HexDump.toHex(sin.getHeader().length));
		if (!_bundle.simulate()) {
			command = "signature:"+HexDump.toHex(sin.getHeader().length);
			USBFlash.write(command.getBytes());
			USBFlash.readReply();
			logger.info("   signature reply : "+new String(USBFlash.getLastReply()));
		}
		if (!_bundle.simulate()) {
			USBFlash.write(sin.getHeader());
			USBFlash.readReply();
			logger.info("   signature status : "+new String(USBFlash.getLastReply()));
		}
		TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sin.getFile())));
		TarArchiveEntry entry=null;
		while ((entry = tarIn.getNextTarEntry()) != null) {
			if (!entry.getName().endsWith("cms")) {
				logger.info("   sending "+entry.getName());
				logger.info("      download:"+HexDump.toHex((int)entry.getSize()));
				if (!_bundle.simulate()) {
					command = "download:"+HexDump.toHex((int)entry.getSize());
					USBFlash.write(command.getBytes());
					USBFlash.readReply();
					logger.info("      Download reply : "+new String(USBFlash.getLastReply()));
				}
				CircularByteBuffer cb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
				IOUtils.copy(tarIn, cb.getOutputStream());
				while (cb.getAvailable()>0) {
					byte[] buffer = new byte[cb.getAvailable()>=USBFlash.getUSBBufferSize()?USBFlash.getUSBBufferSize():cb.getAvailable()];
					cb.getInputStream().read(buffer);
					if (!_bundle.simulate()) {
						USBFlash.write(buffer);
					}
				}
				if (!_bundle.simulate()) {
					USBFlash.readReply();
					logger.info("      Download status : "+new String(USBFlash.getLastReply()));
				}
				logger.info("   Repartition:"+partnumber);
				if (!_bundle.simulate()) {
					command="Repartition:"+partnumber;
					USBFlash.write(command.getBytes());
					USBFlash.readReply();
					logger.info("   Repartition status : "+new String(USBFlash.getLastReply()));
				}
			}
		}
		tarIn.close();
		
    }

	public void flashImage(SinFile sin,String partitionname) throws X10FlashException, IOException {
		String command="";
		logger.info("processing "+sin.getName());
		logger.info("   signature:"+HexDump.toHex(sin.getHeader().length));
		if (!_bundle.simulate()) {
			command = "signature:"+HexDump.toHex(sin.getHeader().length);
			USBFlash.write(command.getBytes());
			USBFlash.readReply();
			logger.info("   signature reply : "+new String(USBFlash.getLastReply()));
			USBFlash.write(sin.getHeader());
			USBFlash.readReply();
			logger.info("   signature status : "+new String(USBFlash.getLastReply()));	
		}
		logger.info("   erase:"+partitionname);
		if (!_bundle.simulate()) {
			command="erase:"+partitionname;
			USBFlash.write(command.getBytes());
			USBFlash.readReply();
			logger.info("   erase status : "+new String(USBFlash.getLastReply()));
		}
		TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sin.getFile())));
		TarArchiveEntry entry=null;
		while ((entry = tarIn.getNextTarEntry()) != null) {
			if (!entry.getName().endsWith("cms")) {
				logger.info("   sending "+entry.getName());
				logger.info("      download:"+HexDump.toHex((int)entry.getSize()));
				if (!_bundle.simulate()) {
					command = "download:"+HexDump.toHex((int)entry.getSize());
					USBFlash.write(command.getBytes());
					USBFlash.readReply();
					logger.info("      Download reply : "+new String(USBFlash.getLastReply()));
				}
				CircularByteBuffer cb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
				IOUtils.copy(tarIn, cb.getOutputStream());
				LogProgress.initProgress(cb.getAvailable()/USBFlash.getUSBBufferSize()+1);
				while (cb.getAvailable()>0) {
					byte[] buffer = new byte[cb.getAvailable()>=USBFlash.getUSBBufferSize()?USBFlash.getUSBBufferSize():cb.getAvailable()];
					cb.getInputStream().read(buffer);
					LogProgress.updateProgress();
					if (!_bundle.simulate()) {
						USBFlash.write(buffer);
					}
					else {
						try {
							Thread.sleep(50);} catch (Exception e) {}
					}
				}
				if (!_bundle.simulate()) {
					USBFlash.readReply();
					logger.info("      Download status : "+new String(USBFlash.getLastReply()));
				}
				logger.info("      flash:"+partitionname);
				if (!_bundle.simulate()) {
					command="flash:"+partitionname;
					USBFlash.write(command.getBytes());
					USBFlash.readReply();
					logger.info("      flash status : "+new String(USBFlash.getLastReply()));
				}
				LogProgress.initProgress(0);
			}
		}
		tarIn.close();
	}

	public String getVar(String key) throws X10FlashException, IOException {
		logger.info("Reading variable value for "+ key);
		if (!_bundle.simulate()) {
			String command = "getvar:"+key;
			USBFlash.write(command.getBytes());
			CommandPacket reply = USBFlash.readCommandReply();
			logger.info("Reply : "+reply.getMessage());
			return reply.getMessage();
		}
		return "";
	}
	
	public void writeTA(int partition, TAUnit unit) throws X10FlashException, IOException {
		try {
			logger.info("Writing TA unit "+unit.getUnitNumber()+" to partition "+partition);
			logger.info("Sending data with lenght of "+unit.getDataLength()+" ("+HexDump.toHex(unit.getDataLength())+")");
			if (!_bundle.simulate()) {
				String command = "download:"+HexDump.toHex(unit.getDataLength());
				USBFlash.write(command.getBytes());
				USBFlash.readReply();
				logger.info("Download reply : "+new String(USBFlash.getLastReply()));
				if (unit.getDataLength()>0) {
					USBFlash.write(unit.getUnitData());
				}
				USBFlash.readReply();
				logger.info("Data reply : "+new String(USBFlash.getLastReply()));
				command = "Write-TA:"+partition+":"+unit.getUnitNumber();
				USBFlash.write(command.getBytes());
				USBFlash.readReply();
				logger.info("Write-TA reply : "+new String(USBFlash.getLastReply()));
			}
		} 
		catch (Exception e) {
		}
	}
	
	public void sendLoader() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
		
	}
	
	public void backupTA() {
		
	}
	
	public String getCurrentDevice() {
		return currentdevice;
	}
	
	public String getSerial() {
		return serial;
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

}
