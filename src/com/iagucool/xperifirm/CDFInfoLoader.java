package com.iagucool.xperifirm;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javafx.collections.transformation.SortedList;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sonyericsson.cs.ma3.common.communication.ng.NGHash;
import com.sonyericsson.cs.ma3.common.data.ng.DistributedFileInfo;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceContent;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchHistory;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchHistoryEntry;
import com.sonyericsson.cs.ma3.common.data.ng.ServiceSearchResultEntry;
import com.sonyericsson.cs.ma3.common.data.ng.us.ScriptSearchInfo;
import com.sonyericsson.cs.ma3.common.data.serviceclient.DataFile;
import com.sonyericsson.cs.ma3.common.data.serviceclient.DataFileProperty;
//import com.sonyericsson.cs.ma3.common.data.serviceclient.DataFile;
import com.sonyericsson.cs.ma3.common.data.serviceclient.DataIdentifier;
import com.sonymobile.cs.generic.charset.StandardCharset;
import com.sun.javafx.animation.TickCalculation;

public class CDFInfoLoader
{
	private static final String defaultOutput = "<XperiFirm/>";
	private static final String nsDomain = "http://software.sonymobile.com";
	private static final String nsRootPath = "ns/";
	private static final String nsFileExtension = ".ser.gz";
	private Document doc;
	private Element rootNode;
	private Properties userinfo;
	private Firmware latest=null;
	
	
	public CDFInfoLoader(String tac8, String cda) throws MalformedURLException, IOException, ParserConfigurationException {
		userinfo = new Properties(); 
		userinfo.load(new URL("http://software.sonymobile.com/ns/omsi/1/common/userinfo/user.properties").openStream());
		userinfo.setProperty("user.name", userinfo.getProperty("user.name").toLowerCase());
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		rootNode=doc.createElement("XperiFirm");
		doc.appendChild(rootNode);
		load(tac8, cda);
		FirmwaresList flist = new FirmwaresList();
		NodeList nl = rootNode.getChildNodes();
		for (int i=0;i<nl.getLength();i++) {
			Node release = nl.item(i);
			Firmware f = new Firmware(((Element)release).getAttribute("swVer"),((Element)release).getAttribute("cdfVer"));
			NodeList files = release.getChildNodes();
			for (int j=0;j<files.getLength();j++) {
				Element file = (Element)files.item(j);
				String id = file.getAttribute("id");
				String folder = id.substring(id.length()-3);
				String filepath = "";
				if (file.getAttribute("parts").equals("1")) {
					filepath = "common/1/file/"+folder+"/"+file.getAttribute("id");
					filepath = filepath + "_"+ NGHash.generateHash(filepath);
					FileSet fs = new FileSet();
					fs.setId(Integer.parseInt(id));
					fs.addUrl("http://software.sonymobile.com/ns/"+filepath+".bin");
					fs.setCheckSum(Long.parseLong(file.getAttribute("checksum")));
					f.addFileSet(fs);
				}
				else {
					FileSet fs = new FileSet();
					fs.setId(Integer.parseInt(id));
					for (int k=1; k<=Integer.parseInt(file.getAttribute("parts")); k++) {
						filepath = "common/1/file/"+folder+"/"+file.getAttribute("id")+"_"+file.getAttribute("chunk")+"_"+k;
						filepath = filepath + "_"+ NGHash.generateHash(filepath);
						fs.addUrl("http://software.sonymobile.com/ns/"+filepath+".bin");
					}
					fs.setCheckSum(Long.parseLong(file.getAttribute("checksum")));
					f.addFileSet(fs);;
				}
			}
			
			flist.add(f);
		}
		latest = flist.getLatest();
	}

