package org.system.db.sql.file;

import org.system.db.serviceclient.DataFile;
import org.system.db.serviceclient.DataFileProperty;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertFile
  extends AbstractDbObject
{
  private static final String INSERT_FILE_SQL = "INSERT INTO tblFile( fil_serverversion_id, fil_name, fil_securityname, fil_typename, fil_servercontentinfo_id, fil_contentlength, fil_blob_checksum, fil_serverfile_lastupdated, fil_serverversion_lastupdated, fil_server_id, fil_presentation, fil_description, fil_serverversionname, fil_releasenotes, fil_ContentVerificationCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String INSERT_FILE_PROPERTY_SQL = "INSERT INTO tblFileProperty( fp_fil_local_fk, fp_categoryname, fp_propertyvalue) VALUES(?,?,?)";
  
  public int execute(DataFile paramDataFile, long paramLong1, long paramLong2)
    throws SQLException
  {
    int i = 0;
    Connection localConnection = getConnection();
    PreparedStatement localPreparedStatement1 = null;
    PreparedStatement localPreparedStatement2 = null;
    try
    {
      synchronized (getInsertLockObject())
      {
        localPreparedStatement1 = localConnection.prepareStatement("INSERT INTO tblFile( fil_serverversion_id, fil_name, fil_securityname, fil_typename, fil_servercontentinfo_id, fil_contentlength, fil_blob_checksum, fil_serverfile_lastupdated, fil_serverversion_lastupdated, fil_server_id, fil_presentation, fil_description, fil_serverversionname, fil_releasenotes, fil_ContentVerificationCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        setInsertFileStatmentValues(paramDataFile, paramLong1, paramLong2, localPreparedStatement1);
        localPreparedStatement1.execute();
        i = getLastInsertedId();
        localPreparedStatement2 = localConnection.prepareStatement("INSERT INTO tblFileProperty( fp_fil_local_fk, fp_categoryname, fp_propertyvalue) VALUES(?,?,?)");
        DataFileProperty[] arrayOfDataFileProperty1 = paramDataFile.getFileProperties();
        if (arrayOfDataFileProperty1 != null) {
          for (DataFileProperty localDataFileProperty : arrayOfDataFileProperty1)
          {
            String str1 = localDataFileProperty.getFilePropertyCategoryName();
            String str2 = localDataFileProperty.getFilePropertyValue();
            localPreparedStatement2.setLong(1, i);
            localPreparedStatement2.setString(2, str1);
            localPreparedStatement2.setString(3, str2);
            localPreparedStatement2.execute();
          }
        }
      }
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement1);
      DbUtil.closeStatement(localPreparedStatement2);
    }
    DbUtil.closeStatement(localPreparedStatement1);
    DbUtil.closeStatement(localPreparedStatement2);
    return i;
  }
  
  private void setInsertFileStatmentValues(DataFile paramDataFile, long paramLong1, long paramLong2, PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    paramPreparedStatement.setLong(1, paramDataFile.aFileVersionId);
    paramPreparedStatement.setString(2, paramDataFile.aFileName);
    paramPreparedStatement.setString(3, paramDataFile.aFileSecurityTag);
    paramPreparedStatement.setString(4, paramDataFile.aFileTypeTag);
    paramPreparedStatement.setLong(5, paramDataFile.aFileContentInfoId);
    paramPreparedStatement.setLong(6, paramDataFile.aFileContentLength);
    paramPreparedStatement.setLong(7, paramLong1);
    paramPreparedStatement.setLong(8, paramDataFile.aFileLastUpdate);
    paramPreparedStatement.setLong(9, paramDataFile.aFileVersionLastUpdate);
    paramPreparedStatement.setLong(10, paramDataFile.aFileId);
    paramPreparedStatement.setString(11, paramDataFile.aFilePresentation);
    paramPreparedStatement.setString(12, paramDataFile.aFileDescription);
    paramPreparedStatement.setString(13, paramDataFile.aFileVersionName);
    paramPreparedStatement.setString(14, paramDataFile.getFileReleaseNotes());
    paramPreparedStatement.setLong(15, paramLong2);
  }
}