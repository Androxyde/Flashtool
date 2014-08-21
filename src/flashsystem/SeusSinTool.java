package flashsystem;

import java.io.*;

import org.system.OS;
import org.system.ProcessBuilderWrapper;

import com.sonymobile.cs.generic.encoding.RC4DecryptingInputStream;
import com.sonymobile.cs.generic.encoding.RC4EncryptingOutputStream;

public class SeusSinTool {

	public static void decrypt(String sinfile) {
		byte[] buf = new byte[1024];
		try {
			String folder = new File((new File(sinfile)).getParent()).getAbsolutePath();
			FileInputStream f = new FileInputStream(sinfile);
			//DecodeInputStream in = new DecodeInputStream(f);
			RC4DecryptingInputStream in = new RC4DecryptingInputStream(f);
	        String basefile = sinfile+"_dek";
	        OutputStream out = new FileOutputStream(basefile+".zip.gz");
	        int len;
	        while((len = in.read(buf)) >= 0) {
	            out.write(buf, 0, len);
	        }
	        out.flush();
	        out.close();
	        in.close();
	        
	        byte[] magic = new byte[2];
        	FileInputStream fload = new FileInputStream(new File(basefile+".zip.gz"));
        	fload.read(magic);
        	fload.close();

        	if (HexDump.toHex(magic).equals("[1F, 8B]")) {
		        File fxml = new File(folder+"\\update.xml");
		        if (fxml.isFile()) fxml.renameTo(new File(folder+"\\update1.xml"));
		        try {
			        if (OS.getName().equals("windows")) {
			        	ProcessBuilderWrapper run = new ProcessBuilderWrapper(new String[] {OS.get7z(),"e", "-y", basefile+".zip.gz", "-o"+folder},false);
			        }
			        else {
			        	ProcessBuilderWrapper run = new ProcessBuilderWrapper(new String[] {"gunzip", basefile+".zip.gz"},false);
			        }
		        } catch (Exception e) {}
	        	fload = new FileInputStream(new File(basefile+".zip"));
	        	fload.read(magic);
	        	fload.close();
	        	if (HexDump.toHex(magic).equals("[50, 4B]")) {
	        		try {
				        if (OS.getName().equals("windows")) {
				        	ProcessBuilderWrapper run1 = new ProcessBuilderWrapper(new String[] {OS.get7z(), "e", "-y", basefile+".zip", "-o"+folder},false);
				        }
				        else {
				        	ProcessBuilderWrapper run1 = new ProcessBuilderWrapper(new String[] {"unzip", "-o", basefile+".zip","-d",folder},false);
				        }
	        		}
	        		catch (Exception e1) {}	        		
	        	}
	        	else if (HexDump.toHex(magic).equals("[2F, 2F]")) {
	        		File fl = new File(basefile+".zip");
	        		fl.renameTo(new File(folder+"/preset1.ta"));	        		
	        	}
	        	else { 
	        		File fl = new File(basefile+".zip");
	        		fl.renameTo(new File(folder+"/loader.sin"));
	        	}
        	}

        	File fdek = new File(basefile+".zip.gz");
	        fdek.delete();
	        File ftar = new File(basefile+".zip");
	        ftar.delete();
	        if (new File(folder+File.separator+"boot.zip").exists()) {
	        	OS.ZipExplode(folder+File.separator+"boot.zip");
	        	new File(folder+File.separator+"boot.zip").delete();
	        }
	      } catch(IOException e) {
	    	  e.printStackTrace();
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