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
 * Denotes the header of an ELF file.
 */
public class ElfHeader
{
  // CONSTANTS

  /* e_ident offsets */
  /** Magic number: 0x7f */
  private final static int EI_MAG0 = 0;
  /** Magic number: 'E' */
  private final static int EI_MAG1 = 1;
  /** Magic number: 'L' */
  private final static int EI_MAG2 = 2;
  /** Magic number: 'F' */
  private final static int EI_MAG3 = 3;
  /** The file's class, or capacity. */
  private final static int EI_CLASS = 4;
  /** Data encoding */
  private final static int EI_DATA = 5;
  /** ELF header version number. */
  @SuppressWarnings("unused")
  private final static int EI_VERSION = 6;
  /** the unused bytes in e_ident. These bytes are reserved and set to zero. */
  @SuppressWarnings("unused")
  private final static int EI_PAD = 7;
  /** */
  private final static int EI_NDENT = 16;

  /* e_ident[EI_CLASS] */
  private final static int ELFCLASSNONE = 0;
  private final static int ELFCLASS32 = 1;
  private final static int ELFCLASS64 = 2;

  /* e_ident[EI_DATA] */
  @SuppressWarnings("unused")
  private final static int ELFDATANONE = 0;
  private final static int ELFDATA2LSB = 1;
  private final static int ELFDATA2MSB = 2;

  /* values of e_type */
  public static final int ET_NONE = 0;
  public static final int ET_REL = 1;
  public static final int ET_EXEC = 2;
  public static final int ET_DYN = 3;
  public static final int ET_CORE = 4;
  public static final int ET_NUM = 5;
  public static final int ET_LOOS = 0xfe00;
  public static final int ET_HIOS = 0xfeff;
  public static final int ET_LOPROC = 0xff00;
  public static final int ET_HIPROC = 0xffff;

  /* values of e_machine */
  public static final int EM_NONE = 0;
  public static final int EM_M32 = 1;
  public static final int EM_SPARC = 2;
  public static final int EM_386 = 3;
  public static final int EM_68K = 4;
  public static final int EM_88K = 5;
  public static final int EM_486 = 6;
  public static final int EM_860 = 7;
  public static final int EM_MIPS = 8;
  public static final int EM_MIPS_RS3_LE = 10;
  public static final int EM_RS6000 = 11;
  public static final int EM_PARISC = 15;
  public static final int EM_nCUBE = 16;
  public static final int EM_VPP550 = 17;
  public static final int EM_SPARC32PLUS = 18;
  public static final int EM_960 = 19;
  public static final int EM_PPC = 20;
  public static final int EM_PPC64 = 21;
  public static final int EM_S390 = 22;
  public static final int EM_V800 = 36; /* NEC V800 series */
  public static final int EM_FR20 = 37; /* Fujitsu FR20 */
  public static final int EM_RH32 = 38; /* TRW RH-32 */
  public static final int EM_RCE = 39; /* Motorola RCE */
  public static final int EM_ARM = 40;
  public static final int EM_FAKE_ALPHA = 41; /* Digital Alpha */
  public static final int EM_SH = 42;
  public static final int EM_SPARCV9 = 43;
  public static final int EM_TRICORE = 44;
  public static final int EM_ARC = 45; /* Argonaut RISC Core */
  public static final int EM_H8_300 = 46;
  public static final int EM_H8_300H = 47;
  public static final int EM_H8S = 48; /* Hitachi H8S */
  public static final int EM_H8_500 = 49; /* Hitachi H8/500 */
  public static final int EM_IA_64 = 50;
  public static final int EM_MIPS_X = 51; /* Stanford MIPS-X */
  public static final int EM_COLDFIRE = 52;
  public static final int EM_68HC12 = 53; /* Motorola M68HC12 */
  public static final int EM_MMA = 54; /* Fujitsu MMA Multimedia Accelerator */
  public static final int EM_PCP = 55; /* Siemens PCP */
  public static final int EM_NCPU = 56; /* Sony nCPU embeeded RISC */
  public static final int EM_NDR1 = 57; /* Denso NDR1 microprocessor */
  public static final int EM_STARCORE = 58;
  public static final int EM_ME16 = 59; /* Toyota ME16 processor */
  public static final int EM_ST100 = 60; /* STMicroelectronic ST100 processor */
  public static final int EM_TINYJ = 61; /* Advanced Logic Corp. Tinyj emb.fam */
  public static final int EM_X86_64 = 62;
  public static final int EM_PDSP = 63; /* Sony DSP Processor */

