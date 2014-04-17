package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptSynchSecure")
public class DataScriptSynchSecure
  extends DataScriptSynch
  implements Binarifiable
{
  private static final long serialVersionUID = 1L;
  public long aSecurePass;
  
  public DataScriptSynchSecure(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, DataScriptSynchResult[] paramArrayOfDataScriptSynchResult, DataIconSynchResult[] paramArrayOfDataIconSynchResult, DataFileSynchResult[] paramArrayOfDataFileSynchResult, long paramLong6)
  {
    super(paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramArrayOfDataScriptSynchResult, paramArrayOfDataIconSynchResult, paramArrayOfDataFileSynchResult);
    this.aSecurePass = paramLong6;
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
    DataScriptSynchSecure localDataScriptSynchSecure = (DataScriptSynchSecure)paramObject;
    return this.aSecurePass == localDataScriptSynchSecure.aSecurePass;
  }
  
  public long getSecurePass()
  {
    return this.aSecurePass;
  }
}


/* Location:           C:\Program Files (x86)\Sony Mobile\Emma\plugins\com.sonyericsson.cs_ma3_communication_server-sc_3.1.0.201403101311.jar
 * Qualified Name:     com.sonyericsson.cs.ma3.common.data.serviceclient.DataScriptSynchSecure
 * JD-Core Version:    0.7.0.1
 */