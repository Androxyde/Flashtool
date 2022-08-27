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
public class Coff
{
  // VARIABLES

  private FileHeader filehdr;
  private OptionalHeader opthdr;
  private final ERandomAccessFile rfile;
  private byte[] string_table;
  private SectionHeader[] scnhdrs;
  private Symbol[] symbols;

  // CONSTRUCTORS

  /**
   * Creates a new Coff instance.
   * 
   * @param aFile
   * @throws IOException
   */
  public Coff(File aFile) throws IOException
  {
    this.rfile = new ERandomAccessFile(aFile, "r");
    this.rfile.setEndiannes(true /* aLittleEndian */);

    try
    {
      this.filehdr = new FileHeader(this.rfile);
      if (this.filehdr.hasOptionalHeader())
      {
        this.opthdr = new OptionalHeader(this.rfile);
      }
    }
    finally
    {
      if (this.filehdr == null)
      {
        this.rfile.close();
      }
    }
  }

  // METHODS

  /**
   * @return
   * @throws IOException
   */
  public FileHeader getFileHeader() throws IOException
  {
    return this.filehdr;
  }

  /**
   * @return
   * @throws IOException
   */
  public OptionalHeader getOptionalHeader() throws IOException
  {
    return this.opthdr;
  }

  /**
   * @return
   * @throws IOException
   */
  public SectionHeader[] getSectionHeaders() throws IOException
  {
    if (this.scnhdrs == null)
    {
      FileHeader header = getFileHeader();

      this.scnhdrs = new SectionHeader[header.getSymbolTableEntryCount()];
      for (int i = 0; i < this.scnhdrs.length; i++)
      {
        this.scnhdrs[i] = new SectionHeader(this.rfile);
      }
    }
    return this.scnhdrs;
  }

  /**
   * @return
   * @throws IOException
   */
  public byte[] getStringTable() throws IOException
  {
    if (this.string_table == null)
    {
      FileHeader header = getFileHeader();

      long symbolSize = Symbol.SYMSZ * header.getSymbolTableEntryCount();
      long offset = header.getSymbolTableOffset() + symbolSize;

      this.rfile.seek(offset);

      int str_len = this.rfile.readIntE();
      if ((str_len > 4) && (str_len < this.rfile.length()))
      {
        str_len -= 4;
        this.string_table = new byte[str_len];
        this.rfile.seek(offset + 4);
        this.rfile.readFully(this.string_table);
      }
      else
      {
        this.string_table = new byte[0];
      }
    }
    return this.string_table;
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getSymbols() throws IOException
  {
    if (this.symbols == null)
    {
      long offset = getFileHeader().getSymbolTableOffset();
      this.rfile.seek(offset);
      this.symbols = new Symbol[getFileHeader().getSymbolTableEntryCount()];
      for (int i = 0; i < this.symbols.length; i++)
      {
        this.symbols[i] = new Symbol(this.rfile);
      }
    }
    return this.symbols;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    try
    {
      FileHeader header = getFileHeader();
      if (header != null)
      {
        buffer.append(header);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    try
    {
      OptionalHeader opt = null;
      opt = getOptionalHeader();
      if (opt != null)
      {
        buffer.append(opt);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    try
    {
      SectionHeader[] sections = getSectionHeaders();
      for (SectionHeader section : sections)
      {
        buffer.append(section);
      }
    }
    catch (IOException e)
    {
    }

    try
    {
      Symbol[] table = getSymbols();
      for (Symbol element : table)
      {
        buffer.append(element.getName(getStringTable())).append('\n');
      }
    }
    catch (IOException e)
    {
    }

// try {
// String[] strings = getStringTable(getStringTable());
// for (int i = 0; i < strings.length; i++) {
// buffer.append(strings[i]);
// }
// } catch (IOException e) {
// e.printStackTrace();
// }
    return buffer.toString();
  }
}
