package org.system.db.serviceclient;

import java.io.Serializable;

public class DataProdIdentificationFormula
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public long aId;
  public long aLastUpdated;
  public String aName;
  public String aTextFormula;
  public String aLogicalFormula;
  public DataProdIdentificationFormulaTerm[] aTerms;
  
  public DataProdIdentificationFormula(long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, DataProdIdentificationFormulaTerm[] paramArrayOfDataProdIdentificationFormulaTerm)
  {
    this.aLastUpdated = paramLong1;
    this.aId = paramLong2;
    this.aName = paramString1;
    this.aTextFormula = paramString2;
    this.aLogicalFormula = paramString3;
    this.aTerms = paramArrayOfDataProdIdentificationFormulaTerm;
  }
}