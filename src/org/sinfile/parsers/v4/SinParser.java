package org.sinfile.parsers.v4;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rauschig.jarchivelib.ArchiveEntry;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiveStream;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.rauschig.jarchivelib.IOUtils;
import org.sinfile.parsers.SinFile;

import com.Ostermiller.util.CircularByteBuffer;


public class SinParser {

	private File sinfile;
	
	static final Logger logger = LogManager.getLogger(SinParser.class);
	Map databuffer = new HashMap<String, CircularByteBuffer>();
	
	public SinParser(File f) {
		this.sinfile=f;
	}
	
	public void parse() throws FileNotFoundException, IOException {
	}

	public byte[] getHeader() {
		try {
			TarArchiveEntry entry=null;
			TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sinfile)));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((entry = tarIn.getNextTarEntry()) != null) {
				if (entry.getName().endsWith("cms")) {
					IOUtils.copy(tarIn, bout);
					break;
				}
			}
			tarIn.close();
			return bout.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	public void dumpImage() {
		try {
			TarArchiveEntry entry=null;
			TarArchiveInputStream tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(sinfile)));
			FileOutputStream fout = new FileOutputStream(new File("D:\\test.ext4"));
			while ((entry = tarIn.getNextTarEntry()) != null) {
				if (!entry.getName().endsWith("cms")) {
					IOUtils.copy(tarIn, fout);
				}
			}
			tarIn.close();
			fout.flush();
			fout.close();
			logger.info("Extraction finished to "+"D:\\test.ext4");
		} catch (Exception e) {}		
	}

}