	private void load(String tac8, String cda)
	{
		String filepath = userinfo.getProperty("user.name") + "/2/script/search/TAC8=" + tac8 + "/CDA=" + cda;
		String ngHash = NGHash.generateHash(filepath);
		//ProductInfoLoader pi = new ProductInfoLoader(userinfo.getProperty("user.name"));
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
		Set<String> versions = new HashSet<String>();
		
		List<ServiceSearchHistoryEntry> sshEntries = obj.getServiceSearchHistoryList();
		for (Iterator<ServiceSearchHistoryEntry> sshEntriesIt = sshEntries.iterator(); sshEntriesIt.hasNext();)
		{
			List<ServiceSearchResultEntry> ssrEntries = sshEntriesIt.next().getServiceSearchResult().getServiceSearchResultEntryList();
			for (Iterator<ServiceSearchResultEntry> ssrEntriesIt = ssrEntries.iterator(); ssrEntriesIt.hasNext();)
			{
				ServiceSearchResultEntry ssrEntry = ssrEntriesIt.next();
				List<ScriptSearchInfo> ssiEntries = ssrEntry.getScriptSearchInfoList().getScripts();
				if (!ssiEntries.isEmpty())
				{
					ScriptSearchInfo ssiEntry = ssiEntries.get(0);
					Set<DataFile> df = ssiEntry.getDataFiles();
					String iAppSWVer = null;
					String iCDFVer = null;
					String iNetwork = null;
					Map<Long, DataFile> files = new HashMap<Long, DataFile>();
					for (Iterator<DataFile> dfItr = df.iterator(); dfItr.hasNext();)
					{
						DataFile dfEntry = dfItr.next();
						if (!dfEntry.getFileName().isEmpty())
						{
							// Discard dummy files
							if (dfEntry.getFileName().contains("Dummy File"))
								continue;
							if (dfEntry.getFileName().contains("@Template"))
								continue;
							DataFileProperty[] dfp = dfEntry.getFileProperties();
							int dfpLength = dfp.length;
							for (int dfpIdx = 0; dfpIdx < dfpLength; ++dfpIdx)
							{
								DataFileProperty dfpEntry = dfp[dfpIdx];
								switch (dfpEntry.getFilePropertyCategoryName())
								{
									case "FSVer":
										iAppSWVer = dfpEntry.getFilePropertyValue();
										break;
									case "CDFVer":
										iCDFVer = dfpEntry.getFilePropertyValue();
										break;
									case "LangRegion":
										iNetwork = dfpEntry.getFilePropertyValue();
										break;
								}
							}
							files.put(dfEntry.getFileContentInfoId(), dfEntry);
						}
					}
					if (iAppSWVer == null || iCDFVer == null)
						continue; // This shouldn't happen
					String verEntry = iAppSWVer + "/" + iCDFVer;
					if (!versions.contains(verEntry))
					{
						// <release swVer="" cdfVer="" network="">
						Element cdfNode = doc.createElement("release");
						cdfNode.setAttribute("network", iNetwork);
						cdfNode.setAttribute("cdfVer", iCDFVer);
						cdfNode.setAttribute("swVer", iAppSWVer);
						// Add release files
						ServiceContent sc = (ServiceContent)ssrEntry.getServiceContent().values().toArray()[0];
						Set<DistributedFileInfo> dfi = sc.getDistributedFileInfos();
						for (Iterator<DistributedFileInfo> dfiItr = dfi.iterator(); dfiItr.hasNext();)
						{
							DistributedFileInfo dfiEntry = dfiItr.next();
							long fileId = dfiEntry.getFileContentInfoId();
							if (files.containsKey(fileId))
							{
								DataFile file = files.get(fileId);
								String fileName = getFileName(file);
								String internFileName = file.getFileName();
								String internCategory = file.getFileTypeTag();
								long fileLength = dfiEntry.getLength();
								long fileChecksum = dfiEntry.getChecksum();
								int filePartsCount = dfiEntry.getFileParts().size();
								long fileChunkSize = dfiEntry.getChunkSize();
								// <file id="" name="" length="" length="" checksum="" parts="" chunk="" />
								Element fileNode = doc.createElement("file");
								if (filePartsCount > 1)
									fileNode.setAttribute("chunk", String.valueOf(fileChunkSize));
								fileNode.setAttribute("parts", String.valueOf(filePartsCount));
								fileNode.setAttribute("checksum", String.valueOf(fileChecksum));
								fileNode.setAttribute("length", String.valueOf(fileLength));
								fileNode.setAttribute("name", fileName);
								fileNode.setAttribute("internalname", internFileName);
								fileNode.setAttribute("internalcategory", internCategory);
								fileNode.setAttribute("id", String.valueOf(fileId));
								cdfNode.appendChild(fileNode);
							}
						}
						rootNode.appendChild(cdfNode);
						versions.add(verEntry);
					}
				}
			}
		}
	}

	private String getFileName(DataFile dfEntry) {
		String dfName = dfEntry.getFileName();
		// Rename files for the unpacker
		switch (dfEntry.getFileTypeTag())
		{
			case "SIN_FILE_SET":
				dfName = dfName.split("_")[0];
				if (dfName.equals("FSP"))
				{
				}
				dfName = dfName.toLowerCase().concat(".zip");
				break;
			case "LOADER":
				dfName = dfName.contains("TEST") ? "loader-test.sin" : "loader.sin";
				break;
			case "SOFTWARE":
				dfName = dfName.contains("TEST") ? "boot-test.sin" : "boot.sin";
				break;
			case "TA":
				switch (dfName)
				{
					case "S1_TA_ResetCustomizationSelector":
						dfName = "cust-reset.ta";
						break;
					case "S1_TA_Tamsui_FlashStartupShutdownResult":
						dfName = "tamsui-fssr.ta";
						break;
					default:
						if (!dfName.endsWith(".ta"))
							dfName = dfName.concat(".ta");
						break;
				}
				break;
		}
		return dfName;
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
		return latest.getRelease();
	}

	public Firmware getFiles() {
		return latest;
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
