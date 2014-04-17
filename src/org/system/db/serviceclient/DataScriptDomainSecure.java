package org.system.db.serviceclient;

public class DataScriptDomainSecure
  extends DataScriptDomainId
{
  private static final long serialVersionUID = 1L;
  public long aSecurityPass;
  
  public DataScriptDomainSecure(long paramLong1, long paramLong2, long paramLong3)
  {
    super(paramLong1, paramLong2);
    this.aSecurityPass = paramLong3;
  }
  
  public long getSecurityPass()
  {
    return this.aSecurityPass;
  }
}