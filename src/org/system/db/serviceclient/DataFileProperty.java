package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("FileProperty")
public class DataFileProperty
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aFilePropertyCategoryName;
  public String aFilePropertyCategoryTag;
  public String aFilePropertyValue;
  
  public DataFileProperty(String paramString1, String paramString2)
  {
    this(paramString1, null, paramString2);
  }
  
  public DataFileProperty(String paramString1, String paramString2, String paramString3)
  {
    this.aFilePropertyCategoryName = paramString1;
    this.aFilePropertyCategoryTag = paramString2;
    this.aFilePropertyValue = paramString3;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aFilePropertyCategoryName == null ? 0 : this.aFilePropertyCategoryName.hashCode());
    i = 31 * i + (this.aFilePropertyCategoryTag == null ? 0 : this.aFilePropertyCategoryTag.hashCode());
    i = 31 * i + (this.aFilePropertyValue == null ? 0 : this.aFilePropertyValue.hashCode());
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
    DataFileProperty localDataFileProperty = (DataFileProperty)paramObject;
    if (this.aFilePropertyCategoryName == null)
    {
      if (localDataFileProperty.aFilePropertyCategoryName != null) {
        return false;
      }
    }
    else if (!this.aFilePropertyCategoryName.equals(localDataFileProperty.aFilePropertyCategoryName)) {
      return false;
    }
    if (this.aFilePropertyCategoryTag == null)
    {
      if (localDataFileProperty.aFilePropertyCategoryTag != null) {
        return false;
      }
    }
    else if (!this.aFilePropertyCategoryTag.equals(localDataFileProperty.aFilePropertyCategoryTag)) {
      return false;
    }
    if (this.aFilePropertyValue == null)
    {
      if (localDataFileProperty.aFilePropertyValue != null) {
        return false;
      }
    }
    else if (!this.aFilePropertyValue.equals(localDataFileProperty.aFilePropertyValue)) {
      return false;
    }
    return true;
  }
  
  public String getFilePropertyCategoryName()
  {
    return this.aFilePropertyCategoryName;
  }
  
  public String getFilePropertyCategoryTag()
  {
    return this.aFilePropertyCategoryTag;
  }
  
  public String getFilePropertyValue()
  {
    return this.aFilePropertyValue;
  }
  
  public String toString()
  {
    return "DataFileProperty [aFilePropertyCategoryName=" + this.aFilePropertyCategoryName + ", aFilePropertyCategoryTag=" + this.aFilePropertyCategoryTag + ", aFilePropertyValue=" + this.aFilePropertyValue + "]";
  }
}