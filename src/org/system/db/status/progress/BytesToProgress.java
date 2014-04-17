package org.system.db.status.progress;

public class BytesToProgress
{
  public static final int BYTES_PER_UNIT_WORK = 1024;
  private final ProgressMonitor aProgress;
  private int aUnreportedBytes;
  
  public BytesToProgress(ProgressMonitor paramProgressMonitor)
  {
    this.aProgress = paramProgressMonitor;
    this.aUnreportedBytes = 0;
  }
  
  public static int bytesToWorkUnits(long paramLong)
  {
    return Math.round((float)paramLong / 1024.0F);
  }
  
  public void workedBytes(long paramLong)
  {
    this.aUnreportedBytes = ((int)(this.aUnreportedBytes + paramLong));
    if (this.aUnreportedBytes >= 1024)
    {
      int i = bytesToWorkUnits(this.aUnreportedBytes);
      this.aUnreportedBytes -= i * 1024;
      this.aProgress.worked(i);
    }
  }
  
  public void throwIfCanceled(String paramString)
    throws UserCanceledException
  {
    if (this.aProgress.isCanceled()) {
      throw new UserCanceledException(paramString);
    }
  }
  
  public void setBytesRemaining(long paramLong)
  {
    int i = bytesToWorkUnits(paramLong);
    this.aUnreportedBytes = 0;
    this.aProgress.setWorkRemaining(i);
  }
}