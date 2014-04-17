package org.system.db.blob;

public final class FileContentId
{
  public static final String FILE_PREFIX = "FILE_";
  private final long aContentId;
  
  public FileContentId(long paramLong)
  {
    this.aContentId = paramLong;
  }
  
  public static FileContentId parseFromFileName(String paramString)
  {
    if ((paramString == null) || (!paramString.startsWith("FILE_"))) {
      return null;
    }
    try
    {
      String str = paramString.substring("FILE_".length());
      long l = Long.parseLong(str);
      return new FileContentId(l);
    }
    catch (Exception localException) {}
    return null;
  }
  
  public long toLong()
  {
    return this.aContentId;
  }
  
  public String toFileName()
  {
    return "FILE_" + this.aContentId;
  }
  
  public String toString()
  {
    return toFileName();
  }
  
  public int hashCode()
  {
    return (int)(this.aContentId ^ this.aContentId >>> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof FileContentId))
    {
      FileContentId localFileContentId = (FileContentId)paramObject;
      return localFileContentId.toLong() == this.aContentId;
    }
    return false;
  }
}