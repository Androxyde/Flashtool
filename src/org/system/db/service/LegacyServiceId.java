package org.system.db.service;

public class LegacyServiceId
  implements ServiceId
{
  static final String LEGACY_ID_PREFIX = "LegacyId:";
  private final long aScriptId;
  private final long aDomainId;
  
  public LegacyServiceId(long paramLong1, long paramLong2)
  {
    this.aScriptId = paramLong1;
    this.aDomainId = paramLong2;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public int hashCode()
  {
    Long localLong = Long.valueOf(this.aScriptId);
    return localLong.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof LegacyServiceId))
    {
      LegacyServiceId localLegacyServiceId = (LegacyServiceId)paramObject;
      bool = (localLegacyServiceId.getScriptId() == getScriptId()) && (localLegacyServiceId.getDomainId() == getDomainId());
    }
    return bool;
  }
  
  public String toString()
  {
    return "LegacyServiceId [Domain Id=" + this.aDomainId + ", Script Id=" + this.aScriptId + "]";
  }
  
  public String getAsString()
  {
    return "LegacyId:" + this.aScriptId + "_" + this.aDomainId;
  }
}