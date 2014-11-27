package flashsystem;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.sinfile.parsers.SinFileException;
import org.system.OS;
import org.system.ProcessBuilderWrapper;
import org.util.HexDump;

import com.sonymobile.cs.generic.encoding.RC4DecryptingInputStream;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;

public class SeusSinTool {

	public static void decrypt(String sinfile) throws FileNotFoundException,IOException {
		String folder = new File((new File(sinfile)).getParent()).getAbsolutePath();
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
	    
	    ZipFile file=null;
	    try {
	    	 file = new ZipFile(basefile);
	    	 Enumeration<? extends ZipEntry> entries = file.entries();
	    	 while ( entries.hasMoreElements() ) {
	    		 ZipEntry entry = entries.nextElement();
	    		 System.out.println( entry.getName() );
	    		 //use entry input stream:
	    		 //readInputStream( file.getInputStream( entry ) )
	    	 }
	    } catch (Exception e) {
	    	try {
	    		org.sinfile.parsers.SinFile sf = new org.sinfile.parsers.SinFile(new File(basefile));
	    		System.out.println(basefile + " is " + sf.getType());
	    	} catch (SinFileException sine) {
	    		System.out.println(basefile+" is not a sin file");
	    	}
	    }
	    finally {
	    	try {
	    		file.close();
	    	} catch (Exception e) {}
	    }

	}
/*
 * final ZipFile file = new ZipFile( FILE_NAME );
try
{
    final Enumeration<? extends ZipEntry> entries = file.entries();
    while ( entries.hasMoreElements() )
    {
        final ZipEntry entry = entries.nextElement();
        System.out.println( entry.getName() );
        //use entry input stream:
        readInputStream( file.getInputStream( entry ) )
    }
}
finally
{
    file.close();
}
    
private static int readInputStream( final InputStream is ) throws IOException {
    final byte[] buf = new byte[ 8192 ];
    int read = 0;
    int cntRead;
    while ( ( cntRead = is.read( buf, 0, buf.length ) ) >=0  )
    {
        read += cntRead;
    }
    return read;
}
 */
	public static void extractDecrypted(String sinfile) throws FileNotFoundException,IOException {
		String folder = new File((new File(sinfile)).getParent()).getAbsolutePath();
		try {
		
	        	ZipInputStream zis =  new ZipInputStream(new FileInputStream(sinfile));
	        	ZipEntry ze = zis.getNextEntry();
	        	while (ze!=null) {
	        		System.out.println(ze.getName());
	        		ze=zis.getNextEntry();
	        	}
	        } catch (Exception zipe){
	        	zipe.printStackTrace();
	        }
	        byte[] magic = new byte[2];
	        
        	FileInputStream fload = new FileInputStream(new File(sinfile));
        	fload.read(magic);
        	fload.close();
        	if (HexDump.toHex(magic).equals("[1F, 8B]")) {
		        File fxml = new File(folder+"\\update.xml");
		        if (fxml.isFile()) fxml.renameTo(new File(folder+"\\update1.xml"));
		        try {
			        if (OS.getName().equals("windows")) {
			        	ProcessBuilderWrapper run = new ProcessBuilderWrapper(new String[] {OS.get7z(),"e", "-y", sinfile, "-o"+folder},false);
			        }
			        else {
			        	ProcessBuilderWrapper run = new ProcessBuilderWrapper(new String[] {"gunzip", sinfile},false);
			        }
		        } catch (Exception e) {e.printStackTrace();}
	        	fload = new FileInputStream(new File(sinfile));
	        	fload.read(magic);
	        	fload.close();
	        	if (HexDump.toHex(magic).equals("[50, 4B]")) {
	        		try {
				        if (OS.getName().equals("windows")) {
				        	ProcessBuilderWrapper run1 = new ProcessBuilderWrapper(new String[] {OS.get7z(), "e", "-y", sinfile+".zip", "-o"+folder},false);
				        }
				        else {
				        	ProcessBuilderWrapper run1 = new ProcessBuilderWrapper(new String[] {"unzip", "-o", sinfile+".zip","-d",folder},false);
				        }
	        		}
	        		catch (Exception e1) {e1.printStackTrace();}	        		
	        	}
	        	else if (HexDump.toHex(magic).equals("[2F, 2F]")) {
	        		File fl = new File(sinfile+".zip");
	        		fl.renameTo(new File(folder+"/preset1.ta"));	        		
	        	}
	        	else { 
	        		File fl = new File(sinfile+".zip");
	        		fl.renameTo(new File(folder+"/loader.sin"));
	        	}
        	}

        	File fdek = new File(sinfile+".zip.gz");
	        fdek.delete();
	        File ftar = new File(sinfile+".zip");
	        ftar.delete();
	        if (new File(folder+File.separator+"boot.zip").exists()) {
	        	OS.ZipExplode(folder+File.separator+"boot.zip");
	        	new File(folder+File.separator+"boot.zip").delete();
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

}