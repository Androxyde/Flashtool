package org.system.db.service;

public class ServiceIdFactory
{
  public static ServiceId parse(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      return null;
    }
    if (paramString.startsWith("LegacyId:")) {
      localObject = parseLegacyId(paramString);
    } else if (paramString.startsWith("EveId:")) {
      localObject = new EveServiceId(paramString);
    }
    return (ServiceId) localObject;
  }
  
  static ServiceId parseLegacyId(String paramString)
  {
    try
    {
      String str1 = paramString.substring("LegacyId:".length());
      int i = str1.indexOf("_");
      String str2 = str1.substring(0, i);
      long l1 = Long.parseLong(str2);
      String str3 = str1.substring(i + 1, str1.length());
      long l2 = Long.parseLong(str3);
      return new LegacyServiceId(l1, l2);
    }
    catch (Exception localException) {}
    return null;
  }
}