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
package org.flashtool.binutils.elf;


import java.io.*;
import java.util.*;


/**
 * An object file's section header table lets one locate all the file's
 * sections. The section header table is an array of {@link Section}s. A section
 * header table index (see {@link #getHeaderTableIndexLink()}) is a subscript
 * into this array.
 */
public class Section
{
  // CONSTANTS

  /* sh_type */
  public final static int SHT_NULL = 0;
  public final static int SHT_PROGBITS = 1;
  public final static int SHT_SYMTAB = 2;
  public final static int SHT_STRTAB = 3;
  public final static int SHT_RELA = 4;
  public final static int SHT_HASH = 5;
  public final static int SHT_DYNAMIC = 6;
  public final static int SHT_NOTE = 7;
  public final static int SHT_NOBITS = 8;
  public final static int SHT_REL = 9;
  public final static int SHT_SHLIB = 10;
  public final static int SHT_DYNSYM = 11;

  public final static int SHT_LOPROC = 0x70000000;

  /* sh_flags */
  public final static int SHF_WRITE = 1;
  public final static int SHF_ALLOC = 2;
  public final static int SHF_EXECINTR = 4;

  // VARIABLES

  private int sh_name;
  private int sh_type;
  private long sh_flags;
  private long sh_addr;
  private long sh_offset;
  private long sh_size;
  private long sh_link;
  private long sh_info;
  private long sh_addralign;
  private long sh_entsize;

  private final Elf elf;
  private String name;

  // CONSTRUCTORS

  /**
   * Creates a new Section instance.
   * 
   * @param elf
   */
  private Section(Elf elf)
  {
    this.elf = elf;
  }

  // METHODS

  /**
   * @param aHeader
   * @param aFile
   * @return
   */
  static Section[] create(Elf aElf, ElfHeader aHeader, ERandomAccessFile aFile) throws IOException
  {
    if (!aHeader.hasSectionHeaderTable())
    {
      return new Section[0];
    }

    final int length = aHeader.getSectionHeaderEntryCount();
    final int shentsize = aHeader.getSectionHeaderEntrySize();

    final Section[] sections = new Section[length];
    long offset = aHeader.getSectionHeaderFileOffset();

    for (int i = 0; i < length; i++, offset += shentsize)
    {
      aFile.seek(offset);

      sections[i] = createSection(aElf, aHeader, aFile);
    }

    return sections;
  }

  /**
   * @param aElf
   * @param aHeader
   * @param aFile
   * @return
   * @throws IOException
   */
  private static Section createSection(Elf aElf, ElfHeader aHeader, ERandomAccessFile aFile) throws IOException
  {
    Section section = new Section(aElf);

    section.sh_name = aFile.readIntE();
    section.sh_type = aFile.readIntE();

    if (aHeader.is32bit())
    {
      final byte[] addrArray = new byte[Elf.ELF32_ADDR_SIZE];
      section.sh_flags = aFile.readIntE();
      aFile.readFullyE(addrArray);
      section.sh_addr = Elf.createAddr32(addrArray);
      section.sh_offset = aFile.readIntE();
      section.sh_size = aFile.readIntE();
    }
    else if (aHeader.is64bit())
    {
      final byte[] addrArray = new byte[Elf.ELF64_ADDR_SIZE];
      section.sh_flags = aFile.readLongE();
      aFile.readFullyE(addrArray);
      section.sh_addr = Elf.createAddr64(addrArray);
      section.sh_offset = Elf.readUnsignedLong(aFile);
      section.sh_size = Elf.readUnsignedLong(aFile);
    }
    else
    {
      throw new IOException("Unknown ELF class!");
    }

    section.sh_link = aFile.readIntE();
    section.sh_info = aFile.readIntE();

    if (aHeader.is32bit())
    {
      section.sh_addralign = aFile.readIntE();
      section.sh_entsize = aFile.readIntE();
    }
    else if (aHeader.is64bit())
    {
      section.sh_addralign = aFile.readLongE();
      section.sh_entsize = Elf.readUnsignedLong(aFile);
    }
    else
    {
      throw new IOException("Unknown ELF class!");
    }

    return section;
  }

  /**
   * Returns whether this section holds a table of fixed-size entries.
   * 
   * @return <code>true</code> if this section holds a table, <code>false</code>
   *         otherwise.
   */
  public boolean containsTable()
  {
    return this.sh_entsize != 0;
  }

  /**
   * If the section will appear in the memory image of a process, this method
   * returns the address at which the section's first byte should reside.
   * Otherwise, the member contains 0.
   * 
   * @return the address in the memory image, >= 0.
   */
  public long getAddress()
  {
    return this.sh_addr;
  }

