package org.flashtool.gui.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.flashtool.flashsystem.CommandFlasher.EmmcInfos;
import org.flashtool.flashsystem.CommandFlasher.UfsInfos;
import org.flashtool.gui.FileSelector;
import org.flashtool.gui.TARestore;
import org.flashtool.parsers.sin.SinFile;
import org.jdom2.input.SAXBuilder;

import lombok.extern.slf4j.Slf4j;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

@Slf4j
public class XMLPartitionDelivery {

	private Vector<String> partitions = new Vector<String>();
	static final Logger logger = LogManager.getLogger(XMLPartitionDelivery.class);
	String folder = "";
	UfsInfos ufs_infos = null;
	EmmcInfos emmc_infos=null;

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

	public void setEmmcInfos(EmmcInfos infos) {
		emmc_infos = infos;
	}

	public void setUfsInfos(UfsInfos infos) {
		ufs_infos = infos;
	}

	public String getMatchingFile(String match, Shell shell) {
		Vector<String> matched = new Vector<String>();
		Iterator<String> file = partitions.iterator();
		while(file.hasNext()) {
			String name = file.next();
			if (SinFile.getShortName(name).equals(match))
				matched.add(name);
		}
		if (matched.size()>0) {
			if (emmc_infos!=null) {
				Enumeration<String> e = matched.elements();
				while (e.hasMoreElements()) {
					String f=e.nextElement();
					if (!f.contains(String.valueOf(emmc_infos.getDiskSize()))) matched.removeElement(f);
				}
			} else if (ufs_infos!=null) {
				Enumeration<String> e = matched.elements();
				while (e.hasMoreElements()) {
					String f=e.nextElement();
					if (f.contains("LUN0"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(0)))) matched.removeElement(f);
					if (f.contains("LUN1"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(1)))) matched.removeElement(f);
					if (f.contains("LUN2"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(2)))) matched.removeElement(f);
					if (f.contains("LUN3"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(3)))) matched.removeElement(f);
					if (f.contains("LUN4"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(4)))) matched.removeElement(f);
					if (f.contains("LUN5"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(5)))) matched.removeElement(f);
					if (f.contains("LUN6"))
						if (!f.contains(String.valueOf(ufs_infos.getLunSize(6)))) matched.removeElement(f);
				}
			}
		}

		if (matched.size()==1)
			return (folder.length()>0?folder+"/":"")+matched.get(0);

		if (matched.size()>0) {
				String result=WidgetTask.getPartition(matched, shell);
				if (result.length() > 0) 
					return (folder.length()>0?folder+"/":"")+result;
				else
					return null;
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