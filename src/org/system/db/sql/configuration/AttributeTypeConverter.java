package org.system.db.sql.configuration;

import org.system.db.serviceclient.DataBooleanAttribute;
import org.system.db.serviceclient.DataConfigurationAttribute;
import org.system.db.serviceclient.DataFileAttribute;
import org.system.db.serviceclient.DataLongAttribute;
import org.system.db.serviceclient.DataStringAttribute;

class AttributeTypeConverter
{
  static DataConfigurationAttribute getDataAttribute(AttributeType paramAttributeType, String paramString)
  {
    Object localObject = null;
    switch (paramAttributeType)
    {
    case Hex: 
      localObject = new DataStringAttribute(paramString);
      break;
    case Long: 
      boolean bool = Boolean.parseBoolean(paramString);
      localObject = new DataBooleanAttribute(bool);
      break;
    case Boolean: 
    case File: 
      localObject = new DataLongAttribute(Long.parseLong(paramString));
      break;
    case String: 
      throw new IllegalArgumentException("A file has to be retrieved from the blob repository.");
    }
    return (DataConfigurationAttribute) localObject;
  }
  
  static AttributeType getType(DataConfigurationAttribute paramDataConfigurationAttribute)
  {
    if ((paramDataConfigurationAttribute instanceof DataStringAttribute)) {
      return AttributeType.String;
    }
    if ((paramDataConfigurationAttribute instanceof DataBooleanAttribute)) {
      return AttributeType.Boolean;
    }
    if ((paramDataConfigurationAttribute instanceof DataLongAttribute)) {
      return AttributeType.Long;
    }
    if ((paramDataConfigurationAttribute instanceof DataFileAttribute)) {
      return AttributeType.File;
    }
    return null;
  }
  
  static AttributeType getType(String paramString)
  {
    AttributeType localAttributeType;
    if ((paramString == null) || (paramString.trim().isEmpty()))
    {
      localAttributeType = AttributeType.String;
    }
    else
    {
      localAttributeType = AttributeType.valueOf(paramString);
      if (localAttributeType == null) {
        throw new IllegalArgumentException("Unknown attribute type: " + paramString);
      }
    }
    return localAttributeType;
  }
}