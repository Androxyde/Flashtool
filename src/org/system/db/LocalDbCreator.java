package org.system.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class LocalDbCreator
{
  public static final int LOCAL_DATABASE_VERSION_COUNTER = 6;
  private static final LocalDbAcess aDbAccess = LocalDbAcess.getInstance();
  
  public LocalDbCreator()
  {
  }
  
  public void checkLocalDatabase()
    throws SQLException
  {
    Connection localConnection = aDbAccess.getConnection(false);
    DatabaseMetaData localDatabaseMetaData = localConnection.getMetaData();
    ResultSet localResultSet = localDatabaseMetaData.getTables(null, null, null, new String[] { "TABLE" });
    if (!localResultSet.next())
    {
      // No Tables present in database!
      dropTables(localConnection);
      createTables(localConnection);
    }
    localResultSet.close();
  }
  
  public void resetLocalDatabase()
    throws SQLException, ClassNotFoundException
  {
    Class.forName("org.hsqldb.jdbc.JDBCDriver");
    Connection localConnection = aDbAccess.getConnection(false);
    dropTables(localConnection);
    createTables(localConnection);
    aDbAccess.updated();
  }
  
  private synchronized void createTables(Connection paramConnection)
    throws SQLException
  {
    String[] arrayOfString1 = { "CREATE TABLE tblFile ( fil_local_pk INTEGER IDENTITY PRIMARY KEY, fil_server_id BIGINT NOT NULL, fil_serverversion_id VARCHAR(4000) NOT NULL, fil_serverversionname VARCHAR(4000) NOT NULL, fil_name VARCHAR(4000) NOT NULL, fil_securityname VARCHAR(4000) NOT NULL, fil_typename VARCHAR(4000) NOT NULL, fil_servercontentinfo_id BIGINT NOT NULL, fil_contentlength BIGINT NOT NULL, fil_blob_checksum BIGINT NOT NULL, fil_serverfile_lastupdated BIGINT NOT NULL, fil_serverversion_lastupdated BIGINT NOT NULL,  fil_presentation VARCHAR(4000) NOT NULL,  fil_description VARCHAR(4000) NOT NULL,  fil_releasenotes VARCHAR(4000) NOT NULL,  fil_ContentVerificationCode BIGINT NOT NULL, UNIQUE( fil_serverversion_id, fil_serverfile_lastupdated, fil_serverversion_lastupdated));", "CREATE TABLE tblFileProperty ( fp_local_pk INTEGER IDENTITY PRIMARY KEY, fp_fil_local_fk INTEGER NOT NULL, fp_categoryname VARCHAR(4000) NOT NULL, fp_propertyvalue VARCHAR(4000) NOT NULL, FOREIGN KEY(fp_fil_local_fk) REFERENCES tblFile(fil_local_pk));", "CREATE TABLE tblService ( ser_local_pk INTEGER IDENTITY PRIMARY KEY, ser_id BIGINT NOT NULL, ser_name VARCHAR(4000) NOT NULL, ser_description VARCHAR(4000), ser_onlineonly VARCHAR(4000) NOT NULL, ser_pidf_id BIGINT NOT NULL, ser_domain VARCHAR(4000) NOT NULL, ser_testversion VARCHAR(4000) NOT NULL, ser_domain_id BIGINT NOT NULL, ser_servicetype VARCHAR(4000) NOT NULL,  ser_code LONGVARCHAR NOT NULL,  ser_created BIGINT NOT NULL,  ser_lastmodified BIGINT NOT NULL,  ser_synchtime BIGINT, UNIQUE( ser_id, ser_domain_id));", "CREATE TABLE tblIdentifiersToService ( its_local_pk INTEGER IDENTITY PRIMARY KEY, its_ser_local_fk INTEGER NOT NULL, its_identifiercategory VARCHAR(4000) NOT NULL, its_identifiervalue VARCHAR(4000) NOT NULL, its_clientdescription VARCHAR(4000), FOREIGN KEY(its_ser_local_fk) REFERENCES tblService(ser_local_pk));", "CREATE TABLE tblAttributesToService ( cts_local_pk INTEGER IDENTITY PRIMARY KEY, cts_ser_local_fk INTEGER NOT NULL, cts_attribute_name VARCHAR(4000) NOT NULL, cts_attribute_type VARCHAR(4000), cts_attribute_value VARCHAR(4000), cts_fil_local_fk INTEGER,FOREIGN KEY (cts_ser_local_fk) REFERENCES tblService(ser_local_pk), FOREIGN KEY (cts_fil_local_fk) REFERENCES tblFile(fil_local_pk));" };
    Statement localStatement = paramConnection.createStatement();
    try
    {
      for (String str : arrayOfString1) {
        localStatement.executeUpdate(str.toUpperCase(Locale.ENGLISH));
      }
    }
    finally
    {
      DbUtil.closeStatement(localStatement);
    }
  }
  
  private synchronized void dropTables(Connection paramConnection)
    throws SQLException
  {
    String[] arrayOfString1 = { "DROP TABLE tblIdentifiersToService IF EXISTS;", "DROP TABLE tblConfigurationToService IF EXISTS;", "DROP TABLE tblAttributesToService IF EXISTS;", "DROP TABLE tblService IF EXISTS;", "DROP TABLE tblFilePropertiesToFile IF EXISTS", "DROP TABLE tblFileProperty IF EXISTS;", "DROP TABLE tblFile IF EXISTS;" };
    Statement localStatement = paramConnection.createStatement();
    try
    {
      for (String str : arrayOfString1) {
        localStatement.executeUpdate(str.toUpperCase(Locale.ENGLISH));
      }
    }
    finally
    {
      DbUtil.closeStatement(localStatement);
    }
  }
  
  public boolean ensureSchemaUpdated()
  {
    Connection localConnection = aDbAccess.getConnection(false);
    String str = "No action set";
    try
    {
      aDbAccess.updated();
      return true;
    }
    catch (Exception localException)
    {
      // Upgrade failed!
    }
    return false;
  }
  
  void updateFromVersion5to6(Connection paramConnection)
    throws SQLException
  {
    String str = "ALTER TABLE tblService ADD COLUMN ser_synchtime BIGINT";
    executeStatements(paramConnection, new String[] { str });
  }
  
  protected void executeStatements(Connection paramConnection, String... paramVarArgs)
    throws SQLException
  {
    for (String str : paramVarArgs)
    {
      Statement localStatement = paramConnection.createStatement();
      try
      {
        localStatement.executeUpdate(str.toUpperCase(Locale.ENGLISH));
      }
      finally
      {
        DbUtil.closeStatement(localStatement);
      }
    }
  }
}