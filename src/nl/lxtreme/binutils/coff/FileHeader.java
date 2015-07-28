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
import java.text.*;
import java.util.*;

import nl.lxtreme.binutils.elf.*;


/**
 * 
 */
public class FileHeader
{
  // CONSTANTS

  public final static int FILHSZ = 20;

  // relocation info stripped from file
  public final static int F_RELFLG = 0x0001;
  // file is executable (no unresolved external references)
  public final static int F_EXEC = 0x0002;
  // line numbers stripped from file
  public final static int F_LNNO = 0x0004;
  // local symbols stripped from file
  public final static int F_LSYMS = 0x0008;
  // file is 16-bit little-endian
  public final static int F_AR16WR = 0x0080;
  // file is 32-bit little-endian
  public final static int F_AR32WR = 0x0100;
  // file is 32-bit big-endian
  public final static int F_AR32W = 0x0200;
  // rs/6000 aix: dynamically loadable w/imports & exports
  public final static int F_DYNLOAD = 0x1000;
  // rs/6000 aix: file is a shared object
  public final static int F_SHROBJ = 0x2000;
  // PE format DLL.
  public final static int F_DLL = 0x2000;

  // VARIABLES

  private final int magic; /* 00-01 2 bytes: magic number */
  private final int nscns; /* 02-03 2 bytes: number of sections: 2 bytes */
  private final int timdat; /* 04-07 4 bytes: time & date stamp */
  private final int symptr; /* 08-11 4 bytes: file pointer to symtab */
  private final int nsyms; /* 12-15 4 bytes: number of symtab entries */
  private final int opthdr; /* 16-17 2 bytes: sizeof(optional hdr) */
  private final int flags; /* 18-19 2 bytes: flags */

  // CONSTRUCTORS

  /**
   * Creates a new FileHeader instance.
   * 
   * @param aFile
   * @throws IOException
   */
  FileHeader(ERandomAccessFile aFile) throws IOException
  {
    this.magic = aFile.readShortE();
    this.nscns = aFile.readShortE();
    this.timdat = aFile.readIntE();
    this.symptr = aFile.readIntE();
    this.nsyms = aFile.readIntE();
    this.opthdr = aFile.readShortE();
    this.flags = aFile.readShortE();
  }

  // METHODS

  /**
   * Returns the time & date stamp.
   * 
   * @return a time & date stamp.
   */
  public int getDateTime()
  {
    return this.timdat;
  }

  /**
   * Returns the current value of flags.
   * 
   * @return the flags
   */
  public int getFlags()
  {
    return this.flags;
  }

  /**
   * Returns the current value of magic.
   * 
   * @return the magic
   */
  public int getMagic()
  {
    return this.magic;
  }

  /**
   * @return
   */
  public int getOptionalHeaderSize()
  {
    return this.opthdr;
  }

  /**
   * Returns the number of sections.
   * 
   * @return a section count, >= 0.
   */
  public int getSectionCount()
  {
    return this.nscns;
  }

  /**
   * @return the file pointer to symbol table.
   */
  public int getSymbolTableEntryCount()
  {
    return this.nsyms;
  }

  /**
   * @return the file pointer to symbol table.
   */
  public int getSymbolTableOffset()
  {
    return this.symptr;
  }

  /**
   * @return
   */
  public boolean hasOptionalHeader()
  {
    return this.opthdr > 0;
  }

  /**
   * @return
   */
  public boolean isDebug()
  {
    return !((this.flags & F_LNNO) == F_LNNO);
  }

  /**
   * @return
   */
  public boolean isExec()
  {
    return (this.flags & F_EXEC) == F_EXEC;
  }

  /**
   * @return
   */
  public boolean isStrip()
  {
    return (this.flags & F_RELFLG) == F_RELFLG;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("FILE HEADER VALUES").append('\n');

    buffer.append("f_magic = ").append(this.magic).append('\n');
    buffer.append("f_nscns = ").append(this.nscns).append('\n');

    buffer.append("f_timdat = ");
    buffer.append(DateFormat.getDateInstance().format(new Date(this.timdat)));
    buffer.append('\n');

    buffer.append("f_symptr = ").append(this.symptr).append('\n');
    buffer.append("f_nsyms = ").append(this.nsyms).append('\n');
    buffer.append("f_opthdr = ").append(this.opthdr).append('\n');
    buffer.append("f_flags = ").append(this.flags).append('\n');
    return buffer.toString();
  }
}
