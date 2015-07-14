/*******************************************************************************
 * Copyright (c) 2011, J.W. Janssen
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     J.W. Janssen - Cleanup and make API more OO-oriented.
 *******************************************************************************/
package nl.lxtreme.binutils.hex;


import java.io.*;
import java.nio.*;

import nl.lxtreme.binutils.hex.util.*;


/**
 * Provides a data provider based on a Motorola SRecord file.
 * 
 * <pre>
 *      S&lt;recordtype&gt;&lt;recordlength&gt;&lt;address&gt;&lt;data&gt;&lt;checksum&gt;
 * </pre>
 * <p>
 * In which is:
 * </p>
 * <ul>
 * <li><b>'S'</b>, the identifying character of the record (therefore called
 * S-record);</li>
 * <li><b>recordtype</b>, a character specifying the type of the record:
 * <ul>
 * <li>'0': header record with 2 bytes address (value 0); contains usually not
 * much interesting data, e.g. with the hex encoded text "HEADER";</li>
 * <li>'1', '2', '3': data record with 2, 3, respectively 4-byte address that
 * represents the absolute starting address of the first data byte of the
 * record;</li>
 * <li>'5': data record count record with a 2-byte address that represents the
 * preceding number of data records; this record contains no data bytes;</li>
 * <li>'7', '8', '9': termination record with a 4, 3, respectively 2-byte
 * address that represents the Entry Point in the terminated data block, so e.g.
 * the initial value of the program counter of a processor.</li>
 * </ul>
 * </li>
 * <li><b>recordlength</b>, the hex-encoded byte representing the number of
 * address, data, and checksum bytes;</li>
 * <li><b>address</b>, the hex-encoded 2, 3, or 4-byte address, depending on the
 * record type;</li>
 * <li><b>data</b>, the hex-encoded data bytes (usually not more than 64 per
 * record, maximum 255);</li>
 * <li><b>checksum</b>, the one's complement of the sum of all bytes in the
 * record after the recordtype.</li>
 * </ul>
 * <p>
 * Files with only S0, S1 and S9 records are also called to be in Motorola
 * Exorcisor format. If also S2 and S8 records appear, the format is also called
 * Motorola Exormax.
 * </p>
 */
public class SRecordReader extends AbstractReader
{
  // CONSTANTS

  private static final char PREAMBLE = 'S';

  // VARIABLES

  private Integer address;
  private Integer dataLength;
  private int dataSum;
  private boolean inDataRecord;

  private Integer oldAddress;
  private int oldDataSum;
  private Integer oldDataLength;
  private boolean oldInDataRecord;

  // CONSTRUCTORS

  /**
   * Creates a new SRecordDataProvider instance.
   * 
   * @param aReader
   */
  public SRecordReader(final Reader aReader)
  {
    super(aReader);
  }

  // METHODS

  /**
   * @see nl.lxtreme.cpemu.util.data.IDataProvider#getAddress()
   */
  @Override
  public long getAddress() throws IOException
  {
    if (this.address == null)
    {
      throw new IOException("Unexpected call to getAddress!");
    }
    return this.address;
  }

  /**
   * @see nl.lxtreme.cpemu.util.data.impl.AbstractDataProvider#mark()
   */
  @Override
  public void mark() throws IOException
  {
    super.mark();

    this.oldAddress = this.address;
    this.oldDataSum = this.dataSum;
    this.oldDataLength = this.dataLength;
    this.oldInDataRecord = this.inDataRecord;
  }

  /**
   * @see nl.lxtreme.cpemu.util.data.IDataProvider#readByte()
   */
  @Override
  public int readByte() throws IOException
  {
    int ch;

    do
    {
      ch = readSingleByte();
      if (ch == -1)
      {
        // End-of-file reached; return immediately!
        return -1;
      }

      if (PREAMBLE == ch)
      {
        // New record started...
        this.inDataRecord = isDataRecord(startNewRecord());
      }
      else if (this.dataLength != null)
      {
        final int secondHexDigit = this.reader.read();
        if (secondHexDigit == -1)
        {
          throw new IOException("Unexpected end-of-stream!");
        }
        final char[] buf = { (char) ch, (char) secondHexDigit };

        final int dataByte = HexUtils.parseHexByte(buf);
        if (this.dataLength == 0)
        {
          // All data-bytes returned? If so, verify the CRC we've just read...
          final int calculatedCRC = (~this.dataSum) & 0xFF;
          if (dataByte != calculatedCRC)
          {
            throw new IOException("CRC Error! Expected: " + dataByte + "; got: " + calculatedCRC);
          }
        }
        else
        {
          // Decrease the number of hex-bytes we've got to read...
          this.dataSum += (byte) dataByte;

          this.dataLength--;
          this.address++;

          if (this.inDataRecord)
          {
            return dataByte;
          }
        }
      }
    }
    while (ch != -1);

    // We should never come here; it means that we've found a situation that
    // isn't covered by our loop above...
    throw new IOException("Invalid Intel HEX-file!");
  }

