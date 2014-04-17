package org.system.db.status.progress;

public final class UserCanceledException
  extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public UserCanceledException(String paramString)
  {
    super(paramString);
  }
  
  public UserCanceledException() {}
}