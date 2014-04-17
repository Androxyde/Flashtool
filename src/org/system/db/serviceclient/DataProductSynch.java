package org.system.db.serviceclient;

import java.io.Serializable;

public class DataProductSynch
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aId;
  public long aProductVersionNumber;
  public long aProductLastUpdated;
  public DataIconSynchResult aIconSynch;
  public long aSecurePass;
  
  public DataProductSynch(long paramLong1, long paramLong2, long paramLong3, DataIconSynchResult paramDataIconSynchResult, long paramLong4)
  {
    this.aId = paramLong1;
    this.aProductVersionNumber = paramLong2;
    this.aProductLastUpdated = paramLong3;
    this.aIconSynch = paramDataIconSynchResult;
    this.aSecurePass = paramLong4;
  }
}