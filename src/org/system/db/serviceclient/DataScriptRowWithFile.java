package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptRowWithFile")
public class DataScriptRowWithFile
  extends DataScriptRow
{
  private static final long serialVersionUID = 1L;
  public DataFileSynch aDataFileSynch;
  
  public DataScriptRowWithFile(String paramString1, String paramString2, long paramLong, boolean paramBoolean, String paramString3, DataScriptRowParamValue[] paramArrayOfDataScriptRowParamValue, DataFileSynch paramDataFileSynch)
  {
    super(paramString1, paramString2, paramLong, paramBoolean, paramString3, paramArrayOfDataScriptRowParamValue);
    this.aDataFileSynch = paramDataFileSynch;
  }
  
  public DataFileSynch getDataFileSynch()
  {
    return this.aDataFileSynch;
  }
}