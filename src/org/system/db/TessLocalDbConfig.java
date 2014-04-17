package org.system.db;

public abstract interface TessLocalDbConfig
{
  public static final int UNKNOWN_LOCAL_DB_VERSION = -1;
  
  public abstract void setVersion(int paramInt);
  
  public abstract int getVersion();
  
  public abstract String getDbPathForCurrentUser();
  
  public abstract boolean inMemoryOnly();
  
  public abstract String getLocalDbName();
}
