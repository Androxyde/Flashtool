package org.system.db.status.progress;

public class SubMonitor
  implements ProgressMonitor
{
  private ProgressMonitor aRootMonitor;
  private int aTotalParent;
  private int aUsedForParent;
  private int aTotalChildren;
  private double aUsedForChildren;
  private boolean aIsCancelable;
  private ProgressMonitor aChild;
  private boolean aSuppressBeginTask;
  static final int DEFAULT_RESOLUTION = 1000;
  
  SubMonitor(ProgressMonitor paramProgressMonitor, int paramInt1, int paramInt2)
  {
    this.aRootMonitor = paramProgressMonitor;
    this.aTotalParent = paramInt1;
    this.aTotalChildren = paramInt2;
    this.aUsedForParent = 0;
    this.aUsedForChildren = 0.0D;
    this.aSuppressBeginTask = false;
  }
  
  public SubMonitor newChild(int paramInt, boolean paramBoolean)
  {
    double d = Math.min(paramInt, this.aTotalChildren - this.aUsedForChildren);
    cleanupChild();
    SubMonitor localSubMonitor = new SubMonitor(this.aRootMonitor, consume(d), 0);
    localSubMonitor.setSuppressBeginTask(paramBoolean);
    this.aChild = localSubMonitor;
    return localSubMonitor;
  }
  
  public SubMonitor newChild(int paramInt)
  {
    return newChild(paramInt, this.aSuppressBeginTask);
  }
  
  public static SubMonitor convert(ProgressMonitor paramProgressMonitor, String paramString, int paramInt)
  {
    if ((paramProgressMonitor instanceof SubMonitor))
    {
      paramProgressMonitor.beginTask(paramString, paramInt, paramProgressMonitor.isCancelable());
      return (SubMonitor)paramProgressMonitor;
    }
    paramProgressMonitor.beginTask(paramString, 1000, paramProgressMonitor.isCancelable());
    return new SubMonitor(paramProgressMonitor, 1000, paramInt);
  }
  
  void setSuppressBeginTask(boolean paramBoolean)
  {
    this.aSuppressBeginTask = paramBoolean;
  }
  
  public void beginTask(String paramString, int paramInt, boolean paramBoolean)
  {
    if (!this.aSuppressBeginTask) {
      this.aRootMonitor.setTaskName(paramString);
    }
    setWorkRemaining(paramInt);
    this.aIsCancelable = paramBoolean;
    this.aRootMonitor.subTask(paramString);
  }
  
  public void done()
  {
    cleanupChild();
    int i = this.aTotalParent - this.aUsedForParent;
    if (i > 0) {
      this.aRootMonitor.worked(i);
    }
    this.aTotalParent = 0;
    this.aUsedForParent = 0;
    this.aTotalChildren = 0;
    this.aUsedForChildren = 0.0D;
  }
  
  public boolean isCancelable()
  {
    return this.aRootMonitor.isCancelable();
  }
  
  public boolean isCanceled()
  {
    return this.aRootMonitor.isCanceled();
  }
  
  public void setCancelable(boolean paramBoolean)
  {
    this.aRootMonitor.setCancelable(paramBoolean);
  }
  
  public void setCanceled(boolean paramBoolean)
  {
    this.aRootMonitor.setCanceled(paramBoolean);
  }
  
  public void setTaskName(String paramString)
  {
    this.aRootMonitor.setTaskName(paramString);
  }
  
  public void setWorkRemaining(int paramInt)
  {
    if ((this.aTotalChildren > 0) && (this.aTotalParent > this.aUsedForParent))
    {
      double d = this.aTotalParent * (1.0D - this.aUsedForChildren / this.aTotalChildren);
      this.aUsedForChildren = (paramInt * (1.0D - d / (this.aTotalParent - this.aUsedForParent)));
    }
    else
    {
      this.aUsedForChildren = 0.0D;
    }
    this.aTotalParent -= this.aUsedForParent;
    this.aUsedForParent = 0;
    this.aTotalChildren = paramInt;
  }
  
  int getWorkRemaining()
  {
    return (int)(this.aTotalChildren - this.aUsedForChildren);
  }
  
  int consume(double paramDouble)
  {
    if ((this.aTotalParent == 0) || (this.aTotalChildren == 0)) {
      return 0;
    }
    this.aUsedForChildren += paramDouble;
    if (this.aUsedForChildren > this.aTotalChildren) {
      this.aUsedForChildren = this.aTotalChildren;
    } else if (this.aUsedForChildren < 0.0D) {
      this.aUsedForChildren = 0.0D;
    }
    int i = (int)(this.aTotalParent * this.aUsedForChildren / this.aTotalChildren);
    int j = i - this.aUsedForParent;
    this.aUsedForParent = i;
    return j;
  }
  
  public void worked(int paramInt)
  {
    int i = consume(paramInt);
    if (i != 0) {
      this.aRootMonitor.worked(i);
    }
  }
  
  public void subTask(String paramString)
  {
    this.aRootMonitor.subTask(paramString);
  }
  
  private void cleanupChild()
  {
    if (this.aChild != null) {
      this.aChild.done();
    }
    this.aChild = null;
  }
  
  public int getTotalWorked()
  {
    return (int)this.aUsedForChildren;
  }
  
  public void timeLeft(int paramInt) {}
}