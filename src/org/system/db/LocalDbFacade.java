package org.system.db;

import org.system.db.serviceclient.DataFile;
import org.system.db.serviceclient.DataFileSynch;
import org.system.db.service.LegacyServiceId;
import org.system.db.blob.FileContentId;
import org.system.db.vo.ServiceVO;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public abstract interface LocalDbFacade
{
  public abstract long insertService(ServiceVO paramServiceVO)
    throws SQLException;
  
  public abstract void deleteServiceByLocalId(long paramLong)
    throws SQLException;
  
  public abstract void deleteService(LegacyServiceId paramLegacyServiceId)
    throws SQLException;
  
  public abstract ServiceVO getService(LegacyServiceId paramLegacyServiceId)
    throws SQLException;
  
  public abstract int insertFile(DataFile paramDataFile, long paramLong1, long paramLong2)
    throws SQLException;
  
  public abstract long getFileContentVerificationCode(DataFileSynch paramDataFileSynch)
    throws SQLException;
  
  public abstract DataFile getFile(long paramLong1, long paramLong2, long paramLong3)
    throws SQLException;
  
  public abstract DataFile getFile(DataFileSynch paramDataFileSynch)
    throws SQLException;
  
  public abstract boolean isServiceInDatabase(LegacyServiceId paramLegacyServiceId)
    throws SQLException;
  
  public abstract DataFile getFileByLocalId(long paramLong)
    throws SQLException;
  
  public abstract List<ServiceVO> getAllLocalServices()
    throws SQLException;
  
  public abstract ServiceVO getServiceByLocalId(long paramLong)
    throws SQLException;
  
  public abstract Collection<DataFile> getFilesUsedByLocalService(LegacyServiceId paramLegacyServiceId)
    throws SQLException;
  
  public abstract List<FileContentId> deleteUnreferencedFiles()
    throws SQLException;
}