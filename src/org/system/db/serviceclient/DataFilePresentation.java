package org.system.db.serviceclient;

import java.io.Serializable;

public class DataFilePresentation
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final int RESULT_CODE_SUCCESS = 1;
  public static final int RESULT_CODE_ERROR = 2;
  public DataScriptDomainId aDataScriptDomainId;
  public String aFilePresentation;
  public String aFileName;
  public String aFileDescription;
  public String aFileTypeTag;
  public String aFileVersionName;
  public int aFileReleaseStatus;
  public DataFileProperty[] aFileProperties;
  public long aFileContentLength;
  public int aResultCode;
  public String aErrorMessage;
  
  public DataFilePresentation(DataScriptDomainId paramDataScriptDomainId, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt, DataFileProperty[] paramArrayOfDataFileProperty, long paramLong)
  {
    this.aDataScriptDomainId = paramDataScriptDomainId;
    this.aFilePresentation = paramString1;
    this.aFileName = paramString2;
    this.aFileDescription = paramString3;
    this.aFileTypeTag = paramString4;
    this.aFileVersionName = paramString5;
    this.aFileReleaseStatus = paramInt;
    this.aFileProperties = paramArrayOfDataFileProperty;
    this.aFileContentLength = paramLong;
    this.aResultCode = 1;
    this.aErrorMessage = null;
  }
  
  public DataFilePresentation(String paramString)
  {
    this.aErrorMessage = paramString;
    this.aResultCode = 2;
  }
}