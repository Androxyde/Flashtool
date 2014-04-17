package org.system.db.serviceclient;

import java.io.Serializable;

public class TrackingIdentifier
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final String aCategory;
  private final String aValue;
  private final String aContext;
  
  public TrackingIdentifier(String paramString1, String paramString2, DeviceUpdateContext paramDeviceUpdateContext)
  {
    this.aCategory = paramString1;
    this.aValue = paramString2;
    this.aContext = EnumSerializer.serialize(paramDeviceUpdateContext);
  }
  
  public String getCategory()
  {
    return this.aCategory;
  }
  
  public String getValue()
  {
    return this.aValue;
  }
  
  public DeviceUpdateContext getContext()
  {
    return (DeviceUpdateContext)EnumSerializer.deSerialize(DeviceUpdateContext.class, this.aContext);
  }
  
  public int hashCode()
  {
    int i = 1;
    i = 31 * i + (this.aCategory == null ? 0 : this.aCategory.hashCode());
    i = 31 * i + (this.aContext == null ? 0 : this.aContext.hashCode());
    i = 31 * i + (this.aValue == null ? 0 : this.aValue.hashCode());
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
    TrackingIdentifier localTrackingIdentifier = (TrackingIdentifier)paramObject;
    if (this.aCategory == null)
    {
      if (localTrackingIdentifier.aCategory != null) {
        return false;
      }
    }
    else if (!this.aCategory.equals(localTrackingIdentifier.aCategory)) {
      return false;
    }
    if (this.aContext == null)
    {
      if (localTrackingIdentifier.aContext != null) {
        return false;
      }
    }
    else if (!this.aContext.equals(localTrackingIdentifier.aContext)) {
      return false;
    }
    if (this.aValue == null)
    {
      if (localTrackingIdentifier.aValue != null) {
        return false;
      }
    }
    else if (!this.aValue.equals(localTrackingIdentifier.aValue)) {
      return false;
    }
    return true;
  }
  
  public String toString()
  {
    return "TrackingIdentifier [aCategory=" + this.aCategory + ", aValue=" + this.aValue + ", aContext=" + this.aContext + "]";
  }
}