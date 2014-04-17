package org.system.db.serviceclient;

import java.io.Serializable;

public class DataFilterCategory
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aCategory;
  public String[] aValues;
  
  public DataFilterCategory(String paramString, String[] paramArrayOfString)
  {
    this.aCategory = paramString;
    this.aValues = paramArrayOfString;
  }
}