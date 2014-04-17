package org.system.db.sql;

import org.system.db.DbUtil;
import org.system.db.LocalDbAcess;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractDbObject
{
  protected static final int OBJECT_NOT_IN_DB = -1;
  private final LocalDbAcess aLocalDb = LocalDbAcess.getInstance();
  
  protected Connection getConnection()
  {
    return this.aLocalDb.getConnection();
  }
  
  protected Object getInsertLockObject()
  {
    return this.aLocalDb.getInsertLockObject();
  }
  
  protected int getLastInsertedId()
    throws SQLException
  {
    Statement localStatement = null;
    ResultSet localResultSet = null;
    Thread.currentThread();
    if (!Thread.holdsLock(getInsertLockObject())) {
      // Retrieving last inserted id without lock! This can read to data corruption
    }
    try
    {
      localStatement = getConnection().createStatement();
      localResultSet = localStatement.executeQuery("CALL IDENTITY()");
      if (localResultSet.next())
      {
        int i = localResultSet.getInt(1);
        return i;
      }
      return -1;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localStatement);
    }
  }
}