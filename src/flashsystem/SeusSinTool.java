package flashsystem;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.sinfile.parsers.SinFileException;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;

import com.sonymobile.cs.generic.encoding.RC4DecryptingInputStream;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;

public class SeusSinTool {

	private static Logger logger = Logger.getLogger(SeusSinTool.class);
	
	public static void decryptAndExtract(String FILESET) throws FileNotFoundException,IOException {
		File enc= new File(FILESET);
		File dec = new File(enc.getParent()+File.separator+"decrypted_"+enc.getName());
		decrypt(enc);
		String folder = enc.getParentFile().getAbsolutePath()+File.separator+"decrypted";
		new File(folder).mkdirs();
	    logger.info("Identifying fileset content");
	    ZipFile file=null;
	    try {
	    	 file = new ZipFile(dec.getAbsolutePath());
	    	 Enumeration<? extends ZipEntry> entries = file.entries();
	    	 while ( entries.hasMoreElements() ) {
	    		 ZipEntry entry = entries.nextElement();
	    		 File out = getFile(new File(folder+File.separator+entry.getName()));
	    		 dumpStreamTo(file.getInputStream(entry),out);
	    		 if (entry.getName().endsWith(".zip")) {
	    			 String subfolder = folder + File.separator+entry.getName().substring(0,entry.getName().lastIndexOf("."));
	    			 ZipFile subzip = new ZipFile(out); 
	    			 Enumeration<? extends ZipEntry> subentries = subzip.entries();
	    			 while ( subentries.hasMoreElements() ) {
	    				 ZipEntry subentry = subentries.nextElement();
	    	    		 File subout = getFile(new File(subfolder+File.separator+subentry.getName()));
	    	    		 dumpStreamTo(subzip.getInputStream(subentry),subout);
	    			 }
	    			 subzip.close();
	    			 out.delete();
	    		 }
	    	 }
	    	 file.close();
	    } catch (Exception e) {
	    	try {
	    		file.close();
	    	} catch (Exception ex) {}
	    	try {
	    		org.sinfile.parsers.SinFile sf = new org.sinfile.parsers.SinFile(new File(dec.getAbsolutePath()));
	    		if (sf.getType().equals("LOADER"))
	    			dec.renameTo(getFile(new File(folder+File.separator+"loader.sin")));
	    		if (sf.getType().equals("BOOT"))
	    			dec.renameTo(getFile(new File(folder+File.separator+"boot.sin")));
	    	} catch (SinFileException sine) {
	    		try {
	    			TAFileParser ta = new TAFileParser(new FileInputStream(dec.getAbsolutePath()));
	    			dec.renameTo(getFile(new File(folder+File.separator+"preset.ta")));
	    		} catch(TAFileParseException tae) {
	    			logger.error(dec.getAbsolutePath() + " is unrecognizable");
	    		}
	    	}
	    }
	    dec.delete();
	}

	public static void dumpStreamTo(InputStream in, File out) throws IOException {
		out.getParentFile().mkdirs();
		ByteBuffer buffer = ByteBuffer.allocate(20480000);
	    ReadableByteChannel channel = Channels.newChannel(in);
	    RandomAccessFile afile = new RandomAccessFile (out,"rw");
	    FileChannel cout = afile.getChannel();
	    cout.truncate(0L);
	    while (channel.read(buffer)>0) {
	    	buffer.flip();
	    	while(buffer.hasRemaining()) {
	    	    cout.write(buffer);
	    	}
	    	buffer.clear();
	    }
	    channel.close();
	    in.close();
	    cout.close();
	    afile.close();
	}
	
	public static void decrypt(File enc) throws FileNotFoundException, IOException {
		dumpStreamTo(new GZIPInputStream(new RC4DecryptingInputStream(new FileInputStream(enc))),new File(enc.getParent()+File.separator+"decrypted_"+enc.getName()));
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
	
	public static File getFile(File file) {
		if (file.exists()) {
			int i=1;
			String folder = file.getParent();
			int point = file.getName().lastIndexOf(".");
			if (point==-1) return file;
			String name = file.getName().substring(0,point);
			String ext = file.getName().substring(point+1);
			while (new File(folder+File.separator+name+i+"."+ext).exists()) {
				i++;
			}
			return new File(folder+File.separator+name+i+"."+ext);
		}
		else return file;
	}

}