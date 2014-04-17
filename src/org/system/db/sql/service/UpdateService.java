package org.system.db.sql.service;

import org.system.db.serviceclient.DataIdentifier;
import org.system.db.service.LegacyServiceId;
import org.system.db.service.ServiceType;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import org.system.db.vo.ServiceVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateService
  extends AbstractDbObject
{
  private static final String UPDATE_SERVICE_SQL = "UPDATE tblService SET ser_name = ?, ser_onlineonly = ?, ser_pidf_id = ?, ser_domain = ?, ser_testversion = ?, ser_domain_id = ?, ser_servicetype = ?, ser_code = ?, ser_created = ?, ser_lastmodified = ?, ser_synchtime = ? WHERE ser_id = ?";
  private static final String DELETE_IDENTIFIERS_SQL = "DELETE FROM tblIdentifiersToService WHERE its_ser_local_fk = ";
  private static final String INSERT_IDENTIFIERS_SQL = "INSERT INTO tblIdentifiersToService ( its_ser_local_fk, its_identifiercategory, its_identifiervalue, its_clientdescription)VALUES(?,?,?,?)";
  
  public long execute(ServiceVO paramServiceVO)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    LegacyServiceId localLegacyServiceId = paramServiceVO.getServiceId();
    try
    {
      synchronized (getInsertLockObject())
      {
        localPreparedStatement = getConnection().prepareStatement("UPDATE tblService SET ser_name = ?, ser_onlineonly = ?, ser_pidf_id = ?, ser_domain = ?, ser_testversion = ?, ser_domain_id = ?, ser_servicetype = ?, ser_code = ?, ser_created = ?, ser_lastmodified = ?, ser_synchtime = ? WHERE ser_id = ?");
        localPreparedStatement.setString(1, paramServiceVO.getName());
        localPreparedStatement.setBoolean(2, paramServiceVO.isOnlineOnly());
        localPreparedStatement.setLong(3, paramServiceVO.getPidfId());
        localPreparedStatement.setString(4, paramServiceVO.getDomainName());
        localPreparedStatement.setBoolean(5, paramServiceVO.isTestVersion());
        localPreparedStatement.setLong(6, localLegacyServiceId.getDomainId());
        localPreparedStatement.setString(7, paramServiceVO.getServiceType().getName());
        localPreparedStatement.setString(8, paramServiceVO.getCode());
        localPreparedStatement.setLong(9, 0L);
        localPreparedStatement.setLong(10, paramServiceVO.getLastModifiedDate());
        localPreparedStatement.setLong(11, paramServiceVO.getSynchronizationTime());
        localPreparedStatement.setLong(12, localLegacyServiceId.getScriptId());
        localPreparedStatement.execute();
        i = getLastInsertedId();
        localPreparedStatement = getConnection().prepareStatement("DELETE FROM tblIdentifiersToService WHERE its_ser_local_fk = " + i);
        localPreparedStatement.execute();
        if (paramServiceVO.getDataIdentifiers() != null)
        {
          localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblIdentifiersToService ( its_ser_local_fk, its_identifiercategory, its_identifiervalue, its_clientdescription)VALUES(?,?,?,?)");
          for (DataIdentifier localDataIdentifier : paramServiceVO.getDataIdentifiers())
          {
            localPreparedStatement.setLong(1, i);
            localPreparedStatement.setString(2, localDataIdentifier.aIdentifierCategory);
            localPreparedStatement.setString(3, localDataIdentifier.aIdentifierValue);
            localPreparedStatement.setString(4, localDataIdentifier.aClientDescription);
            localPreparedStatement.execute();
          }
        }
      }
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
    }
    DbUtil.closeStatement(localPreparedStatement);
    return i;
  }
}