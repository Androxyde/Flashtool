package org.system.db.sql.file;

import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetPrimaryKeyForFile
  extends AbstractDbObject
{
  private static final String vSql = "SELECT fil_local_pk FROM tblFile WHERE fil_server_id = ? AND fil_serverversion_id = ? AND fil_serverfile_lastupdated = ?";
  
  public int execute(long paramLong1, long paramLong2, long paramLong3)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("SELECT fil_local_pk FROM tblFile WHERE fil_server_id = ? AND fil_serverversion_id = ? AND fil_serverfile_lastupdated = ?");
      localPreparedStatement.setLong(1, paramLong1);
      localPreparedStatement.setInt(2, (int)paramLong2);
      localPreparedStatement.setLong(3, paramLong3);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        int i = localResultSet.getInt("fil_local_pk");
        int j = i;
        return j;
      }
      return -1;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
}