package org.system.db.sql.service;

import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetLocalServices
  extends AbstractDbObject
{
  private static final String FIND_ALL_SERVICES_QUERY = "SELECT ser_local_pk FROM tblservice";
  
  public List<Long> execute()
    throws SQLException
  {
    // Getting all services from database
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("SELECT ser_local_pk FROM tblservice");
      localResultSet = localPreparedStatement.executeQuery();
      ArrayList localArrayList1 = new ArrayList();
      while (localResultSet.next())
      {
        long l = localResultSet.getLong("ser_local_pk");
        localArrayList1.add(Long.valueOf(l));
      }
      ArrayList localArrayList2 = localArrayList1;
      return localArrayList2;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
}