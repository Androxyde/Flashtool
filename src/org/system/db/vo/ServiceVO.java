package org.system.db.vo;

import org.system.db.serviceclient.DataIdentifier;
import org.system.db.service.LegacyServiceId;
import org.system.db.service.ServiceType;

public class ServiceVO
{
  private final LegacyServiceId aServiceId;
  private String aName;
  private ServiceType aServiceType;
  private String aCode;
  private ConfigurationVO aConfiguration;
  private String aDescription;
  private boolean aOnlineOnly;
  private long aPidfId;
  private String aDomainName;
  private boolean aIsTestVersion;
  private DataIdentifier[] aDataIdentifiers;
  private long aLocalId;
  private long aLastModifiedDate;
  private long aSynchronizationTime;
  
  public ServiceVO(LegacyServiceId paramLegacyServiceId)
  {
    this.aServiceId = paramLegacyServiceId;
  }
  
  public String getCode()
  {
    return this.aCode;
  }
  
  public ConfigurationVO getConfiguration()
  {
    return this.aConfiguration;
  }
  
  public LegacyServiceId getServiceId()
  {
    return this.aServiceId;
  }
  
  public String getName()
  {
    return this.aName;
  }
  
  public ServiceType getServiceType()
  {
    return this.aServiceType;
  }
  
  public void setLastModifiedDate(long paramLong)
  {
    this.aLastModifiedDate = paramLong;
  }
  
  public long getLastModifiedDate()
  {
    return this.aLastModifiedDate;
  }
  
  public void setName(String paramString)
  {
    this.aName = paramString;
  }
  
  public void setServiceType(ServiceType paramServiceType)
  {
    this.aServiceType = paramServiceType;
  }
  
  public void setCode(String paramString)
  {
    this.aCode = paramString;
  }
  
  public void setConfiguration(ConfigurationVO paramConfigurationVO)
  {
    this.aConfiguration = paramConfigurationVO;
  }
  
  public String getDescription()
  {
    return this.aDescription;
  }
  
  public void setDescription(String paramString)
  {
    this.aDescription = paramString;
  }
  
  public boolean isOnlineOnly()
  {
    return this.aOnlineOnly;
  }
  
  public void setOnlineOnly(boolean paramBoolean)
  {
    this.aOnlineOnly = paramBoolean;
  }
  
  public long getPidfId()
  {
    return this.aPidfId;
  }
  
  public void setPidfId(long paramLong)
  {
    this.aPidfId = paramLong;
  }
  
  public String getDomainName()
  {
    return this.aDomainName;
  }
  
  public void setDomainName(String paramString)
  {
    this.aDomainName = paramString;
  }
  
  public boolean isTestVersion()
  {
    return this.aIsTestVersion;
  }
  
  public void setTestVersion(boolean paramBoolean)
  {
    this.aIsTestVersion = paramBoolean;
  }
  
  public void setDataIdentifiers(DataIdentifier[] paramArrayOfDataIdentifier)
  {
    this.aDataIdentifiers = ((DataIdentifier[])paramArrayOfDataIdentifier.clone());
  }
  
  public DataIdentifier[] getDataIdentifiers()
  {
    return this.aDataIdentifiers;
  }
  
  public long getLocalId()
  {
    return this.aLocalId;
  }
  
  public void setLocalId(long paramLong)
  {
    this.aLocalId = paramLong;
  }
  
  public void setSynchronizationTime(long paramLong)
  {
    this.aSynchronizationTime = paramLong;
  }
  
  public String toString()
  {
    return "ServiceVO [ServiceId=" + this.aServiceId + ", Name=" + this.aName + ", ServiceType=" + this.aServiceType + ", Description=" + this.aDescription + "]";
  }
  
  public long getSynchronizationTime()
  {
    return this.aSynchronizationTime;
  }
}