package org.flashtool.jna.adb;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;

@Slf4j
public class APKUtility {

	public static String getPackageName(String apkname) throws Exception {

		try (ApkFile apkFile = new ApkFile(new File(apkname))) {
		    return apkFile.getApkMeta().getPackageName();
		}
	}

}
