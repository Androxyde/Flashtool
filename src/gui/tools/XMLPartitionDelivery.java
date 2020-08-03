package gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.input.SAXBuilder;
import org.sinfile.parsers.SinFile;

import flashsystem.CommandFlasher.UfsInfos;
import gui.FileSelector;

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
	UfsInfos ufs_infos = null;

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

	public void setUfsInfos(UfsInfos infos) {
		ufs_infos = infos;
		if (ufs_infos != null) {  
		Enumeration<String> files = partitions.elements();
		while (files.hasMoreElements()) {
			String file=files.nextElement();
			if (!file.contains("LUN0_X") && !file.contains("LUN1_X") && !file.contains("LUN2_X") && !file.contains("LUN3_X")) {
				if ( file.contains("LUN0") ) {
                  if (!file.contains(String.valueOf(ufs_infos.getLunSize(0)))) {
            		partitions.remove(file);
                  }	
               }
               if ( file.contains("LUN1") ) {
            	   if (!file.contains(String.valueOf(ufs_infos.getLunSize(1)))) {
            		   partitions.remove(file);
            	   }	
               }
               if ( file.contains("LUN2") ) {
            	   if (!file.contains(String.valueOf(ufs_infos.getLunSize(2)))) {
            		   partitions.remove(file);
            	   }	
               }
               if ( file.contains("LUN3") ) {
            	   if (!file.contains(String.valueOf(ufs_infos.getLunSize(3)))) {
            		   partitions.remove(file);
            	   }	
               }
			}
		}
		}
	}

	public String getMatchingFile(String match, Shell shell) {
		Vector<String> matched = new Vector<String>();
		Iterator<String> file = partitions.iterator();
		while(file.hasNext()) {
			String name = file.next();
			if (SinFile.getShortName(name).equals(match))
				matched.add(name);
		}
		if (matched.size()==1)
			return (folder.length()>0?folder+"/":"")+matched.get(0);
		if (ufs_infos==null) {
			if (matched.size()>0) {
				String result=WidgetTask.getPartition(matched, shell);
				if (result.length() > 0) 
					return (folder.length()>0?folder+"/":"")+result;
				else
					return null;
			}
		}
		return null;
	}

	public void setFolder(String folder) {
		this.folder=folder+File.separator+"partition";
	}

	public Enumeration<String> getFiles() {
        return partitions.elements();
	}

}