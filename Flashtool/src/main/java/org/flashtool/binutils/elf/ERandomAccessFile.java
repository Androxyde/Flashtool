/*******************************************************************************
 * Copyright (c) 2011 - J.W. Janssen
 * 
 * Copyright (c) 2000, 2008 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     J.W. Janssen - Clean up and made API more OO-oriented
 *******************************************************************************/
package org.flashtool.binutils.elf;


import java.io.*;


/**
 * @author jawi
 */
public final class ERandomAccessFile extends RandomAccessFile
{
  // VARIABLES

  private boolean isle;

  // CONSTRUCTORS

  /**
   * @param aFile
   * @param aMode
   * @throws IOException
   */
  public ERandomAccessFile(final File aFile, final String aMode) throws IOException
  {
    super(aFile, aMode);
  }

  /**
   * @param aFile
   * @param aMode
   * @throws IOException
   */
  public ERandomAccessFile(final String aFile, final String aMode) throws IOException
  {
    super(aFile, aMode);
  }

  // METHODS

  /**
   * Reads <code>aBuffer.length</code> bytes from this file into the byte
   * array, starting at the current file pointer.
   * <p>
   * This method reads repeatedly from the file until the requested number of
   * bytes are read. This method blocks until the requested number of bytes are
   * read, the end of the stream is detected, or an exception is thrown.
   * </p>
   * <p>
   * <em>This method converts the read buffer according to the set endianness!</em>
   * </p>
   * 
   * @param aBuffer
   *          the buffer into which the data is read.
   * @exception EOFException
   *              if this file reaches the end before reading all the bytes.
   * @exception IOException
   *              if an I/O error occurs.
   */
  public final void readFullyE(final byte[] aBuffer) throws IOException
  {
    super.readFully(aBuffer);
    byte tmp = 0;
    if (this.isle)
    {
      for (int i = 0; i < (aBuffer.length / 2); i++)
      {
        tmp = aBuffer[i];
        aBuffer[i] = aBuffer[aBuffer.length - i - 1];
        aBuffer[aBuffer.length - i - 1] = tmp;
      }
    }
  }

  /**
   * Reads a signed 32-bit integer from this file.
   * <p>
   * This method reads 4 bytes from the file, starting at the current file
   * pointer. If the bytes read, in order, are <code>b1</code>, <code>b2</code>,
   * <code>b3</code>, and <code>b4</code>, where
   * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>, then the
   * result is equal to: <tt>
   * (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
   * </tt>.
   * </p>
   * <p>
   * This method blocks until the four bytes are read, the end of the stream is
   * detected, or an exception is thrown.
   * </p>
   * <p>
   * <em>This method converts the read buffer according to the set endianness!</em>
   * </p>
   * 
   * @return the next four bytes of this file, interpreted as an
   *         <code>int</code>.
   * @exception EOFException
   *              if this file reaches the end before reading four bytes.
   * @exception IOException
   *              if an I/O error occurs.
   */
  public final int readIntE() throws IOException
  {
    final int val[] = new int[4];

    val[0] = read();
    val[1] = read();
    val[2] = read();
    val[3] = read();
    if ((val[0] | val[1] | val[2] | val[3]) < 0)
    {
      throw new EOFException();
    }

    if (this.isle)
    {
      return ((val[3] << 24) + (val[2] << 16) + (val[1] << 8) + val[0]);
    }
    return ((val[0] << 24) + (val[1] << 16) + (val[2] << 8) + val[3]);
  }

  /**
   * Reads a signed 64-bit integer from this file.
   * <p>
   * This method reads eight bytes from the file, starting at the current file
   * pointer. If the bytes read, in order, are <code>b1</code>, <code>b2</code>,
   * <code>b3</code>, <code>b4</code>, <code>b5</code>, <code>b6</code>,
   * <code>b7</code>, and <code>b8,</code> where: <tt>
   *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
   * </tt> then the result is equal to: <tt>
   *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
   *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
   *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
   *     + ((long)b7 &lt;&lt; 8) + b8
   * </tt>
   * </p>
   * <p>
   * This method blocks until the eight bytes are read, the end of the stream is
   * detected, or an exception is thrown.
   * </p>
   * <p>
   * <em>This method converts the read buffer according to the set endianness!</em>
   * </p>
   * 
   * @return the next eight bytes of this file, interpreted as a
   *         <code>long</code>.
   * @exception EOFException
   *              if this file reaches the end before reading eight bytes.
   * @exception IOException
   *              if an I/O error occurs.
   */
  public final long readLongE() throws IOException
  {
    final byte[] bytes = new byte[8];

    long result = 0;
    super.readFully(bytes);
    int shift = 0;
    if (this.isle)
    {
      for (int i = 7; i >= 0; i--)
      {
        shift = i * 8;
        result += (((long) bytes[i]) << shift) & (0xffL << shift);
      }
    }
    else
    {
      for (int i = 0; i <= 7; i++)
      {
        shift = (7 - i) * 8;
        result += (((long) bytes[i]) << shift) & (0xffL << shift);
      }
    }
    return result;
  }

  /**
   * Reads a signed 16-bit number from this file. The method reads two
   * bytes from this file, starting at the current file pointer.
   * If the two bytes read, in order, are <code>b1</code> and <code>b2</code>,
   * where each of the two values is
   * between <code>0</code> and <code>255</code>, inclusive, then the
   * result is equal to: <tt>
   *     (short)((b1 &lt;&lt; 8) | b2)
   * </tt></p>
   * <p>
   * This method blocks until the two bytes are read, the end of the stream is
   * detected, or an exception is thrown.
   * </p>
   * <p>
   * <em>This method converts the read buffer according to the set endianness!</em>
   * </p>
   * 
   * @return the next two bytes of this file, interpreted as a signed 16-bit
   *         number.
   * @exception EOFException
   *              if this file reaches the end before reading two bytes.
   * @exception IOException
   *              if an I/O error occurs.
   */
  public final short readShortE() throws IOException
  {
    final int val[] = new int[2];

    val[0] = read();
    val[1] = read();
    if ((val[0] | val[1]) < 0)
    {
      throw new EOFException();
    }
    if (this.isle)
    {
      return (short) ((val[1] << 8) + val[0]);
    }
    return (short) ((val[0] << 8) + val[1]);
  }

  /**
   * Sets whether the (long) words are to be interpreted in big or little endian
   * mode.
   * 
   * @param aLittleEndian
   *          <code>true</code> if data is expected to be little endian,
   *          <code>false</code> otherwise.
   */
  public void setEndiannes(final boolean aLittleEndian)
  {
    this.isle = aLittleEndian;
  }
}
