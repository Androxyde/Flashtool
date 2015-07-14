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
package nl.lxtreme.binutils.hex.util;


import java.io.*;
import java.nio.*;


/**
 * 
 */
public final class HexUtils
{
  // CONSTRUCTORS

  /**
   * Creates a new HexUtils instance.
   */
  private HexUtils()
  {
    // NO-op
  }

  // METHODS

  /**
   * Parses the hex-byte in the given character sequence at the given offset.
   * 
   * @param aInput
   *          the characters to parse as hex-bytes.
   * @return a byte value.
   * @throws IllegalArgumentException
   *           in case the given char sequence was <code>null</code>, in case
   *           the given input did not yield a hex-byte, or the requested offset
   *           is outside the boundaries of the given char sequence.
   */
  public static int parseHexByte(final char[] aInput) throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }
    return parseHexByte(new String(aInput), 0);
  }

  /**
   * Parses the hex-byte in the given character sequence at the given offset.
   * 
   * @param aInput
   *          the char sequence to parse as hex-bytes;
   * @param aOffset
   *          the offset in the char sequence to start parsing.
   * @return a byte value.
   * @throws IllegalArgumentException
   *           in case the given char sequence was <code>null</code>, in case
   *           the given input did not yield a hex-byte, or the requested offset
   *           is outside the boundaries of the given char sequence.
   */
  public static int parseHexByte(final CharSequence aInput, final int aOffset) throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }

    try
    {
      return (Integer.parseInt(aInput.subSequence(aOffset, aOffset + 2).toString(), 16));
    }
    catch (IndexOutOfBoundsException exception)
    {
      throw new IllegalArgumentException("No such offset: " + aOffset + "; length = " + aInput.length());
    }
    catch (NumberFormatException exception)
    {
      throw new IllegalArgumentException("Not a hex-digit string: " + aInput);
    }
  }

  /**
   * Reads two characters from the given reader and parses them as a single
   * hex-value byte.
   * 
   * @param aReader
   * @return
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public static int parseHexByte(final Reader aReader) throws IllegalArgumentException, IOException
  {
    return parseHexNumber(aReader, 1);
  }

  /**
   * Parses the hex-byte in the given character sequence at the given offset.
   * 
   * @param aInput
   *          the char sequence to parse as hex-bytes;
   * @param aOffset
   *          the offset in the char sequence to start parsing.
   * @return a byte value.
   * @throws IllegalArgumentException
   *           in case the given char sequence was <code>null</code>, in case
   *           the given input did not yield a hex-byte, or the requested offset
   *           is outside the boundaries of the given char sequence.
   */
  public static int[] parseHexBytes(final CharSequence aInput, final int aOffset, final int aByteCount)
      throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }
    if (aByteCount < 0)
    {
      throw new IllegalArgumentException("Byte count cannot be less than one!");
    }

    try
    {
      final int[] result = new int[aByteCount];
      int offset = aOffset;
      for (int i = 0; i < aByteCount; i++, offset += 2)
      {
        result[i] = Integer.parseInt(aInput.subSequence(offset, offset + 2).toString(), 16);
      }
      return result;
    }
    catch (IndexOutOfBoundsException exception)
    {
      throw new IllegalArgumentException("No such offset: " + aOffset + "; length = " + aInput.length());
    }
    catch (NumberFormatException exception)
    {
      throw new IllegalArgumentException("Not a hex-digit string!");
    }
  }

  /**
   * @param aInput
   * @param aOffset
   * @param aByteCount
   * @return
   * @throws IllegalArgumentException
   */
  public static int parseHexNumber(final CharSequence aInput, final int aOffset, final int aByteCount)
      throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }
    if (aByteCount < 1)
    {
      throw new IllegalArgumentException("Byte count cannot be less than one!");
    }

    final int[] addressBytes = parseHexBytes(aInput, aOffset, aByteCount);

    int address = 0;
    for (int i = 0; i < aByteCount; i++)
    {
      address |= addressBytes[i];
      if (i < aByteCount - 1)
      {
        address <<= 8;
      }
    }

    return address;
  }

  /**
   * Reads a number of characters from the given reader and parses them as a
   * hex-value.
   * 
   * @param aReader
   *          the reader to read the data from;
   * @param aByteCount
   *          the number of bytes to read (= 2 * amount of actual characters
   *          read).
   * @return the parsed number.
   * @throws IllegalArgumentException
   *           in case the given reader was <code>null</code> or the given byte
   *           count was <= 0.
   * @throws IOException
   *           in case of I/O problems.
   */
  public static int parseHexNumber(final Reader aReader, final int aByteCount) throws IllegalArgumentException,
      IOException
  {
    if (aReader == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }
    if (aByteCount <= 0)
    {
      throw new IllegalArgumentException("Byte count cannot be less or equal to zero!");
    }

    final char[] buf = new char[2 * aByteCount];
    if (aReader.read(buf) != buf.length)
    {
      throw new IOException("Unexpected end-of-stream?!");
    }

    int result = 0;
    for (char element : buf)
    {
      int hexdigit = Character.digit(element, 16);
      if (hexdigit < 0)
      {
        throw new IOException("Unexpected character: " + element);
      }
      result *= 16;
      result |= hexdigit;
    }

    return result;
  }

  /**
   * Parses the hex-word in the given character sequence at the given offset
   * assuming it is in big endian byte order.
   * 
   * @param aInput
   *          the char sequence to parse as hex-bytes;
   * @param aOffset
   *          the offset in the char sequence to start parsing.
   * @return a word value, in big endian byte order.
   * @throws IllegalArgumentException
   *           in case the given char sequence was <code>null</code>, in case
   *           the given input did not yield a hex-byte, or the requested offset
   *           is outside the boundaries of the given char sequence.
   */
  public static int parseHexWord(final char[] aInput) throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }
    return parseHexWord(new String(aInput), 0);
  }

  /**
   * Parses the hex-word in the given character sequence at the given offset
   * assuming it is in big endian byte order.
   * 
   * @param aInput
   *          the char sequence to parse as hex-bytes;
   * @param aOffset
   *          the offset in the char sequence to start parsing.
   * @return a word value, in big endian byte order.
   * @throws IllegalArgumentException
   *           in case the given char sequence was <code>null</code>, in case
   *           the given input did not yield a hex-byte, or the requested offset
   *           is outside the boundaries of the given char sequence.
   */
  public static int parseHexWord(final CharSequence aInput, final int aOffset) throws IllegalArgumentException
  {
    if (aInput == null)
    {
      throw new IllegalArgumentException("Input cannot be null!");
    }

    try
    {
      int msb = Integer.parseInt(aInput.subSequence(aOffset + 0, aOffset + 2).toString(), 16);
      int lsb = Integer.parseInt(aInput.subSequence(aOffset + 2, aOffset + 4).toString(), 16);
      return ByteOrderUtils.createWord(ByteOrder.BIG_ENDIAN, msb, lsb);
    }
    catch (IndexOutOfBoundsException exception)
    {
      throw new IllegalArgumentException("No such offset: " + aOffset + "; length = " + aInput.length());
    }
    catch (NumberFormatException exception)
    {
      throw new IllegalArgumentException("Not a hex-digit string!");
    }
  }

  /**
   * Reads four characters from the given reader and parses them as a single
   * hex-value word.
   * 
   * @param aReader
   * @return
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public static int parseHexWord(final Reader aReader) throws IllegalArgumentException, IOException
  {
    return parseHexNumber(aReader, 2);
  }

}
