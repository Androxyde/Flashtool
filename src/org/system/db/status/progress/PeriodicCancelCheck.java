package org.system.db.status.progress;

import java.util.Timer;
import java.util.TimerTask;

public abstract class PeriodicCancelCheck
{
  private static final long DEFAULT_INTERVAL = 500L;
  private static final Timer aTimer = new Timer("PeriodicCancelCheckTimer", true);
  private final ProgressMonitor aProgressMonitor;
  private final TimerTask aCheckCanceledTask;
  private final long aInterval;
  private long aStartTime;
  private long aStopTime;
  
  public PeriodicCancelCheck(ProgressMonitor paramProgressMonitor, long paramLong)
  {
    this.aProgressMonitor = paramProgressMonitor;
    this.aInterval = paramLong;
    this.aCheckCanceledTask = new CheckCancelTask();
  }
  
  public PeriodicCancelCheck(ProgressMonitor paramProgressMonitor)
  {
    this(paramProgressMonitor, 500L);
  }
  
  public void start()
  {
    aTimer.schedule(this.aCheckCanceledTask, this.aInterval, this.aInterval);
    this.aStartTime = System.currentTimeMillis();
  }
  
  public void stop()
  {
    this.aCheckCanceledTask.cancel();
    this.aStopTime = System.currentTimeMillis();
  }
  
  public long getExecutionTime()
  {
    if (this.aStopTime > 0L) {
      return this.aStopTime - this.aStartTime;
    }
    if (this.aStartTime > 0L) {
      return System.currentTimeMillis() - this.aStartTime;
    }
    return 0L;
  }
  
  public boolean isCanceled()
  {
    return this.aProgressMonitor.isCanceled();
  }
  
  public abstract void cancelOperation();
  
  class CheckCancelTask
    extends TimerTask
  {
    CheckCancelTask() {}
    
    public void run()
    {
      if (PeriodicCancelCheck.this.aProgressMonitor.isCanceled())
      {
        PeriodicCancelCheck.this.cancelOperation();
        cancel();
      }
    }
  }
}