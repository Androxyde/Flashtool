package org.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.logger.LogProgress;
import org.util.HexDump;

import com.sonymobile.cs.generic.encoding.RC4DecryptingInputStream;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;

import java.util.zip.CheckedInputStream;
import java.util.zip.Adler32;

public class OS {

	private static Logger logger = Logger.getLogger(OS.class);
	
	public static String getName() {
		  String os = "";
		  if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
		    os = "windows";
		  } else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
		    os = "linux";
		  } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
		    os = "mac";
		  }
		  return os;
	}

	public static String getTimeStamp() {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
    	df.setTimeZone( TimeZone.getTimeZone("PST"));  
    	String date = ( df.format(new Date()));    
    	DateFormat df1 = new SimpleDateFormat("hh-mm-ss") ;    
    	df1.setTimeZone( TimeZone.getDefault()) ;  
    	String time = ( df1.format(new Date()));
    	return date+"_"+time;
    }

	public static void unyaffs(String yaffsfile, String folder) {
		try {
			File f = new File(folder);
			if (!f.exists()) f.mkdirs();
			else if (f.isFile()) throw new IOException("destination must be a folder");
			ProcessBuilderWrapper command = new ProcessBuilderWrapper(new String[] {getWorkDir()+File.separator+"x10flasher_lib"+File.separator+"unyaffs."+getName(),yaffsfile,folder},false);
		}
		catch (Exception e) {
			logger.warn("Failed : "+e.getMessage());
		}
	}
	
	public static String getAdbPath() {
		String fsep = OS.getFileSeparator();
		if (OS.getName().equals("windows"))
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"adb.exe").getAbsolutePath();
		else
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"adb."+OS.getName()).getAbsolutePath();
	}

	public static String getBin2SinPath() {
		String fsep = OS.getFileSeparator();
		if (OS.getName().equals("windows"))
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"bin2sin.exe").getAbsolutePath();
		else
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"bin2sin").getAbsolutePath();
	}

	public static String getBin2ElfPath() {
		String fsep = OS.getFileSeparator();
		if (OS.getName().equals("windows"))
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"bin2elf.exe").getAbsolutePath();
		else
			return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"bin2elf").getAbsolutePath();
	}

	public static String get7z() {
		String fsep = OS.getFileSeparator();
		return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"7z.exe").getAbsolutePath();	
}

	public static String getFastBootPath() {
		String fsep = OS.getFileSeparator();
	   if (OS.getName().equals("windows"))
		   return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"fastboot.exe").getAbsolutePath();
	   else
		   return new File(System.getProperty("user.dir")+fsep+"x10flasher_lib"+fsep+"fastboot."+OS.getName()).getAbsolutePath();
	}
	
	public static String getWorkDir() {
		return System.getProperty("user.dir");
	}

	public static String getSHA256(byte[] array) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(array, 0, array.length);
			byte[] sha256 = digest.digest();
			return HexDump.toHex(sha256);
		}
		catch(NoSuchAlgorithmException nsa) {
			throw new RuntimeException("Unable to process file for SHA-256", nsa);
		}
	}

	public static String getSHA1(byte[] array) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(array, 0, array.length);
			byte[] sha1 = digest.digest();
			return HexDump.toHex(sha1);
		}
		catch(NoSuchAlgorithmException nsa) {
			throw new RuntimeException("Unable to process file for SHA-256", nsa);
		}
	}

	public static String getSHA256(File f) {
		byte[] buffer = new byte[8192];
		int read = 0;
		InputStream is=null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			is = new FileInputStream(f);
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] sha256 = digest.digest();
			BigInteger bigInt = new BigInteger(1, sha256);
			String output = bigInt.toString(32);
			return output.toUpperCase();
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for SHA-256", e);
		}
		catch(NoSuchAlgorithmException nsa) {
			throw new RuntimeException("Unable to process file for SHA-256", nsa);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for SHA-256 calculation", e);
			}
		}
	}
	
	public static void copyfile(String srFile, String dtFile){
		  try{
		  File f1 = new File(srFile);
		  File f2 = new File(dtFile);
		  if (!f1.getAbsolutePath().equals(f2.getAbsolutePath())) {
			  InputStream in = new FileInputStream(f1);
			  
			  //For Append the file.
			//  OutputStream out = new FileOutputStream(f2,true);
	
			  //For Overwrite the file.
			  OutputStream out = new FileOutputStream(f2);
	
			  byte[] buf = new byte[1024];
			  int len;
			  while ((len = in.read(buf)) > 0){
			  out.write(buf, 0, len);
			  }
			  in.close();
			  out.close();
		  }
		  }
		  catch(FileNotFoundException ex){
			  logger.error(ex.getMessage() + " in the specified directory.");
		  }
		  catch(IOException e){
			  logger.error(e.getMessage());  
		  }
	}
	
	public static long getAlder32(File f) {
		try {
			FileInputStream inputStream = new FileInputStream(f);
			Adler32 adlerChecksum = new Adler32();
			CheckedInputStream cinStream = new CheckedInputStream(inputStream, adlerChecksum);
			byte[] b = new byte[128];
			while (cinStream.read(b) >= 0) {
			}
			long checksum = cinStream.getChecksum().getValue();
			cinStream.close();
			return checksum;
		} catch (IOException e) {
			return 0;
		}
	}

	public static String getMD5(File f) {
		byte[] buffer = new byte[8192];
		int read = 0;
		InputStream is=null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			is = new FileInputStream(f);
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			return String.format("%32s", output).replace(' ', '0');
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
		catch(NoSuchAlgorithmException nsa) {
			throw new RuntimeException("Unable to process file for MD5", nsa);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}
	
	public static String getVersion() {
		return System.getProperty("os.version");
	}

	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}
	
	public static String getWinDir() {
		if (System.getenv("WINDIR")==null) return System.getenv("SYSTEMROOT");
		if (System.getenv("WINDIR").length()==0) return System.getenv("SYSTEMROOT");
		return System.getenv("WINDIR");
	}
	
	public static String getSystem32Dir() {
		return getWinDir()+getFileSeparator()+"System32";
	}

	public static Collection<File> listFileTree(File dir) {
	    Set<File> fileTree = new HashSet<File>();
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile()) fileTree.add(entry);
	        else {
	        	fileTree.addAll(listFileTree(entry));
	        	fileTree.add(entry);
	        }
	    }
	    return fileTree;
	}

	
	public static byte[] copyBytes(byte[] arr, int length)
	{
		byte[] newArr = null;
		if (arr.length == length)
			newArr = arr;
		else
		{
			newArr = new byte[length];
			for (int i = 0; i < length; i++)
			{
				newArr[i] = (byte) arr[i];
			}
		}
		return newArr;
	}

	public static RandomAccessFile generateEmptyFile(String fname, long size, byte fill) {
		// To fill the empty file with FF values
		logger.info("File size : "+size/1024/1024+" Mb");
		try {
			byte[] empty = new byte[65*1024];
			for (int i=0; i<empty.length;i++)
				empty[i] = fill;		
			// Creation of empty file
			File f = new File(fname);
			f.delete();
			FileOutputStream fout = new FileOutputStream(f);
			LogProgress.initProgress(size/empty.length+size%empty.length);
			for (long i = 0; i<size/empty.length; i++) {
				fout.write(empty);
				LogProgress.updateProgress();
			}
			for (long i = 0; i<size%empty.length; i++) {
				fout.write(fill);
				LogProgress.updateProgress();
			}
			LogProgress.initProgress(0);
			fout.flush();
			fout.close();
			RandomAccessFile fo = new RandomAccessFile(f,"rw");
			return fo;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}

	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}

	public static void ZipExplodeToFolder(String zippath) throws FileNotFoundException, IOException  {
		byte buffer[] = new byte[10240];
		File zipfile = new File(zippath);
		File outfolder = new File(zipfile.getParentFile().getAbsolutePath()+File.separator+zipfile.getName().replace(".zip", "").replace(".ZIP", ""));
		outfolder.mkdirs();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zippath));
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			FileOutputStream fout = new FileOutputStream(outfolder.getAbsolutePath()+File.separator+ze.getName());
			int len;
			while ((len=zis.read(buffer))>0) {
				fout.write(buffer,0,len);
			}
			fout.close();
			ze=zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	public static void ZipExplodeToHere(String zippath) throws FileNotFoundException, IOException  {
		byte buffer[] = new byte[10240];
		File zipfile = new File(zippath);
		File outfolder = new File(zipfile.getParentFile().getAbsolutePath()+File.separator+zipfile.getName().replace(".zip", "").replace(".ZIP", ""));
		outfolder.mkdirs();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zippath));
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			FileOutputStream fout = new FileOutputStream(outfolder.getAbsolutePath()+File.separator+ze.getName());
			int len;
			while ((len=zis.read(buffer))>0) {
				fout.write(buffer,0,len);
			}
			fout.close();
			ze=zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	
	public static void viewAllThreads() {


    	// Walk up all the way to the root thread group
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

        listThreads(rootGroup, "");
    }


    // List all threads and recursively list all subgroup
    public static void listThreads(ThreadGroup group, String indent) {
        System.out.println(indent + "Group[" + group.getName() + 
        		":" + group.getClass()+"]");
        int nt = group.activeCount();
        Thread[] threads = new Thread[nt*2 + 10]; //nt is not accurate
        nt = group.enumerate(threads, false);

        // List every thread in the group
        for (int i=0; i<nt; i++) {
            Thread t = threads[i];
            System.out.println(indent + "  Thread[" + t.getName() 
            		+ ":" + t.getClass() + "]");
        }

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng*2 + 10];
        ng = group.enumerate(groups, false);

        for (int i=0; i<ng; i++) {
            listThreads(groups[i], indent + "  ");
        }
    }
    
    public static Manifest getManifest( Class<?> cl ) {
        InputStream inputStream = null;
        try {
          URLClassLoader classLoader = (URLClassLoader)cl.getClassLoader();
          String classFilePath = cl.getName().replace('.','/')+".class";
          URL classUrl = classLoader.getResource(classFilePath);
          if ( classUrl==null ) return null;
          String classUri = classUrl.toString();
          if ( !classUri.startsWith("jar:") ) return null;
          int separatorIndex = classUri.lastIndexOf('!');
          if ( separatorIndex<=0 ) return null;
          String manifestUri = classUri.substring(0,separatorIndex+2)+"META-INF/MANIFEST.MF";
          URL url = new URL(manifestUri);
          inputStream = url.openStream();
          return new Manifest( inputStream );
        } catch ( Throwable e ) {
          // handle errors
          //...
          return null;
        } finally {
          if ( inputStream!=null ) {
            try {
              inputStream.close();
            } catch ( Throwable e ) {
              // ignore
            }
          }
        }
      }
    
    public static String getChannel() {
    	try {
    		return OS.getManifest(OS.class).getMainAttributes().getValue("Internal-Channel");
    	} catch (Exception e) {
    		return "";
    	}
    }

    public static void decrypt(File infile) {
		byte[] buf = new byte[1024];
		try {
			if (!infile.getName().endsWith(".enc")) throw new IOException("Bad filename");
			RC4DecryptingInputStream in = new RC4DecryptingInputStream(new FileInputStream(infile));
			File outfile = new File(infile.getAbsolutePath().substring(0, infile.getAbsolutePath().length()-4));
	        OutputStream out = new FileOutputStream(outfile);
	        int len;
	        while((len = in.read(buf)) >= 0) {
	            out.write(buf, 0, len);
	        }
	        out.flush();
	        out.close();
	        in.close();
	      } catch(IOException e) {
	    	  e.printStackTrace();
	      }
	}

	  public static void encrypt(File infile) {
		  byte[] buf = new byte[1024];
	      try {
	    	  String outname = infile.getAbsolutePath()+".enc";
	    	  FileInputStream in = new FileInputStream(infile);
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

	  public static String getUserHome() {
		  	return System.getProperty("user.home");
 	  }

	  public static String getFolderCustom() {
		  return getWorkDir()+File.separator+"custom";
	  }

	  public static String getFolderDevices() {
			return OS.getWorkDir()+File.separator+"devices";
	  }

	  public static String getFolderUserFlashtool() {
		  new File(getUserHome()+File.separator+"FlashTool").mkdirs();
		  return getUserHome()+File.separator+"FlashTool";
	  }

	  public static String getFolderFirmwares() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares";
	  }

	  public static String getFolderFirmwaresPrepared() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"prepared").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"prepared";
	  }

	  public static String getFolderFirmwaresDownloaded() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"Downloads").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"Downloads";
	  }

	  public static String getFolderFirmwaresSinExtracted() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"sinExtracted").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"firmwares"+File.separator+"Downloads";
	  }

	  public static String getFolderCustomDevices() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"devices").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"devices";		  
	  }

	  public static String getFolderMyDevices() {
		  new File(getUserHome()+File.separator+"FlashTool"+File.separator+"registeredDevices").mkdirs();
		  return getUserHome()+File.separator+"FlashTool"+File.separator+"registeredDevices";
	  }

}