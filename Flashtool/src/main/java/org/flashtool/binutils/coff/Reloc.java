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
public class Reloc
{
  // VARIABLES

  /**
   * 4 byte: Pointer to an area in raw data that represents a referenced
   * address.
   */
  private final int r_vaddr;
  /** 4 byte: Index into symbol table. */
  private final int r_symndx;
  /** 2 byte(unsigned short): Type of address reference. */
  private final int r_type;

  // CONSTRUCTORS

  /**
   * Creates a new Reloc instance.
   * 
   * @param aFile
   * @throws IOException
   */
  Reloc(ERandomAccessFile aFile) throws IOException
  {
    this.r_vaddr = aFile.readIntE();
    this.r_symndx = aFile.readIntE();
    this.r_type = aFile.readShortE();
  }

  // METHODS

  /**
   * Returns a pointer to an area in raw data that represents a referenced
   * address.
   * 
   * @return
   */
  public int getAddress()
  {
    return this.r_vaddr;
  }

  /**
   * Returns the index into symbol table.
   * 
   * @return a symbol table index, >= 0.
   */
  public int getSymbolTableIndex()
  {
    return this.r_symndx;
  }

  /**
   * Returns the address reference type.
   * 
   * @return an address reference type, >= 0.
   */
  public int getType()
  {
    return this.r_type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("RELOC VALUES").append('\n');
    buffer.append("r_vaddr = ").append(this.r_vaddr);
    buffer.append(" r_symndx = ").append(this.r_symndx).append('\n');
    return buffer.toString();
  }
}
