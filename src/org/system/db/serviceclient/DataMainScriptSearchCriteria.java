package org.system.db.serviceclient;

import java.io.Serializable;

public class DataMainScriptSearchCriteria
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aScriptName;
  public String aModel;
  public String aServiceType;
  public String aDomain;
  public long aStartDate;
  public long aEndDate;
  
  public DataMainScriptSearchCriteria(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong1, long paramLong2)
  {
    this.aScriptName = paramString1;
    this.aModel = paramString2;
    this.aServiceType = paramString3;
    this.aDomain = paramString4;
    this.aStartDate = paramLong1;
    this.aEndDate = paramLong2;
  }
}