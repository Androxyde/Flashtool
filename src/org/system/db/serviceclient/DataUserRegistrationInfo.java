package org.system.db.serviceclient;

import java.io.Serializable;

public class DataUserRegistrationInfo
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String aFirstName;
  private String aLastName;
  private String aUserName;
  private byte[] aPassword;
  private String aEmail;
  private String aOfficePhone;
  private String aMobilePhone;
  private String aTitle;
  private long aUserType;
  private String aCompany;
  private String aStreet;
  private String aZipCode;
  private String aCity;
  private long aState;
  private long aCountry;
  private String aBuilding;
  private String aUserDescription;
  private long aLastUpdated;
  
  public DataUserRegistrationInfo(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte, String paramString4, String paramString5, String paramString6, String paramString7, long paramLong1, String paramString8, String paramString9, String paramString10, String paramString11, long paramLong2, long paramLong3, String paramString12, String paramString13, long paramLong4)
  {
    setFirstName(paramString1);
    setLastName(paramString2);
    setUserName(paramString3);
    setPassword(paramArrayOfByte);
    setEmail(paramString4);
    setOfficePhone(paramString5);
    setMobilePhone(paramString6);
    setTitle(paramString7);
    setUserType(paramLong1);
    setCompany(paramString8);
    setStreet(paramString9);
    setZipCode(paramString10);
    setCity(paramString11);
    setState(paramLong2);
    setCountry(paramLong3);
    setBuilding(paramString12);
    setUserDescription(paramString13);
    setLastUpdated(paramLong4);
  }
  
  public DataUserRegistrationInfo() {}
  
  public void setFirstName(String paramString)
  {
    this.aFirstName = paramString;
  }
  
  public String getFirstName()
  {
    return this.aFirstName;
  }
  
  public void setLastName(String paramString)
  {
    this.aLastName = paramString;
  }
  
  public String getLastName()
  {
    return this.aLastName;
  }
  
  public void setUserName(String paramString)
  {
    this.aUserName = paramString;
  }
  
  public String getUserName()
  {
    return this.aUserName;
  }
  
  public void setPassword(byte[] paramArrayOfByte)
  {
    this.aPassword = paramArrayOfByte;
  }
  
  public byte[] getPassword()
  {
    return this.aPassword;
  }
  
  public void setEmail(String paramString)
  {
    this.aEmail = paramString;
  }
  
  public String getEmail()
  {
    return this.aEmail;
  }
  
  public void setOfficePhone(String paramString)
  {
    this.aOfficePhone = paramString;
  }
  
  public String getOfficePhone()
  {
    return this.aOfficePhone;
  }
  
  public void setMobilePhone(String paramString)
  {
    this.aMobilePhone = paramString;
  }
  
  public String getMobilePhone()
  {
    return this.aMobilePhone;
  }
  
  public void setTitle(String paramString)
  {
    this.aTitle = paramString;
  }
  
  public String getTitle()
  {
    return this.aTitle;
  }
  
  public void setUserType(long paramLong)
  {
    this.aUserType = paramLong;
  }
  
  public long getUserType()
  {
    return this.aUserType;
  }
  
  public void setCompany(String paramString)
  {
    this.aCompany = paramString;
  }
  
  public String getCompany()
  {
    return this.aCompany;
  }
  
  public void setStreet(String paramString)
  {
    this.aStreet = paramString;
  }
  
  public String getStreet()
  {
    return this.aStreet;
  }
  
  public void setZipCode(String paramString)
  {
    this.aZipCode = paramString;
  }
  
  public String getZipCode()
  {
    return this.aZipCode;
  }
  
  public void setCity(String paramString)
  {
    this.aCity = paramString;
  }
  
  public String getCity()
  {
    return this.aCity;
  }
  
  public void setState(long paramLong)
  {
    this.aState = paramLong;
  }
  
  public long getState()
  {
    return this.aState;
  }
  
  public void setCountry(long paramLong)
  {
    this.aCountry = paramLong;
  }
  
  public long getCountry()
  {
    return this.aCountry;
  }
  
  public void setBuilding(String paramString)
  {
    this.aBuilding = paramString;
  }
  
  public String getBuilding()
  {
    return this.aBuilding;
  }
  
  public void setUserDescription(String paramString)
  {
    this.aUserDescription = paramString;
  }
  
  public String getUserDescription()
  {
    return this.aUserDescription;
  }
  
  public void setLastUpdated(long paramLong)
  {
    this.aLastUpdated = paramLong;
  }
  
  public long getLastUpdated()
  {
    return this.aLastUpdated;
  }
}