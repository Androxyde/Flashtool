package gui.tools;

import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Element;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class XMLRebrand {

	private Document document;

	public XMLRebrand(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		document = builder.build(xmlsource);
	}

	public Enumeration<String> getDevices() {
		Vector<String> devs = new Vector<String>();
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
        while (i.hasNext()) {
        	Element elem = i.next();
        	devs.add(elem.getAttributeValue("name"));
        }
        return devs.elements();
	}
	
	public Enumeration<String> getBrands(String device) {
		Vector<String> brands = new Vector<String>();
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element elem = i.next();
			if (elem.getAttributeValue("name").equals(device)) {
				Iterator<Element> i1=elem.getChildren().iterator();
		        while (i1.hasNext()) {
		        	Element elem1 = i1.next();
		        	brands.add(elem1.getAttributeValue("name"));
		        }
		        return brands.elements();
			}
		}
		return null;
	}
	
	public boolean hasId(String device,String id) {
		boolean hasid = false;
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element elem = i.next();
			if (elem.getAttributeValue("name").equals(device)) {
				Iterator<Element> i1=elem.getChildren().iterator();
		        while (i1.hasNext()) {
		        	Element elem1 = i1.next();
		        	if (elem1.getAttributeValue("value").equals(id))
		        		hasid = true;
		        }
			}
		}
		return hasid;
	}
	
	public String getId(String device, String brand) {
		Iterator<Element> i=document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element elem = i.next();
			if (elem.getAttributeValue("name").equals(device)) {
				Iterator<Element> i1=elem.getChildren().iterator();
		        while (i1.hasNext()) {
		        	Element elem1 = i1.next();
		        	if (elem1.getAttributeValue("name").equals(brand))
		        		return elem1.getAttributeValue("value");
		        }
			}
		}
		return null;
	}

}