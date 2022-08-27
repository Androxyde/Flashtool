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


/**
 * An executable or shared object file's program header table is an array of
 * structures, each describing a segment or other information the system needs
 * to prepare the program for execution. An object file segment contains one or
 * more sections. Program headers are meaningful only for executable and shared
 * object files.
 */
public class ProgramHeader
{
  // CONSTANTS

  public static final int PT_NULL = 0;
  public static final int PT_LOAD = 1;
  public static final int PT_DYNAMIC = 2;
  public static final int PT_INTERP = 3;
  public static final int PT_NOTE = 4;
  public static final int PT_SHLIB = 5;
  public static final int PT_PHDR = 6;

  public static final int PT_GNU_EH_FRAME = 0x6474e550;
  public static final int PT_GNU_STACK = 0x6474e551;
  public static final int PT_GNU_RELRO = 0x6474e552;

  public static final int PT_PAX_FLAGS = 0x65041580;

  public static final int PT_SUNWBSS = 0x6ffffffa;
  public static final int PT_SUNWSTACK = 0x6ffffffb;

  public static final int PT_ARM_ARCHEXT = 0x70000000;
  public static final int PT_ARM_UNWIND = 0x70000001;

  public static final int PF_X = (1 << 0); /* Segment is executable */
  public static final int PF_W = (1 << 1); /* Segment is writable */
  public static final int PF_R = (1 << 2); /* Segment is readable */
  public static final int PF_PAGEEXEC = (1 << 4); /* Enable PAGEEXEC */
  public static final int PF_NOPAGEEXEC = (1 << 5); /* Disable PAGEEXEC */
  public static final int PF_SEGMEXEC = (1 << 6); /* Enable SEGMEXEC */
  public static final int PF_NOSEGMEXEC = (1 << 7); /* Disable SEGMEXEC */
  public static final int PF_MPROTECT = (1 << 8); /* Enable MPROTECT */
  public static final int PF_NOMPROTECT = (1 << 9); /* Disable MPROTECT */
  public static final int PF_RANDEXEC = (1 << 10); /* Enable RANDEXEC */
  public static final int PF_NORANDEXEC = (1 << 11); /* Disable RANDEXEC */
  public static final int PF_EMUTRAMP = (1 << 12); /* Enable EMUTRAMP */
  public static final int PF_NOEMUTRAMP = (1 << 13); /* Disable EMUTRAMP */
  public static final int PF_RANDMMAP = (1 << 14); /* Enable RANDMMAP */
  public static final int PF_NORANDMMAP = (1 << 15); /* Disable RANDMMAP */
  public static final int PF_MASKOS = 0x0ff00000; /* OS-specific */
  public static final int PF_MASKPROC = 0xf0000000; /* Processor-specific */

  /* NOTE: 64 bit and 32 bit ELF have different order and size of elements */
  private int p_type;
  private long p_offset;
  private long p_vaddr;
  private long p_paddr;
  private long p_filesz;
  private long p_memsz;
  private int p_flags;
  private long p_align;

  // CONSTRUCTORS

  /**
   * Creates a new PHdr instance.
   */
  public ProgramHeader()
  {
    super();
  }

  // METHODS

  /**
   * @param aHeader
   * @param aFile
   * @return
   */
  static ProgramHeader[] createHeaders(ElfHeader aHeader, ERandomAccessFile aFile) throws IOException
  {
    if (!aHeader.hasProgramHeaderTable())
    {
      return new ProgramHeader[0];
    }

    final int length = aHeader.getProgramHeaderEntryCount();
    final int phentsize = aHeader.getProgramHeaderEntrySize();

    final ProgramHeader phdrs[] = new ProgramHeader[length];
    long offset = aHeader.getProgramHeaderFileOffset();

    for (int i = 0; i < length; i++, offset += phentsize)
    {
      aFile.seek(offset);

      phdrs[i] = createHeader(aHeader, aFile);
    }

    return phdrs;
  }

