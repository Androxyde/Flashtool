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


/**
 * An object file's symbol table holds information needed to locate and relocate
 * a program's symbolic definitions and references. A symbol table index is a
 * subscript into this array. Index 0 both designates the first entry in the
 * table and serves as the undefined symbol index.
 */
public class Symbol implements Comparable<Object>
{
  // CONSTANTS

  /* Symbol bindings */
  public final static int STB_LOCAL = 0;
  public final static int STB_GLOBAL = 1;
  public final static int STB_WEAK = 2;
  /* Symbol type */
  public final static int STT_NOTYPE = 0;
  public final static int STT_OBJECT = 1;
  public final static int STT_FUNC = 2;
  public final static int STT_SECTION = 3;
  public final static int STT_FILE = 4;
  /* Special Indexes */
  public final static int SHN_UNDEF = 0;
  public final static int SHN_LORESERVE = 0xffffff00;
  public final static int SHN_LOPROC = 0xffffff00;
  public final static int SHN_HIPROC = 0xffffff1f;
  public final static int SHN_LOOS = 0xffffff20;
  public final static int SHN_HIOS = 0xffffff3f;
  public final static int SHN_ABS = 0xfffffff1;
  public final static int SHN_COMMON = 0xfffffff2;
  public final static int SHN_XINDEX = 0xffffffff;
  public final static int SHN_HIRESERVE = 0xffffffff;

  // VARIABLES

  /* NOTE: 64 bit and 32 bit ELF sections has different order */
  private long st_name;
  private long st_value;
  private long st_size;
  private short st_info;
  private short st_other;
  private short st_shndx;

  private String name = null;
  private final Section sym_section;

  // CONSTRUCTORS

  /**
   * Creates a new Symbol instance.
   * 
   * @param aSection
   */
  private Symbol(final Section aSection)
  {
    this.sym_section = aSection;
  }

  // METHODS

  /**
   * @param aClass
   * @param aSection
   * @param aFile
   * @return
   * @throws IOException
   */
  static Symbol create(ElfHeader aHeader, final Section aSection, final ERandomAccessFile aFile) throws IOException
  {
    final Symbol symbol = new Symbol(aSection);
    if (aHeader.is32bit())
    {
      final byte[] addrArray = new byte[Elf.ELF32_ADDR_SIZE];

      symbol.st_name = aFile.readIntE();
      aFile.readFullyE(addrArray);
      symbol.st_value = Elf.createAddr32(addrArray);
      symbol.st_size = aFile.readIntE();
      symbol.st_info = aFile.readByte();
      symbol.st_other = aFile.readByte();
      symbol.st_shndx = aFile.readShortE();
    }
    else if (aHeader.is64bit())
    {
      final byte[] addrArray = new byte[Elf.ELF64_ADDR_SIZE];

      symbol.st_name = aFile.readIntE();
      symbol.st_info = aFile.readByte();
      symbol.st_other = aFile.readByte();
      symbol.st_shndx = aFile.readShortE();
      aFile.readFullyE(addrArray);
      symbol.st_value = Elf.createAddr64(addrArray);
      symbol.st_size = Elf.readUnsignedLong(aFile);
    }
    else
    {
      throw new IOException("Unknown ELF class!");
    }
    return symbol;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Object obj)
  {
    return this.st_value < ((Symbol) obj).st_value ? -1 : this.st_value == ((Symbol) obj).st_value ? 0 : 1;
  }

  /**
   * Returns the binding value.
   * 
   * @return a binding value.
   */
  public int getBind()
  {
    return (this.st_info >> 4) & 0xf;
  }

  /**
   * Returns the symbol's type and binding attributes.
   * 
   * @return the raw symbol information.
   * @see #getBind()
   * @see #getType()
   */
  public short getInfo()
  {
    return this.st_info;
  }

  /**
   * Returns the name of this symbol.
   * 
   * @return a symbol name, never <code>null</code>.
   */
  public String getName()
  {
    if (this.name == null)
    {
      this.name = this.sym_section.getStringByIndex((int) this.st_name);
    }
    return this.name;
  }

  /**
   * Returns an index into the object file's symbol string table, which holds
   * the character representations of the symbol names.
   * 
   * @return a name index, >= 0.
   */
  public long getNameIndex()
  {
    return this.st_name;
  }

  /**
   * Returns 0 and has no defined meaning.
   * 
   * @return always 0.
   */
  public short getOther()
  {
    return this.st_other;
  }

  /**
   * @return
   */
  public Section getSection()
  {
    return this.sym_section;
  }

  /**
   * Every symbol table entry is "defined" in relation to some section; this
   * method returns the relevant section header table index.
   * 
   * @return a section header table index, >= 0.
   */
  public short getSectionHeaderTableIndex()
  {
    return this.st_shndx;
  }

  /**
   * Returns the size of this symbol.
   * <p>
   * Many symbols have associated sizes. For example, a data object's size is
   * the number of bytes contained in the object. This member holds 0 if the
   * symbol has no size or an unknown size.
   * </p>
   * 
   * @return a symbol size.
   */
  public long getSize()
  {
    return this.st_size;
  }

  /**
   * Returns the type of this symbol.
   * 
   * @return a symbol type.
   */
  public int getType()
  {
    return this.st_info & 0xf;
  }

  /**
   * Returns the value of the associated symbol. Depending on the context, this
   * may be an absolute value, an address, and so on; details appear below.
   * 
   * @return a value, >= 0.
   */
  public long getValue()
  {
    return this.st_value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return getName();
  }
}
