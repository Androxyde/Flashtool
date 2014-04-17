package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;
import java.util.Arrays;

@XStreamAlias("Icon")
public class DataIcon
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aIconId;
  public String aIconName;
  public byte[] aIconFile;
  public long aIconLastUpdated;
  
  public DataIcon(long paramLong1, String paramString, byte[] paramArrayOfByte, long paramLong2)
  {
    this.aIconId = paramLong1;
    this.aIconName = paramString;
    this.aIconFile = paramArrayOfByte;
    this.aIconLastUpdated = paramLong2;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + Arrays.hashCode(this.aIconFile);
    i = 31 * i + (int)(this.aIconId ^ this.aIconId >>> 32);
    i = 31 * i + (int)(this.aIconLastUpdated ^ this.aIconLastUpdated >>> 32);
    i = 31 * i + (this.aIconName == null ? 0 : this.aIconName.hashCode());
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
    DataIcon localDataIcon = (DataIcon)paramObject;
    if (!Arrays.equals(this.aIconFile, localDataIcon.aIconFile)) {
      return false;
    }
    if (this.aIconId != localDataIcon.aIconId) {
      return false;
    }
    if (this.aIconLastUpdated != localDataIcon.aIconLastUpdated) {
      return false;
    }
    if (this.aIconName == null)
    {
      if (localDataIcon.aIconName != null) {
        return false;
      }
    }
    else if (!this.aIconName.equals(localDataIcon.aIconName)) {
      return false;
    }
    return true;
  }
  
  public byte[] getIconFile()
  {
    return this.aIconFile;
  }
  
  public long getIconId()
  {
    return this.aIconId;
  }
  
  public long getIconLastUpdated()
  {
    return this.aIconLastUpdated;
  }
  
  public String getIconName()
  {
    return this.aIconName;
  }
}