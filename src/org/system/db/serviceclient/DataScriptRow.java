package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.util.Arrays;

@XStreamAlias("ScriptRow")
public class DataScriptRow
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aScriptRowName;
  public String aDisplayText;
  public long aRowNumber;
  public boolean aExitOnError;
  public String aScriptMethodName;
  public DataScriptRowParamValue[] aScriptRowParameters;
  
  public DataScriptRow(String paramString1, String paramString2, long paramLong, boolean paramBoolean, String paramString3, DataScriptRowParamValue[] paramArrayOfDataScriptRowParamValue)
  {
    this.aScriptRowName = paramString1;
    this.aDisplayText = paramString2;
    this.aRowNumber = paramLong;
    this.aExitOnError = paramBoolean;
    this.aScriptMethodName = paramString3;
    this.aScriptRowParameters = paramArrayOfDataScriptRowParamValue;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aDisplayText == null ? 0 : this.aDisplayText.hashCode());
    i = 31 * i + (this.aExitOnError ? 1231 : 1237);
    i = 31 * i + (int)(this.aRowNumber ^ this.aRowNumber >>> 32);
    i = 31 * i + (this.aScriptMethodName == null ? 0 : this.aScriptMethodName.hashCode());
    i = 31 * i + (this.aScriptRowName == null ? 0 : this.aScriptRowName.hashCode());
    i = 31 * i + Arrays.hashCode(this.aScriptRowParameters);
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    DataScriptRow localDataScriptRow = (DataScriptRow)paramObject;
    if (this.aDisplayText == null)
    {
      if (localDataScriptRow.aDisplayText != null) {
        return false;
      }
    }
    else if (!this.aDisplayText.equals(localDataScriptRow.aDisplayText)) {
      return false;
    }
    if (this.aExitOnError != localDataScriptRow.aExitOnError) {
      return false;
    }
    if (this.aRowNumber != localDataScriptRow.aRowNumber) {
      return false;
    }
    if (this.aScriptMethodName == null)
    {
      if (localDataScriptRow.aScriptMethodName != null) {
        return false;
      }
    }
    else if (!this.aScriptMethodName.equals(localDataScriptRow.aScriptMethodName)) {
      return false;
    }
    if (this.aScriptRowName == null)
    {
      if (localDataScriptRow.aScriptRowName != null) {
        return false;
      }
    }
    else if (!this.aScriptRowName.equals(localDataScriptRow.aScriptRowName)) {
      return false;
    }
    return Arrays.equals(this.aScriptRowParameters, localDataScriptRow.aScriptRowParameters);
  }
  
  public String getDisplayText()
  {
    return this.aDisplayText;
  }
  
  public boolean isExitOnError()
  {
    return this.aExitOnError;
  }
  
  public long getRowNumber()
  {
    return this.aRowNumber;
  }
  
  public String getScriptMethodName()
  {
    return this.aScriptMethodName;
  }
  
  public String getScriptRowName()
  {
    return this.aScriptRowName;
  }
  
  public DataScriptRowParamValue[] getScriptRowParameters()
  {
    return this.aScriptRowParameters;
  }
}