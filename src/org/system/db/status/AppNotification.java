package org.system.db.status;

public abstract interface AppNotification
{
  public abstract void displayError(String paramString);
  
  public abstract void displayError(String paramString1, String paramString2);
}