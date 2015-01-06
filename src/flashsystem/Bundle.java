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

import com.turn.ttorrent.common.Torrent;

public final class Bundle {

	private JarFile _firmware;
    private boolean _simulate=false;
    private Properties bundleList=new Properties();
    private String _version;
    private String _branding;
    private String _device;
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
    
    public Bundle(String path, int type) {
    	feed(path,type);
    }

    public void setMeta(BundleMetaData meta) {
    	_meta = meta;
    	feedFromMeta();
    }
    
    public Bundle(String path, int type, BundleMetaData meta) {
    	_meta = meta;
    	feed(path,type);
    }
    
    private void feed(String path, int type) {
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
			_cmd25 = _firmware.getManifest().getMainAttributes().getValue("cmd25");
			Enumeration<JarEntry> e = _firmware.entries();
			while (e.hasMoreElements()) {
				BundleEntry entry = new BundleEntry(this,e.nextElement());
				if (!entry.getName().toUpperCase().startsWith("BOOT/")) {
				if (entry.getName().toUpperCase().endsWith("SIN") || entry.getName().toUpperCase().endsWith("TA") || entry.getName().toUpperCase().endsWith("XML")) {
					try {
						_meta.process(entry.getName(), "");
					}
					catch (Exception e1) {e1.printStackTrace();
					}
					bundleList.put(entry.getName(), entry);
					logger.debug("Added this entry to the bundle list : "+entry.getName());
				}
				}
			}
		}
		catch (IOException ioe) {
			logger.error("Cannot open the file "+path);
		}
	}

	private void feedFromFolder(String path) {
		File[] list = (new File(path)).listFiles(new FirmwareFileFilter());
		for (int i=0;i<list.length;i++) {
			BundleEntry entry = new BundleEntry(list[i],list[i].getName());
			bundleList.put(entry.getName(), entry);
			logger.debug("Added this entry to the bundle list : "+entry.getName());
		}
	}

	private void feedFromMeta() {
		bundleList.clear();
		Enumeration<String> all = _meta.getAllEntries(true);
		while (all.hasMoreElements()) {
			String name = all.nextElement();
			BundleEntry entry = new BundleEntry(new File(_meta.getPath(name)),name);
			bundleList.put(entry.getName(), entry);
			logger.debug("Added this entry to the bundle list : "+entry.getName());
		}
	}

	public void setLoader(File loader) {
		try {
			if (_meta!=null)
				_meta.process("loader.sin", loader.getAbsolutePath());
		}
		catch (Exception e) {
		}
		BundleEntry entry = new BundleEntry(loader,"loader.sin");
		bundleList.put("loader.sin", entry);
	}

	public void setSimulate(boolean simulate) {
		_simulate = simulate;
	}

	public BundleEntry getEntry(String name) {
		return (BundleEntry)bundleList.get(name);
	}
	
	public Enumeration <BundleEntry> allEntries() {
		Vector<BundleEntry> v = new Vector<BundleEntry>();
		Enumeration<Object> e = bundleList.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			BundleEntry entry = (BundleEntry)bundleList.get(key);
			v.add(entry);
		}
		return v.elements();
	}

	public InputStream getImageStream(JarEntry j) throws IOException {
		return _firmware.getInputStream(j);
	}
		
	public boolean hasTA() {
		return _meta.hasCategorie("TA",true);
	}

	public boolean isBootDeliveryFlashed() {
		return bootdeliveryflashed;
	}
	
	public void setBootDeliveryFlashed(boolean flashed) {
		bootdeliveryflashed=flashed;
	}
	
	public BundleEntry getLoader() throws IOException, FileNotFoundException {
		return (BundleEntry)bundleList.get("loader.sin");
	}

	public BundleEntry getBootDelivery()  throws IOException, FileNotFoundException {
		return (BundleEntry)bundleList.get("boot_delivery.xml");
	}
	
	public boolean hasLoader() {
		return _meta.hasCategorie("LOADER",false);
	}

	public boolean hasBootDelivery() {
		return _meta.hasCategorie("BOOTBUNDLE",true);
	}

	public boolean hasBoot() {
		return _meta.hasCategorie("BOOT",true);
	}

	public BundleEntry getPartition() throws IOException, FileNotFoundException {
		return (BundleEntry)bundleList.get(_meta.getEntriesOf("PARTITION",true).nextElement());
	}

	public boolean hasPartition() {
		return _meta.hasCategorie("PARTITION",true);
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
		File ftf = new File(OS.getFirmwaresFolder()+File.separator+_device+"_"+_version+"_"+_branding+".ftf");
		byte buffer[] = new byte[10240];
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Manifest-Version: 1.0\n");
		sbuf.append("Created-By: FlashTool\n");
		sbuf.append("version: "+_version+"\n");
		sbuf.append("branding: "+_branding+"\n");
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
			String name = entry.getName();
			int S1pos = name.toUpperCase().indexOf("_S1");
			if (S1pos > 0) name = name.substring(0,S1pos)+".sin";
			logger.info("Adding "+name+" to the bundle");
		    JarEntry jarAdd = new JarEntry(name);
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

	private void saveEntry(BundleEntry entry, boolean addmeta) throws IOException {
		if (entry.isJarEntry()) {
			logger.debug("Saving entry "+entry.getName()+" to disk");
			InputStream in = entry.getInputStream();
			String outname = OS.getFirmwaresPreparedFolder()+File.separator+entry.getName();
			new File(outname).getParentFile().mkdirs();
			logger.debug("Writing Entry to "+outname);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(outname));
			byte[] buffer = new byte[10240];
			int len;
			while((len = in.read(buffer)) >= 0)
				out.write(buffer, 0, len);
			in.close();
			out.close();
			if (addmeta)
				bundleList.put(entry.getName(), new BundleEntry(new File(outname),entry.getName()));
		}
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
	    Enumeration<String> e = getMeta().getAllEntries(true);
	    long totalsize = 8;
	    while (e.hasMoreElements()) {
	    	BundleEntry entry = getEntry(e.nextElement());
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
	    return totalsize;
	}

	public long getMaxProgress(int chunksize) {
		    Enumeration<String> e = getMeta().getAllEntries(true);
		    long totalsize = 15;
		    while (e.hasMoreElements()) {
		    	BundleEntry entry = getEntry(e.nextElement());
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
		    if (hasCmd25()) totalsize = totalsize + 1;
		    if (hasPartition()) totalsize = totalsize + 2;
		    return totalsize;
	}

	public boolean open() {
		try {
			logger.info("Preparing files for flashing");
			File f = new File(OS.getFirmwaresPreparedFolder());
			if (f.exists()) {
				File[] f1 = f.listFiles();
				for (int i = 0;i<f1.length;i++) {
					if (!f1[i].delete()) throw new Exception("Cannot delete "+f1[i].getAbsolutePath());
				}
				if (!f.delete()) throw new Exception("Cannot delete "+f.getAbsolutePath());
			}
			f.mkdir();
			logger.debug("Created the "+f.getName()+" folder");
			Enumeration<String> entries = _meta.getAllEntries(true);
			while (entries.hasMoreElements()) {
				String entry = entries.nextElement();
				saveEntry(getEntry(entry),true);
				if (entry.toUpperCase().equals("BOOT_DELIVERY.XML")) {
					BundleEntry xmlentry = getEntry(entry);
					xmlb = new XMLBootDelivery(new File(xmlentry.getAbsolutePath()));
					Enumeration files = xmlb.getFiles();
					while (files.hasMoreElements()) {
						String file = (String)files.nextElement();
						JarEntry j = _firmware.getJarEntry("boot/"+file.replace(".sin", ".sinb").replace(".ta", ".tab"));
						BundleEntry bent = new BundleEntry(this,j);
						bent.setName(bent.getName().replace(".sinb", ".sin").replace(".tab", ".ta"));
						saveEntry(bent,false);
					}
				}
			}
			if (hasLoader())
				saveEntry(getLoader(),true);
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
				String outname = OS.getFirmwaresPreparedFolder()+File.separator+entry.getName();
				if (entry.getName().toUpperCase().endsWith(".SINB") || entry.getName().toUpperCase().endsWith(".TAB")) {
					outname = outname.replace(".sinb", ".sin").replace(".tab", ".ta");
				}
				f = new File(outname);
				if (f.exists())
					f.delete();
			}
			f = new File(OS.getFirmwaresPreparedFolder()+File.separator+"boot");
			if (f.exists())
				f.delete();
			f = new File(OS.getFirmwaresPreparedFolder());
			if (f.exists())
				f.delete();
			try {
				_firmware.close();
			}
			catch (IOException ioe) {ioe.printStackTrace();}
		}
	}
	
	public void removeEntry(String name) {
		bundleList.remove(name);
	}
	
	public BundleMetaData getMeta() {
		return _meta;
	}

	public String toString() {
	    return "Bundle for " + Devices.getVariantName(_device) + ". FW release : " + _version + ". Customization : " + _branding;
	}
	
	public XMLBootDelivery getXMLBootDelivery() {
		return xmlb;
	}

}