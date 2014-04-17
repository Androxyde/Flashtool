package org.system.db.serviceclient;

import java.io.Serializable;
import java.util.Map;

public class DataStatisticSession
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aSessionId;
  public String aOS;
  public DataStatisticExecution[] aSessionExcecutions;
  public String aMA3Version;
  public String aInstallationVersion;
  public long aServerSessionId;
  public String aNGSessionId;
  public String aClientId;
  public String aClientTypeTag;
  public String aClientApplicationId;
  public Map<String, String> aSessionAttributes;
  public String aServicePack;
  
  public DataStatisticSession(long paramLong1, String paramString1, String paramString2, DataStatisticExecution[] paramArrayOfDataStatisticExecution, String paramString3, String paramString4, long paramLong2, String paramString5, String paramString6, String paramString7, String paramString8, Map<String, String> paramMap)
  {
    this.aSessionId = paramLong1;
    this.aOS = paramString1;
    this.aServicePack = paramString2;
    this.aSessionExcecutions = paramArrayOfDataStatisticExecution;
    this.aMA3Version = paramString3;
    this.aInstallationVersion = paramString4;
    this.aServerSessionId = paramLong2;
    this.aClientTypeTag = paramString5;
    this.aClientApplicationId = paramString6;
    this.aClientId = paramString7;
    this.aNGSessionId = paramString8;
    this.aSessionAttributes = paramMap;
  }
}