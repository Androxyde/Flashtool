package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("ScriptSynchError")
public class DataScriptSynchError
  extends DataScriptSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final int ERROR_CODE_SYNCH_ERROR = 1;
  public static final int ERROR_CODE_NO_ACCESS = 2;
  public static final int ERROR_CODE_NO_MACTH = 3;
  public static final int ERROR_CODE_DELETED = 4;
  public String aErrorMessage;
  public int aErrorCode;
  public long aDomainId;
  public long aScriptId;
  
  public DataScriptSynchError(long paramLong1, long paramLong2, String paramString)
  {
    this.aErrorMessage = paramString;
    this.aErrorCode = 1;
    this.aDomainId = paramLong1;
    this.aScriptId = paramLong2;
  }
  
  public DataScriptSynchError(long paramLong1, long paramLong2, int paramInt)
  {
    this.aErrorCode = paramInt;
    this.aErrorMessage = null;
    this.aDomainId = paramLong1;
    this.aScriptId = paramLong2;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public int getErrorCode()
  {
    return this.aErrorCode;
  }
  
  public String getErrorMessage()
  {
    return this.aErrorMessage;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
  
  public String toString()
  {
    return "Error code: " + this.aErrorCode + " Message: " + this.aErrorMessage + " scriptId: " + this.aScriptId + " domainId: " + this.aDomainId;
  }
}