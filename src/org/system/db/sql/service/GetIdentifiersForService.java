package org.system.db.sql.service;

import org.system.db.serviceclient.DataIdentifier;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetIdentifiersForService
  extends AbstractDbObject
{
  private static final String SELECT_IDENTIFIERS_FOR_SERVICE = "SELECT its_identifiercategory, its_identifiervalue FROM tblidentifierstoservice WHERE its_ser_local_fk = ";
  
  public DataIdentifier[] execute(long paramLong)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("SELECT its_identifiercategory, its_identifiervalue FROM tblidentifierstoservice WHERE its_ser_local_fk = " + paramLong);
      localResultSet = localPreparedStatement.executeQuery();
      ArrayList localArrayList = new ArrayList();
      while (localResultSet.next())
      {
        String str1 = localResultSet.getString("its_identifiercategory");
        String str2 = localResultSet.getString("its_identifiervalue");
        localArrayList.add(new DataIdentifier(str1, str2));
      }
      DataIdentifier[] arrayOfDataIdentifier = (DataIdentifier[])localArrayList.toArray(new DataIdentifier[0]);
      return arrayOfDataIdentifier;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
}