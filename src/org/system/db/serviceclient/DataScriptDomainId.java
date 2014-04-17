package org.system.db.serviceclient;

import java.io.Serializable;

public class DataScriptDomainId
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final long aScriptId;
  private final long aDomainId;
  
  public DataScriptDomainId(long paramLong1, long paramLong2)
  {
    this.aScriptId = paramLong1;
    this.aDomainId = paramLong2;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (int)(this.aDomainId ^ this.aDomainId >>> 32);
    i = 31 * i + (int)(this.aScriptId ^ this.aScriptId >>> 32);
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
    DataScriptDomainId localDataScriptDomainId = (DataScriptDomainId)paramObject;
    if (this.aDomainId != localDataScriptDomainId.aDomainId) {
      return false;
    }
    return this.aScriptId == localDataScriptDomainId.aScriptId;
  }
  
  public String toString()
  {
    return "DataScriptDomainId(" + this.aScriptId + "," + this.aDomainId + ")";
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
}