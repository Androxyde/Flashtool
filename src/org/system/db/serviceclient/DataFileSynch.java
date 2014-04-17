package org.system.db.serviceclient;

import java.io.Serializable;

public class DataFileSynch
  extends DataFileSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final int REL_STATUS_UNDEFINED = -1;
  public static final int REL_STATUS_VERSION = 1;
  public static final int REL_STATUS_TEST = 2;
  public static final int REL_STATUS_RELEASED = 3;
  public long aFileId;
  public long aFileVersionId;
  public long aFileLastUpdated;
  public long aFileVersionLastUpdated;
  public long aFileContentInfoId;
  public long aFileContentLength;
  public String aFileSecurityTag;
  public int aFileReleaseStatus;
  
  public DataFileSynch(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, String paramString, int paramInt)
  {
    this.aFileId = paramLong1;
    this.aFileVersionId = paramLong2;
    this.aFileLastUpdated = paramLong3;
    this.aFileVersionLastUpdated = paramLong4;
    this.aFileContentInfoId = paramLong5;
    this.aFileContentLength = paramLong6;
    this.aFileSecurityTag = paramString;
    this.aFileReleaseStatus = paramInt;
  }
  
  public DataFileSynch(DataFile paramDataFile)
  {
    this.aFileId = paramDataFile.aFileId;
    this.aFileVersionId = paramDataFile.aFileVersionId;
    this.aFileLastUpdated = paramDataFile.aFileLastUpdate;
    this.aFileVersionLastUpdated = paramDataFile.aFileVersionLastUpdate;
    this.aFileContentInfoId = paramDataFile.aFileContentInfoId;
    this.aFileContentLength = paramDataFile.aFileContentLength;
    this.aFileSecurityTag = paramDataFile.aFileSecurityTag;
    this.aFileReleaseStatus = -1;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (int)(this.aFileContentInfoId ^ this.aFileContentInfoId >>> 32);
    i = 31 * i + (int)(this.aFileContentLength ^ this.aFileContentLength >>> 32);
    i = 31 * i + (int)(this.aFileId ^ this.aFileId >>> 32);
    i = 31 * i + (int)(this.aFileLastUpdated ^ this.aFileLastUpdated >>> 32);
    i = 31 * i + this.aFileReleaseStatus;
    i = 31 * i + (this.aFileSecurityTag == null ? 0 : this.aFileSecurityTag.hashCode());
    i = 31 * i + (int)(this.aFileVersionId ^ this.aFileVersionId >>> 32);
    i = 31 * i + (int)(this.aFileVersionLastUpdated ^ this.aFileVersionLastUpdated >>> 32);
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
    DataFileSynch localDataFileSynch = (DataFileSynch)paramObject;
    if (this.aFileContentInfoId != localDataFileSynch.aFileContentInfoId) {
      return false;
    }
    if (this.aFileContentLength != localDataFileSynch.aFileContentLength) {
      return false;
    }
    if (this.aFileId != localDataFileSynch.aFileId) {
      return false;
    }
    if (this.aFileLastUpdated != localDataFileSynch.aFileLastUpdated) {
      return false;
    }
    if (this.aFileReleaseStatus != localDataFileSynch.aFileReleaseStatus) {
      return false;
    }
    if (this.aFileSecurityTag == null)
    {
      if (localDataFileSynch.aFileSecurityTag != null) {
        return false;
      }
    }
    else if (!this.aFileSecurityTag.equals(localDataFileSynch.aFileSecurityTag)) {
      return false;
    }
    if (this.aFileVersionId != localDataFileSynch.aFileVersionId) {
      return false;
    }
    return this.aFileVersionLastUpdated == localDataFileSynch.aFileVersionLastUpdated;
  }
  
  public long getFileContentInfoId()
  {
    return this.aFileContentInfoId;
  }
  
  public long getFileContentLength()
  {
    return this.aFileContentLength;
  }
  
  public long getFileId()
  {
    return this.aFileId;
  }
  
  public long getFileLastUpdated()
  {
    return this.aFileLastUpdated;
  }
  
  public int getFileReleaseStatus()
  {
    return this.aFileReleaseStatus;
  }
  
  public String getFileSecurityTag()
  {
    return this.aFileSecurityTag;
  }
  
  public long getFileVersionId()
  {
    return this.aFileVersionId;
  }
  
  public long getFileVersionLastUpdated()
  {
    return this.aFileVersionLastUpdated;
  }
}