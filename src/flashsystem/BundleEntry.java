package flashsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;

import org.apache.log4j.Logger;
import org.logger.MyLogger;
import org.system.OS;

public class BundleEntry {

	private File fileentry=null;
	private JarEntry jarentry=null;
	private Bundle _bundle;
	private String _name;
	private String _categorie;
	private static Logger logger = Logger.getLogger(BundleEntry.class);
	
	public BundleEntry(File f,String name) {
		fileentry = f;
		_name = name;
	}
	
	public BundleEntry(Bundle b, JarEntry j) {
		jarentry=j;
		_bundle = b;
		_name = j.getName();
	}
	
	public void setName(String name) {
		_name = name;
	}
	public InputStream getInputStream() throws FileNotFoundException, IOException {
		if (fileentry!=null) {
			logger.info("Streaming from file : "+fileentry.getPath());
			return new FileInputStream(fileentry);
		}
		else {
			logger.debug("Streaming from jar entry : "+jarentry.getName());
			return _bundle.getImageStream(jarentry);
		}
	}
	
	public String getName() {
		return _name;
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
	
	public void setCategorie(String categorie) {
		_categorie=categorie;
	}
	
	public String getCategorie() {
		return _categorie;
	}

	public String getFolder() {
		return new File(getAbsolutePath()).getParent();
	}
}
