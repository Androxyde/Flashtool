package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FileSynchSecure")
public class DataFileSynchSecure
  extends DataFileSynch
{
  private static final long serialVersionUID = 1L;
  public long aSecurePass;
  
  public DataFileSynchSecure(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, String paramString, int paramInt, long paramLong7)
  {
    super(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramLong6, paramString, paramInt);
    this.aSecurePass = paramLong7;
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
    DataFileSynchSecure localDataFileSynchSecure = (DataFileSynchSecure)paramObject;
    return this.aSecurePass == localDataFileSynchSecure.aSecurePass;
  }
  
  public long getSecurePass()
  {
    return this.aSecurePass;
  }
}