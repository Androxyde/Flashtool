package org.system.db.blob;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ContentVerificationCodeCalculator
{
  static final int DEFAULT_DATA_SIZE = 200;
  private final File aFile;
  private final long aFileLength;
  
  public ContentVerificationCodeCalculator(File paramFile)
  {
    this(paramFile, paramFile.length());
  }
  
  ContentVerificationCodeCalculator(File paramFile, long paramLong)
  {
    this.aFile = paramFile;
    this.aFileLength = paramLong;
  }
  
  public long createContentVerificationCode()
    throws IOException
  {
    int i = (int)Math.min(this.aFileLength, 200L);
    long l = this.aFileLength - i;
    byte[] arrayOfByte = new byte[i * 2];
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(this.aFile, "r");
    try
    {
      localRandomAccessFile.readFully(arrayOfByte, 0, i);
      localRandomAccessFile.seek(l);
      localRandomAccessFile.readFully(arrayOfByte, i, i);
    }
    finally
    {
      localRandomAccessFile.close();
    }
    return createCRC32Checksum(arrayOfByte);
  }
  
  long createCRC32Checksum(byte[] paramArrayOfByte)
  {
    CRC32 localCRC32 = new CRC32();
    localCRC32.update(paramArrayOfByte, 0, paramArrayOfByte.length);
    return localCRC32.getValue();
  }
}