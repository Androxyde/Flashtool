package org.system.db.serviceclient;

import java.io.Serializable;

public class DataPermission
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String aHashCodeString;
  private String aElementTag;
  private String aActionTag;
  
  public DataPermission(String paramString1, String paramString2)
  {
    this.aElementTag = paramString1;
    this.aActionTag = paramString2;
    this.aHashCodeString = (this.aElementTag + this.aActionTag);
  }
  
  public String getActionTag()
  {
    return this.aActionTag;
  }
  
  public String getElementTag()
  {
    return this.aElementTag;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DataPermission)) {
      return (this.aElementTag.equals(((DataPermission)paramObject).aElementTag)) && (this.aActionTag.equals(((DataPermission)paramObject).aActionTag));
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.aHashCodeString.hashCode();
  }
}