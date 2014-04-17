package org.adb;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.AXmlResourceParser;

public class APKUtility {

	public static String getPackageName(String apkname) throws Exception {
		ZipInputStream inStream = new ZipInputStream(new FileInputStream(apkname));
        ZipEntry entry;
        while (((entry = inStream.getNextEntry()) != null)) {
        	if (entry.getName().equals("AndroidManifest.xml")) {
    			AXmlResourceParser parser=new AXmlResourceParser();
    			parser.open(inStream);
    			while (true) {
    				int type=parser.next();
    				if (type==XmlPullParser.START_TAG) {
    					for (int i=0;i!=parser.getAttributeCount();++i) {
    						if (parser.getAttributeName(i).equals("package"))
    							return parser.getAttributeValue(i);
    					}
    					break;
    				}
    			}
    			break;
        	}
        }
        return "";
	}

}
