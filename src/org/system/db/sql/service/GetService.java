package org.system.db.sql.service;

import org.system.db.service.LegacyServiceId;
import org.system.db.service.ServiceType;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import org.system.db.vo.ServiceVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetService
  extends AbstractDbObject
{
  private static final String GET_SERVICE_FIELDS = "SELECT ser_local_pk, ser_id, ser_name, ser_description, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype, ser_code, ser_lastmodified, ser_synchtime FROM tblService ";
  private static final String GET_SERVICE_BY_SERVICE_ID = "SELECT ser_local_pk, ser_id, ser_name, ser_description, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype, ser_code, ser_lastmodified, ser_synchtime FROM tblService WHERE ser_id = ? AND ser_domain_id = ?";
  private static final String GET_SERVICE_BY_LOCAL_ID = "SELECT ser_local_pk, ser_id, ser_name, ser_description, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype, ser_code, ser_lastmodified, ser_synchtime FROM tblService WHERE ser_local_pk = ? ";
  
  public Integer getLocalServiceId(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    ResultSet localResultSet = null;
    PreparedStatement localPreparedStatement = getConnection().prepareStatement("SELECT ser_local_pk FROM tblService WHERE ser_id = ? AND ser_domain_id = ?");
    try
    {
      localPreparedStatement.setLong(1, paramLegacyServiceId.getScriptId());
      localPreparedStatement.setLong(2, paramLegacyServiceId.getDomainId());
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        Integer localInteger1 = Integer.valueOf(localResultSet.getInt("ser_local_pk"));
        Integer localInteger2 = localInteger1;
        return localInteger2;
      }
      return null;
    }
    finally
    {
      DbUtil.closeResultSet(localResultSet);
      DbUtil.closeStatement(localPreparedStatement);
    }
  }
  
  public ServiceVO getServiceVOByServiceId(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = getConnection().prepareStatement("SELECT ser_local_pk, ser_id, ser_name, ser_description, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype, ser_code, ser_lastmodified, ser_synchtime FROM tblService WHERE ser_id = ? AND ser_domain_id = ?");
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement.setLong(1, paramLegacyServiceId.getScriptId());
      localPreparedStatement.setLong(2, paramLegacyServiceId.getDomainId());
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        ServiceVO localServiceVO = createServiceVO(localResultSet);
        return localServiceVO;
      }
      return null;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
  
  public ServiceVO getServiceVOByLocalId(long paramLong)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = getConnection().prepareStatement("SELECT ser_local_pk, ser_id, ser_name, ser_description, ser_onlineonly, ser_pidf_id, ser_domain, ser_testversion, ser_domain_id, ser_servicetype, ser_code, ser_lastmodified, ser_synchtime FROM tblService WHERE ser_local_pk = ? ");
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement.setLong(1, paramLong);
      localResultSet = localPreparedStatement.executeQuery();
      if (localResultSet.next())
      {
        ServiceVO localServiceVO = createServiceVO(localResultSet);
        return localServiceVO;
      }
      return null;
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
  }
  
  ServiceVO createServiceVO(ResultSet paramResultSet)
    throws SQLException
  {
    long l1 = paramResultSet.getLong("ser_id");
    long l2 = paramResultSet.getLong("ser_domain_id");
    LegacyServiceId localLegacyServiceId = new LegacyServiceId(l1, l2);
    ServiceVO localServiceVO = new ServiceVO(localLegacyServiceId);
    localServiceVO.setLocalId(paramResultSet.getLong("ser_local_pk"));
    localServiceVO.setName(paramResultSet.getString("ser_name"));
    localServiceVO.setCode(paramResultSet.getString("ser_code"));
    localServiceVO.setServiceType(ServiceType.toServiceType(paramResultSet.getString("ser_servicetype")));
    localServiceVO.setDescription(paramResultSet.getString("ser_description"));
    localServiceVO.setDomainName(paramResultSet.getString("ser_domain"));
    localServiceVO.setOnlineOnly(Boolean.valueOf(paramResultSet.getString("ser_onlineonly")).booleanValue());
    localServiceVO.setPidfId(paramResultSet.getLong("ser_pidf_id"));
    localServiceVO.setTestVersion(Boolean.valueOf(paramResultSet.getString("ser_testversion")).booleanValue());
    localServiceVO.setLastModifiedDate(paramResultSet.getLong("ser_lastmodified"));
    localServiceVO.setSynchronizationTime(paramResultSet.getLong("ser_synchtime"));
    return localServiceVO;
  }
}