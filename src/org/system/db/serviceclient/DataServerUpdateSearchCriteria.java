package org.system.db.serviceclient;

import java.io.Serializable;

public class DataServerUpdateSearchCriteria
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aModel;
  public String aScriptName;
  public long aCreationDateStart;
  public long aCreationDateEnd;
  public long aModifiedDateStart;
  public long aModifiedDateEnd;
  
  public DataServerUpdateSearchCriteria(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.aModel = paramString1;
    this.aScriptName = paramString2;
    this.aCreationDateStart = paramLong1;
    this.aCreationDateEnd = paramLong2;
    this.aModifiedDateStart = paramLong3;
    this.aModifiedDateEnd = paramLong4;
  }
}