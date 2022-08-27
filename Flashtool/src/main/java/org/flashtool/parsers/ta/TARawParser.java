package org.flashtool.parsers.ta;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flashtool.gui.models.TABag;
import org.flashtool.system.OS;

import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TARawParser {
	JBBPBitInputStream ddStream = null;
	FileInputStream fin = null;
	BufferedInputStream bin = null;
	File ddFile = null;
	Vector<TABag> bags = new Vector<TABag>();
	static final Logger logger = LogManager.getLogger(TARawParser.class);

	JBBPParser partblock = JBBPParser.prepare(
	            "          <int magic;"
	                    + "<int hash;"
	            		+ "byte unknown;"
	            		+ "byte partnumber;"
	            		+ "byte partition;"
	            		+ "byte numblocks;"
	            		+ "byte[131072-12] units;"
	);

	public TARawParser(File ddfile) throws FileNotFoundException, IOException {
		if (ddfile.getName().endsWith(".fta")) {
			open(ddfile);
			ddFile=new File(ddfile.getParentFile().getAbsolutePath()+File.separator+"prepared"+File.separator+"ta.dd");
		}
		else
			ddFile = ddfile;
		log.info("Parsing image "+ddFile.getAbsolutePath());
		openStreams();
		while (ddStream.hasAvailableData()) {
			TARawBlock parsedblock = partblock.parse(ddStream).mapTo(new TARawBlock());
			if (parsedblock.magic==0x3BF8E9C1) {
				parsedblock.parseUnits();
				Iterator<TABag> ib = bags.iterator();
				TABag b  = null;
				boolean found = false;
				while (ib.hasNext()) {
					b = ib.next();
					if (b.partition==parsedblock.partition) {
						found=true;
						break;
					}
				}
				if (!found) b = new TABag(parsedblock.partition);
				Iterator<TAUnit> iu = parsedblock.getUnits().iterator();
				while (iu.hasNext()) {
					b.addUnit(iu.next());
				}
				if (!found) bags.add(b);
			}
		}
		closeStreams();
		log.info("Parsing finished");
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

	public Vector<TABag> getBags() {
		return bags;
	}

	public boolean open(File bundle) {
		try {
			log.info("Preparing files for flashing");
			String prepared = bundle.getParentFile().getAbsolutePath()+File.separator+"prepared";
			OS.deleteDirectory(new File(prepared));
			File f = new File(prepared);
			f.mkdir();
			log.debug("Created the "+f.getName()+" folder");
			JarFile jar=new JarFile(bundle);
			Enumeration<JarEntry>  entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("ta")) {
					InputStream in = jar.getInputStream(entry);
					String outname=prepared+File.separator+entry.getName();
					OutputStream out = new BufferedOutputStream(new FileOutputStream(outname));
					byte[] buffer = new byte[10240];
					int len;
					while((len = in.read(buffer)) >= 0)
						out.write(buffer, 0, len);
					in.close();
					out.close();
				}
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return false;
		}
    }

}