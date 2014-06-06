package org.adb;

import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.logger.MyLogger;
import org.system.OS;
import org.system.ProcessBuilderWrapper;
import org.system.RunOutputs;

public class FastbootUtility {

	private static String adbpath = OS.getAdbPath();
	private static String fastbootpath = OS.getFastBootPath();
	private static Logger logger = Logger.getLogger(FastbootUtility.class);
	
	public static void adbRebootFastboot() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {adbpath,"reboot", "bootloader"},false);
	}
	
	public static Enumeration<String> getDevices() {
		Vector<String>  v = new Vector<String>();
		try {
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath, "devices"},false);
			Scanner sc = new Scanner(command.getStdOut());
			while (sc.hasNextLine()) {
				v.add(sc.nextLine().split("\t")[0]);
			}
		}
		catch (Exception e) {
		}
		return v.elements();
	}

	public static RunOutputs hotBoot(String bootimg) throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"boot", bootimg},false);
		return command.getOutputs();
	}
	
	public static RunOutputs flashBoot(String bootimg) throws Exception {
		ProcessBuilderWrapper pbd = new ProcessBuilderWrapper(new String[] {fastbootpath,"flash", "boot", bootimg},true);
		return pbd.getOutputs();
	}

	public static RunOutputs flashSystem(String systemimg) throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"flash", "system", systemimg},false);
		return command.getOutputs();
	}

	public static RunOutputs unlock(String key) throws Exception {
		logger.info("Unlocking phone using key "+key);
		ProcessBuilderWrapper pbd = new ProcessBuilderWrapper(new String[] {fastbootpath,"-i", "0xfce","oem", "unlock","0x"+key },true);
		return pbd.getOutputs();
	}
	
	public static void rebootDevice() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"reboot"},false);
	}
	
	public static void rebootFastboot() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"reboot-bootloader"},false);
	}
	
	public static void wipeDataCache() throws Exception {
		// currently there seems to be some issue executing this
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"-w"},false);
	}
	
	public static RunOutputs getDeviceInfo() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"devices"},false);
		return command.getOutputs();
	}
	
	public static RunOutputs getFastbootVerInfo() throws Exception {
		ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {fastbootpath,"-i", "0x0fce", "getvar", "version"},false);
		return command.getOutputs();
	}

	public static void killFastbootWindows() {
		try {
			ProcessBuilderWrapper fastboot = new ProcessBuilderWrapper(new String[] {"taskkill", "/F", "/T", "/IM", "fastboot*"},false);
		}
		catch (Exception e) {
		}
	}

}
