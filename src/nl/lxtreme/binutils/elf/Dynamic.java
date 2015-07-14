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
package nl.lxtreme.binutils.elf;


import java.io.*;
import java.util.*;


/**
 * If an object file participates in dynamic linking, its program header table
 * will have an element of type PT_DYNAMIC. This "segment" contains the .dynamic
 * section. A special symbol, _DYNAMIC, labels the section, which contains an
 * array of the following structures.
 */
public class Dynamic
{
  // CONSTANTS

  public final static int DYN_ENT_SIZE_32 = 8;
  public final static int DYN_ENT_SIZE_64 = 16;

  public final static int DT_NULL = 0;
  public final static int DT_NEEDED = 1;
  public final static int DT_PLTRELSZ = 2;
  public final static int DT_PLTGOT = 3;
  public final static int DT_HASH = 4;
  public final static int DT_STRTAB = 5;
  public final static int DT_SYMTAB = 6;
  public final static int DT_RELA = 7;
  public final static int DT_RELASZ = 8;
  public final static int DT_RELAENT = 9;
  public final static int DT_STRSZ = 10;
  public final static int DT_SYMENT = 11;
  public final static int DT_INIT = 12;
  public final static int DT_FINI = 13;
  public final static int DT_SONAME = 14;
  public final static int DT_RPATH = 15;
  public static final int DT_SYMBOLIC = 16;
  public static final int DT_REL = 17;
  public static final int DT_RELSZ = 18;
  public static final int DT_RELENT = 19;
  public static final int DT_PLTREL = 20;
  public static final int DT_DEBUG = 21;
  public static final int DT_TEXTREL = 22;
  public static final int DT_JMPREL = 23;
  public static final int DT_BIND_NOW = 24;
  public static final int DT_INIT_ARRAY = 25;
  public static final int DT_FINI_ARRAY = 26;
  public static final int DT_INIT_ARRAYSZ = 27;
  public static final int DT_FINI_ARRAYSZ = 28;
  public static final int DT_RUNPATH = 29;
  public static final int DT_FLAGS = 30;
  public static final int DT_ENCODING = 32;
  public static final int DT_PREINIT_ARRAY = 32;
  public static final int DT_PREINIT_ARRAYSZ = 33;

  // VARIABLES

  private final Section section;

  private long d_tag;
  private long d_val;
  private int size;
  private String name;

  // CONSTRUCTORS

  /**
   * Creates a new Dynamic instance.
   * 
   * @param aSection
   *          the section.
   */
  private Dynamic(final Section aSection)
  {
    this.section = aSection;
  }

  // METHODS

  /**
   * Factory method.
   * 
   * @param aHeader
   * @param aSection
   * @param efile
   * @return
   * @throws IOException
   */
  static Dynamic[] create(final ElfHeader aHeader, final Section aSection, final ERandomAccessFile efile)
      throws IOException
  {
    if (aSection.getType() != Section.SHT_DYNAMIC)
    {
      return new Dynamic[0];
    }

    final ArrayList<Dynamic> dynList = new ArrayList<Dynamic>();
    efile.seek(aSection.getFileOffset());

    int off = 0;
    // We must assume the section is a table ignoring the sh_entsize as it
    // is not set for MIPS.
    while (off < aSection.getSize())
    {
      final Dynamic dynEnt = createDynamic(aHeader, aSection, efile);
      off += dynEnt.getSize();

      if (dynEnt.getTag() != Dynamic.DT_NULL)
      {
        dynList.add(dynEnt);
      }
    }
    return dynList.toArray(new Dynamic[dynList.size()]);
  }

  /**
   * @param aClass
   * @param aSection
   * @param efile
   * @return
   * @throws IOException
   */
  private static Dynamic createDynamic(final ElfHeader aHeader, final Section aSection, final ERandomAccessFile efile)
      throws IOException
  {
    final Dynamic result = new Dynamic(aSection);
    if (aHeader.is32bit())
    {
      result.d_tag = efile.readIntE();
      result.d_val = efile.readIntE();
      result.size = DYN_ENT_SIZE_32;
    }
    else if (aHeader.is64bit())
    {
      result.d_tag = efile.readLongE();
      result.d_val = efile.readLongE();
      result.size = DYN_ENT_SIZE_64;
    }
    else
    {
      throw new IOException("Unknown ELF class!");
    }
    return result;
  }

  /**
   * @return
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * @return
   */
  public Section getSection()
  {
    return this.section;
  }

  /**
   * @return
   */
  public int getSize()
  {
    return this.size;
  }

  /**
   * Controls the interpretation of {@link #getValue()}.
   * 
   * @return a tag, >= 0.
   */
  public long getTag()
  {
    return this.d_tag;
  }

  /**
   * Returns integer values with various interpretations.
   * <p>
   * Returns either the program virtual addresses. A file's virtual addresses
   * might not match the memory virtual addresses during execution. When
   * interpreting addresses contained in the dynamic structure, the dynamic
   * linker computes actual addresses, based on the original file value and the
   * memory base address. For consistency, files do not contain relocation
   * entries to "correct" addresses in the dynamic structure.
   * 
   * @return a virtual address.
   */
  public long getValue()
  {
    return this.d_val;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    if (this.name == null)
    {
      switch ((int) this.d_tag)
      {
        case Dynamic.DT_NEEDED:
        case Dynamic.DT_SONAME:
        case Dynamic.DT_RPATH:
          this.name = this.section.getStringByIndex((int) this.d_val);
          break;
        default:
          this.name = "";
      }
    }
    return this.name;
  }
}
