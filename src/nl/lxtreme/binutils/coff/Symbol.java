/*******************************************************************************
 * Copyright (c) 2011, J.W. Janssen
 * 
 * Copyright (c) 2000, 2010 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     J.W. Janssen - Cleanup and make API more OO-oriented.
 *******************************************************************************/
package nl.lxtreme.binutils.coff;


import java.io.*;


public class Symbol
{
  // CONSTANTS

  public final static int SYMSZ = 18;
  public final static int SYMNMLEN = 8;

  /* Derived types, in n_type. */

  /** no derived type */
  public final static int DT_NON = 0;
  /** pointer */
  public final static int DT_PTR = 1;
  /** function */
  public final static int DT_FCN = 2;
  /** array */
  public final static int DT_ARY = 3;

  public final static int N_TMASK = 0x30;
  public final static int N_BTSHFT = 4;
  public final static int N_TSHIFT = 2;

  /** No symbol */
  public final static int T_NULL = 0x00;
  /** -- 0001 void function argument (not used) */
  public final static int T_VOID = 0x01;
  /** -- 0010 character */
  public final static int T_CHAR = 0x02;
  /** -- 0011 short integer */
  public final static int T_SHORT = 0x03;
  /** -- 0100 integer */
  public final static int T_INT = 0x04;
  /** -- 0101 long integer */
  public final static int T_LONG = 0x05;
  /** -- 0110 floating point */
  public final static int T_FLOAT = 0x06;
  /** -- 0111 double precision float */
  public final static int T_DOUBLE = 0x07;
  /** -- 1000 structure */
  public final static int T_STRUCT = 0x08;
  /** -- 1001 union */
  public final static int T_UNION = 0x09;
  /** -- 1010 enumeration */
  public final static int T_ENUM = 0x10;
  /** -- 1011 member of enumeration */
  public final static int T_MOE = 0x11;
  /** -- 1100 unsigned character */
  public final static int T_UCHAR = 0x12;
  /** -- 1101 unsigned short */
  public final static int T_USHORT = 0x13;
  /** -- 1110 unsigned integer */
  public final static int T_UINT = 0x14;
  /** -- 1111 unsigned long */
  public final static int T_ULONG = 0x15;
  /** -1 0000 long double (special case bit pattern) */
  public final static int T_LNGDBL = 0x16;

  // VARIABLES

  /**
   * Symbol name, or pointer into string table if symbol name is greater than
   * SYMNMLEN.
   */
  private final byte[] _n_name = new byte[SYMNMLEN];
  /** long. Symbol's value: dependent on section number, storage class and type. */
  private int n_value;
  /** short, Section number. */
  private final short n_scnum;
  /** Unsigned short. Symbolic type. */
  private final int n_type;
  /** char, Storage class. */
  private final byte n_sclass;
  /** char. Number of auxiliary enties. */
  private final byte n_numaux;

  // CONSTRUCTORS

  /**
   * Creates a new Symbol instance.
   * 
   * @param file
   * @throws IOException
   */
  protected Symbol(RandomAccessFile file) throws IOException
  {
    file.readFully(this._n_name);
    this.n_value = file.readInt();
    this.n_scnum = file.readShort();
    this.n_type = file.readUnsignedShort();
    this.n_sclass = file.readByte();
    this.n_numaux = file.readByte();
  }

  // METHODS

  /**
   * @return
   */
  public int getAuxiliaryEntryCount()
  {
    return this.n_numaux;
  }

  /**
   * @return
   */
  public String getName()
  {
    // For a long name, _n_name[0] == 0 and this would just return empty string.
    for (int i = 0; i < this._n_name.length; i++)
    {
      if (this._n_name[i] == 0)
      {
        return new String(this._n_name, 0, i);
      }
    }
    // all eight bytes are filled
    return new String(this._n_name);
  }

  /**
   * @param aTable
   * @return
   */
  public String getName(byte[] aTable)
  {
    if ((aTable.length > 0) && isLongName())
    {
      // The first four bytes of the string table represent the
      // number of bytes in the string table.
      int offset = (((this._n_name[8] & 0xff) << 24) //
          | ((this._n_name[7] & 0xff) << 16) //
          | ((this._n_name[6] & 0xff) << 8) //
      | (this._n_name[5] & 0xff)) - 4;

      if (offset > 0)
      {
        for (int i = offset; i < aTable.length; i++)
        {
          if (aTable[i] == 0)
          {
            return new String(aTable, offset, i - offset);
          }
        }
      }
    }
    return getName();
  }

  /**
   * @return
   */
  public int getSectionNumber()
  {
    return this.n_scnum;
  }

  /**
   * @return
   */
  public byte getStorageClass()
  {
    return this.n_sclass;
  }

  /**
   * @return
   */
  public int getType()
  {
    return this.n_type;
  }

  /**
   * Returns the current value of n_value.
   * 
   * @return the n_value
   */
  public int getValue()
  {
    return this.n_value;
  }

  /**
   * @return
   */
  public boolean isArray()
  {
    return (this.n_type & N_TMASK) == (DT_ARY << N_BTSHFT);
  }

  /**
   * @return
   */
  public boolean isFunction()
  {
    return (this.n_type & N_TMASK) == (DT_FCN << N_BTSHFT);
  }

  /**
   * @return
   */
  public boolean isLongName()
  {
    return (this._n_name[0] == 0);
  }

  /**
   * /* @since 5.3
   * 
   * @return
   */
  public boolean isNoSymbol()
  {
    return (this.n_type == T_NULL);
  }

  /**
   * @return
   */
  public boolean isPointer()
  {
    return (this.n_type & N_TMASK) == (DT_PTR << N_BTSHFT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return getName();
  }

  /**
   * @param aOffset
   */
  protected void relocateRelative(int aOffset)
  {
    this.n_value += aOffset;
  }
}