  /**
   * Some sections have address alignment constraints. For example, if a section
   * holds a double word, the system must ensure double word alignment for the
   * entire section. That is, the value of sh_addr must be congruent to 0,
   * modulo the value of sh_addralign. Currently, only 0 and positive integral
   * powers of two are allowed. Values 0 and 1 mean the section has no alignment
   * constraints.
   * 
   * @return the address alignment constraints, >= 0.
   */
  public long getAddressAlignment()
  {
    return this.sh_addralign;
  }

  /**
   * @return
   */
  public Elf getElf()
  {
    return this.elf;
  }

  /**
   * Returns the byte offset from the beginning of the file to the first byte in
   * the section. One section type, SHT_NOBITS, occupies no space in the file,
   * and its sh_offset member locates the conceptual placement in the file.
   * 
   * @return the file offset to this section (in bytes).
   */
  public long getFileOffset()
  {
    return this.sh_offset;
  }

  /**
   * Returns 1-bit flags that describe miscellaneous attributes.
   * 
   * @return the flags, >= 0.
   */
  public long getFlags()
  {
    return this.sh_flags;
  }

  /**
   * Returns a section header table index link, whose interpretation depends on
   * the section type.
   * 
   * @return the index link, >= 0.
   */
  public long getHeaderTableIndexLink()
  {
    return this.sh_link;
  }

  /**
   * Returns the extra information, whose interpretation depends on the section
   * type.
   * 
   * @return the extra information.
   */
  public long getInfo()
  {
    return this.sh_info;
  }

  /**
   * Returns the name of this section.
   * 
   * @return a name, never <code>null</code>.
   */
  public String getName()
  {
    if (this.name == null)
    {
      try
      {
        final byte[] stringTable = this.elf.getStringTable();

        int length = 0;
        int offset = getNameIndex();

        if (offset > stringTable.length)
        {
          this.name = "";
        }
        else
        {
          while (stringTable[offset + length] != 0)
          {
            length++;
          }

          this.name = new String(stringTable, offset, length);
        }
      }
      catch (IOException exception)
      {
        this.name = "";
      }
    }
    return this.name;
  }

  /**
   * Returns the name of the section. Its value is an index into the section
   * header string table section, giving the location of a null-terminated
   * string.
   * 
   * @return the index of this section's name in the string table, >= 0.
   */
  public int getNameIndex()
  {
    return this.sh_name;
  }

  /**
   * Some sections hold a table of fixed-size entries, such as a symbol table.
   * For such a section, this member gives the size in bytes of each entry. The
   * member contains 0 if the section does not hold a table of fixed-size
   * entries.
   * 
   * @return the sh_entsize
   */
  public long getSectionTableEntrySize()
  {
    return this.sh_entsize;
  }

  /**
   * Returns the section's size in bytes. Unless the section type is SHT_NOBITS,
   * the section occupies sh_size bytes in the file. A section of type
   * SHT_NOBITS may have a non-zero size, but it occupies no space in the file.
   * 
   * @return the size (in bytes), >= 0.
   */
  public long getSize()
  {
    return this.sh_size;
  }

  /**
   * If this section contains a table, this method returns the number of entries
   * in this table.
   * 
   * @return a table entry count, >= 1.
   */
  public int getTableEntryCount()
  {
    if (!containsTable())
    {
      return 1;
    }

    return (int) (getSize() / getSectionTableEntrySize());
  }

  /**
   * Returns the category denoting the section's contents and semantics.
   * 
   * @return the section type value.
   */
  public int getType()
  {
    return this.sh_type;
  }

  /**
   * Loads the symbols for this section.
   * 
   * @param aHeader
   *          the ELF header to use;
   * @param aFile
   *          the ELF-file to read the symbols from.
   * @return an array of symbols, never <code>null</code>.
   * @throws IOException
   *           in case of I/O problems.
   */
  public Symbol[] loadSymbols(ElfHeader aHeader, ERandomAccessFile aFile) throws IOException
  {
    int numSyms = 1;
    if (containsTable())
    {
      numSyms = getTableEntryCount();
    }

    final ArrayList<Symbol> symList = new ArrayList<Symbol>(numSyms);

    long entsize = getSectionTableEntrySize();
    long offset = getFileOffset();

    for (int c = 0; c < numSyms; offset += entsize, c++)
    {
      aFile.seek(offset);

      Symbol symbol = Symbol.create(aHeader, this, aFile);
      if (symbol.getInfo() == 0)
      {
        continue;
      }

      symList.add(symbol);
    }

    final Symbol[] results = symList.toArray(new Symbol[symList.size()]);
    Arrays.sort(results);
    return results;
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
   * @param aIndex
   * @return
   */
  final String getStringByIndex(int aIndex)
  {
    try
    {
      final Section sections[] = this.elf.getSections();
      final Section symstr = sections[(int) this.sh_link];
      return this.elf.getStringFromSection(symstr, aIndex);
    }
    catch (IOException exception)
    {
      return "";
    }
  }
}
