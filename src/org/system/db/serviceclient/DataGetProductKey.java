package org.system.db.serviceclient;

import java.io.Serializable;

public class DataGetProductKey
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aProductId;
  public long aVersionNumber;
  public long aProductLastUpdated;
  public long aSecurePass;
  
  public DataGetProductKey(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.aProductId = paramLong1;
    this.aVersionNumber = paramLong2;
    this.aProductLastUpdated = paramLong3;
    this.aSecurePass = paramLong4;
  }
}