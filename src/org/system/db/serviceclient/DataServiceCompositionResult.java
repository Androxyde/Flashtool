package org.system.db.serviceclient;

import java.io.Serializable;

public class DataServiceCompositionResult
  implements Serializable
{
  private static final int OK = 1;
  private static final long serialVersionUID = 1L;
  private DataServiceComposition aService;
  private final int aErrorCode;
  
  public DataServiceCompositionResult(DataServiceComposition paramDataServiceComposition, int paramInt)
  {
    this.aService = paramDataServiceComposition;
    this.aErrorCode = paramInt;
  }
  
  public DataServiceComposition getService()
  {
    return this.aService;
  }
  
  public boolean isServiceAccessible()
  {
    return this.aErrorCode == 1;
  }
}