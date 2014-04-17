package org.system.db.serviceclient;

import java.io.Serializable;

public class DataGetFileKey
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aFileVersionId;
  public long aSecurePass;
  
  public DataGetFileKey(long paramLong1, long paramLong2)
  {
    this.aFileVersionId = paramLong1;
    this.aSecurePass = paramLong2;
  }
}