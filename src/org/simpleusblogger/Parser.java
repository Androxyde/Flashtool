package org.simpleusblogger;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.system.TextFile;
import org.util.HexDump;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;



public class Parser {
	
	private static Logger logger = Logger.getLogger(Parser.class);
	
	static JBBPParser USBRecord = JBBPParser.prepare(
			"<long irp;" +
			"byte[8] reserved1;" +  
			"<int recordlength;" + 
			"byte[28] reserved2;"
			);

	static JBBPParser SinHeader = JBBPParser.prepare(
			"byte version;" +
			"byte[3] magic;" +
			"int headerLen;" +
			"int payloadType;" +
			"int hashType;" +
			"int reserved;" +
			"int hashLen;"
			);

	static JBBPParser S1Packet = JBBPParser.prepare(
			  "byte[4] command;"
			+ "byte[4] flag;"
			+ "byte[4] length;"
			+ "byte headercksum;"
			);

	//public static byte[] record = new byte[] {(byte)0x80,0x00,0x09,0x00,0x00,0x00,0x00,0x00};	
		  
	  public static Session parse(String usblog, String extractedsin) throws Exception {
		  
		  TextFile tf = new TextFile("D:\\usb.txt","ISO8859-1");
		  tf.open(false);
		  	Session session = new Session();
		  	S1Packet current=null;
			USBHeader head=null;
			FileInputStream fin=new FileInputStream(usblog);
			
			JBBPBitInputStream usbStream = new JBBPBitInputStream(fin);
			usbStream.skip(52);
			int recnum = 0;
			while (usbStream.hasAvailableData()) {
				USBRecord rec = readRecord(usbStream);
				rec.recnum=recnum++;
				if (rec.header!=null) {
					//if (rec.header.usb_TransferFlags==0)
						//System.out.println(rec.header.usb_TransferBuffer + " / "+rec.header.usb_TransferBufferLength+" / "+rec.header.usb_TransferBufferMDL);
				}
					
				if (rec.header==null) continue;
				if (rec.header.usb_UsbDeviceHandle==0) continue;
				
				if (rec.data.length<13) {
					if (current!=null)
						current.addData(rec.data);
				}
				else {
					JBBPBitInputStream dataStream = new JBBPBitInputStream(new ByteArrayInputStream(rec.data));
					S1Packet p = S1Packet.parse(dataStream).mapTo(S1Packet.class);
					if (p.isHeaderOK()) {
						if (rec.header.usb_TransferBufferLength > 13) {
							p.addData(dataStream.readByteArray(rec.data.length-13));
						}
						p.setRecord(rec.recnum);
						p.setDirection(rec.header.usb_TransferFlags);
						if (current!=null) {
							current.finalise();
							//if (current.getCommand()!=6) {
								if (current.direction.equals("READ REPLY")) {
									if (current.getLength()>0)
										if (current.getCommand()!=6)
											session.addPacket(current);
								} else {
									if (current.getCommand()==5)
										current.setFileName(getSin(extractedsin,current.data));
									if (current.getCommand()!=6)
										session.addPacket(current);
									tf.writeln(current.getStartRecord() + " : " + current.getCommandName()+ " / " + current.getSin() + " / " + current.getNbParts() + " " + current.getLength());
								}
							//}
							
						}
						current = p;
					}
					else {
						if (current!=null)
							current.addData(rec.data);
					}
					dataStream.close();
				}
			}
			usbStream.close();
			tf.close();
			return session;
	  }
	  
	  
	  private static USBRecord readRecord(JBBPBitInputStream usbStream) throws Exception {
		  USBRecord rec = USBRecord.parse(usbStream).mapTo(USBRecord.class);
		  rec.parse(usbStream);
		  return rec;
		  
	  }
	  
	  private static String getSin(String folder, byte[] source) throws Exception {
		  File f = new File(folder);
		  File[] list = f.listFiles();
		  for (int i=0;i < list.length;i++) {
			  if (list[i].getName().endsWith(".sin")) {
				  try {
				  JBBPBitInputStream sinStream = new JBBPBitInputStream(new FileInputStream(list[i]));
				  byte[] res = sinStream.readByteArray(source.length);
				  if (Arrays.equals(source, res))
					  return list[i].getName();
				  } catch (EOFException eof) {
				  }
			  }
		  }
		  if (new File(folder+"\\boot").exists()) {
			  f = new File(folder+"\\boot");
			  list = f.listFiles();
			  for (int j=0;j < list.length;j++) {
				  if (list[j].getName().endsWith(".sin")) {
					  JBBPBitInputStream sinStream = new JBBPBitInputStream(new FileInputStream(list[j]));
					  byte[] res = sinStream.readByteArray(source.length);
					  if (Arrays.equals(source, res))
						  return list[j].getName();
				  }
			  }
		  }
		  return "Not identified";
	  }
}
