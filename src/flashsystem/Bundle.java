package flashsystem;

import gui.tools.FirmwareFileFilter;
import gui.tools.XMLBootDelivery;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;

import org.apache.log4j.Logger;
import org.logger.LogProgress;
import org.system.Devices;
import org.system.OS;

import com.sonymobile.cs.generic.file.FileUtils;
import com.turn.ttorrent.common.Torrent;

public final class Bundle {

	private JarFile _firmware;
    private boolean _simulate=false;
    //private Properties bundleList=new Properties();
    private String _version;
    private String _branding;
    private String _device;
    private String _cda="";
    private String _revision="";
    private String _cmd25;
    private String _resetCust;
    public final static int JARTYPE=1;
    public final static int FOLDERTYPE=2;
    private BundleMetaData _meta;
    private boolean bootdeliveryflashed=false;
    private XMLBootDelivery xmlb;
    private static Logger logger = Logger.getLogger(Bundle.class);

    public Bundle() {
    	_meta = new BundleMetaData();
    }
    
    public Bundle(String path, int type) throws Exception {
    	feed(path,type);
    }

    public void setMeta(BundleMetaData meta) throws Exception {
    	_meta = meta;
    	feedFromMeta();
    }
    
    public Bundle(String path, int type, BundleMetaData meta) throws Exception {
    	_meta = meta;
    	feed(path,type);
    }
    
    private void feed(String path, int type) throws Exception {
    	if (type==JARTYPE) feedFromJar(path);
    	if (type==FOLDERTYPE) feedFromFolder(path);    	
    }
    
	private void feedFromJar(String path) {
		try {
			_firmware = new JarFile(path);
			_meta = new BundleMetaData();
			logger.debug("Creating bundle from ftf file : "+_firmware.getName());
			_device = _firmware.getManifest().getMainAttributes().getValue("device");
			_version = _firmware.getManifest().getMainAttributes().getValue("version");
			_branding = _firmware.getManifest().getMainAttributes().getValue("branding");
			_cda = _firmware.getManifest().getMainAttributes().getValue("cda");
			_cmd25 = _firmware.getManifest().getMainAttributes().getValue("cmd25");
			Enumeration<JarEntry> e = _firmware.entries();
			while (e.hasMoreElements()) {
				BundleEntry entry = new BundleEntry(_firmware,e.nextElement());
				if (!entry.getName().toUpperCase().startsWith("BOOT/")) {
				if (entry.getName().toUpperCase().endsWith("SIN") || entry.getName().toUpperCase().endsWith("TA") || entry.getName().toUpperCase().endsWith("XML")) {
					try {
						_meta.process(entry);
					}
					catch (Exception e1) {e1.printStackTrace();
					}
					logger.debug("Added this entry to the bundle list : "+entry.getName());
				}
				}
			}
		}
		catch (IOException ioe) {
			logger.error("Cannot open the file "+path);
		}
	}

	private void feedFromFolder(String path) throws Exception {
		File[] list = (new File(path)).listFiles(new FirmwareFileFilter());
		for (int i=0;i<list.length;i++) {
			BundleEntry entry = new BundleEntry(list[i]);
			_meta.process(entry);
			logger.debug("Added this entry to the bundle list : "+entry.getName());
		}
	}

	private void feedFromMeta() throws Exception {
		Iterator<Category> all = _meta.getAllEntries(true).iterator();
		while (all.hasNext()) {
			Category cat = all.next();
			Iterator<BundleEntry> icat = cat.getEntries().iterator();
			while (icat.hasNext()) {
				BundleEntry f = icat.next();
				_meta.process(f);
				logger.debug("Added this entry to the bundle list : "+f.getName());
			}
		}
	}

	public void setLoader(File loader) {
		BundleEntry entry = new BundleEntry(loader);
		try {
			if (_meta!=null)
				_meta.process(entry);
		}
		catch (Exception e) {
		}
	}

	public void setSimulate(boolean simulate) {
		_simulate = simulate;
	}

