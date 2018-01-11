package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.input.SAXBuilder;
import org.sinfile.parsers.SinFile;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class XMLPartitionDelivery {

	private Vector<String> partitions = new Vector<String>();
	static final Logger logger = LogManager.getLogger(XMLPartitionDelivery.class);
	String folder = "";

	public XMLPartitionDelivery(File xmlsource) throws IOException, JDOMException {
		SAXBuilder builder = new SAXBuilder();
		FileInputStream fin = new FileInputStream(xmlsource);
		Document document = builder.build(fin);
		document.getRootElement().getChild("PARTITION_IMAGES");
		//String spaceid = document.getRootElement().getAttribute("SPACE_ID").getValue();
		Iterator<Element> i=document.getRootElement().getChild("PARTITION_IMAGES").getChildren().iterator();
		while (i.hasNext()) {
			Element e = i.next();
			partitions.addElement(e.getAttributeValue("PATH"));
		}
		fin.close();
	}

	public String getMatchingFile(String match) {
		Vector<String> matched = new Vector<String>();
		Iterator<String> file = partitions.iterator();
		while(file.hasNext()) {
			String name = file.next();
			if (name.equals(match))
				matched.add(name);
		}
		if (matched.size()==1)
			return (folder.length()>0?folder+"/":"")+matched.get(0);
		return null;
	}

	public void setFolder(String folder) {
		this.folder=folder+File.separator+"partition";
	}

	public Enumeration<String> getFiles() {
        return partitions.elements();
	}

}