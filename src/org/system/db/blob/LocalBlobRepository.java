package org.system.db.blob;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

public abstract interface LocalBlobRepository
{
  public abstract File getFile(FileContentId paramFileContentId);
  
  public abstract BufferedInputStream getBlobAsStream(String paramString)
    throws IOException;
  
  public abstract boolean hasLocalFile(FileContentId paramFileContentId);
  
  public abstract void addLocalFile(FileContentId paramFileContentId);
  
  public abstract boolean deleteLocalFile(FileContentId paramFileContentId);
  
  public abstract File getNewFilePath(FileContentId paramFileContentId);
}