package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptRowWithSubScript")
public class DataScriptRowWithSubScript
  extends DataScriptRow
{
  private static final long serialVersionUID = 1L;
  public long aSubScriptId;
  public long aSubScriptVersionNumber;
  public long aSubScriptLastUpdated;
  
  public DataScriptRowWithSubScript(String paramString1, String paramString2, long paramLong1, boolean paramBoolean, String paramString3, DataScriptRowParamValue[] paramArrayOfDataScriptRowParamValue, long paramLong2, long paramLong3, long paramLong4)
  {
    super(paramString1, paramString2, paramLong1, paramBoolean, paramString3, paramArrayOfDataScriptRowParamValue);
    this.aSubScriptId = paramLong2;
    this.aSubScriptVersionNumber = paramLong3;
    this.aSubScriptLastUpdated = paramLong4;
  }
  
  public long getSubScriptId()
  {
    return this.aSubScriptId;
  }
  
  public long getSubScriptLastUpdated()
  {
    return this.aSubScriptLastUpdated;
  }
  
  public long getSubScriptVersionNumber()
  {
    return this.aSubScriptVersionNumber;
  }
}