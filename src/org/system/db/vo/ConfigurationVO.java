package org.system.db.vo;

import org.system.db.serviceclient.DataConfigurationAttribute;
import org.system.db.serviceclient.DataFileAttribute;
import org.system.db.serviceclient.DataFileSynch;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ConfigurationVO
{
  private long aId;
  private Map<String, DataConfigurationAttribute> aAttributes;
  
  public long getId()
  {
    return this.aId;
  }
  
  public void setId(long paramLong)
  {
    this.aId = paramLong;
  }
  
  public Map<String, DataConfigurationAttribute> getAttributes()
  {
    return this.aAttributes;
  }
  
  public void setAttributes(Map<String, DataConfigurationAttribute> paramMap)
  {
    this.aAttributes = paramMap;
  }
  
  public Map<String, DataFileSynch> getFileSynchAttributes()
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.aAttributes.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      DataConfigurationAttribute localDataConfigurationAttribute = (DataConfigurationAttribute)localEntry.getValue();
      if ((localDataConfigurationAttribute instanceof DataFileAttribute))
      {
        String str = (String)localEntry.getKey();
        DataFileSynch localDataFileSynch = ((DataFileAttribute)localDataConfigurationAttribute).getAttributeValue();
        localHashMap.put(str, localDataFileSynch);
      }
    }
    return localHashMap;
  }
}