package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SuperScriptSynch")
public class DataSuperScriptSynch
  extends DataScriptSynchSecure
{
  private static final long serialVersionUID = 1L;
  public boolean aOnlineOnly;
  
  public DataSuperScriptSynch(DataScriptSynchSecure paramDataScriptSynchSecure, boolean paramBoolean)
  {
    super(paramDataScriptSynchSecure.aScriptId, paramDataScriptSynchSecure.aDomainId, paramDataScriptSynchSecure.aScriptVersionNumber, paramDataScriptSynchSecure.aScriptLastUpdated, paramDataScriptSynchSecure.aLatestForceUpdateVersion, paramDataScriptSynchSecure.aSubScripts, paramDataScriptSynchSecure.aIconsInScript, paramDataScriptSynchSecure.aFilesInScript, paramDataScriptSynchSecure.aSecurePass);
    this.aOnlineOnly = paramBoolean;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (this.aOnlineOnly ? 1231 : 1237);
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
    DataSuperScriptSynch localDataSuperScriptSynch = (DataSuperScriptSynch)paramObject;
    return this.aOnlineOnly == localDataSuperScriptSynch.aOnlineOnly;
  }
  
  public boolean isOnlineOnly()
  {
    return this.aOnlineOnly;
  }
}