package org.flashtool.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.flashtool.jna.adb.AdbUtility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FTShell {

	File _file;
	File _runfile=null;
	String _name;
	String content;
	String fsep = OS.getFileSeparator();
	
	public FTShell(File file) throws Exception {
		init(file);
	}

	public void init(File file) throws Exception {
		_file = file;
		_name = file.getName();
		FileInputStream fin = new FileInputStream(_file);
		content = IOUtils.toString(fin, "ISO-8859-1");
		fin.close();
		setProperty("DEVICEWORKDIR",GlobalConfig.getProperty("deviceworkdir"));
		setProperty("SHELLPATH",AdbUtility.getShPath(false));
	}
	
	public FTShell(String shell) throws Exception {
		init(new File(AdbUtility.getShellPath()+fsep+shell));
	}
	
	public void save() throws Exception {
		_runfile=new File(_file.getAbsoluteFile()+"_work");
		FileOutputStream fout = new FileOutputStream(_runfile);
		IOUtils.write(content, fout, "ISO-8859-1");
		fout.flush();
		fout.close();
	}
	
	public void setProperty(String property, String value) {
		content=content.replaceAll(property, value);
	}
	
	public String getName() {
		return _name;
	}
	
	public String getPath() {
		if (_runfile!=null)
			return _runfile.getAbsolutePath();
		return _file.getAbsolutePath();
	}
	
	public void clean() {
		if (_runfile!=null)
			_runfile.delete();
	}
	
	public String run(boolean log) throws Exception {
		save();
		String result = AdbUtility.run(this,log);
		clean();
		return result;
	}
	
	public String runRoot() throws Exception {
		save();
		boolean datamounted=true;
		boolean systemmounted=true;
		if (Devices.getCurrent().isRecovery()) {
			datamounted = AdbUtility.isMounted("/data");
			systemmounted = AdbUtility.isMounted("/data");
			if (!datamounted) AdbUtility.mount("/data", "rw", "yaffs2");
			if (!systemmounted) AdbUtility.mount("/system", "ro", "yaffs2");
		}
		String result = AdbUtility.runRoot(this);
		clean();
		if (Devices.getCurrent().isRecovery()) {
			if (!datamounted) AdbUtility.umount("/data");
			if (!systemmounted) AdbUtility.umount("/system");
		}
		return result;
	}

	public String runRoot(boolean debug) throws Exception {
		save();
		boolean datamounted=true;
		boolean systemmounted=true;
		if (Devices.getCurrent().isRecovery()) {
			datamounted = AdbUtility.isMounted("/data");
			systemmounted = AdbUtility.isMounted("/data");
			if (!datamounted) AdbUtility.mount("/data", "rw", "yaffs2");
			if (!systemmounted) AdbUtility.mount("/system", "ro", "yaffs2");
		}
		String result = AdbUtility.runRoot(this,debug);
		clean();
		if (Devices.getCurrent().isRecovery()) {
			if (!datamounted) AdbUtility.umount("/data");
			if (!systemmounted) AdbUtility.umount("/system");
		}
		return result;
	}

}