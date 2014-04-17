package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptRowWithIcon")
public class DataScriptRowWithIcon
  extends DataScriptRow
{
  private static final long serialVersionUID = 1L;
  public long aIconId;
  
  public DataScriptRowWithIcon(String paramString1, String paramString2, long paramLong1, boolean paramBoolean, String paramString3, DataScriptRowParamValue[] paramArrayOfDataScriptRowParamValue, long paramLong2)
  {
    super(paramString1, paramString2, paramLong1, paramBoolean, paramString3, paramArrayOfDataScriptRowParamValue);
    this.aIconId = paramLong2;
  }
  
  public long getIconId()
  {
    return this.aIconId;
  }
}