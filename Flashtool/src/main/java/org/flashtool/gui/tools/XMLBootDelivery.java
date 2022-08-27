package org.flashtool.gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.gui.TARestore;
import org.jdom2.input.SAXBuilder;

import lombok.extern.slf4j.Slf4j;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

@Slf4j
public class XMLBootDelivery {

	private Vector<XMLBootConfig> bootconfigs = new Vector<XMLBootConfig>();
	private String bootversion;
	static final Logger logger = LogManager.getLogger(XMLBootDelivery.class);

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
			if (e.getChild("BOOT_CONFIG").getChild("FILE") != null) {
				c.setTA(e.getChild("BOOT_CONFIG").getChild("FILE").getAttributeValue("PATH"));
			}
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
		logger.info("Phone boot version : "+bootver+". Boot delivery version : "+getBootVersion());
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
			if (bc.getTA().length()>0)
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