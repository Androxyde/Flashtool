package org.system.db.sql.configuration;

import org.system.db.serviceclient.DataFileAttribute;
import org.system.db.serviceclient.DataFileSynch;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import org.system.db.sql.file.GetFile;
import org.system.db.vo.ConfigurationVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GetConfiguration
  extends AbstractDbObject
{
  private static final String GET_CONFIGURATION_SQL = "SELECT cts_attribute_name, cts_attribute_type, cts_attribute_value, cts_fil_local_fk FROM tblAttributesToService WHERE cts_ser_local_fk = ?";
  
  public ConfigurationVO execute(long paramLong)
    throws SQLException
  {
    ConfigurationVO localConfigurationVO = new ConfigurationVO();
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localPreparedStatement = getConnection().prepareStatement("SELECT cts_attribute_name, cts_attribute_type, cts_attribute_value, cts_fil_local_fk FROM tblAttributesToService WHERE cts_ser_local_fk = ?");
      localPreparedStatement.setInt(1, (int)paramLong);
      localResultSet = localPreparedStatement.executeQuery();
      HashMap localHashMap = new HashMap();
      while (localResultSet.next())
      {
        String str1 = localResultSet.getString(1);
        String str2 = localResultSet.getString(2);
        String str3 = localResultSet.getString(3);
        long l = localResultSet.getLong(4);
        Object localObject1;
        Object localObject2;
        if (str3 != null)
        {
          localObject1 = AttributeTypeConverter.getType(str2);
          localObject2 = AttributeTypeConverter.getDataAttribute((AttributeType)localObject1, str3);
          localHashMap.put(str1, localObject2);
        }
        else
        {
          localObject1 = new DataFileSynch(new GetFile().getFileByLocalId(l));
          localObject2 = new DataFileAttribute((DataFileSynch)localObject1);
          localHashMap.put(str1, localObject2);
        }
      }
      localConfigurationVO.setAttributes(localHashMap);
    }
    finally
    {
      DbUtil.closeStatement(localPreparedStatement);
      DbUtil.closeResultSet(localResultSet);
    }
    return localConfigurationVO;
  }
}