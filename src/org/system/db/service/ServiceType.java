package org.system.db.service;

public enum ServiceType
{
  UPDATE("Software Update"),  UPDATE_CONTENT_REFRESH("Software Update Content Refresh"),  UPDATE_CONTENT_ERASE("Software Update Content Erase"),  MEM_STICK_CONTENT_LOAD("Mem Stick Content Load"),  REFURBISH("Refurbish"),  CUSTOMIZE("Customize"),  ACTIVATION("Activation"),  EMBEDDED_TEST_SOFTWARE("Embedded Test Software"),  OTHER("Other"),  BOOT_UPDATE("Boot Update"),  WIFI_MAC_ADDRESS_SUBSTITUTION("WiFi MAC address Substitution"),  BACKUP_AND_RESTORE("Backup and Restore"),  FLASH("Flash"),  QUICK_CUSTOMIZE("Quick Customize"),  VIEW("View"),  SETTINGS("Settings"),  RESET("Reset"),  PHONE_LOCK_RESET("Phone Lock Reset"),  SLIDER_COUNTER_RESET("Slider Counter Reset"),  TOTAL_CALLTIME_RESET("Total Calltime Reset"),  ADVANCED_CONTENT_LOAD("Advanced Content Load"),  ADVANCED_CONTENT_LOAD_II("Advanced Content Load II");
  
  private final String aName;
  
  private ServiceType(String paramString)
  {
    this.aName = paramString;
  }
  
  public static ServiceType toServiceType(String s)
  {
      ServiceType aservicetype[];
      int j = (aservicetype = values()).length;
      for(int i = 0; i < j; i++)
      {
          ServiceType servicetype = aservicetype[i];
          if(servicetype.aName.equals(s))
              return servicetype;
      }

      return null;
  }
  
  public String getName()
  {
    return this.aName;
  }
  
  public boolean isUpdate()
  {
    return equals(UPDATE);
  }
  
  public boolean isRepair()
  {
    return (equals(UPDATE_CONTENT_ERASE)) || (equals(UPDATE_CONTENT_REFRESH));
  }
  
  public String toString()
  {
    return this.aName;
  }
}