package org.system.db.serviceclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DataStatisticExecution
  implements Serializable
{
  private static final long serialVersionUID = 2L;
  private static final String INFO_SEPERATOR = "; ";
  private static final int OTHER_INFO_MAX_SIZE = 500;
  private String aModel;
  private String aMSN;
  protected int aState;
  private String aCommunicatorType;
  protected ArrayList<DeviceSoftwareInfo> aDeviceSoftwareList = new ArrayList();
  private String aIMEI;
  private boolean aGDFSChanged = false;
  private long aCreatedOnClientDate;
  private int aCreatedOnClientTimeZoneOffset;
  private volatile int aResult;
  private long aScriptId;
  private long aScriptVersionNbr;
  private String aOtherInformation;
  private long aRowNumberWithError;
  private long aDomainId;
  private String aDongleId;
  private String aCid;
  private String aOtpCid;
  private Long aWarrantyTimestamp;
  private String aExitState;
  private String aSalesItemId;
  private transient int aLocalDbId;
  protected Long aProductId;
  private String aAccessorySerialNumber;
  private String aDRMKeys;
  private String aMEID;
  private String[] aOldMacAddresses;
  private String[] aNewMacAddresses;
  private String aSignature;
  private String aLogicName;
  private String aLogicVersion;
  private List<TrackingIdentifier> aTrackingIdentifiers;
  
  public DataStatisticExecution() {}
  
  public DataStatisticExecution(String paramString1, String paramString2, boolean paramBoolean, int paramInt1, int paramInt2, String paramString3, long paramLong1, long paramLong2, String paramString4, long paramLong3, long paramLong4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, int paramInt3, Long paramLong5, String paramString10, String paramString11, String paramString12, Long paramLong6, String[] paramArrayOfString, String paramString13, String paramString14)
  {
    this();
    this.aCommunicatorType = paramString1;
    this.aIMEI = paramString2;
    this.aGDFSChanged = paramBoolean;
    this.aResult = paramInt1;
    this.aScriptId = paramLong1;
    this.aState = paramInt2;
    this.aModel = paramString3;
    this.aScriptVersionNbr = paramLong2;
    this.aOtherInformation = paramString4;
    this.aRowNumberWithError = paramLong3;
    this.aDomainId = paramLong4;
    this.aDongleId = paramString5;
    this.aCid = paramString6;
    this.aOtpCid = paramString7;
    this.aSalesItemId = paramString8;
    this.aExitState = paramString9;
    this.aLocalDbId = paramInt3;
    this.aProductId = paramLong5;
    this.aAccessorySerialNumber = paramString10;
    this.aDRMKeys = paramString11;
    this.aMEID = paramString12;
    this.aWarrantyTimestamp = paramLong6;
    this.aNewMacAddresses = paramArrayOfString;
    this.aSignature = null;
    this.aLogicName = paramString13;
    this.aLogicVersion = paramString14;
    this.aOldMacAddresses = null;
  }
  
  public void setCommunicatorType(String paramString)
  {
    this.aCommunicatorType = paramString;
  }
  
  public void setCreatedOnClientDate(long paramLong, int paramInt)
  {
    this.aCreatedOnClientDate = paramLong;
    this.aCreatedOnClientTimeZoneOffset = paramInt;
  }
  
  public int getCreatedOnClientTimeZoneOffset()
  {
    return this.aCreatedOnClientTimeZoneOffset;
  }
  
  public String getCommunicatorType()
  {
    return this.aCommunicatorType;
  }
  
  public void setModel(String paramString)
  {
    this.aModel = paramString;
  }
  
  public String getModel()
  {
    return this.aModel;
  }
  
  public void setMSN(String paramString)
  {
    this.aMSN = paramString;
  }
  
  public String getMSN()
  {
    return this.aMSN;
  }
  
  public int getStateNbr()
  {
    return this.aState;
  }
  
  public void setStatemachineState(int paramInt)
  {
    this.aState = paramInt;
  }
  
  public void setResult(int paramInt)
  {
    this.aResult = paramInt;
  }
  
  public int getResult()
  {
    return this.aResult;
  }
  
  public long getCreationDate()
  {
    return this.aCreatedOnClientDate;
  }
  
  public boolean isGdfsChanged()
  {
    return this.aGDFSChanged;
  }
  
  public void setScriptId(long paramLong)
  {
    this.aScriptId = paramLong;
  }
  
  public long getScriptId()
  {
    return this.aScriptId;
  }
  
  public long getScriptVersion()
  {
    return this.aScriptVersionNbr;
  }
  
  public void setDomainId(long paramLong)
  {
    this.aDomainId = paramLong;
  }
  
  public long getDomainId()
  {
    return this.aDomainId;
  }
  
  public void setScriptVersion(long paramLong)
  {
    this.aScriptVersionNbr = paramLong;
  }
  
  public synchronized void addOtherInformation(String paramString)
  {
    if (this.aOtherInformation == null)
    {
      this.aOtherInformation = paramString;
    }
    else
    {
      this.aOtherInformation += "; ";
      this.aOtherInformation += paramString;
    }
    if ((this.aOtherInformation != null) && (this.aOtherInformation.length() > 500))
    {
      this.aOtherInformation = this.aOtherInformation.substring(0, 497);
      this.aOtherInformation += "...";
    }
  }
  
  public void clearOtherInformation()
  {
    this.aOtherInformation = null;
  }
  
  public String getOtherInformation()
  {
    return this.aOtherInformation;
  }
  
  public void setErrorOnRow(long paramLong)
  {
    this.aRowNumberWithError = paramLong;
  }
  
  public void setIMEI(String paramString)
  {
    this.aIMEI = paramString;
  }
  
  public String getIMEI()
  {
    return this.aIMEI;
  }
  
  public void setMEID(String paramString)
  {
    this.aMEID = paramString;
  }
  
  public String getMEID()
  {
    return this.aMEID;
  }
  
  public List<DeviceSoftwareInfo> getAllSwInfoList()
  {
    return this.aDeviceSoftwareList;
  }
  
  public long getScriptErrorRowNumber()
  {
    return this.aRowNumberWithError;
  }
  
  public boolean hasSwInfo()
  {
    return !this.aDeviceSoftwareList.isEmpty();
  }
  
  public int getLocalDbId()
  {
    return this.aLocalDbId;
  }
  
  public DeviceSoftwareInfo getDeviceSoftwareInfo(int paramInt, String paramString, DeviceUpdateContext paramDeviceUpdateContext, boolean paramBoolean)
  {
    Iterator localIterator = this.aDeviceSoftwareList.iterator();
    DeviceSoftwareInfo localDeviceSoftwareInfo;
    while (localIterator.hasNext())
    {
      localDeviceSoftwareInfo = (DeviceSoftwareInfo)localIterator.next();
      if (localDeviceSoftwareInfo.matches(paramString, paramDeviceUpdateContext, paramInt)) {
        return localDeviceSoftwareInfo;
      }
    }
    if (paramBoolean)
    {
      localDeviceSoftwareInfo = new DeviceSoftwareInfo(null, null, paramString, paramDeviceUpdateContext, paramInt);
      this.aDeviceSoftwareList.add(localDeviceSoftwareInfo);
      return localDeviceSoftwareInfo;
    }
    return null;
  }
  
  public String getDongleId()
  {
    return this.aDongleId;
  }
  
  public void setSalesItemId(String paramString)
  {
    this.aSalesItemId = paramString;
  }
  
  public void setDongleId(String paramString)
  {
    this.aDongleId = paramString;
  }
  
  public void setCid(String paramString)
  {
    this.aCid = paramString;
  }
  
  public String getCid()
  {
    return this.aCid;
  }
  
  public void setProductId(Long paramLong)
  {
    this.aProductId = paramLong;
  }
  
  public Long getProductId()
  {
    return this.aProductId;
  }
  
  public void setOtpCid(String paramString)
  {
    this.aOtpCid = paramString;
  }
  
  public String getOtpCid()
  {
    return this.aOtpCid;
  }
  
  public String getSalesItemId()
  {
    return this.aSalesItemId;
  }
  
  public String getAccessorySerialNumber()
  {
    return this.aAccessorySerialNumber;
  }
  
  public void setAccessorySerialNumber(String paramString)
  {
    this.aAccessorySerialNumber = paramString;
  }
  
  public String getExitState()
  {
    return this.aExitState;
  }
  
  public void setCurrentExitState(String paramString)
  {
    this.aExitState = paramString;
  }
  
  public void setDRMKeys(String paramString)
  {
    this.aDRMKeys = paramString;
  }
  
  public String getDRMKeysAsString()
  {
    return this.aDRMKeys;
  }
  
  public Set<String> getDRMKeys()
  {
    return parseDRMKeys(this.aDRMKeys);
  }
  
  public Long getWarrantyTimestamp()
  {
    return this.aWarrantyTimestamp;
  }
  
  public void setWarrantyTimestamp(Long paramLong)
  {
    this.aWarrantyTimestamp = paramLong;
  }
  
  public String[] getOldMacAddresses()
  {
    return this.aOldMacAddresses;
  }
  
  public String[] getNewMacAddresses()
  {
    return this.aNewMacAddresses;
  }
  
  public void setOldMacAddresses(String[] paramArrayOfString)
  {
    this.aOldMacAddresses = paramArrayOfString;
  }
  
  public void setNewMacAddresses(String[] paramArrayOfString)
  {
    this.aNewMacAddresses = paramArrayOfString;
  }
  
  protected static Set<String> parseDRMKeys(String paramString)
  {
    if (paramString != null)
    {
      String[] arrayOfString1 = paramString.trim().split(" \\+ ");
      HashSet localHashSet = new HashSet();
      for (String str : arrayOfString1) {
        if (!str.trim().equals("")) {
          localHashSet.add(str);
        }
      }
      return localHashSet;
    }
    return null;
  }
  
  public List<TrackingIdentifier> getTrackingIdentifiers()
  {
    return this.aTrackingIdentifiers;
  }
  
  public void setTrackingIdentifiers(List<TrackingIdentifier> paramList)
  {
    this.aTrackingIdentifiers = paramList;
  }
  
  public void setTrackingSignature(String paramString)
  {
    this.aSignature = paramString;
  }
  
  public String getTrackingSignature()
  {
    return this.aSignature;
  }
  
  public void setLogicName(String paramString)
  {
    this.aLogicName = paramString;
  }
  
  public String getLogicName()
  {
    return this.aLogicName;
  }
  
  public void setLogicVersion(String paramString)
  {
    this.aLogicVersion = paramString;
  }
  
  public String getLogicVersion()
  {
    return this.aLogicVersion;
  }
  
  public class DeviceSoftwareInfo
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    private String aSwNumber;
    private String aSwVersion;
    private int aSequenceNumber;
    private String aSwType;
    private boolean aBeforeUpdate;
    private String aDeviceUpdateContext;
    
    protected boolean matches(String paramString, DeviceUpdateContext paramDeviceUpdateContext, int paramInt)
    {
      return (this.aSwType.equals(paramString)) && (paramDeviceUpdateContext.equals(getDeviceUpdateContext())) && (paramInt == this.aSequenceNumber);
    }
    
    DeviceSoftwareInfo(String paramString1, String paramString2, String paramString3, DeviceUpdateContext paramDeviceUpdateContext, int paramInt)
    {
      this.aSwNumber = paramString1;
      this.aSwVersion = paramString2;
      this.aSwType = paramString3;
      this.aDeviceUpdateContext = EnumSerializer.serialize(paramDeviceUpdateContext);
      this.aSequenceNumber = paramInt;
      if (paramDeviceUpdateContext == null) {
        throw new IllegalArgumentException("pType must not be null");
      }
    }
    
    public void setSwNumber(String paramString)
    {
      this.aSwNumber = paramString;
    }
    
    public String getSwNumber()
    {
      return this.aSwNumber;
    }
    
    public void setSwVersion(String paramString)
    {
      this.aSwVersion = paramString;
    }
    
    public String getSwVersion()
    {
      return this.aSwVersion;
    }
    
    public String getSwType()
    {
      return this.aSwType;
    }
    
    public DeviceUpdateContext getDeviceUpdateContext()
    {
      DeviceUpdateContext localDeviceUpdateContext = (DeviceUpdateContext)EnumSerializer.deSerialize(DeviceUpdateContext.class, this.aDeviceUpdateContext);
      if (localDeviceUpdateContext == null) {
        localDeviceUpdateContext = this.aBeforeUpdate ? DeviceUpdateContext.BEFORE : DeviceUpdateContext.AFTER;
      }
      return localDeviceUpdateContext;
    }
    
    public int getSequenceNumber()
    {
      return this.aSequenceNumber;
    }
    
    public String toOldStringRepresentation()
    {
      if ((this.aSwVersion == null) && (this.aSwNumber == null)) {
        return null;
      }
      StringBuffer localStringBuffer = new StringBuffer();
      if (this.aSwVersion != null) {
        localStringBuffer.append(this.aSwVersion);
      }
      localStringBuffer.append("$");
      if (this.aSwNumber != null) {
        localStringBuffer.append(this.aSwNumber);
      }
      return localStringBuffer.toString();
    }
    
    public boolean isEmpty()
    {
      return (this.aSwNumber == null) && (this.aSwVersion == null);
    }
    
    public String toString()
    {
      return "Software number: " + this.aSwNumber + " version: " + this.aSwVersion + " swtype: " + this.aSwType + " Script sequence: " + this.aSequenceNumber + " " + getDeviceUpdateContext() + " update";
    }
  }
}