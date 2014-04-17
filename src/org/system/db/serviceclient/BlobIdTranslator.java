package org.system.db.serviceclient;

public class BlobIdTranslator
{
  private static final String SEPERATOR = "_";
  private static final String ICON = "ICON";
  private static final String FILE_PREFIX = "FILE";
  
  public static String buildBlobIdentifier(DataFile paramDataFile)
  {
    return buildBlobIdentifier(paramDataFile.aFileContentInfoId);
  }
  
  public static String buildIconBlobIdentifier(long paramLong1, long paramLong2)
  {
    return "ICON_" + paramLong1 + "_" + paramLong2;
  }
  
  public static String buildIconBlobIdentifier(DataIcon paramDataIcon)
  {
    return buildIconBlobIdentifier(paramDataIcon.getIconId(), paramDataIcon.getIconLastUpdated());
  }
  
  public static String buildBlobIdentifier(DataFileSynch paramDataFileSynch)
  {
    return buildBlobIdentifier(paramDataFileSynch.aFileContentInfoId);
  }
  
  public static String buildBlobIdentifier(long paramLong)
  {
    return "FILE_" + paramLong;
  }
}
