package org.system.db.serviceclient;

import java.io.Serializable;

public class ClientEvent
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final String aType;
  private final long aTimestamp;
  private final String aName;
  private final String aDetails;
  
  public ClientEvent(String paramString1, String paramString2, String paramString3, long paramLong)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new IllegalArgumentException("A ClientEvent has to have a name and a type.");
    }
    this.aType = paramString1;
    this.aName = paramString2;
    this.aDetails = paramString3;
    this.aTimestamp = paramLong;
  }
  
  public long getTimestamp()
  {
    return this.aTimestamp;
  }
  
  public String getName()
  {
    return this.aName;
  }
  
  public String getDetails()
  {
    return this.aDetails;
  }
  
  public String getType()
  {
    return this.aType;
  }
  
  public String toString()
  {
    return "Type: " + this.aType + " at " + this.aTimestamp + " Action: " + this.aName + " Details: " + this.aDetails;
  }
  
  public int hashCode()
  {
    int i = 31;
    i += (this.aDetails == null ? 0 : this.aDetails.hashCode());
    i += (this.aName == null ? 0 : this.aName.hashCode());
    i += (int)(this.aTimestamp ^ this.aTimestamp >>> 32);
    i += (this.aType == null ? 0 : this.aType.hashCode());
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
    ClientEvent localClientEvent = (ClientEvent)paramObject;
    if (this.aDetails == null)
    {
      if (localClientEvent.aDetails != null) {
        return false;
      }
    }
    else if (!this.aDetails.equals(localClientEvent.aDetails)) {
      return false;
    }
    if (this.aName == null)
    {
      if (localClientEvent.aName != null) {
        return false;
      }
    }
    else if (!this.aName.equals(localClientEvent.aName)) {
      return false;
    }
    if (this.aTimestamp != localClientEvent.aTimestamp) {
      return false;
    }
    if (this.aType == null)
    {
      if (localClientEvent.aType != null) {
        return false;
      }
    }
    else if (!this.aType.equals(localClientEvent.aType)) {
      return false;
    }
    return true;
  }
}