package org.flashtool.gui.tools;

import java.io.File;
import java.io.FileFilter;

import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtfFilter implements FileFilter{

	private String ext;
	
	public FtfFilter(String endswith) {
		ext = endswith;
		if (ext.length()==0) ext="ftf";
	}
	
	  public boolean accept(File file)
	  {
	      if (file.getName().toLowerCase().endsWith(ext.toLowerCase()) && file.isFile())
	      {
	        return true;
	      }
	    return false;
	  }

}
