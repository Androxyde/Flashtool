package org.flashtool.system;

import org.jdom2.input.SAXBuilder;
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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class XMLFwInfo  {

    private String project="";
    private String product="";
    private String model="";
    private String cda="";
    private String market="";
    private String operator="";
    private String network="";
    private String swVer="";
    private String cdfVer="";

	public XMLFwInfo(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		FileInputStream fin = new FileInputStream(xmlsource);
		Document document = builder.build(fin);
		fin.close();
		Iterator i = document.getRootElement().getChildren().iterator();
		while (i.hasNext()) {
			Element element = (Element)i.next();
			if (element.getName().equals("project"))
				project = element.getValue();
			if (element.getName().equals("product"))
				product = element.getValue();
			if (element.getName().equals("model"))
				model = element.getValue();
			if (element.getName().equals("cda"))
				cda = element.getValue();
			if (element.getName().equals("market"))
				market = element.getValue();
			if (element.getName().equals("operator"))
				operator = element.getValue();
			if (element.getName().equals("network"))
				network = element.getValue();
			if (element.getName().equals("swVer"))
				swVer = element.getValue();
			if (element.getName().equals("cdfVer"))
				cdfVer = element.getValue();
		}
	}

	public String getVersion() {
		return swVer;
	}
	
	public String getCDA() {
		return cda;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public String getModel() {
		return model;
	}
	
	public String getProduct() {
		return product;
	}
	
	public String getRevision() {
		return cdfVer;
	}

}