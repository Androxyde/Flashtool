package org.system.db.status.progress;

public class BasicProgressMonitor
  implements ProgressMonitor
{
  private String aTaskName;
  private int aTotalWork;
  private int aWorked;
  private boolean aIsCancelable = false;
  private boolean aIsCanceled = false;
  private int aSecondsLeft;
  
  public void beginTask(String paramString, int paramInt, boolean paramBoolean)
  {
    this.aWorked = 0;
    this.aTaskName = paramString;
    this.aTotalWork = paramInt;
    this.aIsCancelable = paramBoolean;
  }
  
  public void done()
  {
    this.aWorked = this.aTotalWork;
  }
  
  public boolean isCancelable()
  {
    return this.aIsCancelable;
  }
  
  public boolean isCanceled()
  {
    return this.aIsCanceled;
  }
  
  public void setCancelable(boolean paramBoolean)
  {
    this.aIsCancelable = paramBoolean;
  }
  
  public void setCanceled(boolean paramBoolean)
  {
    this.aIsCanceled = paramBoolean;
  }
  
  public void setTaskName(String paramString)
  {
    this.aTaskName = paramString;
  }
  
  public void setWorkRemaining(int paramInt)
  {
    int i = this.aTotalWork - this.aWorked;
    this.aTotalWork = (this.aTotalWork + paramInt - i);
  }
  
  public void worked(int paramInt)
  {
    this.aWorked += paramInt;
  }
  
  public String getTaskName()
  {
    return this.aTaskName;
  }
  
  public int getTotalWork()
  {
    return this.aTotalWork;
  }
  
  public int getTimeLeft()
  {
    return this.aSecondsLeft;
  }
  
  public int getTotalWorked()
  {
    return this.aWorked;
  }
  
  public void subTask(String paramString)
  {
    setTaskName(paramString);
  }
  
  public void timeLeft(int paramInt)
  {
    this.aSecondsLeft = paramInt;
  }
}