package org.system.db.sql.configuration;

import org.system.db.serviceclient.DataConfigurationAttribute;
import org.system.db.serviceclient.DataFileSynch;
import org.system.db.DbUtil;
import org.system.db.sql.AbstractDbObject;
import org.system.db.sql.file.GetPrimaryKeyForFile;
import org.system.db.vo.ConfigurationVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class InsertConfiguration
  extends AbstractDbObject
{
  private static final String INSERT_SQL = "INSERT INTO tblAttributesToService ( cts_ser_local_fk, cts_attribute_name, cts_attribute_type, cts_attribute_value, cts_fil_local_fk) VALUES(?,?,?,?,?)";
  
  public long execute(long paramLong, ConfigurationVO paramConfigurationVO)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = null;
    int i = -1;
    try
    {
      synchronized (getInsertLockObject())
      {
        Iterator localIterator;
        Object localObject1;
        Object localObject2;
        if (paramConfigurationVO.getAttributes() != null)
        {
          localIterator = paramConfigurationVO.getAttributes().keySet().iterator();
          while (localIterator.hasNext())
          {
            localObject1 = (String)localIterator.next();
            localObject2 = (DataConfigurationAttribute)paramConfigurationVO.getAttributes().get(localObject1);
            AttributeType localAttributeType = AttributeTypeConverter.getType((DataConfigurationAttribute)localObject2);
            if (localAttributeType != AttributeType.File)
            {
              localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblAttributesToService ( cts_ser_local_fk, cts_attribute_name, cts_attribute_type, cts_attribute_value, cts_fil_local_fk) VALUES(?,?,?,?,?)");
              localPreparedStatement.setLong(1, paramLong);
              localPreparedStatement.setString(2, (String)localObject1);
              localPreparedStatement.setString(3, localAttributeType.name());
              Object localObject3 = ((DataConfigurationAttribute)localObject2).getAttributeValue();
              localPreparedStatement.setString(4, String.valueOf(localObject3));
              localPreparedStatement.setNull(5, 0);
              localPreparedStatement.execute();
            }
          }
        }
        if (paramConfigurationVO.getFileSynchAttributes() != null)
        {
          localIterator = paramConfigurationVO.getFileSynchAttributes().entrySet().iterator();
          while (localIterator.hasNext())
          {
            localObject1 = (Map.Entry)localIterator.next();
            localObject2 = (DataFileSynch)((Map.Entry)localObject1).getValue();
            long l = new GetPrimaryKeyForFile().execute(((DataFileSynch)localObject2).aFileId, ((DataFileSynch)localObject2).aFileVersionId, ((DataFileSynch)localObject2).aFileLastUpdated);
            localPreparedStatement = getConnection().prepareStatement("INSERT INTO tblAttributesToService ( cts_ser_local_fk, cts_attribute_name, cts_attribute_type, cts_attribute_value, cts_fil_local_fk) VALUES(?,?,?,?,?)");
            localPreparedStatement.setLong(1, paramLong);
            localPreparedStatement.setString(2, (String)((Map.Entry)localObject1).getKey());
            localPreparedStatement.setString(3, AttributeType.File.name());
            localPreparedStatement.setNull(4, 0);
            localPreparedStatement.setLong(5, l);
            localPreparedStatement.execute();
          }
        }
        i = getLastInsertedId();
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