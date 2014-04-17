package org.system.db.service;

public class SalesItem
  implements Comparable<SalesItem>
{
  private final String aId;
  private final String aDescription;
  
  public SalesItem(String paramString1, String paramString2)
  {
    this.aId = paramString1;
    this.aDescription = paramString2;
  }
  
  public String getId()
  {
    return this.aId;
  }
  
  public String getDescription()
  {
    return this.aDescription;
  }
  
  public String toString()
  {
    if (this.aDescription != null) {
      return this.aId + " " + this.aDescription;
    }
    return this.aId;
  }
  
  public int compareTo(SalesItem paramSalesItem)
  {
    return toString().compareTo(paramSalesItem.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof SalesItem)) {
      return toString().equals(paramObject.toString());
    }
    return false;
  }
  
  public int hashCode()
  {
    return getId().hashCode();
  }
}