package org.system.db.status.progress;

public class NullProgressMonitor
  implements ProgressMonitor
{
  public void beginTask(String paramString, int paramInt, boolean paramBoolean) {}
  
  public void done() {}
  
  public boolean isCancelable()
  {
    return false;
  }
  
  public boolean isCanceled()
  {
    return false;
  }
  
  public void setCancelable(boolean paramBoolean) {}
  
  public void setCanceled(boolean paramBoolean) {}
  
  public void setTaskName(String paramString) {}
  
  public void setWorkRemaining(int paramInt) {}
  
  public void subTask(String paramString) {}
  
  public void worked(int paramInt) {}
  
  public int getTotalWorked()
  {
    return 0;
  }
  
  public void timeLeft(int paramInt) {}
}