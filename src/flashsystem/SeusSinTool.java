package flashsystem;


import gui.MainSWT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.sinfile.parsers.SinFileException;
import org.system.OS;
import org.system.ProcessBuilderWrapper;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;
import org.util.HexDump;

import com.sonymobile.cs.generic.encoding.RC4DecryptingInputStream;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;

public class SeusSinTool {

	private static Logger logger = Logger.getLogger(SeusSinTool.class);
	
	public static void decryptAndExtract(String FILESET) throws FileNotFoundException,IOException {
		decrypt(FILESET);
		String folder = new File((new File(FILESET)).getParent()).getAbsolutePath()+File.separator+"decrypted";
		new File(folder).mkdirs();
		String basefile = FILESET+"_dek";
	    logger.info("Identifying fileset content");
	    ZipFile file=null;
	    try {
	    	 file = new ZipFile(basefile);
	    	 Enumeration<? extends ZipEntry> entries = file.entries();
	    	 while ( entries.hasMoreElements() ) {
	    		 ZipEntry entry = entries.nextElement();
	    		 extractTo(file.getInputStream(entry),entry.getName(),folder);
	    	 }
	    	 file.close();
	    } catch (Exception e) {
	    	try {
	    		file.close();
	    	} catch (Exception ex) {}
	    	try {
	    		org.sinfile.parsers.SinFile sf = new org.sinfile.parsers.SinFile(new File(basefile));
	    		if (sf.getType().equals("LOADER"))
	    			new File(basefile).renameTo(new File(folder+File.separator+"loader.sin"));
	    		if (sf.getType().equals("BOOT"))
	    			new File(basefile).renameTo(new File(folder+File.separator+"boot.sin"));
	    	} catch (SinFileException sine) {
	    		try {
	    			TAFileParser ta = new TAFileParser(new FileInputStream(basefile));
	    			new File(basefile).renameTo(new File(folder+File.separator+"preset.ta"));
	    		} catch(TAFileParseException tae) {
	    			logger.error(basefile + " is unrecognizable");
	    		}
	    	}
	    }
	    new File(FILESET+"_dek").delete();
	}

	public static void extractTo(InputStream in, String file, String folder) throws IOException {
		new File(folder).mkdirs();
		String fullpath = folder+File.separator+file;
		ByteBuffer buffer = ByteBuffer.allocate(20480000);
	    ReadableByteChannel channel = Channels.newChannel(in);
	    RandomAccessFile afile = new RandomAccessFile (fullpath,"rw");
	    FileChannel out = afile.getChannel();
	    out.truncate(0L);
	    while (channel.read(buffer)>0) {
	    	buffer.flip();
	    	while(buffer.hasRemaining()) {
	    	    out.write(buffer);
	    	}
	    	buffer.clear();
	    }
	    channel.close();
	    in.close();
	    out.close();
	    afile.close();
	}
	
	public static void decrypt(String sinfile) throws FileNotFoundException, IOException {
		ByteBuffer buffer = ByteBuffer.allocate(20480000);
	    ReadableByteChannel channel = Channels.newChannel(new GZIPInputStream(new RC4DecryptingInputStream(new FileInputStream(sinfile))));
	    String basefile = sinfile+"_dek";
	    FileChannel out = new RandomAccessFile (basefile,"rw").getChannel();
	    out.truncate(0L);
	    while (channel.read(buffer)>0) {
	    	buffer.flip();
	    	while(buffer.hasRemaining()) {
	    	    out.write(buffer);
	    	}
	    	buffer.clear();
	    }
	    channel.close();
	    out.close();		
	}

	  public static void encrypt(String tgzfile) {
		  byte[] buf = new byte[1024];
	      try {
	    	  String outname = tgzfile.replaceAll(".tgz", ".sin");
	    	  FileInputStream in = new FileInputStream(tgzfile);
	    	  RC4EncryptingOutputStream out = new RC4EncryptingOutputStream(new FileOutputStream(outname));
	    	  int len;
	    	  while((len = in.read(buf)) >= 0) {
	    		  if (len > 0)
	    			  out.write(buf, 0, len);
	    	  }
	    	  out.flush();
	    	  out.close();
	    	  in.close();
	      } catch(IOException e) {
	        e.printStackTrace();
	      }
	  }

}