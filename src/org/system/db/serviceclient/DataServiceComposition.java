package org.system.db.serviceclient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DataServiceComposition
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final long aServiceId;
  private final long aConfigurationId;
  private final Map<String, DataConfigurationAttribute> aAttributes;
  private final String aCode;
  private final String aServiceType;
  private final String aServiceName;
  private final String aServiceDescription;
  private final long aDomainId;
  private final String aDomainName;
  private final long aPidfId;
  private final boolean aIsOnlineOnly;
  private final boolean aIsTestVersion;
  private final DataIdentifier[] aDataIdentifiers;
  private final long aLastModified;
  
  public DataServiceComposition(long paramLong1, long paramLong2, Map<String, DataConfigurationAttribute> paramMap, String paramString1, String paramString2, String paramString3, String paramString4, long paramLong3, String paramString5, long paramLong4, boolean paramBoolean1, boolean paramBoolean2, DataIdentifier[] paramArrayOfDataIdentifier, long paramLong5)
  {
    this.aServiceId = paramLong1;
    this.aConfigurationId = paramLong2;
    this.aAttributes = paramMap;
    this.aCode = paramString1;
    this.aServiceType = paramString2;
    this.aServiceName = paramString3;
    this.aServiceDescription = paramString4;
    this.aDomainId = paramLong3;
    this.aDomainName = paramString5;
    this.aPidfId = paramLong4;
    this.aIsOnlineOnly = paramBoolean1;
    this.aIsTestVersion = paramBoolean2;
    this.aDataIdentifiers = paramArrayOfDataIdentifier;
    this.aLastModified = paramLong5;
  }
  
  public Map<String, DataConfigurationAttribute> getAttributes()
  {
    return this.aAttributes;
  }
  
  public Map<String, DataFileSynch> getFileAttributes()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = getAttributes().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if ((((DataConfigurationAttribute)localEntry.getValue()).getAttributeValue() instanceof DataFileSynch)) {
        localHashMap.put((String)localEntry.getKey(), (DataFileSynch)((DataConfigurationAttribute)localEntry.getValue()).getAttributeValue());
      }
    }
    return localHashMap;
  }
  
  public String getCode()
  {
    return this.aCode;
  }
  
  public long getServiceId()
  {
    return this.aServiceId;
  }
  
  public long getConfigurationId()
  {
    return this.aConfigurationId;
  }
  
  public String getServiceType()
  {
    return this.aServiceType;
  }
  
  public String getServiceName()
  {
    return this.aServiceName;
  }
  
  public String getServiceDescription()
  {
    return this.aServiceDescription;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public String getDomainName()
  {
    return this.aDomainName;
  }
  
  public long getPidfId()
  {
    return this.aPidfId;
  }
  
  public boolean isOnlineOnly()
  {
    return this.aIsOnlineOnly;
  }
  
  public boolean isTestVersion()
  {
    return this.aIsTestVersion;
  }
  
  public DataIdentifier[] getDataIdentifier()
  {
    return this.aDataIdentifiers;
  }
  
  public long getLastUpdated()
  {
    return this.aLastModified;
  }
}