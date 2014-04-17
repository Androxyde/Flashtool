package org.system.db.serviceclient;

public class DataStringAttribute
  implements DataConfigurationAttribute
{
  private static final long serialVersionUID = 1L;
  private final String aAttributeValue;
  
  public DataStringAttribute(String paramString)
  {
    this.aAttributeValue = paramString;
  }
  
  public String getAttributeValue()
  {
    return this.aAttributeValue;
  }
}