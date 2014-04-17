package org.system.db.serviceclient;

import java.io.Serializable;

public class DataGetScriptKey
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aScriptId;
  public long aDomainId;
  public long aVersionNumber;
  public long aScriptLastUpdated;
  public long aSecurePass;
  
  public DataGetScriptKey(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
  {
    this.aScriptId = paramLong1;
    this.aDomainId = paramLong2;
    this.aVersionNumber = paramLong3;
    this.aScriptLastUpdated = paramLong4;
    this.aSecurePass = paramLong5;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (int)(this.aDomainId ^ this.aDomainId >>> 32);
    i = 31 * i + (int)(this.aScriptId ^ this.aScriptId >>> 32);
    i = 31 * i + (int)(this.aScriptLastUpdated ^ this.aScriptLastUpdated >>> 32);
    i = 31 * i + (int)(this.aSecurePass ^ this.aSecurePass >>> 32);
    i = 31 * i + (int)(this.aVersionNumber ^ this.aVersionNumber >>> 32);
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    DataGetScriptKey localDataGetScriptKey = (DataGetScriptKey)paramObject;
    if (this.aDomainId != localDataGetScriptKey.aDomainId) {
      return false;
    }
    if (this.aScriptId != localDataGetScriptKey.aScriptId) {
      return false;
    }
    if (this.aScriptLastUpdated != localDataGetScriptKey.aScriptLastUpdated) {
      return false;
    }
    if (this.aSecurePass != localDataGetScriptKey.aSecurePass) {
      return false;
    }
    return this.aVersionNumber == localDataGetScriptKey.aVersionNumber;
  }
  
  public String toString()
  {
    return "DataGetScriptKey(" + this.aScriptId + "," + this.aDomainId + "," + this.aVersionNumber + "," + this.aScriptLastUpdated + "," + this.aSecurePass + ")";
  }
}