package org.adb;

import java.io.File;

import net.dongliu.apk.parser.ApkFile;

public class APKUtility {

	public static String getPackageName(String apkname) throws Exception {

		try (ApkFile apkFile = new ApkFile(new File(apkname))) {
		    return apkFile.getApkMeta().getPackageName();
		}
	}

}
