package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("ScriptListInfo")
public class DataScriptListInfo
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aServiceId;
  public long aDomainId;
  public String aServiceName;
  public String aDomainName;
  public long aScriptId;
  public String aScriptName;
  public String aClientDescription;
  public boolean aOnlineOnly;
  public boolean aIsTestScript;
  public String aExcecutionExpression;
  public DataIdentifier[] aIdentifiers;
  public long aSecurityPass;
  public long aCreatedDate;
  public long aLastModifiedDate;
  public boolean aIsTessService;
  
  public DataScriptListInfo(long paramLong1, long paramLong2, String paramString1, String paramString2, long paramLong3, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, String paramString5, DataIdentifier[] paramArrayOfDataIdentifier, long paramLong4, long paramLong5, long paramLong6, boolean paramBoolean3)
  {
    this.aServiceId = paramLong1;
    this.aDomainId = paramLong2;
    this.aServiceName = paramString1;
    this.aDomainName = paramString2;
    this.aScriptId = paramLong3;
    this.aScriptName = paramString3;
    this.aClientDescription = paramString4;
    this.aOnlineOnly = paramBoolean1;
    this.aIsTestScript = paramBoolean2;
    this.aExcecutionExpression = paramString5;
    this.aIdentifiers = paramArrayOfDataIdentifier;
    this.aSecurityPass = paramLong4;
    this.aCreatedDate = paramLong5;
    this.aLastModifiedDate = paramLong6;
    this.aIsTessService = paramBoolean3;
  }
}