package org.system.db.status.progress;

public abstract interface ProgressMonitorProvider
{
  public abstract ProgressMonitor getProgressMonitor();
}