	public BundleEntry getEntry(String name) {
		Category c = _meta.get(Category.getCategoryFromName(name));
		return c.getEntries().iterator().next();
	}

	public BundleEntry searchEntry(String name) {
		Vector<BundleEntry> v = new Vector<BundleEntry>();
		Category c = _meta.get(Category.getCategoryFromName(name));
		if (c!=null && c.isEnabled()) return c.getEntries().iterator().next();
		return null;
	}

	public Enumeration <BundleEntry> allEntries() {
		Iterator<Category> icateg = _meta.getAllEntries(false).iterator();
		Vector<BundleEntry> v = new Vector<BundleEntry>();
		while (icateg.hasNext()) {
			Category c = icateg.next();
			Iterator<BundleEntry> ibentry = c.getEntries().iterator();
			while (ibentry.hasNext()) {
				v.add(ibentry.next());
			}
		}
		return v.elements();
	}
		
	public boolean isBootDeliveryFlashed() {
		return bootdeliveryflashed;
	}
	
	public void setBootDeliveryFlashed(boolean flashed) {
		bootdeliveryflashed=flashed;
	}

	public boolean hasLoader() {
		return _meta.getLoader()!=null;
	}

	public BundleEntry getLoader() throws IOException, FileNotFoundException {
		return _meta.getLoader().getEntries().iterator().next();
	}

	public boolean hasBootDelivery() {
		Category bl = _meta.get("BOOT_DELIVERY");
		if (bl==null) return false;
		if (bl.isEnabled()) return true;
		return false;
	}

	public BundleEntry getBootDelivery()  throws IOException, FileNotFoundException {
		return _meta.get("BOOT_DELIVERY").getEntries().iterator().next();
	}

	public boolean simulate() {
		return _simulate;
	}
	
	public void setVersion(String version) {
		_version=version;
	}
	
	public void setBranding(String branding) {
		_branding=branding;
		
	}
	
	public void setDevice(String device) {
		_device=device;
	}
	
	public void setCDA(String cda) {
		_cda = cda;
	}

	public void setRevision(String revision) {
		_revision = revision;
	}
	
	public void setCmd25(String value) {
		_cmd25 = value;
		if (_cmd25==null) _cmd25="false";
	}
	
	public boolean hasCmd25() {
		try {
			return _cmd25.equals("true");
		}
		catch (Exception e) {
			return false;
		}
	}

	public void setResetStats(String value) {
		_resetCust = value;
		if (_resetCust==null) _resetCust="false";
	}

