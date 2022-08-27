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
public class LineNo
{
  // VARIABLES

  /**
   * long. Index into symbol table if l_linn0 == 0.
   * Break-pointable address if l_lnno > 0.
   */
  private final int l_addr;
  /** unsigned short. Line number */
  private final int l_lnno;

  // CONSTRUCTORS

  /**
   * Creates a new LineNo instance.
   * 
   * @param aFile
   * @throws IOException
   */
  LineNo(ERandomAccessFile aFile) throws IOException
  {
    this.l_addr = aFile.readIntE();
    this.l_lnno = aFile.readShortE();
  }

  // METHODS

  /**
   * Returns the function address.
   * 
   * @return the address, >= 0.
   */
  public int getAddress()
  {
    return this.l_addr;
  }

  /**
   * Returns the line number.
   * 
   * @return the line number, >= 0.
   */
  public int getLineNumber()
  {
    return this.l_lnno;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    if (this.l_lnno == 0)
    {
      buffer.append("Function address = ").append(this.l_addr).append('\n');
    }
    else
    {
      buffer.append("line #").append(this.l_lnno);
      buffer.append(" at address = ").append(this.l_addr).append('\n');
    }
    return buffer.toString();
  }
}
