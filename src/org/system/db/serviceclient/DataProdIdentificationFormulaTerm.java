package org.system.db.serviceclient;

import java.io.Serializable;

public class DataProdIdentificationFormulaTerm
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aLastUpdated;
  public String[] aCategories;
  public boolean[] aNot;
  public int aPriority;
  
  public DataProdIdentificationFormulaTerm(long paramLong, String[] paramArrayOfString, boolean[] paramArrayOfBoolean, int paramInt)
  {
    this.aLastUpdated = paramLong;
    this.aCategories = paramArrayOfString;
    this.aNot = paramArrayOfBoolean;
    this.aPriority = paramInt;
  }
}