package org.system.db.service;

import java.util.Arrays;
import java.util.List;

public enum ServiceTypeGroup
{
  Reinstall(Arrays.asList(new ServiceType[] { ServiceType.UPDATE_CONTENT_ERASE, ServiceType.UPDATE_CONTENT_REFRESH })),  Update(Arrays.asList(new ServiceType[] { ServiceType.UPDATE }));
  
  private final List<ServiceType> aTypes;
  
  private ServiceTypeGroup(List<ServiceType> paramList)
  {
    this.aTypes = paramList;
  }
  
  public List<ServiceType> getTypes()
  {
    return this.aTypes;
  }
}