	public boolean hasResetStats() {
		try {
			return _resetCust.equals("true");
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public void createFTF() throws Exception {
		File ftf = new File(OS.getFolderFirmwares()+File.separator+_device+"_"+_version+"_"+(_cda.length()>0?_cda:_branding)+(_revision.length()>0?("_"+_revision):"")+".ftf");
		if (ftf.exists()) throw new Exception("Bundle already exists");
		byte buffer[] = new byte[10240];
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Manifest-Version: 1.0\n");
		sbuf.append("Created-By: FlashTool\n");
		sbuf.append("version: "+_version+"\n");
		sbuf.append("branding: "+_branding+"\n");
		sbuf.append("cda: "+_cda+"\n");
		sbuf.append("revision: "+_revision+"\n");
		sbuf.append("device: "+_device+"\n");
		sbuf.append("cmd25: "+_cmd25+"\n");
		Manifest manifest = new Manifest(new ByteArrayInputStream(sbuf.toString().getBytes("UTF-8")));
	    FileOutputStream stream = new FileOutputStream(ftf);
	    JarOutputStream out = new JarOutputStream(stream, manifest);
	    out.setLevel(Deflater.BEST_SPEED);
	    long size = 0L;
		Enumeration<BundleEntry> esize = allEntries();
		while (esize.hasMoreElements()) {
			size += esize.nextElement().getSize();
		}
		LogProgress.initProgress(size/10240+(size%10240>0?1:0));
	    Enumeration<BundleEntry> e = allEntries();
		while (e.hasMoreElements()) {
			BundleEntry entry = e.nextElement();
			logger.info("Adding "+entry.getName()+" to the bundle as "+entry.getInternal());
		    JarEntry jarAdd = new JarEntry(entry.getInternal());
	        out.putNextEntry(jarAdd);
	        InputStream in = entry.getInputStream();
	        while (true) {
	          int nRead = in.read(buffer, 0, buffer.length);
	          if (nRead <= 0)
	            break;
	          out.write(buffer, 0, nRead);
	          LogProgress.updateProgress();
	        }
	        in.close();
	        if (new File(entry.getAbsolutePath()).getParentFile().getName().toUpperCase().equals("BOOT")) {
	        	String folder = new File(entry.getAbsolutePath()).getParentFile().getAbsolutePath();
				XMLBootDelivery xml = new XMLBootDelivery(new File(entry.getAbsolutePath()));
				Enumeration files = xml.getFiles();
				while (files.hasMoreElements()) {
					String bootname = (String)files.nextElement();
					logger.info("Adding "+bootname+" to the bundle");
				    jarAdd = new JarEntry("boot/"+bootname.replace(".sin", ".sinb").replace(".ta", ".tab"));
			        out.putNextEntry(jarAdd);
			        InputStream bin = new FileInputStream(new File(folder+File.separator+bootname));
			        while (true) {
			          int nRead = bin.read(buffer, 0, buffer.length);
			          if (nRead <= 0)
			            break;
			          out.write(buffer, 0, nRead);
			          LogProgress.updateProgress();
			        }
			        bin.close();
				}

	        }
		}
		out.close();
	    stream.close();
	    logger.info("Creating torrent file : "+ftf.getAbsolutePath()+".torrent");
	    List<URI> l1 = new ArrayList<URI>();
	    List<URI> l2 = new ArrayList<URI>();
	    List<URI> l3 = new ArrayList<URI>();
	    l1.add(new URI("udp://tracker.openbittorrent.com:80/announce"));
	    l2.add(new URI("udp://tracker.publicbt.com:80/announce"));
	    l3.add(new URI("udp://tracker.ccc.de:80/announce"));
	    List<List<URI>> parent = new ArrayList<List<URI>>();
	    parent.add(l1);
	    parent.add(l2);
	    parent.add(l3);
	    Torrent torrent = Torrent.create(ftf, null, new URI("udp://tracker.openbittorrent.com:80/announce"), parent, "FlashTool");
	    FileOutputStream fout =new FileOutputStream(new File(ftf.getAbsolutePath()+".torrent")); 
	    torrent.save(fout);
	    fout.flush();
	    fout.close();
	    logger.info("Torrent file creation finished");
	    LogProgress.initProgress(0);
	}

	private void saveEntry(BundleEntry entry) throws IOException {
		entry.saveTo(OS.getFolderFirmwaresPrepared());
	}
	
	public long getMaxLoaderProgress() {
		int maxdatasize=0;
		int maxloadersize=0;
		try {
			SinFile loader = new SinFile(getLoader().getAbsolutePath());
			if (loader.sinheader.getVersion()>=2) {
				maxloadersize=0x10000;
			}
			else {
				maxloadersize=0x1000;
			}
		}
		catch (Exception e) {
			maxloadersize=0x1000;
		}	
	    Iterator<Category> e = getMeta().getAllEntries(true).iterator();
	    long totalsize = 8;
	    while (e.hasNext()) {
	    	Category cat = e.next();
	    	Iterator<BundleEntry> icat = cat.getEntries().iterator();
	    	while (icat.hasNext()) {
	    	BundleEntry entry = getEntry(icat.next().getName());
	    	try {
	    		if (!entry.getName().toUpperCase().endsWith(".TA")) {
	    			long filecount = 0;
	    			SinFile s = null;
				    if (entry.getName().contains("loader")) {
			    		s = new SinFile(entry.getAbsolutePath());
				    	s.setChunkSize(maxloadersize);
		    			s.getSinHeader().setChunkSize(maxloadersize);
		    			filecount++;
				    }
		    		filecount = filecount + s.getNbChunks()+s.getSinHeader().getNbChunks();
		    		totalsize += filecount;
	    		}
	    	} catch (Exception ex) {}
	    	}
	    }
	    return totalsize;
	}

	public long getMaxProgress(int chunksize) {
		    Iterator<Category> e = getMeta().getAllEntries(true).iterator();
		    long totalsize = 15;
		    while (e.hasNext()) {
		    	Category cat = e.next();
		    	Iterator<BundleEntry> icat = cat.getEntries().iterator();
		    	while (icat.hasNext()) {
			    	BundleEntry entry = icat.next();
			    	try {
			    		if (!entry.getName().toUpperCase().endsWith(".TA")) {
			    			if (!entry.getName().toUpperCase().contains("LOADER")) {
			    				if (entry.getName().toUpperCase().endsWith("SIN")) {
						    		long filecount = 0;
						    		SinFile s = new SinFile(entry.getAbsolutePath());
						    		s.setChunkSize(chunksize);
						    		s.getSinHeader().setChunkSize(chunksize);
						    		filecount = filecount + s.getNbChunks()+s.getSinHeader().getNbChunks();
						    		totalsize += filecount;
			    				}
			    			}
			    		}
			    	} catch (Exception ex) {}
		    	}
		    }
		    if (hasCmd25()) totalsize = totalsize + 1;
		    return totalsize;
	}

	public boolean open() {
		try {
			logger.info("Preparing files for flashing");
			FileUtils.deleteDir(new File(OS.getFolderFirmwaresPrepared()));
			File f = new File(OS.getFolderFirmwaresPrepared());
			f.mkdir();
			logger.debug("Created the "+f.getName()+" folder");
			Iterator<Category>  entries = _meta.getAllEntries(true).iterator();
			while (entries.hasNext()) {
				Category categ = entries.next();
				Iterator<BundleEntry> icateg = categ.getEntries().iterator();
				while (icateg.hasNext()) {
					BundleEntry bf = icateg.next();
					bf.saveTo(OS.getFolderFirmwaresPrepared());
					if (bf.getCategory().equals("BOOT_DELIVERY")) {
						xmlb = new XMLBootDelivery(new File(bf.getAbsolutePath()));
						Enumeration files = xmlb.getFiles();
						while (files.hasMoreElements()) {
							String file = (String)files.nextElement();
							JarEntry j = _firmware.getJarEntry("boot/"+file.replace(".sin", ".sinb").replace(".ta", ".tab"));
							BundleEntry bent = new BundleEntry(_firmware,j);
							bent.saveTo(OS.getFolderFirmwaresPrepared());
						}
					}
				}
			}
			if (hasLoader())
				getLoader().saveTo(OS.getFolderFirmwaresPrepared());
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
    }
	
	public void close() {
		if (_firmware !=null) {
			File f=null;
			Enumeration<JarEntry> e=_firmware.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String outname = OS.getFolderFirmwaresPrepared()+File.separator+entry.getName();
				if (entry.getName().toUpperCase().endsWith(".SINB") || entry.getName().toUpperCase().endsWith(".TAB")) {
					outname = outname.replace(".sinb", ".sin").replace(".tab", ".ta");
				}
				f = new File(outname);
				if (f.exists())
					f.delete();
			}
			f = new File(OS.getFolderFirmwaresPrepared()+File.separator+"boot");
			if (f.exists())
				f.delete();
			f = new File(OS.getFolderFirmwaresPrepared());
			if (f.exists())
				f.delete();
			try {
				_firmware.close();
			}
			catch (IOException ioe) {ioe.printStackTrace();}
		}
	}
	
	public BundleMetaData getMeta() {
		return _meta;
	}

	public String getDevice() {
		return _device;
	}

	public String getVersion() {
		return _version;
	}
	
	public String toString() {
	    return "Bundle for " + Devices.getVariantName(_device) + ". FW release : " + _version + ". Customization : " + _branding;
	}
	
	public XMLBootDelivery getXMLBootDelivery() {
		return xmlb;
	}

}