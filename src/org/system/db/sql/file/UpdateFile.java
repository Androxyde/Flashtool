package org.system.db.sql.file;

import org.system.db.serviceclient.DataFile;
import org.system.db.serviceclient.DataFileProperty;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateFile
  extends AbstractDbObject
{
  private static final String UPDATE_FILE_SQL = "UPDATE tblFile SET fil_serverversion_id = ?, fil_name = ?, fil_securityname = ?, fil_typename = ?, fil_servercontentinfo_id = ?, fil_contentlength = ?, fil_blob_checksum = ?, fil_serverfile_lastupdated = ?, fil_serverversion_lastupdated = ?, fil_server_id = ?, fil_presentation = ?, fil_description = ?, fil_serverversionname = ?, fil_releasenotes = ?,  fil_ContentVerificationCode= ?  WHERE fil_local_pk = ?";
  private static final String INSERT_FILE_PROPERTY_SQL = "INSERT INTO tblFileProperty( fp_fil_local_fk, fp_categoryname, fp_propertyvalue) VALUES(?,?,?)";
  private static final String DELETE_FILE_PROPERTY_SQL = "DELETE FROM tblFileProperty WHERE fp_fil_local_fk = ? ";
  
  public void execute(int paramInt, DataFile paramDataFile, long paramLong1, long paramLong2)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    try
    {
      synchronized (getInsertLockObject())
      {
        localPreparedStatement = getConnection().prepareStatement("UPDATE tblFile SET fil_serverversion_id = ?, fil_name = ?, fil_securityname = ?, fil_typename = ?, fil_servercontentinfo_id = ?, fil_contentlength = ?, fil_blob_checksum = ?, fil_serverfile_lastupdated = ?, fil_serverversion_lastupdated = ?, fil_server_id = ?, fil_presentation = ?, fil_description = ?, fil_serverversionname = ?, fil_releasenotes = ?,  fil_ContentVerificationCode= ?  WHERE fil_local_pk = ?");
        setUpdateFileStatmentValues(paramInt, paramDataFile, paramLong1, paramLong2, localPreparedStatement);
        localPreparedStatement.execute();
        localPreparedStatement = getConnection().prepareStatement("DELETE FROM tblFileProperty WHERE fp_fil_local_fk = ? ");
        localPreparedStatement.setInt(1, paramInt);
        localPreparedStatement.execute();
        for (DataFileProperty localDataFileProperty : paramDataFile.aFileProperties)
        {
          localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblFileProperty( fp_fil_local_fk, fp_categoryname, fp_propertyvalue) VALUES(?,?,?)");
          localPreparedStatement.setLong(1, paramInt);
          localPreparedStatement.setString(2, localDataFileProperty.aFilePropertyCategoryName);
          localPreparedStatement.setString(3, localDataFileProperty.aFilePropertyValue);
          localPreparedStatement.execute();
        }
      }
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
    }
    DbUtil.closeStatement(localPreparedStatement);
  }
  
  private void setUpdateFileStatmentValues(int paramInt, DataFile paramDataFile, long paramLong1, long paramLong2, PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    paramPreparedStatement.setLong(1, paramDataFile.getFileVersionId());
    paramPreparedStatement.setString(2, paramDataFile.getFileName());
    paramPreparedStatement.setString(3, paramDataFile.getFileSecurityTag());
    paramPreparedStatement.setString(4, paramDataFile.getFileTypeTag());
    paramPreparedStatement.setLong(5, paramDataFile.getFileContentInfoId());
    paramPreparedStatement.setLong(6, paramDataFile.getFileContentLength());
    paramPreparedStatement.setLong(7, paramLong1);
    paramPreparedStatement.setLong(8, paramDataFile.getFileLastUpdate());
    paramPreparedStatement.setLong(9, paramDataFile.getFileVersionLastUpdate());
    paramPreparedStatement.setLong(10, paramDataFile.getFileId());
    paramPreparedStatement.setString(11, paramDataFile.getFilePresentation());
    paramPreparedStatement.setString(12, paramDataFile.getFileDescription());
    paramPreparedStatement.setString(13, paramDataFile.getFileVersionName());
    paramPreparedStatement.setString(14, paramDataFile.getFileReleaseNotes());
    paramPreparedStatement.setLong(15, paramLong2);
    paramPreparedStatement.setInt(16, paramInt);
  }
}