package com.iagucool.xperifirm;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashSet;
//import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.iagucool.encoding.Base64;
import com.sonyericsson.cs.ma3.common.communication.ng.NGHash;
import com.sonyericsson.cs.ma3.common.data.ng.DistributedFileInfo;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceContent;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchHistory;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchHistoryEntry;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchResultEntry;
import com.sonyericsson.cs.ma3.common.data.ng.us.ScriptSearchInfo;
//import com.sonyericsson.cs.ma3.common.data.serviceclient.DataFile;
import com.sonyericsson.cs.ma3.common.data.serviceclient.DataIdentifier;
import com.sonymobile.cs.generic.charset.StandardCharset;

public class CDFInfoLoader
{
	private static final String defaultOutput = "<XperiFirm/>";
	private static final String nsDomain = "http://software.sonymobile.com";
	private static final String nsRootPath = "ns/";
	private static final String nsFileExtension = ".ser.gz";
	private Document doc;
	private Element rootNode;

	public CDFInfoLoader(String username, String tac8, String cda) throws ParserConfigurationException {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		rootNode=doc.createElement("XperiFirm");
		doc.appendChild(rootNode);
		load(username, tac8, cda);
	}

	private void load(String username, String tac8, String cda)
	{
		String filepath = username.toLowerCase() + "/2/script/search/TAC8=" + tac8 + "/CDA=" + cda;
		String ngHash = NGHash.generateHash(filepath);
		System.out.println(nsDomain + "/" + nsRootPath + filepath + "_" + ngHash + nsFileExtension);
			try (InputStream is = new URL(nsDomain + "/" + nsRootPath + filepath + "_" + ngHash + nsFileExtension).openStream())
			{
				GZIPInputStream gzip_is = new GZIPInputStream(is);
				ObjectInputStream obj_is = new ObjectInputStream(gzip_is);
				try
				{
					Object obj = obj_is.readObject();
					if (obj.getClass() == ServiceSearchHistory.class)
						buildXML((ServiceSearchHistory)obj);
				}
				catch (ClassNotFoundException e)
				{
					Element error = doc.createElement("error");
					error.setAttribute("message", "The file is invalid");
					rootNode.appendChild(error);
				}
			}
			catch (IOException e)
			{
				Element error = doc.createElement("error");
				error.setAttribute("message", "The file could not be found or it is invalid");
				rootNode.appendChild(error);
			}
	}
	
	private void buildXML(ServiceSearchHistory obj)
	{	
			
			Set<String> iAppSWVers = new HashSet<String>();
			
			List<ServiceSearchHistoryEntry> sshEntries = obj.getServiceSearchHistoryList();
			for (Iterator<ServiceSearchHistoryEntry> sshEntriesIt = sshEntries.iterator(); sshEntriesIt.hasNext();)
			{
				List<ServiceSearchResultEntry> ssrEntries = sshEntriesIt.next().getServiceSearchResult().getServiceSearchResultEntryList();
				for (Iterator<ServiceSearchResultEntry> ssrEntriesIt = ssrEntries.iterator(); ssrEntriesIt.hasNext();)
				{
					ServiceSearchResultEntry ssrEntry = ssrEntriesIt.next();
					List<ScriptSearchInfo> ssiEntries = ssrEntry.getScriptSearchInfoList().getScripts();
					if (!ssiEntries.isEmpty()) {
						ScriptSearchInfo ssiEntry = ssiEntries.get(0);
						Set<DataIdentifier> di = ssiEntry.getRevisionIdentifiers();
						
						String iAppSWVer = null;
						//String iCDFVer = null;
						
						for (Iterator<DataIdentifier> diItr = di.iterator(); diItr.hasNext();)
						{
							DataIdentifier diEntry = diItr.next();
							if (diEntry.getCategory().equals("AppSWVer") || diEntry.getCategory().equals("SoftwareRev"))
							{
								iAppSWVer = diEntry.getValue();
								break;
							}
							/*switch (diEntry.getCategory())
							{
								case "AppSWVer":
									iAppSWVer = diEntry.getValue();
									break;
								case "CDFVer":
									iCDFVer = diEntry.getValue();
									break;
							}*/
						}
						if (iAppSWVer != null && !iAppSWVers.contains(iAppSWVer)/* && iCDFVer != null*/)
						{
							Element cdfNode = doc.createElement("release");
							//cdfNode.setAttribute("cdfVer", iCDFVer);
							//cdfNode.setAttribute("swVer", iAppSWVer);
							cdfNode.setAttribute("ver", iAppSWVer);
							
							//HashMap<Long, String> files = new HashMap<Long, String>();
							
							ServiceContent sc = (ServiceContent)ssrEntry.getServiceContent().values().toArray()[0];
							//Set<DataFile> df = sc.getFiles();
							//for (Iterator<DataFile> dfItr = df.iterator(); dfItr.hasNext();)
							//{
							//	DataFile dfEntry = dfItr.next();
							//	files.put(dfEntry.getFileContentInfoId(), dfEntry.getFileName());
							//}
							
							Set<DistributedFileInfo> dfi = sc.getDistributedFileInfos();
							for (Iterator<DistributedFileInfo> dfiItr = dfi.iterator(); dfiItr.hasNext();)
							{
								DistributedFileInfo dfiEntry = dfiItr.next();
								long fileId = dfiEntry.getFileContentInfoId();
								//String fileName = files.get(fileId);
								long fileLength = dfiEntry.getLength();
								//long fileChecksum = dfiEntry.getChecksum();
								long fileChunkSize = dfiEntry.getChunkSize();
								int filePartsCount = dfiEntry.getFilePartsCount();
								
								Element fileNode = doc.createElement("file");
								fileNode.setAttribute("id", String.valueOf(fileId));
								//fileNode.setAttribute("name", String.valueOf(fileName));
								fileNode.setAttribute("length", String.valueOf(fileLength));
								//fileNode.setAttribute("checksum", String.valueOf(fileChecksum));
								if (filePartsCount > 1)
									fileNode.setAttribute("chunk", String.valueOf(fileChunkSize));
								fileNode.setAttribute("parts", String.valueOf(filePartsCount));
								
								cdfNode.appendChild(fileNode);
							}
							
							rootNode.appendChild(cdfNode);
							iAppSWVers.add(iAppSWVer);
						}
					}
				}
			}			
	}
	
	public String toString() {
		String output = defaultOutput;
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		try
		{
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharset.ISO_8859_1_CHARSET.name());
			transformer.transform(domSource, result);
			output = writer.toString();
		}
		catch (TransformerException e)
		{
		}
		return output;
	}
	
	public String getRelease() {
		Element e = (Element)doc.getFirstChild().getFirstChild();
		return e.getAttribute("ver");
	}
	
	public long getSize() {
		NodeList nl = doc.getFirstChild().getFirstChild().getChildNodes();
		long size=0;
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element)nl.item(i);
			size+=Long.parseLong(e.getAttribute("length"));
		}
		return size;
	}
}
