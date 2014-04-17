package org.system.db.serviceclient;

import java.io.Serializable;

public class DataFileFormat
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String aName;
  public String aTag;
  
  public DataFileFormat(String paramString1, String paramString2)
  {
    this.aName = paramString1;
    this.aTag = paramString2;
  }
}