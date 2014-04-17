package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("Identifier")
public class DataIdentifier
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @XStreamAlias("aCategory")
  public String aIdentifierCategory;
  @XStreamAlias("aValue")
  public String aIdentifierValue;
  @XStreamAlias("aClientDescription")
  public String aClientDescription;
  
  public DataIdentifier(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public DataIdentifier(String paramString1, String paramString2, String paramString3)
  {
    this.aIdentifierCategory = paramString1;
    this.aIdentifierValue = paramString2;
    this.aClientDescription = paramString3;
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aIdentifierCategory == null ? 0 : this.aIdentifierCategory.hashCode());
    i = 31 * i + (this.aIdentifierValue == null ? 0 : this.aIdentifierValue.hashCode());
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
    DataIdentifier localDataIdentifier = (DataIdentifier)paramObject;
    if (this.aIdentifierCategory == null)
    {
      if (localDataIdentifier.aIdentifierCategory != null) {
        return false;
      }
    }
    else if (!this.aIdentifierCategory.equals(localDataIdentifier.aIdentifierCategory)) {
      return false;
    }
    if (this.aIdentifierValue == null)
    {
      if (localDataIdentifier.aIdentifierValue != null) {
        return false;
      }
    }
    else if (!this.aIdentifierValue.equals(localDataIdentifier.aIdentifierValue)) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return this.aIdentifierCategory + "=" + this.aIdentifierValue;
  }
  
  public String getCategory()
  {
    return this.aIdentifierCategory;
  }
  
  public String getValue()
  {
    return this.aIdentifierValue;
  }
}
