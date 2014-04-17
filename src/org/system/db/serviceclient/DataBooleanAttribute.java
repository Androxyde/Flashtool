package org.system.db.serviceclient;

public class DataBooleanAttribute
  implements DataConfigurationAttribute
{
  private static final long serialVersionUID = 1L;
  private final boolean aAttributeValue;
  
  public DataBooleanAttribute(boolean paramBoolean)
  {
    this.aAttributeValue = paramBoolean;
  }
  
  public Boolean getAttributeValue()
  {
    return Boolean.valueOf(this.aAttributeValue);
  }
}