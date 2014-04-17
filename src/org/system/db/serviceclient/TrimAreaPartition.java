package org.system.db.serviceclient;

public enum TrimAreaPartition
{
  TA,  MISC_TA;
  
  public String getDbText()
  {
    return name();
  }
}