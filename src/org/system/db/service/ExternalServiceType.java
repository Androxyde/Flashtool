package org.system.db.service;

public enum ExternalServiceType
{
  Reinstall(ServiceType.UPDATE_CONTENT_REFRESH),  Update(ServiceType.UPDATE);
  
  private final ServiceType aType;
  
  private ExternalServiceType(ServiceType paramServiceType)
  {
    this.aType = paramServiceType;
  }
  
  public ServiceType getType()
  {
    return this.aType;
  }
}