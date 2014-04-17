package org.system.db;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil
{
  
  public static void closeStatement(Statement paramStatement)
  {
    try
    {
      if (paramStatement != null) {
        paramStatement.close();
      }
    }
    catch (SQLException localSQLException)
    {
    //Logger
    }
  }
  
  public static void closeResultSet(ResultSet paramResultSet)
  {
    try
    {
      if (paramResultSet != null) {
        paramResultSet.close();
      }
    }
    catch (SQLException localSQLException)
    {
      //Logger
    }
  }
}
