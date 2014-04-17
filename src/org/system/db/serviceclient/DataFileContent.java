package org.system.db.serviceclient;

import java.io.Serializable;

@Deprecated
public class DataFileContent
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aFileContentInfoId;
  public String aFileSecurityName;
  public byte[] aFileContent;
  
  public DataFileContent(long paramLong, String paramString, byte[] paramArrayOfByte)
  {
    this.aFileContentInfoId = paramLong;
    this.aFileSecurityName = paramString;
    this.aFileContent = paramArrayOfByte;
  }
}