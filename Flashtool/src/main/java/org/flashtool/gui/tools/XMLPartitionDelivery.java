package org.flashtool.gui.tools;

import org.eclipse.swt.widgets.Shell;
import org.flashtool.flashsystem.CommandFlasher.EmmcInfos;
import org.flashtool.flashsystem.CommandFlasher.UfsInfos;
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
import java.util.Iterator;
import java.util.Vector;


@Slf4j
public class XMLPartitionDelivery {

	private Vector<String> partitions = new Vector<String>();
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
		Vector<String> keep = new Vector<String>();

		for (String name:partitions) {
			if (SinFile.getShortName(name).equals(match))
				matched.add(name);			
		}

		if (matched.size()>0) {
			if (emmc_infos!=null) {
				for (String f:matched) {
					if (f.contains(String.valueOf(emmc_infos.getDiskSize()))) 
						keep.add(f);
				}
			} else if (ufs_infos!=null) {

				String size="";

				if (match.contains("LUN0")) size=String.valueOf(ufs_infos.getLunSize(0));
				if (match.contains("LUN1")) size=String.valueOf(ufs_infos.getLunSize(1));
				if (match.contains("LUN2")) size=String.valueOf(ufs_infos.getLunSize(2));
				if (match.contains("LUN3")) size=String.valueOf(ufs_infos.getLunSize(3));
				if (match.contains("LUN4")) size=String.valueOf(ufs_infos.getLunSize(4));
				if (match.contains("LUN5")) size=String.valueOf(ufs_infos.getLunSize(5));
				if (match.contains("LUN6")) size=String.valueOf(ufs_infos.getLunSize(6));
				
				for (String f : matched) {
					if (f.contains(size)) {
						keep.add(f);
					}
				}
				if (keep.size()>0) {
					matched.clear();
					matched=keep;
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