package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("IconSynchSecure")
public class DataIconSynchSecure
  extends DataIconSynch
{
  private static final long serialVersionUID = 1L;
  public long aSecurePass;
  
  public DataIconSynchSecure(long paramLong1, long paramLong2, long paramLong3)
  {
    super(paramLong1, paramLong2);
    this.aSecurePass = paramLong3;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (int)(this.aSecurePass ^ this.aSecurePass >>> 32);
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    DataIconSynchSecure localDataIconSynchSecure = (DataIconSynchSecure)paramObject;
    return this.aSecurePass == localDataIconSynchSecure.aSecurePass;
  }
  
  public long getSecurePass()
  {
    return this.aSecurePass;
  }
}