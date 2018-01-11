package flashsystem;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.crypto.NoSuchPaddingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sinfile.parsers.SinFileException;
import org.system.AESInputStream;
import org.system.RC4InputStream;
import org.system.RC4OutputStream;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;

public class SeusSinTool {

	static final Logger logger = LogManager.getLogger(SeusSinTool.class);

	public static void decryptAndExtract(String FILESET) throws Exception,FileNotFoundException,IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
		File enc= new File(FILESET);
		File dec = new File(enc.getParent()+File.separator+"decrypted_"+enc.getName());
		if (!decrypt(enc,dec)) throw new Exception("Unable to decrypt "+FILESET);
		String folder = enc.getParentFile().getAbsolutePath()+File.separator+"decrypted";
		new File(folder).mkdirs();
	    logger.info("Identifying fileset content");
	    ZipFile file=null;
	    try {
	    	 file = new ZipFile(dec.getAbsolutePath());
	    	 logger.info("Found zip file. Extracting content");
	    	 Enumeration<? extends ZipEntry> entries = file.entries();
	    	 while ( entries.hasMoreElements() ) {
	    		 ZipEntry entry = entries.nextElement();
	    		 InputStream entryStream = file.getInputStream(entry);
	    		 File out = getFile(new File(folder+File.separator+entry.getName()));
	    		 FileOutputStream streamOut = new FileOutputStream(out);
	    		 ByteStreams.copy(entryStream,streamOut);
	    		 entryStream.close();
	    		 streamOut.close();
	    		 try {
	    			 if (!out.getName().toUpperCase().endsWith("SIN")) {
		    			 ZipFile subzip = new ZipFile(out);
		    			 logger.info("Extracting "+out.getName());
		    			 String subfolder = folder + File.separator+entry.getName().substring(0,entry.getName().lastIndexOf("."));
		    			 new File(subfolder).mkdirs();
		    			 PrintWriter pw = null;
		    			 if (out.getName().equals("partition.zip")) {
		    				 pw = new PrintWriter(new File(subfolder+File.separator+"partition_delivery.xml"));
		    				 pw.println("<PARTITION_DELIVERY FORMAT=\"1\">");
		    			     pw.println(" <PARTITION_IMAGES>");
		    			 }
		    			 Enumeration<? extends ZipEntry> subentries = subzip.entries();
		    			 while ( subentries.hasMoreElements() ) {
		    				 ZipEntry subentry = subentries.nextElement();
		    				 if (pw!=null)
		    					 pw.println("   <FILE PATH=\""+subentry.getName()+"\"/>");
		    	    		 File subout = getFile(new File(subfolder+File.separator+subentry.getName()));
		    	    		 entryStream=subzip.getInputStream(subentry);
		    	    		 streamOut = new FileOutputStream(subout);
		    	    		 ByteStreams.copy(entryStream,streamOut);
		    	    		 entryStream.close();
		    	    		 streamOut.close();
		    			 }
	    				 if (pw!=null) {
	    					 pw.println(" </PARTITION_IMAGES>");
	    					 pw.println("</PARTITION_DELIVERY>");
	    					 pw.flush();
	    					 pw.close();
	    				 }
		    			 subzip.close();
		    			 out.delete();
	    			 }
	    		 } catch (Exception e1) {}
	    	 }
	    	 file.close();
	    } catch (Exception e) {
	    	try {
	    		file.close();
	    	} catch (Exception ex) {}
	    	try {
	    		org.sinfile.parsers.SinFile sf = new org.sinfile.parsers.SinFile(new File(dec.getAbsolutePath()));
	    		if (sf.getType().equals("LOADER")) {
	    			logger.info("Found sin loader. Moving file to loader.sin");
	    			dec.renameTo(getFile(new File(folder+File.separator+"loader.sin")));
	    		}
	    		if (sf.getType().equals("BOOT")) {
	    			logger.info("Found sin boot. Moving file to boot.sin");
	    			dec.renameTo(getFile(new File(folder+File.separator+"boot.sin")));
	    		}
	    	} catch (SinFileException sine) {
	    		try {
	    			TAFileParser ta = new TAFileParser(new FileInputStream(dec.getAbsolutePath()));
	    			logger.info("Found ta file. Moving file to preset.ta");
	    			dec.renameTo(getFile(new File(folder+File.separator+"preset.ta")));
	    		} catch(TAFileParseException tae) {
	    			logger.error(dec.getAbsolutePath() + " is unrecognizable");
	    		}
	    	}
	    }
	    dec.delete();
	}
	
	public static boolean decrypt(File enc, File dec)  {
		if (decryptGzipped(enc,dec)) return true;
		if (decryptAES(enc,dec)) return true;
		if (decryptRC4(enc,dec)) return true;
		return decryptAsIs(enc,dec);
	}

	public static boolean decryptAsIs(File enc, File dec) {
		FileOutputStream localFileOutputStream=null;
		FileInputStream localFileInputStream=null;
		try {
			localFileInputStream = new FileInputStream(enc);
		    localFileOutputStream = new FileOutputStream(dec);
			ByteStreams.copy(localFileInputStream, localFileOutputStream);
		    localFileOutputStream.close();
		    localFileInputStream.close();
		    return true;
		} catch (Exception e) {
			try {
			    localFileOutputStream.close();
			    localFileInputStream.close();
			    dec.delete();
			} catch (Exception e1) {}
		    return false;
		}		
	}
	
	public static boolean decryptGzipped(File enc, File dec) {
		FileOutputStream localFileOutputStream=null;
		GZIPInputStream localGZIPInputStream=null;
		try {
			localGZIPInputStream = new GZIPInputStream(new FileInputStream(enc));
		    localFileOutputStream = new FileOutputStream(dec);
			ByteStreams.copy(localGZIPInputStream, localFileOutputStream);
		    localFileOutputStream.close();
		    localGZIPInputStream.close();
		    return true;
		} catch (Exception e) {
			try {
			    localFileOutputStream.close();
			    localGZIPInputStream.close();
			    dec.delete();
			} catch (Exception e1) {}
		    return false;
		}
	}

	public static boolean decryptAES(File enc, File dec) {
		FileOutputStream localFileOutputStream=null;
		GZIPInputStream localEncodedStream = null;
		try {
		    localFileOutputStream = new FileOutputStream(dec);
		    localEncodedStream = new GZIPInputStream(new AESInputStream(new FileInputStream(enc)));
		    ByteStreams.copy(localEncodedStream, localFileOutputStream);
		    localFileOutputStream.close();
		    localEncodedStream.close();				
		    return true;
		} catch (Exception e) {
			try {
				localFileOutputStream.close();
				localEncodedStream.close();
				dec.delete();
			} catch (Exception e1) {}
		    return false;
		}
	}

	public static boolean decryptRC4(File enc, File dec) {
		FileOutputStream localFileOutputStream=null;
		GZIPInputStream localEncodedStream = null;
		try {
		    localFileOutputStream = new FileOutputStream(dec);
		    localEncodedStream = new GZIPInputStream(new RC4InputStream(new FileInputStream(enc)));
		    ByteStreams.copy(localEncodedStream, localFileOutputStream);
		    localFileOutputStream.close();
		    localEncodedStream.close();				
		    return true;
		} catch (Exception e) {
			try {
				localFileOutputStream.close();
				localEncodedStream.close();
				dec.delete();
			} catch (Exception e1) {}
		    return false;
		}
	}

	public static void encryptRC4(String tgzfile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		  byte[] buf = new byte[1024];
	      try {
	    	  String outname = tgzfile.replaceAll(".tgz", ".sin");
	    	  FileInputStream in = new FileInputStream(tgzfile);
	    	  RC4OutputStream out = new RC4OutputStream(new FileOutputStream(outname));
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