  /**
   * @see nl.lxtreme.cpemu.util.data.impl.AbstractDataProvider#reset()
   */
  @Override
  public void reset() throws IOException
  {
    super.reset();

    this.address = this.oldAddress;
    this.dataSum = this.oldDataSum;
    this.dataLength = this.oldDataLength;
    this.inDataRecord = this.oldInDataRecord;
  }

  /**
   * @see nl.lxtreme.cpemu.util.data.impl.AbstractDataProvider#getByteOrder()
   */
  @Override
  protected ByteOrder getByteOrder()
  {
    return ByteOrder.BIG_ENDIAN;
  }

  /**
   * Returns the address length in number of bytes of a given SRecord-type.
   * 
   * @param aType
   *          the SRecord-type to return the address length for.
   * @return the address length as number of bytes.
   */
  private int getAddressLength(final int aType)
  {
    int result = 2;
    if ((aType == 2) || (aType == 8))
    {
      result = 3;
    }
    else if ((aType == 3) || (aType == 7))
    {
      result = 4;
    }

    return result;
  }

  /**
   * @param aType
   *          the integer (srecord-)type;
   * @return <code>true</code> if the given (srecord-)type is a data record,
   *         otherwise <code>false</code>.
   */
  private boolean isDataRecord(final int aType)
  {
    return (aType == 1) || (aType == 2) || (aType == 3);
  }

  /**
   * @param aType
   *          the integer (srecord-)type;
   * @return <code>true</code> if the given (srecord-)type is a header record,
   *         otherwise <code>false</code>.
   */
  private boolean isHeaderRecord(final int aType)
  {
    return (aType == 0);
  }

  /**
   * Returns whether the given (srecord-)type is valid or not.
   * 
   * @param aType
   *          the integer (srecord-)type;
   * @return <code>true</code> if the given (srecord-)type is valid, otherwise
   *         <code>false</code>.
   */
  private boolean isValidType(final int aType)
  {
    // (S0, S1, S2, S3, S5, S7, S8, or S9)
    return ((aType == 0) || (aType == 1) || (aType == 2) || (aType == 3) || (aType == 5) || (aType == 7)
        || (aType == 8) || (aType == 9));
  }

  /**
   * 
   */
  private int startNewRecord() throws IOException
  {
    // First byte is the length of the data record...
    final int type = this.reader.read() - '0';
    if (!isValidType(type))
    {
      throw new IOException("Unknown type: " + type);
    }

    // recordLength is representing the number of address, data, and checksum
    // bytes;
    final int recordLength = HexUtils.parseHexByte(this.reader);

    final int addressLength = getAddressLength(type);
    this.address = HexUtils.parseHexNumber(this.reader, addressLength);

    // Calculate the first part of the record CRC; which is defined as the
    // ones-complement of all (non-CRC) items in the record...
    // record length
    this.dataSum = (byte) recordLength;
    // address length
    this.dataSum += (byte) ((this.address & 0xff000000) >> 24) + (byte) ((this.address & 0xff0000) >> 16)
        + (byte) ((this.address & 0xff00) >> 8) + (byte) ((this.address & 0xff));

    // The real data length...
    this.dataLength = recordLength - addressLength - 1;

    if (this.dataLength > 0)
    {
      // Make sure NO data is only for records that should have NO data...
      if (!isDataRecord(type) && !isHeaderRecord(type))
      {
        throw new IOException("Data found while record-type should not have data!");
      }

      this.address--;
    }
    else if (this.dataLength == 0)
    {
      // Make sure NO data is only for records that should have NO data...
      if (isDataRecord(type) || isHeaderRecord(type))
      {
        throw new IOException("No data found while record-type should have data!");
      }
    }

    return type;
  }
}
