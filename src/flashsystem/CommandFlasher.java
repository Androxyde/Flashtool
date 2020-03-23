package flashsystem;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.JDOMException;
import org.logger.LogProgress;
import org.rauschig.jarchivelib.IOUtils;
import org.sinfile.parsers.SinFile;
import org.sinfile.parsers.SinFileException;
import org.system.DeviceEntry;
import org.system.Devices;
import org.system.OS;
import org.system.TextFile;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;
import org.ta.parsers.TAUnit;
import org.util.BytesUtil;
import org.util.HexDump;
import com.Ostermiller.util.CircularByteBuffer;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.mapper.Bin;
import flashsystem.io.USBFlash;
import gui.tools.WidgetTask;
import gui.tools.XMLBootConfig;
import gui.tools.XMLBootDelivery;
import gui.tools.XMLPartitionDelivery;

public class CommandFlasher implements Flasher {

	public class Lun {
		
        @Bin public byte lunlength;
        @Bin public byte reserved1;
        @Bin public byte lunid;
        @Bin public byte[] reserved2;
        @Bin public int length;
        @Bin public byte [] lundata;
	
	}

	public class UfsInfos {

		@Bin public byte headerlen;
		@Bin public byte[] ufs_header;
		@Bin public Lun [] luns;
	
		long sectorSize=0;
		
		public void setSectorSize(long sectorsize) {
			sectorSize=sectorsize;
		}
	
		public long getLunSize(int lun) {
			return (long)luns[lun].length*sectorSize/1024L;
		}
		
	    public Object newInstance(Class<?> klazz){
	       return klazz == Lun.class ? new Lun() : null;
	    }
	
	}

