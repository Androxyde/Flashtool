package org.system.db.sql.file;

import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteFile
  extends AbstractDbObject
{
  private static final String DELETE_FILE_ENTRY = "DELETE FROM tblFile WHERE fil_local_pk = ";
  private static final String DELETE_FILE_PROPERTIES = "DELETE FROM tblFileProperty WHERE fp_fil_local_fk = ";
  
  public boolean execute(long paramLong)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("DELETE FROM tblFileProperty WHERE fp_fil_local_fk = " + paramLong);
      localPreparedStatement.execute();
      localPreparedStatement = getConnection().prepareStatement("DELETE FROM tblFile WHERE fil_local_pk = " + paramLong);
      int i = localPreparedStatement.executeUpdate();
      boolean bool = i == 1;
      return bool;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
}