package org.system.db.serviceclient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

@XStreamAlias("application_data")
public class DataApplicationFile
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @XStreamAlias("name")
  public String aName;
  @XStreamAlias("path")
  public String aPath;
  @XStreamAlias("version")
  public String aVersion;
  
  public DataApplicationFile(String paramString1, String paramString2, String paramString3)
  {
    this.aName = paramString1;
    this.aPath = paramString2;
    this.aVersion = paramString3;
  }
}