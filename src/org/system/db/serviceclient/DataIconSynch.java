package org.system.db.serviceclient;

import java.io.Serializable;

public class DataIconSynch
  extends DataIconSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aIconId;
  public long aIconLastUpdated;
  
  public DataIconSynch(long paramLong1, long paramLong2)
  {
    this.aIconId = paramLong1;
    this.aIconLastUpdated = paramLong2;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (int)(this.aIconId ^ this.aIconId >>> 32);
    i = 31 * i + (int)(this.aIconLastUpdated ^ this.aIconLastUpdated >>> 32);
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    DataIconSynch localDataIconSynch = (DataIconSynch)paramObject;
    if (this.aIconId != localDataIconSynch.aIconId) {
      return false;
    }
    return this.aIconLastUpdated == localDataIconSynch.aIconLastUpdated;
  }
  
  public long getIconId()
  {
    return this.aIconId;
  }
  
  public long getIconLastUpdated()
  {
    return this.aIconLastUpdated;
  }
}