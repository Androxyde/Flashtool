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

import nl.lxtreme.binutils.elf.*;


/**
 * 
 */
public class SectionHeader
{
  // CONSTANTS

  /* names of "special" sections */
  public final static String _TEXT = ".text";
  public final static String _DATA = ".data";
  public final static String _BSS = ".bss";
  public final static String _COMMENT = ".comment";
  public final static String _LIB = ".lib";

  /* s_flags "type". */

  /** "regular": allocated, relocated, loaded */
  public final static int STYP_REG = 0x0000;
  /** "dummy": relocated only */
  public final static int STYP_DSECT = 0x0001;
  /** "noload": allocated, relocated, not loaded */
  public final static int STYP_NOLOAD = 0x0002;
  /** "grouped": formed of input sections */
  public final static int STYP_GROUP = 0x0004;
  /** "padding": not allocated, not relocated, loaded */
  public final static int STYP_PAD = 0x0008;
  /**
   * "copy": for decision function used by field update; not allocated, not
   * relocated, loaded; reloc & lineno entries processed normally
   */
  public final static int STYP_COPY = 0x0010;
  /** section contains text only. */
  public final static int STYP_TEXT = 0x0020;
  /**
   * In 3b Update files (output of ogen), sections which appear in SHARED
   * segments of the Pfile will have the S_SHRSEG flag set by ogen, to inform
   * dufr that updating 1 copy of the proc. will update all process invocations.
   */
  public final static int S_SHRSEG = 0x0020;
  /** section contains data only */
  public final static int STYP_DATA = 0x0040;
  /** section contains bss only */
  public final static int STYP_BSS = 0x0080;
  /**
   * In a minimal file or an update file, a new function (as compared with a
   * replaced function)
   */
  public final static int S_NEWFCN = 0x0100;
  /** comment: not allocated not relocated, not loaded */
  public final static int STYP_INFO = 0x0200;
  /** overlay: relocated not allocated or loaded */
  public final static int STYP_OVER = 0x0400;
  /** for .lib: same as INFO */
  public final static int STYP_LIB = 0x0800;
  /** merge section -- combines with text, data or bss sections only */
  public final static int STYP_MERGE = 0x2000;
  /**
   * section will be padded with no-op instructions wherever padding is
   * necessary and there is a word of contiguous bytes beginning on a word
   * boundary.
   */
  public final static int STYP_REVERSE_PAD = 0x4000;
  /** Literal data (like STYP_TEXT) */
  public final static int STYP_LIT = 0x8020;

  // VARIABLES

  private final byte[] s_name = new byte[8]; // 8 bytes: section name
  private final int s_paddr; // 4 bytes: physical address, aliased s_nlib
  private final int s_vaddr; // 4 bytes: virtual address
  private final int s_size; // 4 bytes: section size
  private final int s_scnptr; // 4 bytes: file ptr to raw data for section
  private final int s_relptr; // 4 bytes: file ptr to relocation
  private final int s_lnnoptr; // 4 bytes: file ptr to line numbers
  private final int s_nreloc; // 2 bytes: number of relocation entries
  private final int s_nlnno; // 2 bytes: number of line number entries
  private final int s_flags; // 4 bytes: flags

  private final ERandomAccessFile sfile;

  // CONSTRUCTORS

  /**
   * Creates a new SectionHeader instance.
   * 
   * @param aFile
   * @param offset
   * @throws IOException
   */
  SectionHeader(ERandomAccessFile aFile) throws IOException
  {
    this.sfile = aFile;

    aFile.readFully(this.s_name);
    this.s_paddr = aFile.readIntE();
    this.s_vaddr = aFile.readIntE();
    this.s_size = aFile.readIntE();
    this.s_scnptr = aFile.readIntE();
    this.s_relptr = aFile.readIntE();
    this.s_lnnoptr = aFile.readIntE();
    this.s_nreloc = aFile.readShortE();
    this.s_nlnno = aFile.readShortE();
    this.s_flags = aFile.readIntE();
  }

  /**
   * Returns the current value of s_flags.
   * 
   * @return the s_flags
   */
  public int getFlags()
  {
    return this.s_flags;
  }

  /**
   * @return
   * @throws IOException
   */
  public LineNo[] getLineNumbers() throws IOException
  {
    LineNo[] lines = new LineNo[this.s_nlnno];
    this.sfile.seek(this.s_lnnoptr);
    for (int i = 0; i < this.s_nlnno; i++)
    {
      lines[i] = new LineNo(this.sfile);
    }
    return lines;
  }

  /**
   * @return
   */
  public int getPhysicalAddress()
  {
    return this.s_paddr;
  }

  /**
   * @return
   * @throws IOException
   */
  public byte[] getRawData() throws IOException
  {
    byte[] data = new byte[this.s_size];
    this.sfile.seek(this.s_scnptr);
    this.sfile.readFully(data);
    return data;
  }

  /**
   * @return
   * @throws IOException
   */
  public Reloc[] getRelocs() throws IOException
  {
    Reloc[] relocs = new Reloc[this.s_nreloc];
    this.sfile.seek(this.s_relptr);
    for (int i = 0; i < this.s_nreloc; i++)
    {
      relocs[i] = new Reloc(this.sfile);
    }
    return relocs;
  }

  /**
   * @return
   */
  public int getSize()
  {
    return this.s_size;
  }

  /**
   * @return
   */
  public int getVirtualAddress()
  {
    return this.s_vaddr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("SECTION HEADER VALUES").append('\n');
    buffer.append(new String(this.s_name)).append('\n');
    buffer.append("s_paddr = ").append(this.s_paddr).append('\n');
    buffer.append("s_vaddr = ").append(this.s_vaddr).append('\n');
    buffer.append("s_size = ").append(this.s_size).append('\n');
    buffer.append("s_scnptr = ").append(this.s_scnptr).append('\n');
    buffer.append("s_relptr = ").append(this.s_relptr).append('\n');
    buffer.append("s_lnnoptr = ").append(this.s_lnnoptr).append('\n');
    buffer.append("s_nreloc = ").append(this.s_nreloc).append('\n');
    buffer.append("s_nlnno = ").append(this.s_nlnno).append('\n');
    buffer.append("s_flags = ").append(this.s_flags).append('\n');
// /*
    try
    {
      Reloc[] rcs = getRelocs();
      for (Reloc rc : rcs)
      {
        buffer.append(rc);
      }
    }
    catch (IOException e)
    {
    }
    try
    {
      LineNo[] nos = getLineNumbers();
      for (LineNo no : nos)
      {
        buffer.append(no);
      }
    }
    catch (IOException e)
    {
    }
// */
    return buffer.toString();
  }
}