  public static final int EM_FX66 = 66; /* Siemens FX66 microcontroller */
  public static final int EM_ST9PLUS = 67; /* STMicroelectronics ST9+ 8/16 mc */
  public static final int EM_ST7 = 68; /* STmicroelectronics ST7 8 bit mc */
  public static final int EM_68HC16 = 69; /* Motorola MC68HC16 microcontroller */
  public static final int EM_68HC11 = 70; /* Motorola MC68HC11 microcontroller */
  /* Freescale MC68HC08 Microcontroller */
  public static final int EM_68HC08 = 71;
  /* Motorola MC68HC05 microcontroller */
  public static final int EM_68HC05 = 72;
  public static final int EM_SVX = 73; /* Silicon Graphics SVx */
  public static final int EM_ST19 = 74; /* STMicroelectronics ST19 8 bit mc */
  public static final int EM_VAX = 75; /* Digital VAX */
  /* Axis Communications 32-bit embedded processor */
  public static final int EM_CRIS = 76;
  /* Infineon Technologies 32-bit embedded processor */
  public static final int EM_JAVELIN = 77;
  public static final int EM_FIREPATH = 78; /* Element 14 64-bit DSP Processor */
  public static final int EM_ZSP = 79; /* LSI Logic 16-bit DSP Processor */
  /* Donald Knuth's educational 64-bit processor */
  public static final int EM_MMIX = 80;
  /* Harvard University machine-independent object files */
  public static final int EM_HUANY = 81;
  public static final int EM_PRISM = 82; /* SiTera Prism */
  public static final int EM_AVR = 83;
  /* Fujitsu FR30 */
  public static final int EM_FR30 = 84;
  public static final int EM_D10V = 85; /* Mitsubishi D10V */
  public static final int EM_D30V = 86; /* Mitsubishi D30V */
  public static final int EM_V850 = 87;
  public static final int EM_M32R = 88;
  public static final int EM_MN10300 = 89;
  public static final int EM_MN10200 = 90;
  public static final int EM_PJ = 91; /* picoJava */
  /* OpenRISC 32-bit embedded processor */
  public static final int EM_OPENRISC = 92;
  public static final int EM_ARC_A5 = 93; /* ARC Cores Tangent-A5 */
  public static final int EM_XTENSA = 94; /* Tensilica Xtensa Architecture */
  public static final int EM_MSP430 = 105;
  public static final int EM_BLACKFIN = 106;
  public static final int EM_EXCESS = 111;
  public static final int EM_NIOSII = 113;
  public static final int EM_C166 = 116;
  public static final int EM_M16C = 117;
  /* Freescale RS08 embedded processor */
  public static final int EM_RS08 = 132;

  public static final int EM_MMDSP = 160;
  public static final int EM_NIOS = 0xFEBB;
  public static final int EM_CYGNUS_POWERPC = 0x9025;
  public static final int EM_CYGNUS_M32R = 0x9041;
  public static final int EM_CYGNUS_V850 = 0x9080;
  public static final int EM_CYGNUS_MN10200 = 0xdead;
  public static final int EM_CYGNUS_MN10300 = 0xbeef;
  public static final int EM_CYGNUS_FR30 = 0x3330;
  public static final int EM_XSTORMY16 = 0xad45;
  public static final int EM_CYGNUS_FRV = 0x5441;
  public static final int EM_IQ2000 = 0xFEBA;
  public static final int EM_XILINX_MICROBLAZE = 0xbaab;
  public static final int EM_SDMA = 0xcafe;
  public static final int EM_CRADLE = 0x4d55;

  // VARIABLES

  private final byte e_ident[] = new byte[EI_NDENT];
  private final int e_type;
  private final int e_machine;
  private final long e_version;
  private final long e_entry;
  private final long e_phoff;
  private final long e_shoff;
  private final long e_flags;
  private final short e_ehsize;
  private final short e_phentsize;
  private final short e_phnum;
  private final short e_shentsize;
  private final short e_shnum;
  private final short e_shstrndx;

  // CONSTRUCTORS

