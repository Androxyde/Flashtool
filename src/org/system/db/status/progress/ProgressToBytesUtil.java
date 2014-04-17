package org.system.db.status.progress;

import java.text.NumberFormat;

public class ProgressToBytesUtil
{
  private static final int KILO_BYTE = 1024;
  private static final int HUNDRED_KILO_BYTES = 102400;
  private static final int MEGA_BYTE = 1048576;
  private static final int GIGA_BYTE = 1073741824;
  private static final int WORK_UNITS_PER_KB = 1;
  private static final float WORK_UNITS_PER_MB = Math.round(1024.0F);
  private static final float WORK_UNITS_PER_GB = Math.round(1048576.0F);
  private static final float WORK_UNITS_PER_100KB = 100.0F;
  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
  
  static
  {
    NUMBER_FORMAT.setMaximumFractionDigits(1);
  }
  
  public static String workUnitsToBytesString(int paramInt)
  {
    String str;
	if (paramInt >= WORK_UNITS_PER_GB)
    {
      str = NUMBER_FORMAT.format(paramInt / WORK_UNITS_PER_GB);
      return str += " GB";
    }
    if (paramInt >= 100.0F)
    {
      str = NUMBER_FORMAT.format(paramInt / WORK_UNITS_PER_MB);
      return str + " MB";
    }
    str = NUMBER_FORMAT.format(paramInt / 1.0F);
    return str + " KB";
  }
  
  public static String bytesToSizeString(long paramLong)
  {
    String str;
    if (paramLong >= 1073741824L)
    {
      str = NUMBER_FORMAT.format((float)paramLong / 1.073742E+009F);
      return str += " GB";
    }
    if (paramLong >= 102400L)
    {
      str = NUMBER_FORMAT.format((float)paramLong / 1048576.0F);
      return str + " MB";
    }
    str = NUMBER_FORMAT.format((float)paramLong / 1024.0F);
    return str + " KB";
  }
}
