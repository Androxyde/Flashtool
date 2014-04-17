package org.system.db.serviceclient;

import java.io.Serializable;

public abstract class DataScriptSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public int hashCode()
  {
    int i = 1;
    i *= 31;
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
    return getClass() == paramObject.getClass();
  }
}