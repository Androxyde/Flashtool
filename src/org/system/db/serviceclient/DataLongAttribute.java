package org.system.db.serviceclient;

public class DataLongAttribute
  implements DataConfigurationAttribute
{
  private static final long serialVersionUID = 1L;
  private final long aAttributeValue;
  
  public DataLongAttribute(long paramLong)
  {
    this.aAttributeValue = paramLong;
  }
  
  public Long getAttributeValue()
  {
    return Long.valueOf(this.aAttributeValue);
  }
}