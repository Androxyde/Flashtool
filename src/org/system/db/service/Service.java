package org.system.db.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract interface Service
{
  public abstract ServiceId getId();
  
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract ServiceType getType();
  
  public abstract List<String> getModels();
  
  public abstract List<SalesItem> getSalesItems();
  
  public abstract boolean isTestOnly();
  
  public abstract String getDomain();
  
  public abstract Date getModifiedDate();
  
  public abstract String getSoftwareRevision();
  
  public abstract Set<ServiceRequirement> getRequirements();
}