  /**
   * Creates a new {@link ElfHeader} instance.
   * 
   * @param bytes
   *          the binary header to convert to a header.
   * @throws IOException
   *           in case the given bytes did not look like a valid ELF header.
   */
  protected ElfHeader(final byte[] bytes) throws IOException
  {
    if (bytes.length <= this.e_ident.length)
    {
      throw new EOFException("Not an ELF-file!");
    }
    System.arraycopy(bytes, 0, this.e_ident, 0, this.e_ident.length);

    if (!isElfHeader(this.e_ident))
    {
      throw new IOException("Not an ELF file!");
    }

    final boolean isle = (this.e_ident[ElfHeader.EI_DATA] == ElfHeader.ELFDATA2LSB);

    int offset = this.e_ident.length;
    this.e_type = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_machine = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_version = makeInt(bytes, offset, isle);
    offset += 4;

    switch (this.e_ident[ElfHeader.EI_CLASS])
    {
      case ElfHeader.ELFCLASS32:
      {
        final byte[] addrArray = new byte[Elf.ELF32_ADDR_SIZE];
        System.arraycopy(bytes, offset, addrArray, 0, Elf.ELF32_ADDR_SIZE);
        offset += Elf.ELF32_ADDR_SIZE;
        this.e_entry = Elf.createAddr32(addrArray);
        this.e_phoff = makeInt(bytes, offset, isle);
        offset += Elf.ELF32_OFF_SIZE;
        this.e_shoff = makeInt(bytes, offset, isle);
        offset += Elf.ELF32_OFF_SIZE;
      }
        break;
      case ElfHeader.ELFCLASS64:
      {
        final byte[] addrArray = new byte[Elf.ELF64_ADDR_SIZE];
        System.arraycopy(bytes, offset, addrArray, 0, Elf.ELF64_ADDR_SIZE);
        offset += Elf.ELF64_ADDR_SIZE;
        this.e_entry = Elf.createAddr64(addrArray);
        this.e_phoff = makeUnsignedLong(bytes, offset, isle);
        offset += Elf.ELF64_OFF_SIZE;
        this.e_shoff = makeUnsignedLong(bytes, offset, isle);
        offset += Elf.ELF64_OFF_SIZE;
      }
        break;
      case ElfHeader.ELFCLASSNONE:
      default:
        throw new IOException("Unknown ELF class " + this.e_ident[ElfHeader.EI_CLASS]);
    }

    this.e_flags = makeInt(bytes, offset, isle);
    offset += 4;
    this.e_ehsize = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_phentsize = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_phnum = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_shentsize = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_shnum = makeShort(bytes, offset, isle);
    offset += 2;
    this.e_shstrndx = makeShort(bytes, offset, isle);
    offset += 2;
  }

  /**
   * @param efile
   * @throws IOException
   */
  protected ElfHeader(final ERandomAccessFile efile) throws IOException
  {
    efile.seek(0);
    efile.readFully(this.e_ident);

    if (!isElfHeader(this.e_ident))
    {
      throw new IOException("Not an ELF file!");
    }

    efile.setEndiannes(this.e_ident[ElfHeader.EI_DATA] == ElfHeader.ELFDATA2LSB);

    this.e_type = efile.readShortE();
    this.e_machine = efile.readShortE();
    this.e_version = efile.readIntE();

    switch (this.e_ident[ElfHeader.EI_CLASS])
    {
      case ElfHeader.ELFCLASS32:
      {
        final byte[] addrArray = new byte[Elf.ELF32_ADDR_SIZE];
        efile.readFullyE(addrArray);
        this.e_entry = Elf.createAddr32(addrArray);
        this.e_phoff = efile.readIntE();
        this.e_shoff = efile.readIntE();
      }
        break;
      case ElfHeader.ELFCLASS64:
      {
        final byte[] addrArray = new byte[Elf.ELF64_ADDR_SIZE];
        efile.readFullyE(addrArray);
        this.e_entry = Elf.createAddr64(addrArray);
        this.e_phoff = Elf.readUnsignedLong(efile);
        this.e_shoff = Elf.readUnsignedLong(efile);
      }
        break;
      case ElfHeader.ELFCLASSNONE:
      default:
        throw new IOException("Unknown ELF class " + this.e_ident[ElfHeader.EI_CLASS]);
    }

    this.e_flags = efile.readIntE();
    this.e_ehsize = efile.readShortE();
    this.e_phentsize = efile.readShortE();
    this.e_phnum = efile.readShortE();
    this.e_shentsize = efile.readShortE();
    this.e_shnum = efile.readShortE();
    this.e_shstrndx = efile.readShortE();
  }

