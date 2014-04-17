package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("FileSynchError")
public class DataFileSynchError
  extends DataFileSynchResult
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aErrorMessage;
  
  public DataFileSynchError(String paramString)
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
    DataFileSynchError localDataFileSynchError = (DataFileSynchError)paramObject;
    if (this.aErrorMessage == null)
    {
      if (localDataFileSynchError.aErrorMessage != null) {
        return false;
      }
    }
    else if (!this.aErrorMessage.equals(localDataFileSynchError.aErrorMessage)) {
      return false;
    }
    return true;
  }
  
  public String getErrorMessage()
  {
    return this.aErrorMessage;
  }
}