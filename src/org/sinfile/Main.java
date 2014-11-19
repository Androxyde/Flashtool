package org.sinfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.mapper.Bin;

public class Main {

	public static void main(String[] args) throws IOException {

		int hash_type[] = {0,0,32};
		String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\M35t_China_Mobile_CN_12.0.C.2.58\\decrypted\\kernel_S1-SW-LIVE-7054-PID1-0002-S1-PARTITION.sin";
		//String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\M35t_China_Mobile_CN_12.0.C.2.58\\decrypted\\system_S1-SW-LIVE-7054-PID1-0002-S1-PARTITION.sin";
		//String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\M35t_China_Mobile_CN_12.0.C.2.58\\decrypted\\boot.sin";
		//String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\C6503_Customized_FR_23.0.1.A.0.167\\decrypted\\loader.sin";
		//String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\C6503_Customized_FR_23.0.1.A.0.167\\decrypted\\kernel_S1-SW-LIVE-9C84-PID1-0006-MMC.sin";
		//String filename="C:\\Applications\\Flashtool\\firmwares\\Downloads\\C6503_Customized_FR_23.0.1.A.0.167\\decrypted\\system_S1-SW-LIVE-9C84-PID1-0006-MMC.sin";
		
		
		JBBPBitInputStream sinStream = new JBBPBitInputStream(new FileInputStream(filename));

		final JBBPParser sinParserV2 = JBBPParser.prepare(
			    "byte multipleHeaders;"
              + "int headerLen;"
              + "byte payloadType;"
              + "short unknown;"
              + "byte memId;"
              + "byte compression;"
              + "int hashLen;"
              + "byte[hashLen] hashBlocks;"
              + "int certLen;"
              + "byte[certLen] cert;"
        );

		final JBBPParser hashBlocksV2 = JBBPParser.prepare(
	            "block[_] {int offset;"
              + "int length;"
              + "byte hashlength;"
              + "byte[hashlength] crc;}"
		);

		final JBBPParser sinParserV3 = JBBPParser.prepare(
			    "byte[3] magic;"
              + "int HeaderLen;"
              + "int PayloadType;"
              + "int hashType;"
              + "int reserved;"
              + "int hashLen;"
              + "byte[hashLen] hashBlocks;"
              + "int certLen;"
              + "byte[certLen] cert;"
        );
 

		final JBBPParser sonyPart = JBBPParser.prepare(
	              "int length;"
				+ "byte[length] cert;"
		);
		
		
		class SonyPart {
			@Bin int length;
			@Bin byte[] cert;
		}
		
		System.out.println(new File(filename).getName());
		int version = sinStream.readByte();
		if (version==2) {
	      org.sinfile.parsers.v2.SinParser sin = sinParserV2.parse(sinStream).mapTo(org.sinfile.parsers.v2.SinParser.class);
	      org.sinfile.parsers.v2.HashBlocks hashblocks = hashBlocksV2.parse(sin.hashblocks).mapTo(org.sinfile.parsers.v2.HashBlocks.class);
	      //SinParserV2 sinV2=null;
	      //if (sin.version==2)
	      
	      //  sinV2 = sinParserV2.parse(sinStream).mapTo(SinParserV2.class);
	      System.out.println("Version : "+version+"\nMultiple Headers : "+sin.multipleHeaders+"\nHeader Length : "+sin.headerLen+"\nPayLoad Type : "+sin.payloadType+"\nMem Id : "+sin.memId+"\nCompressoin : "+sin.compression+"\nHash Length : "+sin.hashLen);
	      System.out.println(sin.certLen);
		}
		if (version==3) {
			org.sinfile.parsers.v3.SinParser sin = sinParserV3.parse(sinStream).mapTo(org.sinfile.parsers.v3.SinParser.class);
		      //HashBlocksV3 blocks = hashBlocksV3.parse(sin.blocks).mapTo(HashBlocksV3.class);
		      System.out.println("Version : "+version+"\nMagic : "+new String(sin.magic)+"\nHeader Length : "+sin.header_len+"\nPayLoad Type : "+sin.payload_type+"\nHash type : "+sin.hash_type+"\nReserved : "+sin.reserved+"\nHashList Length : "+sin.hash_len+" ("+sin.hashblocks.length+" hashblocks) \nCert len : "+sin.cert_len);
			}
	      
	}

}
