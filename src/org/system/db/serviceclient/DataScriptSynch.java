package org.system.db.serviceclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataScriptSynch
  extends DataScriptSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aScriptId;
  public long aDomainId;
  public long aScriptVersionNumber;
  public long aScriptLastUpdated;
  public long aLatestForceUpdateVersion;
  public DataScriptSynchResult[] aSubScripts;
  public DataIconSynchResult[] aIconsInScript;
  public DataFileSynchResult[] aFilesInScript;
  
  public DataScriptSynch(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, DataScriptSynchResult[] paramArrayOfDataScriptSynchResult, DataIconSynchResult[] paramArrayOfDataIconSynchResult, DataFileSynchResult[] paramArrayOfDataFileSynchResult)
  {
    this.aScriptId = paramLong1;
    this.aDomainId = paramLong2;
    this.aScriptVersionNumber = paramLong3;
    this.aScriptLastUpdated = paramLong4;
    this.aLatestForceUpdateVersion = paramLong5;
    this.aSubScripts = paramArrayOfDataScriptSynchResult;
    this.aIconsInScript = paramArrayOfDataIconSynchResult;
    this.aFilesInScript = paramArrayOfDataFileSynchResult;
  }
  
  public List<DataScriptSynchResult> getFileAndScriptSynchErrors()
  {
    ArrayList localArrayList = new ArrayList();
    if (this.aSubScripts != null)
    {
      for (int i = 0; i < this.aSubScripts.length; i++) {
        if ((this.aSubScripts[i] instanceof DataScriptSynchError)) {
          localArrayList.add(this.aSubScripts[i]);
        } else if ((this.aSubScripts[i] instanceof DataScriptSynch)) {
          localArrayList.addAll(((DataScriptSynch)this.aSubScripts[i]).getFileAndScriptSynchErrors());
        }
      }
      for (int i = 0; i < this.aFilesInScript.length; i++) {
        if ((this.aFilesInScript[i] instanceof DataFileSynchError))
        {
          DataFileSynchError localDataFileSynchError = (DataFileSynchError)this.aFilesInScript[i];
          DataScriptSynchError localDataScriptSynchError = new DataScriptSynchError(this.aDomainId, this.aScriptId, 1);
          localDataScriptSynchError.aErrorMessage = localDataFileSynchError.aErrorMessage;
          localArrayList.add(localDataScriptSynchError);
        }
      }
    }
    return localArrayList;
  }
  
  public static long getSerialVersionUID()
  {
    return 1L;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public DataFileSynchResult[] getFilesInScript()
  {
    return this.aFilesInScript;
  }
  
  public DataIconSynchResult[] getIconsInScript()
  {
    return this.aIconsInScript;
  }
  
  public long getLatestForceUpdateVersion()
  {
    return this.aLatestForceUpdateVersion;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
  
  public long getScriptLastUpdated()
  {
    return this.aScriptLastUpdated;
  }
  
  public long getScriptVersionNumber()
  {
    return this.aScriptVersionNumber;
  }
  
  public DataScriptSynchResult[] getSubScripts()
  {
    return this.aSubScripts;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (int)(this.aDomainId ^ this.aDomainId >>> 32);
    i = 31 * i + Arrays.hashCode(this.aFilesInScript);
    i = 31 * i + Arrays.hashCode(this.aIconsInScript);
    i = 31 * i + (int)(this.aLatestForceUpdateVersion ^ this.aLatestForceUpdateVersion >>> 32);
    i = 31 * i + (int)(this.aScriptId ^ this.aScriptId >>> 32);
    i = 31 * i + (int)(this.aScriptLastUpdated ^ this.aScriptLastUpdated >>> 32);
    i = 31 * i + (int)(this.aScriptVersionNumber ^ this.aScriptVersionNumber >>> 32);
    i = 31 * i + Arrays.hashCode(this.aSubScripts);
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
    DataScriptSynch localDataScriptSynch = (DataScriptSynch)paramObject;
    if (this.aDomainId != localDataScriptSynch.aDomainId) {
      return false;
    }
    if (!Arrays.equals(this.aFilesInScript, localDataScriptSynch.aFilesInScript)) {
      return false;
    }
    if (!Arrays.equals(this.aIconsInScript, localDataScriptSynch.aIconsInScript)) {
      return false;
    }
    if (this.aLatestForceUpdateVersion != localDataScriptSynch.aLatestForceUpdateVersion) {
      return false;
    }
    if (this.aScriptId != localDataScriptSynch.aScriptId) {
      return false;
    }
    if (this.aScriptLastUpdated != localDataScriptSynch.aScriptLastUpdated) {
      return false;
    }
    if (this.aScriptVersionNumber != localDataScriptSynch.aScriptVersionNumber) {
      return false;
    }
    return Arrays.equals(this.aSubScripts, localDataScriptSynch.aSubScripts);
  }
}