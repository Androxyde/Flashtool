package org.system.db;

import org.system.db.serviceclient.DataFile;
import org.system.db.serviceclient.DataFileSynch;
import org.system.db.serviceclient.DataIdentifier;
import org.system.db.status.progress.ProgressMonitor;
import org.system.db.service.LegacyServiceId;
import org.system.db.blob.FileContentId;
import org.system.db.status.AppNotification;
import org.system.db.LocalDbAcess;
import org.system.db.LocalDbCreator;
import org.system.db.TessLocalDbConfig;
import org.system.db.sql.configuration.DeleteConfiguration;
import org.system.db.sql.configuration.GetConfiguration;
import org.system.db.sql.configuration.InsertConfiguration;
import org.system.db.sql.file.DeleteFile;
import org.system.db.sql.file.GetFile;
import org.system.db.sql.file.GetLocalServiceFiles;
import org.system.db.sql.file.GetPrimaryKeyForFile;
import org.system.db.sql.file.InsertFile;
import org.system.db.sql.file.UnreferencedFiles;
import org.system.db.sql.file.UpdateFile;
import org.system.db.sql.service.DeleteService;
import org.system.db.sql.service.GetIdentifiersForService;
import org.system.db.sql.service.GetLocalServices;
import org.system.db.sql.service.GetService;
import org.system.db.sql.service.InsertService;
import org.system.db.sql.service.UpdateService;
import org.system.db.vo.ConfigurationVO;
import org.system.db.vo.ServiceVO;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LocalDbFacadeImpl
  implements LocalDbFacade, LocalDbLifecycleManager
{
  private static final String HSQL_DB_ALREADY_IN_USE = "database is already in use by another process";
  private static final String HSQL_DB_LOCK_FAILED = "Database lock acquisition failure";
  private static final LocalDbAcess aDbAccess = LocalDbAcess.getInstance();
  private static LocalDbFacadeImpl aLocalDbFacade;
  
  public static LocalDbFacadeImpl getInstance()
  {
    return aLocalDbFacade;
  }
  
  public static void setTessDbEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {
      aLocalDbFacade = new LocalDbFacadeImpl();
    } else {
      aLocalDbFacade = null;
    }
  }
  
  private void removeDbIfTooOld(String FileName)
  {
// nah!
  }
  
  public void startLocalDb(String FileName)
    throws Exception
  {
    try
    {
      aDbAccess.connect(FileName);
      LocalDbCreator localLocalDbCreator = new LocalDbCreator();
      localLocalDbCreator.checkLocalDatabase();
    }
    catch (SQLException localSQLException)
    {
      String str3 = "An error occured while setting up local db";
      String str4 = localSQLException.getMessage();
      if ((str4 != null) && ((str4.indexOf("database is already in use by another process") > -1) || (str4.startsWith("Database lock acquisition failure"))))
      {
        str3 = "The local database is already open by another process.";
      }
      throw new Exception();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new Exception();
    }
    catch (Exception localException)
    {
      throw new Exception();
    }
  }
  
  public void stopLocalDb()
    throws Exception
  {
    try
    {
      aDbAccess.shutdown();
    }
    catch (SQLException localSQLException)
    {
      throw new Exception();
    }
  }
  
  public TessLocalDbConfig getLocalDbConfigurator()
  {
    return null;
  }
  
  public synchronized long insertService(ServiceVO paramServiceVO)
    throws SQLException
  {
    LegacyServiceId localLegacyServiceId = paramServiceVO.getServiceId();
    ConfigurationVO localConfigurationVO = paramServiceVO.getConfiguration();
    long l;
    if (new GetService().getServiceVOByServiceId(localLegacyServiceId) == null)
    {
      l = new InsertService().execute(paramServiceVO);
    }
    else
    {
      l = new UpdateService().execute(paramServiceVO);
      new DeleteConfiguration().execute(l);
    }
    new InsertConfiguration().execute(l, localConfigurationVO);
    return l;
  }
  
  public synchronized void deleteServiceByLocalId(long paramLong)
    throws SQLException
  {
    new DeleteService().execute(paramLong);
  }
  
  public synchronized void deleteService(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    ServiceVO localServiceVO = getService(paramLegacyServiceId);
    if (localServiceVO != null)
    {
      long l = localServiceVO.getLocalId();
      new DeleteService().execute(l);
    }
    else
    {
    }
  }
  
  public synchronized ServiceVO getService(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    ServiceVO localServiceVO = new GetService().getServiceVOByServiceId(paramLegacyServiceId);
    if (localServiceVO == null)
    {
      return null;
    }
    long l = localServiceVO.getLocalId();
    DataIdentifier[] arrayOfDataIdentifier = new GetIdentifiersForService().execute(l);
    localServiceVO.setDataIdentifiers(arrayOfDataIdentifier);
    ConfigurationVO localConfigurationVO = new GetConfiguration().execute(l);
    localServiceVO.setConfiguration(localConfigurationVO);
    return localServiceVO;
  }
  
  public synchronized ServiceVO getServiceByLocalId(long paramLong)
    throws SQLException
  {
    ServiceVO localServiceVO = new GetService().getServiceVOByLocalId(paramLong);
    DataIdentifier[] arrayOfDataIdentifier = new GetIdentifiersForService().execute(paramLong);
    localServiceVO.setDataIdentifiers(arrayOfDataIdentifier);
    ConfigurationVO localConfigurationVO = new GetConfiguration().execute(paramLong);
    localServiceVO.setConfiguration(localConfigurationVO);
    return localServiceVO;
  }
  
  public int insertFile(DataFile paramDataFile, long paramLong1, long paramLong2)
    throws SQLException
  {
    GetPrimaryKeyForFile localGetPrimaryKeyForFile = new GetPrimaryKeyForFile();
    int i = localGetPrimaryKeyForFile.execute(paramDataFile.aFileId, paramDataFile.aFileVersionId, paramDataFile.aFileLastUpdate);
    if (i == -1) {
      i = new InsertFile().execute(paramDataFile, paramLong1, paramLong2);
    } else {
      new UpdateFile().execute(i, paramDataFile, paramLong1, paramLong2);
    }
    return i;
  }
  
  public long getFileContentVerificationCode(DataFileSynch paramDataFileSynch)
    throws SQLException
  {
    GetFile localGetFile = new GetFile();
    int i = localGetFile.getLocalFileId(paramDataFileSynch);
    if (i != -1) {
      return localGetFile.getContentVerificationCode(i).longValue();
    }
    return -1L;
  }
  
  public DataFile getFileByLocalId(long paramLong)
    throws SQLException
  {
    DataFile localDataFile = new GetFile().getFileByLocalId(paramLong);
    return localDataFile;
  }
  
  public DataFile getFile(long paramLong1, long paramLong2, long paramLong3)
    throws SQLException
  {
    DataFile localDataFile = new GetFile().getFile(paramLong1, paramLong2, paramLong3);
    return localDataFile;
  }
  
  public DataFile getFile(DataFileSynch paramDataFileSynch)
    throws SQLException
  {
    DataFile localDataFile = null;
    int i = new GetFile().getLocalFileId(paramDataFileSynch);
    if (i != -1) {
      localDataFile = new GetFile().getFileByLocalId(i);
    }
    return localDataFile;
  }
  
  public boolean isServiceInDatabase(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    Integer localInteger = new GetService().getLocalServiceId(paramLegacyServiceId);
    return localInteger != null;
  }
  
  public Collection<DataFile> getFilesUsedByLocalService(LegacyServiceId paramLegacyServiceId)
    throws SQLException
  {
    Collection localCollection = new GetLocalServiceFiles().execute(paramLegacyServiceId);
    return localCollection;
  }
  
  public synchronized List<ServiceVO> getAllLocalServices()
    throws SQLException
  {
    List localList = new GetLocalServices().execute();
    ArrayList localArrayList = new ArrayList();
    GetService localGetService = new GetService();
    GetIdentifiersForService localGetIdentifiersForService = new GetIdentifiersForService();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Long localLong = (Long)localIterator.next();
      ServiceVO localServiceVO = localGetService.getServiceVOByLocalId(localLong.longValue());
      DataIdentifier[] arrayOfDataIdentifier = localGetIdentifiersForService.execute(localLong.longValue());
      localServiceVO.setDataIdentifiers(arrayOfDataIdentifier);
      localArrayList.add(localServiceVO);
    }
    return localArrayList;
  }
  
  public synchronized List<FileContentId> deleteUnreferencedFiles()
    throws SQLException
  {
    UnreferencedFiles localUnreferencedFiles = new UnreferencedFiles();
    List localList = localUnreferencedFiles.findUnreferencedFileIds();
    GetFile localGetFile = new GetFile();
    DeleteFile localDeleteFile = new DeleteFile();
    LinkedList localLinkedList = new LinkedList();
    Object localObject2 = localList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject1 = (Integer)((Iterator)localObject2).next();
      Object localObject3 = localGetFile.getFileByLocalId(((Integer)localObject1).intValue());
      localDeleteFile.execute(((Integer)localObject1).intValue());
      if (localObject3 != null) {
        localLinkedList.add(localObject3);
      } else {
      }
    }
    Object localObject1 = new LinkedList();
    Object localObject3 = localLinkedList.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject2 = (DataFile)((Iterator)localObject3).next();
      long l = ((DataFile)localObject2).getFileContentInfoId();
      FileContentId localFileContentId = new FileContentId(l);
      int i = localUnreferencedFiles.getNbrFileContentReferences(localFileContentId);
      if (i == 0) {
        ((List)localObject1).add(localFileContentId);
      }
    }
    return (List<FileContentId>) localObject1;
  }

@Override
public void startLocalDb(TessLocalDbConfig paramTessLocalDbConfig,
		ProgressMonitor paramProgressMonitor,
		AppNotification paramAppNotification) throws Exception {
	// TODO Auto-generated method stub
	
}
}
