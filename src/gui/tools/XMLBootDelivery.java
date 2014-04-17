package gui.tools;

import org.apache.commons.io.monitor.FileEntry;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.logger.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

public class XMLBootDelivery {

	private Vector<XMLBootConfig> bootconfigs = new Vector<XMLBootConfig>();
	private String bootversion;

	public XMLBootDelivery(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		FileInputStream fin = new FileInputStream(xmlsource);
		Document document = builder.build(fin);
		String spaceid = document.getRootElement().getAttribute("SPACE_ID").getValue();
		bootversion = document.getRootElement().getAttribute("VERSION").getValue().replaceAll(spaceid, "").trim();
		if (bootversion.startsWith("_")) bootversion = bootversion.substring(1);
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element e = i.next();
			XMLBootConfig c = new XMLBootConfig(e.getAttributeValue("NAME"));
			c.setTA(e.getChild("BOOT_CONFIG").getChild("FILE").getAttributeValue("PATH"));
			Iterator<Element> files = e.getChild("BOOT_IMAGES").getChildren().iterator();
			while (files.hasNext()) {
				c.addFile(files.next().getAttributeValue("PATH"));
			}
			c.setAttributes(e.getChild("ATTRIBUTES").getAttributeValue("VALUE"));
			bootconfigs.add(c);
		}
		fin.close();
	}

	public boolean mustUpdate(String bootver) {
		MyLogger.getLogger().info("Phone boot version : "+bootver+". Boot delivery version : "+getBootVersion());
		bootver = bootver.toUpperCase();
		String deliveryver = getBootVersion().toUpperCase();
		if (bootver.equals(deliveryver)) return false;
		return true;
	}

	public String getBootVersion() {
		return bootversion;
	}
	
	public Enumeration<XMLBootConfig> getBootConfigs() {
		return bootconfigs.elements();
	}
	
	public Enumeration<Object> getFiles() {
		Properties flist = new Properties();
		Enumeration<XMLBootConfig> e = bootconfigs.elements();
		while (e.hasMoreElements()) {
			XMLBootConfig bc = e.nextElement();
			flist.setProperty(bc.getTA(), bc.getTA());
			Iterator<String>fl = bc.getFiles().iterator();
			while (fl.hasNext()) {
				String f = fl.next();
				flist.setProperty(f, f);
			}
		}
        return flist.keys();
	}

}