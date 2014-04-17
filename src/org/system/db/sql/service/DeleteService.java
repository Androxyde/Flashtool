package org.system.db.sql.service;

import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteService
  extends AbstractDbObject
{
  private static final String DELETE_IDENTIFIERS_TO_SERVICE = "DELETE FROM tblIdentifiersToService WHERE its_ser_local_fk = ";
  private static final String DELETE_SERVICE_BY_LOCAL_ID = "DELETE FROM tblService WHERE ser_local_pk = ";
  private static final String DELETE_ATTRIBUTES_TO_SERVICE = "DELETE FROM tblattributestoservice WHERE cts_ser_local_fk = ";
  
  public void execute(long paramLong)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    Connection localConnection = getConnection();
    try
    {
      localPreparedStatement = localConnection.prepareStatement("DELETE FROM tblattributestoservice WHERE cts_ser_local_fk = " + paramLong);
      localPreparedStatement.execute();
      localPreparedStatement = localConnection.prepareStatement("DELETE FROM tblIdentifiersToService WHERE its_ser_local_fk = " + paramLong);
      localPreparedStatement.execute();
      localPreparedStatement = localConnection.prepareStatement("DELETE FROM tblService WHERE ser_local_pk = " + paramLong);
      localPreparedStatement.execute();
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
}