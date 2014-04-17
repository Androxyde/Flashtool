package org.system.db.serviceclient;

import java.io.Serializable;

public abstract interface DataConfigurationAttribute
  extends Serializable
{
  public abstract Object getAttributeValue();
}