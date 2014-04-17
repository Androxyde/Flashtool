package org.system.db.serviceclient;

import java.io.Serializable;

/**
 * @deprecated
 */
public class DataStatisticScript
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aScriptId;
  public long aScriptVersionNbr;
  public long aScriptLastUpdated;
  public String aOtherInformation;
  public int aResult;
  public long aInsertedTimeStamp;
  public long aRowNumberWithError;
  
  public DataStatisticScript(long paramLong1, long paramLong2, long paramLong3)
  {
    this.aScriptId = paramLong1;
    this.aScriptVersionNbr = paramLong2;
    this.aScriptLastUpdated = paramLong3;
  }
}