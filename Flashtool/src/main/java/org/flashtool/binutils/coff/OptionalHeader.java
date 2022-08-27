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
package org.flashtool.binutils.coff;


import java.io.*;

import org.flashtool.binutils.elf.ERandomAccessFile;

import org.flashtool.binutils.elf.*;


/**
 * 
 */
public class OptionalHeader
{
  // CONSTANTS

  public final static int AOUTHDRSZ = 28;

  // VARIABLES

  private final short magic; /* 2 bytes: type of file */
  private final short vstamp; /* 2 bytes: version stamp */
  private final int tsize; /* 4 bytes: text size in bytes, padded to FW bdry */
  private final int dsize; /* 4 bytes: initialized data "  " */
  private final int bsize; /* 4 bytes: uninitialized data "   " */
  private final int entry; /* 4 bytes: entry pt. */
  private final int text_start; /* 4 bytes: base of text used for this file */
  private final int data_start; /* 4 bytes: base of data used for this file */

  // CONSTRUCTORS

  /**
   * Creates a new OptionalHeader instance.
   * 
   * @param aFile
   * @throws IOException
   */
  OptionalHeader(ERandomAccessFile aFile) throws IOException
  {
    aFile.seek(aFile.getFilePointer() + FileHeader.FILHSZ);

    this.magic = aFile.readShortE();
    this.vstamp = aFile.readShortE();
    this.tsize = aFile.readIntE();
    this.dsize = aFile.readIntE();
    this.bsize = aFile.readIntE();
    this.entry = aFile.readIntE();
    this.text_start = aFile.readIntE();
    this.data_start = aFile.readIntE();
  }

  // METHODS

  /**
   * Returns the size of uninitialised data.
   * 
   * @return the uninitialised data size, >= 0.
   */
  public int getBSize()
  {
    return this.bsize;
  }

  /**
   * Returns the current value of data_start.
   * 
   * @return the data_start
   */
  public int getDataStart()
  {
    return this.data_start;
  }

  /**
   * Returns the size of initialised data.
   * 
   * @return the initialised data size, >= 0.
   */
  public int getDSize()
  {
    return this.dsize;
  }

  /**
   * Returns the current value of entry.
   * 
   * @return the entry
   */
  public int getEntryPoint()
  {
    return this.entry;
  }

  /**
   * Returns the current value of magic.
   * 
   * @return the magic
   */
  public short getMagic()
  {
    return this.magic;
  }

  /**
   * Returns the current value of tsize.
   * 
   * @return the tsize
   */
  public int getTextSize()
  {
    return this.tsize;
  }

  /**
   * Returns the current value of text_start.
   * 
   * @return the text_start
   */
  public int getTextStart()
  {
    return this.text_start;
  }

  /**
   * Returns the current value of vstamp.
   * 
   * @return the vstamp
   */
  public short getVersionStamp()
  {
    return this.vstamp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("OPTIONAL HEADER VALUES").append('\n');
    buffer.append("magic      = ").append(this.magic).append('\n');
    buffer.append("vstamp     = ").append(this.vstamp).append('\n');
    buffer.append("tsize      = ").append(this.tsize).append('\n');
    buffer.append("dsize      = ").append(this.dsize).append('\n');
    buffer.append("bsize      = ").append(this.bsize).append('\n');
    buffer.append("entry      = ").append(this.entry).append('\n');
    buffer.append("text_start = ").append(this.text_start).append('\n');
    buffer.append("data_start = ").append(this.data_start).append('\n');
    return buffer.toString();
  }
}
