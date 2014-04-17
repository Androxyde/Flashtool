package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("IconSynchError")
public class DataIconSynchError
  extends DataIconSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aErrorMessage;
  
  public DataIconSynchError(String paramString)
  {
    this.aErrorMessage = paramString;
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 31 * i + (this.aErrorMessage == null ? 0 : this.aErrorMessage.hashCode());
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
    DataIconSynchError localDataIconSynchError = (DataIconSynchError)paramObject;
    if (this.aErrorMessage == null)
    {
      if (localDataIconSynchError.aErrorMessage != null) {
        return false;
      }
    }
    else if (!this.aErrorMessage.equals(localDataIconSynchError.aErrorMessage)) {
      return false;
    }
    return true;
  }
  
  public String getErrorMessage()
  {
    return this.aErrorMessage;
  }
  
  public String toString()
  {
    return "DataIconSynchError [aErrorMessage=" + this.aErrorMessage + "]";
  }
}