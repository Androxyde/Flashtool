package org.system.db.service;

public class EveServiceId
  implements ServiceId
{
  static final String EVE_SERVICE_ID_PREFIX = "EveId:";
  private final String aIdStr;
  
  public EveServiceId(String paramString)
  {
    if (!paramString.startsWith("EveId:")) {
      this.aIdStr = ("EveId:" + paramString);
    } else {
      this.aIdStr = paramString;
    }
  }
  
  public String getAsString()
  {
    return this.aIdStr;
  }
  
  public String getIdWithoutPrefix()
  {
    return this.aIdStr.substring("EveId:".length());
  }
  
  public String toString()
  {
    return getAsString();
  }
  
  public int hashCode()
  {
    return this.aIdStr.hashCode();
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
    EveServiceId localEveServiceId = (EveServiceId)paramObject;
    if (this.aIdStr == null)
    {
      if (localEveServiceId.aIdStr != null) {
        return false;
      }
    }
    else if (!this.aIdStr.equals(localEveServiceId.aIdStr)) {
      return false;
    }
    return true;
  }
}