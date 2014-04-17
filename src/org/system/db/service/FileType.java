package org.system.db.service;

public abstract interface FileType
{
  public abstract String getAsString();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}