package flashsystem;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.sinfile.parsers.SinFileException;
import org.ta.parsers.TAFileParseException;
import org.ta.parsers.TAFileParser;

public class SeusSinTool {

	private static Logger logger = Logger.getLogger(SeusSinTool.class);

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
		    			 ZipFile subzip = new ZipFile(out);
		    			 String subfolder = folder + File.separator+entry.getName().substring(0,entry.getName().lastIndexOf("."));
		    			 new File(subfolder).mkdirs();
		    			 Enumeration<? extends ZipEntry> subentries = subzip.entries();
		    			 while ( subentries.hasMoreElements() ) {
		    				 ZipEntry subentry = subentries.nextElement();
		    	    		 File subout = getFile(new File(subfolder+File.separator+subentry.getName()));
		    	    		 entryStream=subzip.getInputStream(subentry);
		    	    		 streamOut = new FileOutputStream(subout);
		    	    		 ByteStreams.copy(entryStream,streamOut);
		    	    		 entryStream.close();
		    	    		 streamOut.close();
		    			 }
		    			 subzip.close();
		    			 out.delete();
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
	
    private static Cipher setupCipher(int mode, String key, String IV, String method) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        if (method.equals("AES/CTR/NoPadding")) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Hashing.sha256().hashBytes(key.getBytes("UTF-8")).asBytes(), "AES");
        	IvParameterSpec ivParameterSpec = new IvParameterSpec(BaseEncoding.base16().decode(IV));
        	Cipher cipher = Cipher.getInstance(method, "BC");
        	cipher.init(mode, secretKeySpec, ivParameterSpec);
        	return cipher;
        }
        else {
        	SecretKeySpec secretKeySpec = new SecretKeySpec(BaseEncoding.base64().decode(key), "RC4");
        	Cipher cipher = Cipher.getInstance(method);
        	cipher.init(mode, secretKeySpec);
        	return cipher;
        }
    }

	public static boolean decrypt(File enc, File dec)  {
		if (decryptUncrypted(enc,dec)) return true;
		if (decryptAES(enc,dec)) return true;
		if (decryptRC4(enc,dec)) return true;
		return false;
	}

	public static boolean decryptUncrypted(File enc, File dec) {
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
		CipherInputStream localCipherInputStream=null;
		try {
			Cipher localCipher = setupCipher(Cipher.DECRYPT_MODE, "qAp!wmvl!cOS7xSQV!aoR7Qz*neY^5Sx", "5621616F5237517A21634F5337785351", "AES/CTR/NoPadding");
			localCipherInputStream = new CipherInputStream(new FileInputStream(enc), localCipher);
			localFileOutputStream = new FileOutputStream(dec);
			ByteStreams.copy(new GZIPInputStream(localCipherInputStream), localFileOutputStream);
			localFileOutputStream.close();
			localCipherInputStream.close();
		    return true;
		} catch (Exception e) {
			try {
				localFileOutputStream.close();
				localCipherInputStream.close();
				dec.delete();
			} catch (Exception e1) {}
		    return false;
		}
	}

	public static boolean decryptRC4(File enc, File dec) {
		FileOutputStream localFileOutputStream=null;
		CipherInputStream localCipherInputStream=null;
		try {
			Cipher localCipher = setupCipher(Cipher.DECRYPT_MODE, "DoL6FBfnYcNJBjH31Vnz6lKATTaDGe4y", null, "RC4");
		    localCipherInputStream = new CipherInputStream(new FileInputStream(enc), localCipher);
		    localFileOutputStream = new FileOutputStream(dec);
		    ByteStreams.copy(new GZIPInputStream(localCipherInputStream), localFileOutputStream);
		    localFileOutputStream.close();
		    localCipherInputStream.close();				
		    return true;
		} catch (Exception e) {
			try {
				localFileOutputStream.close();
				localCipherInputStream.close();
				dec.delete();
			} catch (Exception e1) {}
		    return false;
		}
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