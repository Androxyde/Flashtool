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

public class InsertService
  extends AbstractDbObject
{
  private static final String INSERT_SERVICE_SQL = "INSERT INTO tblService ( ser_id, ser_name, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype,  ser_code,  ser_created,  ser_lastmodified, ser_synchtime)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
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
        localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblService ( ser_id, ser_name, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype,  ser_code,  ser_created,  ser_lastmodified, ser_synchtime)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
        localPreparedStatement.setLong(1, localLegacyServiceId.getScriptId());
        localPreparedStatement.setString(2, paramServiceVO.getName());
        localPreparedStatement.setBoolean(3, paramServiceVO.isOnlineOnly());
        localPreparedStatement.setLong(4, paramServiceVO.getPidfId());
        localPreparedStatement.setString(5, paramServiceVO.getDomainName());
        localPreparedStatement.setBoolean(6, paramServiceVO.isTestVersion());
        localPreparedStatement.setLong(7, localLegacyServiceId.getDomainId());
        localPreparedStatement.setString(8, paramServiceVO.getServiceType().getName());
        localPreparedStatement.setString(9, paramServiceVO.getCode());
        localPreparedStatement.setLong(10, 0L);
        localPreparedStatement.setLong(11, paramServiceVO.getLastModifiedDate());
        localPreparedStatement.setLong(12, paramServiceVO.getSynchronizationTime());
        localPreparedStatement.execute();
        i = getLastInsertedId();
        if (paramServiceVO.getDataIdentifiers() != null) {
          for (DataIdentifier localDataIdentifier : paramServiceVO.getDataIdentifiers())
          {
            localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblIdentifiersToService ( its_ser_local_fk, its_identifiercategory, its_identifiervalue, its_clientdescription)VALUES(?,?,?,?)");
            localPreparedStatement.setLong(1, i);
            localPreparedStatement.setString(2, localDataIdentifier.aIdentifierCategory);
            localPreparedStatement.setString(3, localDataIdentifier.aIdentifierValue);
            localPreparedStatement.setString(4, localDataIdentifier.aClientDescription);
            localPreparedStatement.execute();
          }
        } else {
          throw new IllegalArgumentException();
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