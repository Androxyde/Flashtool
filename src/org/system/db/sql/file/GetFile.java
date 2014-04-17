package org.system.db.sql.file;

import org.system.db.serviceclient.DataFile;
import org.system.db.serviceclient.DataFileProperty;
import org.system.db.serviceclient.DataFileSynch;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetFile
  extends AbstractDbObject
{
  public DataFile getFileByLocalId(long paramLong)
    throws SQLException
  {
	  return execute(paramLong, -1L, -1L, true);
  }
  
  public DataFile getFile(long paramLong1, long paramLong2, long paramLong3)
    throws SQLException
  {
	  return execute(paramLong1, paramLong2, paramLong3, false);
  }
  
  DataFile execute(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean)
    throws SQLException
  {
    String str = getSelectFileString(paramBoolean);
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement(str);
      if (paramBoolean)
      {
        localPreparedStatement.setInt(1, (int)paramLong1);
      }
      else
      {
        localPreparedStatement.setLong(1, paramLong1);
        localPreparedStatement.setInt(2, (int)paramLong2);
        localPreparedStatement.setLong(3, paramLong3);
      }
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        long l = localResultSet.getLong("fil_local_pk");
        DataFile localDataFile1 = new DataFile(localResultSet.getLong("fil_server_id"), localResultSet.getLong("fil_serverversion_id"), localResultSet.getString("fil_name"), localResultSet.getString("fil_serverversionname"), localResultSet.getString("fil_description"), localResultSet.getString("fil_presentation"), localResultSet.getString("fil_securityname"), localResultSet.getString("fil_typename"), localResultSet.getLong("fil_servercontentinfo_id"), getFileProperties(l), localResultSet.getLong("fil_contentlength"), localResultSet.getLong("fil_serverfile_lastupdated"), localResultSet.getLong("fil_serverversion_lastupdated"));
        DataFile localDataFile2 = localDataFile1;
        return localDataFile2;
      }
      return null;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
  
  public int getLocalFileId(DataFileSynch paramDataFileSynch)
    throws SQLException
  {
    String str = "SELECT fil_local_pk FROM tblFile WHERE fil_serverversion_id = ? AND fil_serverfile_lastupdated = ? AND fil_serverversion_lastupdated = ? AND fil_contentlength = ? AND fil_securityname = ? AND fil_servercontentinfo_id = ? ";
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement(str);
      localPreparedStatement.setLong(1, paramDataFileSynch.aFileVersionId);
      localPreparedStatement.setLong(2, paramDataFileSynch.aFileLastUpdated);
      localPreparedStatement.setLong(3, paramDataFileSynch.aFileVersionLastUpdated);
      localPreparedStatement.setLong(4, paramDataFileSynch.aFileContentLength);
      localPreparedStatement.setString(5, paramDataFileSynch.aFileSecurityTag);
      localPreparedStatement.setLong(6, paramDataFileSynch.aFileContentInfoId);
      localResultSet = localPreparedStatement.executeQuery();
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
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
  
  private static String getSelectFileString(boolean paramBoolean)
  {
    String str = "SELECT fil_local_pk, fil_server_id, fil_serverversion_id, fil_serverversionname, fil_name, fil_securityname, fil_typename, fil_servercontentinfo_id, fil_contentlength, fil_serverfile_lastupdated, fil_serverversion_lastupdated, fil_presentation, fil_description, fil_releasenotes FROM tblFile ";
    if (paramBoolean) {
      return str + "WHERE fil_local_pk = ? ";
    }
    return str + "WHERE fil_server_id = ? " + "AND fil_serverversion_id = ? " + "AND fil_serverfile_lastupdated = ?";
  }
  
  private DataFileProperty[] getFileProperties(long paramLong)
    throws SQLException
  {
    String str = "SELECT fp.fp_categoryname, fp.fp_propertyvalue FROM tblFileProperty fp WHERE fp.fp_fil_local_fk = " + paramLong;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement(str);
      localResultSet = localPreparedStatement.executeQuery();
      ArrayList localArrayList = new ArrayList();
      while (localResultSet.next()) {
        localArrayList.add(new DataFileProperty(localResultSet.getString(1), localResultSet.getString(2)));
      }
      DataFileProperty[] arrayOfDataFileProperty1 = new DataFileProperty[localArrayList.size()];
      localArrayList.toArray(arrayOfDataFileProperty1);
      DataFileProperty[] arrayOfDataFileProperty2 = arrayOfDataFileProperty1;
      return arrayOfDataFileProperty2;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
  
  public Long getContentVerificationCode(int paramInt)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("select fil_contentverificationcode from tblFile where fil_local_pk=" + paramInt);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        Long localLong = Long.valueOf(localResultSet.getLong(1));
        return localLong;
      }
      return null;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
}