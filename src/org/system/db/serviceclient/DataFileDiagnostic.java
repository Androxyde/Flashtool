package org.system.db.serviceclient;

import java.io.Serializable;

public class DataFileDiagnostic
  implements Serializable
{
  private static final long serialVersionUID = 1387400310496468517L;
  private static final int MAC_SIZE = 20;
  private String aTimeStamp;
  private byte[] aDiagnosticData;
  private byte[] aMac;
  private int aHeaderVersion;
  
  public DataFileDiagnostic(int paramInt, String paramString, byte[] paramArrayOfByte)
  {
    this.aHeaderVersion = paramInt;
    this.aTimeStamp = paramString;
    this.aDiagnosticData = extractDiagnosticData(paramArrayOfByte);
    this.aMac = extractMac(paramArrayOfByte);
  }
  
  public String getTimestamp()
  {
    return this.aTimeStamp;
  }
  
  public int getDiagnosticFileDataLength()
  {
    if ((this.aDiagnosticData == null) || (this.aMac == null)) {
      return 0;
    }
    return this.aDiagnosticData.length + this.aMac.length;
  }
  
  public byte[] getMac()
  {
    return this.aMac;
  }
  
  public byte[] getDiagnosticData()
  {
    return this.aDiagnosticData;
  }
  
  public int getHeaderVersion()
  {
    return this.aHeaderVersion;
  }
  
  private byte[] extractMac(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[20];
    System.arraycopy(paramArrayOfByte, paramArrayOfByte.length - 20, arrayOfByte, 0, 20);
    return arrayOfByte;
  }
  
  private byte[] extractDiagnosticData(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte.length - 20];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
}