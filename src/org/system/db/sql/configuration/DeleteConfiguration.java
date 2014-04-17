package org.system.db.sql.configuration;

import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteConfiguration
  extends AbstractDbObject
{
  private static final String vDeleteAttributesToService = "DELETE FROM tblAttributesToService WHERE cts_ser_local_fk = ";
  
  public void execute(long paramLong)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    Statement localStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("DELETE FROM tblAttributesToService WHERE cts_ser_local_fk = " + paramLong);
      localPreparedStatement.execute();
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeStatement(localStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
}