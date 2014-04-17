package org.system.db;

import org.system.db.status.progress.ProgressMonitor;
import org.system.db.status.AppNotification;
import org.system.db.TessLocalDbConfig;

public abstract interface LocalDbLifecycleManager
{
  public abstract void startLocalDb(TessLocalDbConfig paramTessLocalDbConfig, ProgressMonitor paramProgressMonitor, AppNotification paramAppNotification)
    throws Exception;
  
  public abstract void stopLocalDb()
    throws Exception;
  
  public abstract TessLocalDbConfig getLocalDbConfigurator();
}