  /**
   * @param aHeader
   * @param aFile
   * @return
   * @throws IOException
   */
  private static ProgramHeader createHeader(ElfHeader aHeader, ERandomAccessFile aFile) throws IOException
  {
    ProgramHeader result = new ProgramHeader();

    if (aHeader.is32bit())
    {
      final byte[] addrArray = new byte[Elf.ELF32_ADDR_SIZE];

      result.p_type = aFile.readIntE();
      result.p_offset = aFile.readIntE();
      aFile.readFullyE(addrArray);
      result.p_vaddr = Elf.createAddr32(addrArray);
      aFile.readFullyE(addrArray);
      result.p_paddr = Elf.createAddr32(addrArray);
      result.p_filesz = aFile.readIntE();
      result.p_memsz = aFile.readIntE();
      result.p_flags = aFile.readIntE();
      result.p_align = aFile.readIntE();
    }
    else if (aHeader.is64bit())
    {
      final byte[] addrArray = new byte[Elf.ELF64_ADDR_SIZE];

      result.p_type = aFile.readIntE();
      result.p_flags = aFile.readIntE();
      result.p_offset = Elf.readUnsignedLong(aFile);
      aFile.readFullyE(addrArray);
      result.p_vaddr = Elf.createAddr64(addrArray);
      aFile.readFullyE(addrArray);
      result.p_paddr = Elf.createAddr64(addrArray);
      result.p_filesz = Elf.readUnsignedLong(aFile);
      result.p_memsz = Elf.readUnsignedLong(aFile);
      result.p_align = Elf.readUnsignedLong(aFile);
    }
    else
    {
      throw new IOException("Unknown ELF class!");
    }

    return result;
  }

  /**
   * Loadable process segments must have congruent values for p_vaddr and
   * p_offset, modulo the page size. This member gives the value to which the
   * segments are aligned in memory and in the file. Values 0 and 1 mean that no
   * alignment is required. Otherwise, p_align should be a positive, integral
   * power of 2, and p_addr should equal p_offset, modulo p_align.
   * 
   * @return the alignment
   */
  public long getAlignment()
  {
    return this.p_align;
  }

  /**
   * Returns the offset from the beginning of the file at which the first byte
   * of the segment resides.
   * 
   * @return the file offset (in bytes), >= 0.
   */
  public long getFileOffset()
  {
    return this.p_offset;
  }

  /**
   * Returns the number of bytes in the file image of the segment; it may be
   * zero.
   * 
   * @return the file segment size, >= 0.
   */
  public long getFileSize()
  {
    return this.p_filesz;
  }

  /**
   * Returns the flags relevant to the segment.
   * 
   * @return the flags.
   */
  public long getFlags()
  {
    return this.p_flags;
  }

  /**
   * Returns the number of bytes in the memory image of the segment; it may be
   * zero.
   * 
   * @return the memory size of this segment, >= 0.
   */
  public long getMemorySize()
  {
    return this.p_memsz;
  }

  /**
   * On systems for which physical addressing is relevant, this member is
   * reserved for the segment's physical address. This member requires operating
   * system specific information.
   * 
   * @return the p_paddr
   */
  public long getPhysicalAddress()
  {
    return this.p_paddr;
  }

  /**
   * Returns what kind of segment this array element describes or how to
   * interpret the array element's information.
   * 
   * @return the type of this header.
   */
  public long getType()
  {
    return this.p_type;
  }

  /**
   * Returns a string name for the type of this program header.
   * 
   * @return a type name, never <code>null</code>.
   */
  public String getTypeName()
  {
    switch ((int) getType())
    {
      case PT_NULL:
        return "PT_NULL (Unused)";
      case PT_LOAD:
        return "PT_LOAD (Loadable segment)";
      case PT_DYNAMIC:
        return "PT_DYNAMIC (Dynamic linking information)";
      case PT_INTERP:
        return "PT_INTERP (Interpreter path)";
      case PT_NOTE:
        return "PT_NOTE (Location, size and auxiliary information)";
      case PT_SHLIB:
        return "PT_SHLIB (Reserved)";
      case PT_PHDR:
        return "PT_PHDR (Location and size of program header)";
      case PT_GNU_EH_FRAME:
        return "PT_GNU_EH_FRAME (GNU: EH frame)";
      case PT_GNU_STACK:
        return "PT_GNU_STACK (GNU: Indicates stack executability)";
      case PT_GNU_RELRO:
        return "PT_GNU_RELRO (GNU: Read-only after relocation)";
      case PT_PAX_FLAGS:
        return "PT_PAX_FLAGS (PaX: Indicates PaX flag markings)";
      case PT_SUNWBSS:
        return "PT_SUNWBSS (Sun: Sun Specific segment)";
      case PT_SUNWSTACK:
        return "PT_SUNWSTACK (Sun: Stack segment)";
      case PT_ARM_ARCHEXT:
        return "PT_ARM_ARCHEXT (ARM: Platform architecture compatibility information)";
      case PT_ARM_UNWIND:
        return "PT_ARM_UNWIND (ARM: Exception unwind tables)";
      default:
        return String.format("Unknown/processor specific (0x%x)", getType());
    }
  }

  /**
   * Returns the virtual address at which the first byte of the segment resides
   * in memory.
   * 
   * @return the virtual address, >= 0.
   */
  public long getVirtualAddress()
  {
    return this.p_vaddr;
  }
}
