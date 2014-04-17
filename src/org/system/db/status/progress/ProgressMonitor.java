package org.system.db.status.progress;

public abstract interface ProgressMonitor
{
  public static final int UNKNOWN = -1;
  
  public abstract void beginTask(String paramString, int paramInt, boolean paramBoolean);
  
  public abstract void worked(int paramInt);
  
  public abstract int getTotalWorked();
  
  public abstract boolean isCanceled();
  
  public abstract void setCanceled(boolean paramBoolean);
  
  public abstract boolean isCancelable();
  
  public abstract void setCancelable(boolean paramBoolean);
  
  public abstract void setWorkRemaining(int paramInt);
  
  public abstract void setTaskName(String paramString);
  
  public abstract void subTask(String paramString);
  
  public abstract void done();
  
  public abstract void timeLeft(int paramInt);
}