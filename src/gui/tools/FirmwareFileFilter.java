package gui.tools;

import java.io.File;
import java.io.FileFilter;

public class FirmwareFileFilter implements FileFilter
{
	private final String[] okFileExtensions = 
		new String[] {"sin", "ta"};

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
