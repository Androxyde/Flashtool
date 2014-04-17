package org.system.db.serviceclient;

import java.io.Serializable;
import java.net.URL;

public class DataFilePart
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final URL aUrlToFileContent;
  private final long aContentLength;
  private final long aChecksumValue;
  
  public DataFilePart(URL paramURL, long paramLong1, long paramLong2)
  {
    this.aUrlToFileContent = paramURL;
    this.aContentLength = paramLong1;
    this.aChecksumValue = paramLong2;
  }
  
  public URL getUrlToFileContent()
  {
    return this.aUrlToFileContent;
  }
  
  public long getContentLength()
  {
    return this.aContentLength;
  }
  
  public long getChecksumValue()
  {
    return this.aChecksumValue;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    localStringBuilder.append(this.aUrlToFileContent);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.aContentLength);
    localStringBuilder.append(", ");
    localStringBuilder.append(this.aChecksumValue);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}