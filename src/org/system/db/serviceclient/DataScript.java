package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.util.Arrays;

@XStreamAlias("Script")
public class DataScript
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long aScriptId;
  private long aDomainId;
  private String aScriptName;
  /**
   * @deprecated
   */
  private long aMinAppVersion;
  private String aClientDescription;
  private boolean aOnlineOnly;
  /**
   * @deprecated
   */
  private boolean aSendFeedback;
  private long aVersionNumber;
  /**
   * @deprecated
   */
  private String aExcecutionExpression = "";
  private long aScriptLastUpdated;
  public DataScriptRow[] aScriptRows;
  public DataIdentifier[] aIdentifiers;
  private long aProductFormulaId;
  private String aDomainName;
  private String aServiceName;
  private boolean aIsTestScript;
  /**
   * @deprecated
   */
  private long aServiceCreatedDate;
  private long aServiceLastModifiedDate;
  
  public DataScript(long paramLong1, long paramLong2, String paramString1, String paramString2, boolean paramBoolean1, long paramLong3, long paramLong4, DataScriptRow[] paramArrayOfDataScriptRow, DataIdentifier[] paramArrayOfDataIdentifier, long paramLong5, String paramString3, String paramString4, boolean paramBoolean2, long paramLong6)
  {
    this.aScriptId = paramLong1;
    this.aDomainId = paramLong2;
    this.aScriptName = paramString1;
    this.aClientDescription = paramString2;
    this.aOnlineOnly = paramBoolean1;
    this.aVersionNumber = paramLong3;
    this.aScriptLastUpdated = paramLong4;
    this.aScriptRows = paramArrayOfDataScriptRow;
    this.aIdentifiers = paramArrayOfDataIdentifier;
    this.aProductFormulaId = paramLong5;
    this.aDomainName = paramString3;
    this.aServiceName = paramString4;
    this.aIsTestScript = paramBoolean2;
    this.aServiceCreatedDate = 0L;
    this.aServiceLastModifiedDate = paramLong6;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aClientDescription == null ? 0 : this.aClientDescription.hashCode());
    i = 31 * i + (int)(this.aDomainId ^ this.aDomainId >>> 32);
    i = 31 * i + (this.aDomainName == null ? 0 : this.aDomainName.hashCode());
    i = 31 * i + (this.aExcecutionExpression == null ? 0 : this.aExcecutionExpression.hashCode());
    i = 31 * i + Arrays.hashCode(this.aIdentifiers);
    i = 31 * i + (this.aIsTestScript ? 1231 : 1237);
    i = 31 * i + (int)(this.aMinAppVersion ^ this.aMinAppVersion >>> 32);
    i = 31 * i + (this.aOnlineOnly ? 1231 : 1237);
    i = 31 * i + (int)(this.aProductFormulaId ^ this.aProductFormulaId >>> 32);
    i = 31 * i + (int)(this.aScriptId ^ this.aScriptId >>> 32);
    i = 31 * i + (int)(this.aScriptLastUpdated ^ this.aScriptLastUpdated >>> 32);
    i = 31 * i + (this.aScriptName == null ? 0 : this.aScriptName.hashCode());
    i = 31 * i + Arrays.hashCode(this.aScriptRows);
    i = 31 * i + (this.aSendFeedback ? 1231 : 1237);
    i = 31 * i + (this.aServiceName == null ? 0 : this.aServiceName.hashCode());
    i = 31 * i + (int)(this.aVersionNumber ^ this.aVersionNumber >>> 32);
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
    DataScript localDataScript = (DataScript)paramObject;
    if (this.aClientDescription == null)
    {
      if (localDataScript.aClientDescription != null) {
        return false;
      }
    }
    else if (!this.aClientDescription.equals(localDataScript.aClientDescription)) {
      return false;
    }
    if (this.aDomainId != localDataScript.aDomainId) {
      return false;
    }
    if (this.aDomainName == null)
    {
      if (localDataScript.aDomainName != null) {
        return false;
      }
    }
    else if (!this.aDomainName.equals(localDataScript.aDomainName)) {
      return false;
    }
    if (!Arrays.equals(this.aIdentifiers, localDataScript.aIdentifiers)) {
      return false;
    }
    if (this.aIsTestScript != localDataScript.aIsTestScript) {
      return false;
    }
    if (this.aOnlineOnly != localDataScript.aOnlineOnly) {
      return false;
    }
    if (this.aProductFormulaId != localDataScript.aProductFormulaId) {
      return false;
    }
    if (this.aScriptId != localDataScript.aScriptId) {
      return false;
    }
    if (this.aScriptLastUpdated != localDataScript.aScriptLastUpdated) {
      return false;
    }
    if (this.aScriptName == null)
    {
      if (localDataScript.aScriptName != null) {
        return false;
      }
    }
    else if (!this.aScriptName.equals(localDataScript.aScriptName)) {
      return false;
    }
    if (!Arrays.equals(this.aScriptRows, localDataScript.aScriptRows)) {
      return false;
    }
    if (this.aSendFeedback != localDataScript.aSendFeedback) {
      return false;
    }
    if (this.aServiceName == null)
    {
      if (localDataScript.aServiceName != null) {
        return false;
      }
    }
    else if (!this.aServiceName.equals(localDataScript.aServiceName)) {
      return false;
    }
    return this.aVersionNumber == localDataScript.aVersionNumber;
  }
  
  public String getClientDescription()
  {
    return this.aClientDescription;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public String getDomainName()
  {
    return this.aDomainName;
  }
  
  public DataIdentifier[] getIdentifiers()
  {
    return this.aIdentifiers;
  }
  
  public boolean isTestScript()
  {
    return this.aIsTestScript;
  }
  
  public boolean isOnlineOnly()
  {
    return this.aOnlineOnly;
  }
  
  public long getProductFormulaId()
  {
    return this.aProductFormulaId;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
  
  public long getScriptLastUpdated()
  {
    return this.aScriptLastUpdated;
  }
  
  public String getScriptName()
  {
    return this.aScriptName;
  }
  
  public DataScriptRow[] getScriptRows()
  {
    return this.aScriptRows;
  }
  
  public String getServiceName()
  {
    return this.aServiceName;
  }
  
  public long getVersionNumber()
  {
    return this.aVersionNumber;
  }
  
  public long getServiceLastModifiedDate()
  {
    return this.aServiceLastModifiedDate;
  }
}