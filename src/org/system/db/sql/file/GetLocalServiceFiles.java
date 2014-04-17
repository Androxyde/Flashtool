package org.system.db.sql.file;

import org.system.db.serviceclient.DataFile;
import org.system.db.service.LegacyServiceId;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import org.system.db.sql.service.GetService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

public class GetLocalServiceFiles
  extends AbstractDbObject
{
  private static final String GET_FILE_IDS = "select cts_fil_local_fk from tblAttributesToService where cts_ser_local_fk=? and cts_fil_local_fk is not null";
  
  public Collection<DataFile> execute(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    Integer localInteger = new GetService().getLocalServiceId(paramLegacyServiceId);
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    LinkedList localLinkedList1 = new LinkedList();
    try
    {
      localPreparedStatement = getConnection().prepareStatement("select cts_fil_local_fk from tblAttributesToService where cts_ser_local_fk=? and cts_fil_local_fk is not null");
      localPreparedStatement.setInt(1, localInteger.intValue());
      localResultSet = localPreparedStatement.executeQuery();
      GetFile localGetFile = new GetFile();
      while (localResultSet.next())
      {
        long l = localResultSet.getLong("cts_fil_local_fk");
        DataFile localDataFile = localGetFile.getFileByLocalId(l);
        if (localDataFile != null) {
          localLinkedList1.add(localDataFile);
        } else {
        	//
        }
      }
      LinkedList localLinkedList2 = localLinkedList1;
      return localLinkedList2;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
}