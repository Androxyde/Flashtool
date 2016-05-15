package org.system;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class XMLUpdate  {

    private String noerase="";

	public XMLUpdate(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		FileInputStream fin = new FileInputStream(xmlsource);
		Document document = builder.build(fin);
		fin.close();
		Iterator i = document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element element = (Element)i.next();
			if (element.getName().equals("NOERASE"))
				noerase = noerase + element.getValue() + ",";
		}
	}

	public String getNoErase() {
		if (noerase.length()==0) return noerase;
		return noerase.substring(0, noerase.length()-1);
	}

}