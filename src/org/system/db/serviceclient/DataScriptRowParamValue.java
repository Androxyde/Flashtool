package org.system.db.serviceclient;

import java.io.Serializable;

public class DataScriptRowParamValue
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aParamName;
  public String aParamValue;
  
  public DataScriptRowParamValue(String paramString1, String paramString2)
  {
    this.aParamName = paramString1;
    this.aParamValue = paramString2;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aParamName == null ? 0 : this.aParamName.hashCode());
    i = 31 * i + (this.aParamValue == null ? 0 : this.aParamValue.hashCode());
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
    DataScriptRowParamValue localDataScriptRowParamValue = (DataScriptRowParamValue)paramObject;
    if (this.aParamName == null)
    {
      if (localDataScriptRowParamValue.aParamName != null) {
        return false;
      }
    }
    else if (!this.aParamName.equals(localDataScriptRowParamValue.aParamName)) {
      return false;
    }
    if (this.aParamValue == null)
    {
      if (localDataScriptRowParamValue.aParamValue != null) {
        return false;
      }
    }
    else if (!this.aParamValue.equals(localDataScriptRowParamValue.aParamValue)) {
      return false;
    }
    return true;
  }
  
  public String getParamName()
  {
    return this.aParamName;
  }
  
  public String getParamValue()
  {
    return this.aParamValue;
  }
}