package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.util.Arrays;

@XStreamAlias("File")
public class DataFile
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aFileId;
  public long aFileVersionId;
  public String aFileName;
  public String aFileVersionName;
  public String aFileDescription;
  public String aFilePresentation;
  public String aFileSecurityTag;
  public String aFileTypeTag;
  public long aFileContentInfoId;
  public DataFileProperty[] aFileProperties;
  public long aFileContentLength;
  public long aFileLastUpdate;
  public long aFileVersionLastUpdate;
  
  public DataFile(long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, long paramLong3, DataFileProperty[] paramArrayOfDataFileProperty, long paramLong4, long paramLong5, long paramLong6)
  {
    this.aFileId = paramLong1;
    this.aFileVersionId = paramLong2;
    this.aFileName = paramString1;
    this.aFileVersionName = paramString2;
    this.aFileDescription = paramString3;
    this.aFilePresentation = paramString4;
    this.aFileSecurityTag = paramString5;
    this.aFileTypeTag = paramString6;
    this.aFileContentInfoId = paramLong3;
    this.aFileProperties = paramArrayOfDataFileProperty;
    this.aFileContentLength = paramLong4;
    this.aFileLastUpdate = paramLong5;
    this.aFileVersionLastUpdate = paramLong6;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (int)(this.aFileContentInfoId ^ this.aFileContentInfoId >>> 32);
    i = 31 * i + (int)(this.aFileContentLength ^ this.aFileContentLength >>> 32);
    i = 31 * i + (this.aFileDescription == null ? 0 : this.aFileDescription.hashCode());
    i = 31 * i + (int)(this.aFileId ^ this.aFileId >>> 32);
    i = 31 * i + (int)(this.aFileLastUpdate ^ this.aFileLastUpdate >>> 32);
    i = 31 * i + (this.aFileName == null ? 0 : this.aFileName.hashCode());
    i = 31 * i + (this.aFilePresentation == null ? 0 : this.aFilePresentation.hashCode());
    i = 31 * i + Arrays.hashCode(this.aFileProperties);
    i = 31 * i + (this.aFileSecurityTag == null ? 0 : this.aFileSecurityTag.hashCode());
    i = 31 * i + (this.aFileTypeTag == null ? 0 : this.aFileTypeTag.hashCode());
    i = 31 * i + (int)(this.aFileVersionId ^ this.aFileVersionId >>> 32);
    i = 31 * i + (int)(this.aFileVersionLastUpdate ^ this.aFileVersionLastUpdate >>> 32);
    i = 31 * i + (this.aFileVersionName == null ? 0 : this.aFileVersionName.hashCode());
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
    DataFile localDataFile = (DataFile)paramObject;
    if (this.aFileContentInfoId != localDataFile.aFileContentInfoId) {
      return false;
    }
    if (this.aFileContentLength != localDataFile.aFileContentLength) {
      return false;
    }
    if (this.aFileDescription == null)
    {
      if (localDataFile.aFileDescription != null) {
        return false;
      }
    }
    else if (!this.aFileDescription.equals(localDataFile.aFileDescription)) {
      return false;
    }
    if (this.aFileId != localDataFile.aFileId) {
      return false;
    }
    if (this.aFileLastUpdate != localDataFile.aFileLastUpdate) {
      return false;
    }
    if (this.aFileName == null)
    {
      if (localDataFile.aFileName != null) {
        return false;
      }
    }
    else if (!this.aFileName.equals(localDataFile.aFileName)) {
      return false;
    }
    if (this.aFilePresentation == null)
    {
      if (localDataFile.aFilePresentation != null) {
        return false;
      }
    }
    else if (!this.aFilePresentation.equals(localDataFile.aFilePresentation)) {
      return false;
    }
    if (!Arrays.equals(this.aFileProperties, localDataFile.aFileProperties)) {
      return false;
    }
    if (this.aFileSecurityTag == null)
    {
      if (localDataFile.aFileSecurityTag != null) {
        return false;
      }
    }
    else if (!this.aFileSecurityTag.equals(localDataFile.aFileSecurityTag)) {
      return false;
    }
    if (this.aFileTypeTag == null)
    {
      if (localDataFile.aFileTypeTag != null) {
        return false;
      }
    }
    else if (!this.aFileTypeTag.equals(localDataFile.aFileTypeTag)) {
      return false;
    }
    if (this.aFileVersionId != localDataFile.aFileVersionId) {
      return false;
    }
    if (this.aFileVersionLastUpdate != localDataFile.aFileVersionLastUpdate) {
      return false;
    }
    if (this.aFileVersionName == null)
    {
      if (localDataFile.aFileVersionName != null) {
        return false;
      }
    }
    else if (!this.aFileVersionName.equals(localDataFile.aFileVersionName)) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "DataFile(" + this.aFileId + "," + this.aFileVersionId + "," + this.aFileName + "," + this.aFileContentInfoId + ")";
  }
  
  public long getFileContentInfoId()
  {
    return this.aFileContentInfoId;
  }
  
  public long getFileContentLength()
  {
    return this.aFileContentLength;
  }
  
  public String getFileDescription()
  {
    return this.aFileDescription;
  }
  
  public long getFileId()
  {
    return this.aFileId;
  }
  
  public long getFileLastUpdate()
  {
    return this.aFileLastUpdate;
  }
  
  public String getFileName()
  {
    return this.aFileName;
  }
  
  public String getFilePresentation()
  {
    return this.aFilePresentation;
  }
  
  public DataFileProperty[] getFileProperties()
  {
    return this.aFileProperties;
  }
  
  public String getFileReleaseNotes()
  {
    return "";
  }
  
  public String getFileSecurityTag()
  {
    return this.aFileSecurityTag;
  }
  
  public String getFileTypeTag()
  {
    return this.aFileTypeTag;
  }
  
  public long getFileVersionId()
  {
    return this.aFileVersionId;
  }
  
  public long getFileVersionLastUpdate()
  {
    return this.aFileVersionLastUpdate;
  }
  
  public String getFileVersionName()
  {
    return this.aFileVersionName;
  }
}