	private Bundle _bundle;
    private String currentdevice = "";
    private String serial = "";
    private Shell _curshell;
    static final Logger logger = LogManager.getLogger(CommandFlasher.class);
    private HashMap<Long,TAUnit> TaPartition2 = new HashMap<Long,TAUnit>();
    private XMLBootConfig bc=null;
    private XMLPartitionDelivery pd=null;
    private Properties  phoneprops = null;
    private UfsInfos ufs_infos = null;
    
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
    	logger.info("Reading phone properties");
    	phoneprops = new Properties();
    	try {
	    	getVar("max-download-size");
	    	getVar("version");
	    	getVar("version-bootloader");
	    	getVar("version-baseband");
	    	getVar("product");
	    	getVar("serialno");
	    	getVar("secure");
	    	getVar("Sector-size");
	    	getVar("Version-sony");
	    	getVar("USB-version");
	    	getVar("max-download-size");
	    	getVar("Loader-version");
	    	getVar("Phone-id");
	    	getVar("Device-id");
	    	getVar("Rooting-status");
	    	getVar("Sector-size");
	    	getVar("Ufs-info");
	    	getVar("Emmc-info");
	    	getVar("Default-security");
	    	getVar("Platform-id");
	    	getVar("Keystore-counter");
	    	getVar("Security-state");
	    	getVar("S1-root");
	    	getVar("Sake-root");
	    	getVar("Battery");
	    	getVar("Frp-partition");
	    	getVar("Stored-security-state");
	    	getVar("Keystore-xcs");
	    	getRootKeyHash();
	    	phoneprops.setProperty("swrelease", new String(readTA(2, 2202).getUnitData()));
	    	phoneprops.setProperty("customization", new String(readTA(2, 2205).getUnitData()));
	    	phoneprops.setProperty("model",new String(readTA(2, 2210).getUnitData()));
	    	phoneprops.setProperty("serial", new String(readTA(2, 4900).getUnitData()));
	    	phoneprops.setProperty("lastflashdate",new String(readTA(2, 10021).getUnitData()));
	    	getLog();
	    	getUfsInfo();
    	} catch (Exception e) {
    	}    	
    }

    public boolean open(boolean simulate) {
    	USBFlash.setUSBBufferSize(512*1024);
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
				loadProperties();
				currentdevice=getPhoneProperty("product");
				logger.info("Connected device : "+currentdevice+" / SW release "+phoneprops.getProperty("swrelease")+" / Customization "+phoneprops.getProperty("customization"));
				logger.info("Last flash date : "+phoneprops.getProperty("lastflashdate"));
    		}
    		catch (Exception e) {
    			logger.info(e.getMessage());
    			logger.info("Unable to read from phone after having opened it.");
    			logger.info("trying to continue anyway");
    		}
    	    logger.info("Phone ready for flashmode operations.");
			if (_bundle.getDevice()!=null) {
				if (_bundle.getDevice().length()>0 && !currentdevice.equals(_bundle.getDevice())) {
						logger.error("The bundle does not match the connected device");
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
    			if (!flashscript.hasCategory(cat)) {
    				if (!cat.getId().equals("FSCONFIG") ) {
    					ignored.add(cat.getId());
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
    		setFlashState(true);
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
						String file = pd.getMatchingFile(SinFile.getShortName(param2));
						if (file!=null) {
	    					SinFile sin =new SinFile(new File(file));
	    					repartition(sin,Integer.parseInt(param1));
						}
    					else {
    						logger.warn(param2 + " is excluded from bundle");
    					}
					}
					else {
						BundleEntry b = _bundle.searchEntry(param2);
	    				if (b!=null) {
	    					SinFile sin =new SinFile(new File(b.getAbsolutePath()));
	    					repartition(sin,Integer.parseInt(param1));
	    				}
	    				else
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
    			else if (action.equals("set_active")) {
    				setActive(param1);
    			}
    			else if (action.equals("Get-ufs-info")) {
    				getUfsInfo();
    			}
    			else if (action.equals("Get-gpt-info")) {
    				GetGptInfo(Integer.parseInt(param1));
    			}
    		}
    		setFlashState(false);
    	} catch (Exception e) {e.printStackTrace();}
    }

    
	public void flash() throws X10FlashException, IOException {
		try {
			//WidgetTask.openOKBox(_curshell, "This device protocol is not yet supported");
			logger.info("Start Flashing");
			bc = getBootConfig();
			pd = _bundle.getXMLPartitionDelivery();
			if (pd!=null) {
				pd.setFolder(_bundle.getPartitionDelivery().getFolder());
				pd.setUfsInfos(ufs_infos);
			}
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
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		LogProgress.initProgress(0);
		close();
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
    			bc.addMatcher("PLF_ROOT_HASH", phoneprops.getProperty("root-key-hash"));
    			if (bc.matchAttributes()) {
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
			throw new BootDeliveryException ("Found no matching boot config. Aborting");
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
		try {
			sync();
			powerDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		USBFlash.close();
	}

	public String getPhoneProperty(String property) {
		return phoneprops.getProperty(property);
	}

	public Bundle getBundle() {
		return _bundle;
	}

	public void getUfsInfo()  throws IOException,X10FlashException {
    	logger.info("Sending Get-ufs-info");
    	if (!_bundle.simulate()) {
    		String command = "Get-ufs-info";
    		USBFlash.write(command.getBytes());
    		CommandPacket reply = USBFlash.readCommandReply(true);
    		logger.info("   Get-ufs-info status : "+reply.getResponse());

    		JBBPParser ufs_parser = JBBPParser.prepare(
    			    "byte headerlen;"
                  + "byte[headerlen-1] ufs_header;"
                  + "luns [_] { "
                  + "   byte lunlength; "
                  + "   byte reserved1; "
                  + "   byte lunid; "
                  + "   byte[12] reserved2; "
                  + "   int length; "
                  + "   byte[lunlength-19] lundata; "
                  + "}"
             );		

    		try {
    			JBBPBitInputStream stream = new JBBPBitInputStream(new ByteArrayInputStream(reply.getDataArray()));
    			ufs_infos = ufs_parser.parse(stream).mapTo(new UfsInfos());
    			ufs_infos.setSectorSize(Integer.parseInt(getPhoneProperty("Sector-size")));
    			try {
    			   stream.close();
    			} catch (Exception streamclose ) {}
    		}
    		catch (Exception e) {
    			System.out.println(e.getMessage());
    			ufs_infos=null;
    		}

    	}		
	}

	public void GetGptInfo(int partnumber)  throws IOException,X10FlashException {
    	logger.info("Sending Get-gpt-info:"+partnumber);
    	if (!_bundle.simulate()) {
    		String command = "Get-gpt-info:"+partnumber;
    		USBFlash.write(command.getBytes());
    		CommandPacket reply = USBFlash.readCommandReply(true);
    		logger.info("   Get-gpt-info status : "+reply.getResponse());
    	}
	}

	public void setActive(String name)  throws IOException,X10FlashException {
    	logger.info("Sending set_active:"+name);
    	if (!_bundle.simulate()) {
    		String command = "set_active:"+name;
    		USBFlash.write(command.getBytes());
    		CommandPacket reply = USBFlash.readCommandReply(true);
    		logger.info("   set_active status : "+reply.getResponse());
    	}
	}
	
	public void setFlashState(boolean ongoing) throws IOException,X10FlashException {
		if (ongoing) {
    		writeTA(2,new TAUnit(10100,new byte[] {0x01}));
		}
		else {
    	  	String result = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    	  	TAUnit tau = new TAUnit(10021, BytesUtil.concatAll(result.getBytes(), new byte[] {0x00}));
    	  	writeTA(2,tau);
    		writeTA(2,new TAUnit(10100,new byte[] {0x00}));
		}
	}

	public String getLog() throws X10FlashException, IOException {
		logger.info("Sending Getlog");
		if (!_bundle.simulate()) {
			String command = "Getlog";
			USBFlash.write(command.getBytes());
			CommandPacket reply = USBFlash.readCommandReply(true);
			logger.info("   Getlog status : "+reply.getResponse());
			return reply.getMessage();
		}
		return "";
	}

	public TAUnit readTA(int partition, int unit) throws X10FlashException, IOException {
		return readTA(partition, unit, true);
	}

	public TAUnit readTA(int partition, int unit, boolean withlog) throws X10FlashException, IOException {
		if (!_bundle.simulate()) {
			String command = "Read-TA:"+partition+":"+unit;
			if (withlog)
				logger.info("Sending "+command);
			USBFlash.write(command.getBytes());
			CommandPacket reply = USBFlash.readCommandReply(true);
			if (withlog)
				logger.info("   Read-TA status : "+reply.getResponse());
			if (reply.getResponse().equals("OKAY")) {
				TAUnit taunit = new TAUnit(unit,reply.getDataArray());
				return taunit;
			}
			else {
				if (withlog)
					logger.warn("   "+reply.getMessage()+" ( Hex unit value "+HexDump.toHex(unit)+" )");
			}
		}
		return null;
	}

	public void writeTA(int partition, TAUnit unit) throws X10FlashException, IOException {
		try {
			//wrotedata=true;
			logger.info("Writing TA unit "+HexDump.toHex((int)unit.getUnitNumber())+" to partition "+partition);
			if (!_bundle.simulate()) {
				logger.info("   download:"+HexDump.toHex(unit.getDataLength()));
				String command = "download:"+HexDump.toHex(unit.getDataLength());
				USBFlash.write(command.getBytes());
				CommandPacket p = USBFlash.readCommandReply(false);
				if (unit.getDataLength()>0) {
					USBFlash.write(unit.getUnitData());
				}
				p = USBFlash.readCommandReply(true);
				logger.info("   download status : "+p.getResponse());
				logger.info("   Write-TA:"+partition+":"+unit.getUnitNumber());
				command = "Write-TA:"+partition+":"+unit.getUnitNumber();
				USBFlash.write(command.getBytes());
				p = USBFlash.readCommandReply(true);
				logger.info("   Write-TA status : "+p.getResponse());
			}
		} 
		catch (Exception e) {
		}
	}

	public void getVar(String key) throws X10FlashException, IOException {
		if (!_bundle.simulate()) {
			String command = "getvar:"+key;
			logger.info("Sending "+command);
			USBFlash.write(command.getBytes());
			CommandPacket reply = USBFlash.readCommandReply(true);
			logger.info("   getvar status : "+reply.getResponse());
			if (reply.getResponse().equals("FAIL")) {
				logger.warn("   "+reply.getMessage());
			}
			phoneprops.setProperty(key,reply.getMessage());
		}
	}

	public void sync() throws IOException, X10FlashException {
    	logger.info("Sending Sync");
    	if (!_bundle.simulate()) {
    		USBFlash.write(("Sync").getBytes());
    		CommandPacket p = USBFlash.readCommandReply(true);
    		logger.info("   Sync status : "+p.getResponse());
    	}
    }

    public void powerDown() throws IOException, X10FlashException {
    	logger.info("Sending powerdown");
    	if (!_bundle.simulate()) {
			USBFlash.write(("powerdown").getBytes());
			CommandPacket p = USBFlash.readCommandReply(true);
			logger.info("   powerdown status : "+p.getResponse());
    	}
    }

    public void getRootKeyHash() throws IOException, X10FlashException {
    	logger.info("Sending Get-root-key-hash");
    	if (!_bundle.simulate()) {
    		USBFlash.write("Get-root-key-hash".getBytes());
    		CommandPacket p = USBFlash.readCommandReply(true);
    		logger.info("   Get-root-key-hash status : "+p.getResponse());
    		phoneprops.setProperty("root-key-hash", HexDump.toHex(p.getDataArray()).replaceAll(" ", ""));
    	}
    }

	public void repartition(SinFile sin, int partnumber) throws IOException, X10FlashException {
		//wrotedata=true;
		String command="";
		logger.info("processing "+sin.getName());
			command = "signature:"+HexDump.toHex(sin.getHeader().length);
			logger.info("   "+command);
			if (!_bundle.simulate()) {
				USBFlash.write(command.getBytes());
				CommandPacket p = USBFlash.readCommandReply(false);
				USBFlash.write(sin.getHeader());
				p = USBFlash.readCommandReply(true);
				logger.info("   signature status : "+p.getResponse());
			}
		TarArchiveInputStream tarIn = sin.getTarInputStream();
		TarArchiveEntry entry=null;
		while ((entry = tarIn.getNextTarEntry()) != null) {
			if (!entry.getName().endsWith("cms")) {
				logger.info("   sending "+entry.getName());
				if (!_bundle.simulate()) {
					command = "download:"+HexDump.toHex((int)entry.getSize());
					logger.info("      "+command);
					USBFlash.write(command.getBytes());
					CommandPacket p = USBFlash.readCommandReply(false);
					//logger.info("      Download reply : "+p.getResponse());
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
				CommandPacket p=null;
				if (!_bundle.simulate()) {
					p = USBFlash.readCommandReply(true);
					logger.info("      download status : "+p.getResponse());
				}
				command="Repartition:"+partnumber;
				logger.info("   "+command);
				if (!_bundle.simulate()) {
					USBFlash.write(command.getBytes());
					p = USBFlash.readCommandReply(true);
					logger.info("   Repartition status : "+p.getResponse());
				}
			}
		}
		tarIn.close();
		
    }

	public void flashImage(SinFile sin,String partitionname) throws X10FlashException, IOException {
		//wrotedata=true;
		String command="";
		logger.info("processing "+sin.getName());
		command = "signature:"+HexDump.toHex(sin.getHeader().length);
		logger.info("   signature:"+HexDump.toHex(sin.getHeader().length));
		CommandPacket p=null;
		if (!_bundle.simulate()) {
			USBFlash.write(command.getBytes());
			p = USBFlash.readCommandReply(false);
			//logger.info("   signature reply : "+p.getResponse());
			USBFlash.write(sin.getHeader());
			p = USBFlash.readCommandReply(true);
			command="signature";
			USBFlash.write(command.getBytes());
			p = USBFlash.readCommandReply(true);
			logger.info("   signature status : "+p.getResponse());
		}
		command="erase:"+partitionname;
		logger.info("   "+command);
		if (!_bundle.simulate()) {
			USBFlash.write(command.getBytes());
			p = USBFlash.readCommandReply(true);
			logger.info("   erase status : "+p.getResponse());
		}
		TarArchiveInputStream tarIn = sin.getTarInputStream();
		TarArchiveEntry entry=null;
		while ((entry = tarIn.getNextTarEntry()) != null) {
			if (!entry.getName().endsWith("cms")) {
				logger.info("   sending "+entry.getName());
				if (!_bundle.simulate()) {
					command = "download:"+HexDump.toHex((int)entry.getSize());
					logger.info("      "+command);
					USBFlash.write(command.getBytes());
					p = USBFlash.readCommandReply(false);
					//logger.info("      Download reply : "+p.getResponse());
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
					p = USBFlash.readCommandReply(true);
					logger.info("      download status : "+p.getResponse());
				}
				command="flash:"+partitionname;
				logger.info("      "+command);
				if (!_bundle.simulate()) {
					USBFlash.write(command.getBytes());
					p = USBFlash.readCommandReply(true);
					logger.info("      flash status : "+p.getResponse());
				}
				LogProgress.initProgress(0);
			}
		}
		
		tarIn.close();
	}

	
	public String getIMEI() {
		return phoneprops.getProperty("Phone-id").split(":")[1];
	}
	
	public String getRootingStatus() {
		return phoneprops.getProperty("Rooting-status");
	}

	public void sendLoader() throws FileNotFoundException, IOException, X10FlashException, SinFileException {
		
	}
	
	public void backupTA() {
    	logger.info("Making a TA backup");
    	String timeStamp = OS.getTimeStamp();
    	LogProgress.initProgress(24000);
    	try {
    		backupTA(1, timeStamp);
    	} catch (Exception e) {}
    	try {
    		backupTA(2, timeStamp);
    	} catch (Exception e) {}
    	LogProgress.initProgress(0);
	}
	
	private void backupTA(int partition, String timestamp) {
		logger.info("Saving TA partition "+partition);
    	String folder = OS.getFolderRegisteredDevices()+File.separator+getPhoneProperty("serialno")+File.separator+"s1ta"+File.separator+timestamp;
    	new File(folder).mkdirs();
    	TextFile tazone = new TextFile(folder+File.separator+partition+".ta","ISO8859-1");
    	try {
    		tazone.open(false);
    	} catch (Exception e1) {
    		logger.error("Unable to create backup file");
    		return;
    	}
    	try {
    		tazone.writeln(HexDump.toHex((byte)partition));
    		for (int unit = 0 ; unit < 12000; unit++) {
    			LogProgress.updateProgress();
    			try {
    				TAUnit taunit = this.readTA(partition, unit, false);
    				if (taunit != null) 
    					tazone.writeln(taunit.toString());
    			} catch (Exception e3) {
    				e3.printStackTrace();
    			}
    		}
    		tazone.close();
	        logger.info("TA partition "+partition+" saved to "+folder+File.separator+partition+".ta");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
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
