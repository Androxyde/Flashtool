package org.ta.parsers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.util.HexDump;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;

public class TARawParser {
	JBBPBitInputStream ddStream = null;
	FileInputStream fin = null;
	BufferedInputStream bin = null;
	File ddFile = null;

	JBBPParser partblock = JBBPParser.prepare(
	            "          <int pmagic;"
	                    + "<int phash;"
	            		+ "int punknown;"
	);

	public TARawParser(File ddfile) throws FileNotFoundException, IOException {
		ddFile = ddfile;
		openStreams();
		int unitnumber=0;
		while (ddStream.hasAvailableData()) {
			byte [] block = ddStream.readByteArray(0x20000);
			TARawBlock rawblock = partblock.parse(block).mapTo(TARawBlock.class);
			System.out.println(rawblock);
		}
		closeStreams();
	}

	public void closeStreams() {
		try {
			ddStream.close();
		} catch (Exception e) {}
		try {
			fin.close();
		} catch (Exception e) {}
		try {
			bin.close();
		} catch (Exception e) {}
	}
	
	public void openStreams() throws FileNotFoundException {
		closeStreams();
		fin=new FileInputStream(ddFile);
		ddStream = new JBBPBitInputStream(fin);
	}

}
