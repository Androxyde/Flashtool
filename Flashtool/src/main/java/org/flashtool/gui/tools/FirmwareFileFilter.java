package org.flashtool.gui.tools;

import java.io.File;
import java.io.FileFilter;

import org.flashtool.gui.TARestore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirmwareFileFilter implements FileFilter
{
	private final String[] okFileExtensions = 
		new String[] {"sin", "ta", "fsc"};

  public boolean accept(File file)
  {
    for (String extension : okFileExtensions)
    {
      if (file.getName().toLowerCase().endsWith(extension) && file.isFile())
      {
        return true;
      }
    }
    return false;
  }
}
