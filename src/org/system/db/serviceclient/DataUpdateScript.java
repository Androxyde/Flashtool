package org.system.db.serviceclient;

import java.io.Serializable;
import java.util.Collection;

public class DataUpdateScript
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final DataScriptListInfo aDataScriptListInfo;
  private final Collection<DataIdentifier> aRevisionIdentifiers;
  
  public DataUpdateScript(DataScriptListInfo paramDataScriptListInfo, Collection<DataIdentifier> paramCollection)
  {
    this.aDataScriptListInfo = paramDataScriptListInfo;
    this.aRevisionIdentifiers = paramCollection;
  }
  
  public DataScriptListInfo getDataScriptListInfo()
  {
    return this.aDataScriptListInfo;
  }
  
  public Collection<DataIdentifier> getRevisionIdentifiers()
  {
    return this.aRevisionIdentifiers;
  }
}