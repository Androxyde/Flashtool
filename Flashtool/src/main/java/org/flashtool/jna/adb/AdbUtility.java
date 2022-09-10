package org.flashtool.jna.adb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import org.flashtool.system.Devices;
import org.flashtool.system.FTShell;
import org.flashtool.system.GlobalConfig;
import org.flashtool.system.OS;
import org.flashtool.system.ProcessBuilderWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdbUtility  {

	static Properties build = new Properties();
	static boolean rootnative=false;
	static boolean rootperms=false;
	
	private static String fsep = OS.getFileSeparator();
	private static String shellpath = OS.getFolderCustom()+fsep+"shells";
	private static String adbpath = OS.getPathAdb();
	private static String shpath ="";

	public static void resetRoot() {
		rootnative=false;
		rootperms=false;
	}
	
	public static String getShellPath() {
		return shellpath;
	}
	
	public static void setShellPath(String path) {
		shellpath = path;
	}
	
	public static boolean exists(String path) {
		try {
			ProcessBuilderWrapper command;
			command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "ls -l "+path},false);
			return !command.getStdOut().toLowerCase().contains("no such file or directory") && !command.getStdOut().toLowerCase().contains("permission denied");
		}
		catch (Exception e) {
			return false;
		}
	}

	public static boolean existsRoot(String path) {
		try {
			ProcessBuilderWrapper command;
			command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "su -c 'ls -l "+path+"'"},false);
			return !command.getStdOut().toLowerCase().contains("no such file or directory") && !command.getStdOut().toLowerCase().contains("permission denied");
		}
		catch (Exception e) {
			return false;
		}
	}

	public static String getShPath(boolean force) {
		if (shpath==null || force) {
			try {
				if (exists("/system/flashtool/sh"))
					shpath="/system/flashtool/sh";
				else {
					ProcessBuilderWrapper command1 = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "echo $0"},false);
					shpath = command1.getStdOut().trim();
				}
			}
			catch (Exception e) {
				shpath = "";
			}
		}
		log.debug("Default shell for scripts : "+shpath);
		return shpath;
	}

	public static boolean hasRootNative(boolean force) {
		try {
			if (!force && rootnative) return true;
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell","id"},false);
			rootnative=command.getStdOut().contains("uid=0");
		}
		catch (Exception e) {
		}
		return rootnative;
	}
	
	public static void forward(String type,String local, String remote) throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"forward "+type.toUpperCase()+":"+local+" "+type.toUpperCase()+":"+remote},false);
	}
	
	public static HashSet<String> listSysApps() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "ls /system/app/*apk"},false);
		String[] result = command.getStdOut().split("\n");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			String apk = result[i].substring(result[i].lastIndexOf('/')+1);
			if (!apk.contains("No such file or directory"))
				set.add(apk.substring(0,apk.lastIndexOf(".apk")+4));
		}
		command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "ls /system/app/*odex"},false);
		result = command.getStdOut().split("\n");
		for (int i=0;i<result.length;i++) {
			String apk = result[i].substring(result[i].lastIndexOf('/')+1);
			if (!apk.contains("No such file or directory"))
				set.add(apk.substring(0,apk.lastIndexOf(".odex")+5));
		}
		return set;
	}
	
	public static HashSet<String> listKernels() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "find /system/kernel -name 'kernel.desc' -type f"},false);
		String[] result = command.getStdOut().split("\n");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			int first = result[i].indexOf('/', 1);
			first = result[i].indexOf('/', first+1);
			int last = result[i].indexOf('/', first+1);
			set.add(result[i].substring(first+1,last));
		}
		return set;
	}

	public static HashSet<String> listRecoveries() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "find /system/recovery -name 'recovery.desc' -type f"},false);
		String[] result = command.getStdOut().split("\n");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			int first = result[i].indexOf('/', 1);
			first = result[i].indexOf('/', first+1);
			int last = result[i].indexOf('/', first+1);
			set.add(result[i].substring(first+1,last));
		}
		return set;
	}

	/*public static boolean isSystemMounted() throws Exception {
		if (systemmounted==null) {
			systemmounted = isMounted("/system");
		}
		return systemmounted.booleanValue();
	}*/
	
	public static void init() {
		rootnative=false;
		rootperms=false;
		shpath=null;
	}
	
	public static boolean isMounted(String mountpoint) throws Exception {
		boolean result = false;
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "mount"},false);
		Scanner sc = new Scanner(command.getStdOut());
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.contains(mountpoint)) {
				result = true;
			}
		}
		return result;
	}
	
	public static boolean hasSU() {
		boolean result = true;
		try {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "type su"},false);
		Scanner sc = new Scanner(command.getStdOut());
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.toLowerCase().contains("not found")) {
				result = false;
			}
		}
		}
		catch (Exception e) {
			return false;
		}
		return result;	
	}
	
	public static String getFilePerms(String file) {
		try {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "stat "+file},false);
			Scanner sc = new Scanner(command.getStdOut());
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains("Uid")) {
					return line;
				}
			}
			return "   ";
		}
		catch (Exception e) {
			return "   ";
		}
	}
	
	public static ByteArrayInputStream getBuildProp() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "cat /system/build.prop"},false);
		return new ByteArrayInputStream(command.getStdOut().getBytes());
	}
	
	public static boolean hasRootPerms() {
		if (hasRootNative(false)) return true;
		if (rootperms) return true;
		try {
			FTShell shell = new FTShell("checkperms");
			String result=shell.runRoot(false);
			while (result.toLowerCase().contains("segmentation fault")) {
				Thread.sleep(10000);
				result=shell.runRoot(false);
			}
			rootperms=result.contains("uid=0");
			return rootperms;
		}
		catch (Exception e) {
			return false;
		}
	}

	public static HashSet<String> ls(String basedir,String type) throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath+"shell", "find "+basedir+" -maxdepth 1 -type "+type},false);
		String[] result = command.getStdOut().split("\n");
		HashSet<String> set = new HashSet<String>();
		for (int i=0;i<result.length;i++) {
			if (result[i].substring(result[i].lastIndexOf('/')+1).length()>0 && !result[i].substring(result[i].lastIndexOf('/')+1).equals("/"))
				set.add(result[i].substring(result[i].lastIndexOf('/')+1));
		}
		return set;		
	}
	
	public static void pushExe(String source, String destfolder, String destname) throws Exception {
		AdbUtility.push(source, destfolder+"/"+destname);
		AdbUtility.run("chmod 755 "+destfolder+"/"+destname);
	}
	
	public static void uninstall(String apk, boolean silent) throws Exception {
		if (!silent)
			log.info("Uninstalling "+apk);
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"uninstall",apk},false);
	}

	public static void killServer() throws Exception {
		//log.info("Killing adb service");
		if (OS.getName().equals("windows")) {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"kill-server"},false);
		}
		else {
			ProcessBuilderWrapper command3 = new ProcessBuilderWrapper(new String[] {adbpath,"kill-server"},false);
			ProcessBuilderWrapper command1 = new ProcessBuilderWrapper(new String[] {"killall","adb"},false);
		}
	}

	public static void startServer() throws Exception {
			log.info("Starting adb service");
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"start-server"},false);
	}

	public static void push(String source, String destination) throws Exception {
		push(source, destination, true);
	}

	public static void restore(String source) throws Exception {
		File f = new File(source);
		if (!f.exists()) throw new AdbException(source+" : Not found");		
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"restore",f.getAbsolutePath()},false);
		if (command.getStatus()!=0) {
			throw new AdbException(command.getStdOut()+ " " + command.getStdErr());
		}
	}

	public static void push(String source, String destination, boolean logging) throws Exception {
		File f = new File(source);
		if (!f.exists()) throw new AdbException(source+" : Not found");
		if (logging) log.info("Pushing "+f.getAbsolutePath()+" to "+destination);
		else log.debug("Pushing "+f.getAbsolutePath()+" to "+destination);
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"push",f.getAbsolutePath(),destination},false);
		if (command.getStatus()!=0) {
			throw new AdbException(command.getStdOut()+ " " + command.getStdErr());
		}
	}

	
	public static String getBusyboxVersion(String path) {
		try {
			ProcessBuilderWrapper command;
			if (isMounted("/system")) {
				command = new ProcessBuilderWrapper(new String[] {adbpath,"shell",path+"/busybox"},false);
			}
			else {
				command = new ProcessBuilderWrapper(new String[] {adbpath,"shell","/sbin/busybox"},false);
			}
			Scanner sc = new Scanner(command.getStdOut());
			if (sc.hasNextLine()) {	
				String line = sc.nextLine();
				if (line.contains("BusyBox v1") && line.contains("multi-call")) return line;
			}
			return "";
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public static void mount(String mountpoint,String options, String type) throws Exception {
		if (hasRootNative(false)) {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell","mount -o "+options+" -t "+type+" "+mountpoint},false);			
		}
	}
	
	public static void umount(String mountpoint) throws Exception {
		if (hasRootNative(false)) {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "umount "+mountpoint},false);
		}		
	}
	
	public static void pull(String source, String destination) throws Exception {
		pull(source,destination,true);
	}

	public static void pull(String source, String destination, boolean logging) throws Exception {
		if (logging)
			log.info("Pulling "+source+" to "+destination);
		else
			log.debug("Pulling "+source+" to "+destination);
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"pull",source, destination},false);
		if (command.getStatus()!=0) {
			throw new AdbException(command.getStdOut()+ " " + command.getStdErr());
		}
	}

	public static String getKernelVersion(boolean hasbusybox) {
		try {
			String result = "";
			if (!hasbusybox) {
				AdbUtility.push(Devices.getCurrent().getBusybox(false), GlobalConfig.getProperty("deviceworkdir")+"/busybox1",false);
				AdbUtility.run("chmod 755 "+GlobalConfig.getProperty("deviceworkdir")+"/busybox1");
				result = run(GlobalConfig.getProperty("deviceworkdir")+"/busybox1 uname -r");
				run("rm -r "+GlobalConfig.getProperty("deviceworkdir")+"/busybox1");
			}
			else result = run("busybox uname -r");
			return result;
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public static String run(FTShell shell, boolean debug) throws Exception {
		push(shell.getPath(),GlobalConfig.getProperty("deviceworkdir")+"/"+shell.getName(),false);
		if (debug)
			log.debug("Running "+shell.getName());
		else
			log.info("Running "+shell.getName());
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell", "sh "+GlobalConfig.getProperty("deviceworkdir")+"/"+shell.getName()+";exit $?"},false);
		if (command.getStdOut().contains("FTError")) throw new Exception(command.getStdErr()+" "+command.getStdOut());
		return command.getStdOut();
	}

	public static String run(String com, boolean debug) throws Exception {
		if (debug)
			log.debug("Running "+ com + " command");
		else
			log.info("Running "+ com + " command");
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"shell",com},false);
		return command.getStdOut().trim();
	}

	public static String run(String com) throws Exception {
		return run(com,true);
	}

	public static String runRoot(FTShell shell) throws Exception {
		return runRoot(shell,true);
	}
	
	public static String runRoot(FTShell shell,boolean logging) throws Exception {
		FTShell s=new FTShell("sysrun");
		s.save();
		push(s.getPath(),GlobalConfig.getProperty("deviceworkdir")+"/sysrun",false);
		s.clean();
		push(shell.getPath(),GlobalConfig.getProperty("deviceworkdir")+"/runscript",false);
		if (logging)
			log.info("Running "+shell.getName()+"  as root thru sysrun");
		else
			log.debug("Running "+shell.getName()+"  as root thru sysrun");
		ProcessBuilderWrapper command;
		if (rootnative)
			command=new ProcessBuilderWrapper(new String[] {adbpath,"shell", "sh "+GlobalConfig.getProperty("deviceworkdir")+"/sysrun"},false);
		else
			command=new ProcessBuilderWrapper(new String[] {adbpath,"shell", "su -c 'sh "+GlobalConfig.getProperty("deviceworkdir")+"/sysrun'"},false);
		return command.getStdOut();
	}

	public static boolean Sysremountrw() throws Exception {
		log.info("Remounting system read-write");
		FTShell shell = new FTShell("remount");
		return !shell.runRoot(false).contains("FTError");
	}

	public static void clearcache() throws Exception {
		log.info("Clearing dalvik cache and rebooting");
		FTShell shell = new FTShell("clearcache");
		shell.runRoot(false);
	}

	public static void install(String apk) throws Exception {
		log.info("Installing "+apk);
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"install", "\""+apk+"\""},false);
		if (command.getStdOut().contains("Failure")) {
			uninstall(APKUtility.getPackageName(apk),true);
			command = new ProcessBuilderWrapper(new String[] {adbpath,"install","\""+apk+"\""},false);
			if (command.getStdOut().contains("Failure")) {
				Scanner sc = new Scanner(command.getStdOut());
				sc.nextLine();
				log.error(sc.nextLine());
			}
		}
	}

	public static void scanStatus() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"status-window"},false);
	}

	public static String getStatus() {
		try {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"get-state"},false);
		if (command.getStdErr().contains("unauthorized")) return "adb_unauthorized";
		if (command.getStdErr().contains("error")) return "normal";
		if (command.getStdOut().contains("device")) return "adb";
		return "none";
		}
		catch (Exception e) {
			return "none";
		}
	}
	
	public static boolean isConnected() {
		try {
			log.debug("Testing if device is connected");
			return AdbUtility.getDevices().hasMoreElements();
		}
		catch (Exception e) {
			return false;
		}
	}