  // METHODS

  /**
   * Helper method to determine whether the first few bytes of the given array
   * correspond to the "magic" string of an ELF object.
   * 
   * @param e_ident
   *          the byte array to test, cannot be <code>null</code>.
   * @return <code>true</code> if the first few bytes resemble the ELF "magic"
   *         string, <code>false</code> otherwise.
   */
  static boolean isElfHeader(final byte[] e_ident)
  {
    if ((e_ident.length < 4) || (e_ident[ElfHeader.EI_MAG0] != 0x7f) || (e_ident[ElfHeader.EI_MAG1] != 'E')
        || (e_ident[ElfHeader.EI_MAG2] != 'L') || (e_ident[ElfHeader.EI_MAG3] != 'F'))
    {
      return false;
    }
    return true;
  }

  /**
   * Returns the entry point (Elf32_Addr).
   * 
   * @return the entry point address, >= 0.
   */
  public long getEntryPoint()
  {
    return this.e_entry;
  }

  /**
   * Returns the processor flags (Elf32_Word).
   * 
   * @return the processor flags.
   */
  public long getFlags()
  {
    return this.e_flags;
  }

  /**
   * Returns the machine type, or required architecture for an individual file.
   * 
   * @return the machine type.
   */
  public int getMachineType()
  {
    return this.e_machine & 0xFFFF;
  }

  /**
   * Returns the number of entries in the program header table. Thus the
   * product of {@link #getProgramHeaderEntrySize()} and
   * {@link #getProgramHeaderEntryCount()} gives the table's size in bytes. If a
   * file has no program header table, this method returns zero.
   * 
   * @return the number of entries in the program header table, >= 0.
   */
  public int getProgramHeaderEntryCount()
  {
    return this.e_phnum & 0xffff;
  }

  /**
   * Returns the size in bytes of one entry in the file's program header table;
   * all entries are the same size.
   * 
   * @return the program header entry size.
   */
  public int getProgramHeaderEntrySize()
  {
    return this.e_phentsize & 0xffff;
  }

  /**
   * Returns the program header table's file offset in bytes. If the file has no
   * program header table, this method returns zero.
   * 
   * @return the program header table file offset (in bytes), >= 0.
   */
  public long getProgramHeaderFileOffset()
  {
    return this.e_phoff;
  }

  /**
   * Returns the raw elf header.
   * 
   * @return the raw header, never <code>null</code>.
   */
  public byte[] getRawHeader()
  {
    return this.e_ident;
  }

  /**
   * Returns the number of entries in the section header table. Thus the product
   * of e_shentsize and e_shnum gives the section header table's size in bytes.
   * If a file has no section header table, e_shnum holds the value zero.
   * 
   * @return the e_shnum
   */
  public int getSectionHeaderEntryCount()
  {
    return this.e_shnum & 0xffff;
  }

  /**
   * Returns a section header's size in bytes. A section header is one entry
   * in the section header table; all entries are the same size.
   * 
   * @return the section header's size (in bytes), >= 0.
   */
  public int getSectionHeaderEntrySize()
  {
    return this.e_shentsize & 0xffff;
  }

  /**
   * Returns the section header table's file offset in bytes. If the file has no
   * section header table, this member holds zero.
   * 
   * @return the section header table's file offset (in bytes), >= 0.
   */
  public long getSectionHeaderFileOffset()
  {
    return this.e_shoff;
  }

  /**
   * Returns the size of the header.
   * 
   * @return a header size, >= 0.
   */
  public short getSize()
  {
    return this.e_ehsize;
  }

  /**
   * Returns the section header table index of the entry associated with the
   * section name string table. If the file has no section name string table,
   * this member holds the value SHN_UNDEF. See "Sections" and "String Table"
   * below for more information.
   * 
   * @return the e_shstrndx
   */
  public int getStringTableSectionIndex()
  {
    return this.e_shstrndx & 0xFFFF;
  }

  /**
   * Returns the object file type.
   * 
   * @return the file type, >= 0.
   */
  public int getType()
  {
    return this.e_type;
  }

