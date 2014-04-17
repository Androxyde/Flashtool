package org.system.db.sql.file;

import org.system.db.blob.FileContentId;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UnreferencedFiles
  extends AbstractDbObject
{
  private static final String UNREFERENCED_FILES = "select fil_local_pk from tblFile where fil_local_pk not in (select cts_fil_local_fk from tblAttributesToService)";
  
  public List<Integer> findUnreferencedFileIds()
    throws SQLException
  {
    ResultSet localResultSet = null;
    LinkedList localLinkedList1 = new LinkedList();
    PreparedStatement localPreparedStatement = getConnection().prepareStatement("select fil_local_pk from tblFile where fil_local_pk not in (select cts_fil_local_fk from tblAttributesToService)");
    try
    {
      localResultSet = localPreparedStatement.executeQuery();
      while (localResultSet.next())
      {
        Integer localInteger = Integer.valueOf(localResultSet.getInt(1));
        localLinkedList1.add(localInteger);
      }
      LinkedList localLinkedList2 = localLinkedList1;
      return localLinkedList2;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
  
  public int getNbrFileContentReferences(FileContentId paramFileContentId)
    throws SQLException
  {
    ResultSet localResultSet = null;
    PreparedStatement localPreparedStatement = getConnection().prepareStatement("select count(*) from tblFile where fil_servercontentinfo_id=?");
    try
    {
      localPreparedStatement.setLong(1, paramFileContentId.toLong());
      localResultSet = localPreparedStatement.executeQuery();
      localResultSet.next();
      int i = localResultSet.getInt(1);
      int j = i;
      return j;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
}