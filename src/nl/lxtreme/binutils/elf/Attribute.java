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


/**
 * Denotes some general attributes of the ELF file.
 */
public class Attribute
{
  // CONSTANTS

  public static final int ELF_TYPE_EXE = 1;
  public static final int ELF_TYPE_SHLIB = 2;
  public static final int ELF_TYPE_OBJ = 3;
  public static final int ELF_TYPE_CORE = 4;

  public static final int DEBUG_TYPE_NONE = 0;
  public static final int DEBUG_TYPE_STABS = 1;
  public static final int DEBUG_TYPE_DWARF = 2;

  // VARIABLES

  private String cpu;
  private int type;
  private int debugType;
  private boolean isle;
  private int width; // 32 or 64

  // CONSTRUCTORS

  /**
   * Creates a new Attribute instance.
   */
  private Attribute()
  {
    super();
  }

  // METHODS

  /**
   * Factory method for creating an {@link Attribute} instance.
   * 
   * @param aHeader
   *          the ELF-header to create the attribute for;
   * @param aSections
   *          the sections of the ELF-file.
   * @return a new {@link Attribute} instance, never <code>null</code>.
   */
  static Attribute create(final ElfHeader aHeader, final Section[] aSections)
  {
    final Attribute attrib = new Attribute();

    switch (aHeader.getType())
    {
      case ElfHeader.ET_CORE:
        attrib.type = Attribute.ELF_TYPE_CORE;
        break;
      case ElfHeader.ET_EXEC:
        attrib.type = Attribute.ELF_TYPE_EXE;
        break;
      case ElfHeader.ET_REL:
        attrib.type = Attribute.ELF_TYPE_OBJ;
        break;
      case ElfHeader.ET_DYN:
        attrib.type = Attribute.ELF_TYPE_SHLIB;
        break;
    }

    switch (aHeader.getMachineType())
    {
      case ElfHeader.EM_386:
      case ElfHeader.EM_486:
        attrib.cpu = "x86";
        break;
      case ElfHeader.EM_68K:
        attrib.cpu = "m68k";
        break;
      case ElfHeader.EM_PPC:
      case ElfHeader.EM_CYGNUS_POWERPC:
      case ElfHeader.EM_RS6000:
        attrib.cpu = "ppc";
        break;
      case ElfHeader.EM_PPC64:
        attrib.cpu = "ppc64";
        break;
      case ElfHeader.EM_SH:
        attrib.cpu = "sh";
        break;
      case ElfHeader.EM_ARM:
        attrib.cpu = "arm";
        break;
      case ElfHeader.EM_MIPS_RS3_LE:
      case ElfHeader.EM_MIPS:
        attrib.cpu = "mips";
        break;
      case ElfHeader.EM_SPARC32PLUS:
      case ElfHeader.EM_SPARC:
      case ElfHeader.EM_SPARCV9:
        attrib.cpu = "sparc";
        break;
      case ElfHeader.EM_H8_300:
      case ElfHeader.EM_H8_300H:
        attrib.cpu = "h8300";
        break;
      case ElfHeader.EM_V850:
      case ElfHeader.EM_CYGNUS_V850:
        attrib.cpu = "v850";
        break;
      case ElfHeader.EM_MN10300:
      case ElfHeader.EM_CYGNUS_MN10300:
        attrib.cpu = "mn10300";
        break;
      case ElfHeader.EM_MN10200:
      case ElfHeader.EM_CYGNUS_MN10200:
        attrib.cpu = "mn10200";
        break;
      case ElfHeader.EM_M32R:
        attrib.cpu = "m32r";
        break;
      case ElfHeader.EM_FR30:
      case ElfHeader.EM_CYGNUS_FR30:
        attrib.cpu = "fr30";
        break;
      case ElfHeader.EM_XSTORMY16:
        attrib.cpu = "xstormy16";
        break;
      case ElfHeader.EM_CYGNUS_FRV:
        attrib.cpu = "frv";
        break;
      case ElfHeader.EM_IQ2000:
        attrib.cpu = "iq2000";
        break;
      case ElfHeader.EM_EXCESS:
        attrib.cpu = "excess";
        break;
      case ElfHeader.EM_NIOSII:
        attrib.cpu = "alteranios2";
        break;
      case ElfHeader.EM_NIOS:
        attrib.cpu = "alteranios";
        break;
      case ElfHeader.EM_IA_64:
        attrib.cpu = "ia64";
        break;
      case ElfHeader.EM_COLDFIRE:
        attrib.cpu = "coldfire";
        break;
      case ElfHeader.EM_AVR:
        attrib.cpu = "avr";
        break;
      case ElfHeader.EM_MSP430:
        attrib.cpu = "msp430";
        break;
      case ElfHeader.EM_XTENSA:
        attrib.cpu = "xtensa";
        break;
      case ElfHeader.EM_ST100:
        attrib.cpu = "st100";
        break;
      case ElfHeader.EM_X86_64:
        attrib.cpu = "x86_64";
        break;
      case ElfHeader.EM_XILINX_MICROBLAZE:
        attrib.cpu = "microblaze";
        break;
      case ElfHeader.EM_C166:
        attrib.cpu = "c166";
        break;
      case ElfHeader.EM_TRICORE:
        attrib.cpu = "TriCore";
        break;
      case ElfHeader.EM_M16C:
        attrib.cpu = "M16C";
        break;
      case ElfHeader.EM_STARCORE:
        attrib.cpu = "StarCore";
        break;
      case ElfHeader.EM_BLACKFIN:
        attrib.cpu = "bfin";
        break;
      case ElfHeader.EM_SDMA:
        attrib.cpu = "sdma";
        break;
      case ElfHeader.EM_CRADLE:
        attrib.cpu = "cradle";
        break;
      case ElfHeader.EM_MMDSP:
        attrib.cpu = "mmdsp";
        break;
      case ElfHeader.EM_68HC08:
        attrib.cpu = "hc08";
        break;
      case ElfHeader.EM_RS08:
        attrib.cpu = "rs08";
        break;
      case ElfHeader.EM_NONE:
        attrib.cpu = "none";
      default:
        attrib.cpu = String.format("Unknown (0x%x)", aHeader.getMachineType());
    }

    attrib.isle = aHeader.isLittleEndian();
    attrib.width = aHeader.is32bit() ? 32 : aHeader.is64bit() ? 64 : -1;

    if (aSections != null)
    {
      for (final Section element : aSections)
      {
        final String s = element.toString();
        if (s.startsWith(".debug"))
        {
          attrib.debugType = Attribute.DEBUG_TYPE_DWARF;
          break;
        }
        else if (s.equals(".stab"))
        {
          attrib.debugType = Attribute.DEBUG_TYPE_STABS;
          break;
        }
      }
    }

    return attrib;
  }

  /**
   * @return
   */
  public String getCPU()
  {
    return this.cpu;
  }

  /**
   * @return
   */
  public int getDebugType()
  {
    return this.debugType;
  }

  /**
   * @return
   */
  public int getType()
  {
    return this.type;
  }

  /**
   * @return
   */
  public int getWidth()
  {
    return this.width;
  }

  /**
   * @return
   */
  public boolean hasDebug()
  {
    return this.debugType != DEBUG_TYPE_NONE;
  }

  /**
   * @return
   */
  public boolean isLittleEndian()
  {
    return this.isle;
  }
}