/*	public static boolean isConnected() {
		try {
			log.info("Searching Adb Device");
			String res =Device.AdbId();
			if (res.equals("ErrNotPlugged")) {
				MyLogger.error("Please plug your device with USB Debugging and Unknown sources on");
				return false;
			}
			else if (res.equals("ErrDriverError")) {
				MyLogger.error("ADB drivers are not installed");
				return false;
			}
			boolean connected = false;
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(adbpath+" devices");
			command.run();
			String[] result = command.getStdOut().split("\n");
			for (int i=1;i<result.length; i++) {
				connected=result[i].contains("device");
			}
			if (!connected) {
				MyLogger.error("Please plug your device with USB Debugging and Unknown sources turned on");
			}
			return connected;
		}
		catch (Exception e) {
			MyLogger.error("Please plug your device with USB Debugging and Unknown sources turned on");
			return false;
		}
	}*/

	public static Enumeration<String> getDevices() {
		Vector<String> v = new Vector<String>();
		try {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"devices"},false);
			Scanner sc = new Scanner(command.getStdOut());
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.startsWith("List")) {
					String[] content=line.split("\t");
					if (content[content.length-1].contains("device"))
						v.add(content[0]);
				}
			}
		}
		catch (Exception e) {}
		return v.elements();
	}
	
	public static long rawBackup(String source, String destination) {
		String md5source = getMD5(source);
		String result="";
		long transferred=0L;
		try {
			result = AdbUtility.run("su -c 'dd if="+source+" of="+destination+" && sync && sync && sync && sync && chmod 444 "+destination+"'");
		}
		catch (Exception e) {
		}
		if (!result.contains("bytes transferred")) return 0L;
		Scanner sc = new Scanner(result);
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.contains("bytes")) {
				transferred = Long.parseLong(line.split(" ")[0]);
			}
		}
		String md5destination = getMD5(destination);
		if (!md5source.equals(md5destination)) return 0L;
		return transferred;
	}
	
	public static long getSizeOf(String source) {
		String result = "";
		try {
			result = AdbUtility.run("su -c 'busybox stat "+source+"'");
		}
		catch (Exception e) {
			result = "";
		}
		if (!result.contains("Size")) return 0L;
		Scanner sc = new Scanner(result);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.contains("Size")) {
				return Long.parseLong(line.substring(line.indexOf("Size"),line.indexOf("Blocks")).replace("Size:", "").trim());
			}
		}
		return 0L;
	}

	public static String getMD5(String source) {
		try {
			String md5 = AdbUtility.run("su -c 'export PATH=$PATH:/data/local/tmp;busybox md5sum "+source+"'").split(" ")[0].toUpperCase().trim();
			if (md5==null) return "";
			if (md5.length()!=32) md5="";
			return md5;
		}
		catch (Exception e) {
			return "";
		}
	}
	
	public static void antiRic() {
		try {
			Scanner scan = new Scanner(AdbUtility.run("ps"));
			String path = "";
			String pid = "";
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.contains("bin/ric")) {
					String[] fields = line.split("\\s+");
					pid = fields[1];
					path = fields[fields.length-1];
				}
			}
			scan.close();
			if (path.length()>0) {
				log.info("Stopping ric service");
				AdbUtility.run("su -c 'mount -o remount,rw / && mv "+path+" "+path+"c && mount -o remount,ro / && kill -9 "+pid+"'");
				log.info("ric service stopped successfully");
			}
		}
		catch (Exception e) {
		}
	}
}