  /**
   * Returns the object file version.
   * 
   * @return the file version.
   */
  public long getVersion()
  {
    return this.e_version;
  }

  /**
   * Returns whether or not the program header table contains entries.
   * 
   * @return <code>true</code> if there are entries in the program header table,
   *         <code>false</code> otherwise.
   * @see #getProgramHeaderEntryCount()
   */
  public boolean hasProgramHeaderTable()
  {
    return this.e_phoff > 0;
  }

  /**
   * Returns whether or not the program header table contains entries.
   * 
   * @return <code>true</code> if there are entries in the program header table,
   *         <code>false</code> otherwise.
   * @see #getProgramHeaderEntryCount()
   */
  public boolean hasSectionHeaderTable()
  {
    return this.e_shoff > 0;
  }

  /**
   * Returns whether this ELF-file represents a 32-bit file.
   * 
   * @return <code>true</code> if the ELF-file is for 32-bit platforms,
   *         <code>false</code> otherwise.
   */
  public boolean is32bit()
  {
    return this.e_ident[EI_CLASS] == ELFCLASS32;
  }

  /**
   * Returns whether this ELF-file represents a 32-bit file.
   * 
   * @return <code>true</code> if the ELF-file is for 32-bit platforms,
   *         <code>false</code> otherwise.
   */
  public boolean is64bit()
  {
    return this.e_ident[EI_CLASS] == ELFCLASS64;
  }

  /**
   * Returns whether the ELF file's data is in big endian format or not.
   * 
   * @return <code>true</code> if the ELF's data is expected to be big
   *         endian, <code>false</code> if it is expected to be little endian or
   *         unknown.
   */
  public boolean isBigEndian()
  {
    return this.e_ident[EI_DATA] == ELFDATA2MSB;
  }

  /**
   * Returns whether the ELF file's data is in little endian format or not.
   * 
   * @return <code>true</code> if the ELF's data is expected to be little
   *         endian, <code>false</code> if it is expected to be big endian or
   *         unknown.
   */
  public boolean isLittleEndian()
  {
    return this.e_ident[EI_DATA] == ELFDATA2LSB;
  }

  /**
   * @param val
   * @param offset
   * @param isle
   * @return
   * @throws IOException
   */
  private final long makeInt(final byte[] val, final int offset, final boolean isle) throws IOException
  {
    if (val.length < (offset + 4))
    {
      throw new IOException();
    }
    if (isle)
    {
      return ((val[offset + 3] << 24) + (val[offset + 2] << 16) + (val[offset + 1] << 8) + val[offset + 0]);
    }
    return ((val[offset + 0] << 24) + (val[offset + 1] << 16) + (val[offset + 2] << 8) + val[offset + 3]);
  }

  /**
   * @param val
   * @param offset
   * @param isle
   * @return
   * @throws IOException
   */
  private final long makeLong(final byte[] val, final int offset, final boolean isle) throws IOException
  {
    long result = 0;
    int shift = 0;
    if (isle)
    {
      for (int i = 7; i >= 0; i--)
      {
        shift = i * 8;
        result += (((long) val[offset + i]) << shift) & (0xffL << shift);
      }
    }
    else
    {
      for (int i = 0; i <= 7; i++)
      {
        shift = (7 - i) * 8;
        result += (((long) val[offset + i]) << shift) & (0xffL << shift);
      }
    }
    return result;
  }

  /**
   * @param val
   * @param offset
   * @param isle
   * @return
   * @throws IOException
   */
  private final short makeShort(final byte[] val, final int offset, final boolean isle) throws IOException
  {
    if (val.length < (offset + 2))
    {
      throw new IOException();
    }
    if (isle)
    {
      return (short) ((val[offset + 1] << 8) + val[offset + 0]);
    }
    return (short) ((val[offset + 0] << 8) + val[offset + 1]);
  }

  /**
   * @param val
   * @param offset
   * @param isle
   * @return
   * @throws IOException
   */
  private final long makeUnsignedLong(final byte[] val, final int offset, final boolean isle) throws IOException
  {
    final long result = makeLong(val, offset, isle);
    if (result < 0)
    {
      throw new IOException("Maximal file offset is " + Long.toHexString(Long.MAX_VALUE) + " given offset is "
          + Long.toHexString(result));
    }
    return result;
  }
}
