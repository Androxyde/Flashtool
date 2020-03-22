package flashsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sinfile.parsers.SinFile;
import org.system.OS;

public class BundleEntry {

	private File fileentry = null;
	private JarFile jarfile = null;
	private JarEntry jarentry = null;
	private String _category = "";
	private String _internal = "";

	static final Logger logger = LogManager.getLogger(BundleEntry.class);

	private String getExtension() {
		if (fileentry!=null) {
			int extpos = fileentry.getName().lastIndexOf(".");
			if (extpos > -1) {
				return fileentry.getName().substring(extpos);
			}
			return "";
		}
		else {
			int extpos = jarentry.getName().lastIndexOf(".");
			if (extpos > -1) {
				return jarentry.getName().substring(extpos);
			}
			return "";
		}
	}
	
	public BundleEntry(File f) {
		fileentry = f;
		if (f.getName().toUpperCase().endsWith("FSC"))
			_category = "FSC";
		else
			_category = SinFile.getShortName(fileentry.getName()).toUpperCase();
		_internal = org.sinfile.parsers.SinFile.getShortName(fileentry.getName())+getExtension();
	}

	public BundleEntry(JarFile jf, JarEntry j) {
		jarentry = j;
		jarfile = jf;
		if (jarentry.getName().toUpperCase().endsWith("FSC")) {
			_category = "FSC";
		}
		else
			_category = SinFile.getShortName(jarentry.getName()).toUpperCase();
		_internal = org.sinfile.parsers.SinFile.getShortName(jarentry.getName())+getExtension();
	}

	public InputStream getInputStream() throws FileNotFoundException, IOException {
		if (fileentry!=null) {
			logger.info("Streaming from file : "+fileentry.getPath());
			return new FileInputStream(fileentry);
		}
		else {
			logger.debug("Streaming from jar entry : "+jarentry.getName());
			return jarfile.getInputStream(jarentry);
		}
	}

	public String getName() {
		if (this.isJarEntry()) return jarentry.getName();
		return fileentry.getName();
	}

	public String getInternal() {
		return _internal;
	}

	public String getAbsolutePath() {
		return fileentry.getAbsolutePath();
	}

	public boolean isJarEntry() {
		return jarentry!=null;
	}

	public String getMD5() {
		if (fileentry!=null) return OS.getMD5(fileentry);
		else return "";
	}

	public long getSize() {
		if (fileentry!=null) return fileentry.length();
		else return jarentry.getSize();
	}

	public String getCategory() {
		return _category;
	}

	public String getFolder() {
		return new File(getAbsolutePath()).getParent();
	}

	public void saveTo(String folder) throws FileNotFoundException, IOException {
			if (isJarEntry()) {
				logger.debug("Saving entry "+getName()+" to disk");
				InputStream in = getInputStream();
				String outname = folder+File.separator+getName();
				if (outname.endsWith("tab") || outname.endsWith("sinb")) outname = outname.substring(0, outname.length()-1);
				fileentry=new File(outname);
				fileentry.getParentFile().mkdirs();
				logger.debug("Writing Entry to "+outname);
				OS.writeToFile(in, fileentry);
				in.close();
			}
	}

}