package org.system.db.serviceclient;

public class DataFileAttribute
  implements DataConfigurationAttribute
{
  private static final long serialVersionUID = 1L;
  private final DataFileSynch aAttributeValue;
  
  public DataFileAttribute(DataFileSynch paramDataFileSynch)
  {
    this.aAttributeValue = paramDataFileSynch;
  }
  
  public DataFileSynch getAttributeValue()
  {
    return this.aAttributeValue;
  }
}