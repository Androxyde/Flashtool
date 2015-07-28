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
 *     Markus Schorn (Wind River Systems)
 *     J.W. Janssen - Clean up and made API more OO-oriented
 *******************************************************************************/
package nl.lxtreme.binutils.elf;


import java.io.*;
import java.math.*;
import java.util.*;


/**
 * Denotes an ELF (Executable and Linking Format) file.
 * <p>
 * There are three main types of object files:
 * </p>
 * <ul>
 * <li>A relocatable file holds code and data suitable for linking with other
 * object files to create an executable or a shared object file;</li>
 * <li>An executable file holds a program suitable for execution;</li>
 * <li>A shared object file holds code and data suitable for linking in two
 * contexts. First, the link editor may process it with other relocatable and
 * shared object files to create another object file. Second, the dynamic linker
 * combines it with an executable file and other shared objects to create a
 * process image.</li>
 * </ul>
 * <p>
 * Created by the assembler and link editor, object files are binary
 * representations of programs intended to execute directly on a processor.
 * Programs that require other abstract machines are excluded.
 * </p>
 */
public class Elf
{
  // CONSTANTS

  public final static int ELF32_ADDR_SIZE = 4;
  public final static int ELF32_OFF_SIZE = 4;
  public final static int ELF64_ADDR_SIZE = 8;
  public final static int ELF64_OFF_SIZE = 8;

  // VARIABLES

  private ERandomAccessFile efile;
  private ElfHeader ehdr;
  private Section[] sections;
  private String file;
  private byte[] section_strtab;
  private int syms = 0;
  private Symbol[] symbols;
  private Symbol[] symtab_symbols;
  private Section symtab_sym;
  private Symbol[] dynsym_symbols;
  private Section dynsym_sym;

  // CONSTRUCTORS

  /**
   * @param aFile
   * @throws IOException
   */
  public Elf( final File aFile ) throws IOException
  {
    try
    {
      this.efile = new ERandomAccessFile( aFile, "r" );
      this.ehdr = new ElfHeader( this.efile );
      this.file = aFile.getAbsolutePath();
    }
    finally
    {
      if ( this.ehdr == null )
      {
        dispose();
      }
    }
  }

  /**
   * A hollow entry, to be used with caution in controlled situations
   */
  private Elf()
  {
  }

  // METHODS

  /**
   * @param array
   * @return
   * @throws IOException
   */
  public static Attribute getAttributes( final byte[] array ) throws IOException
  {
    final Elf emptyElf = new Elf();
    emptyElf.ehdr = new ElfHeader( array );
    emptyElf.sections = new Section[0];
    final Attribute attrib = emptyElf.getAttributes();
    emptyElf.dispose();

    return attrib;
  }

  /**
   * @param file
   * @return
   * @throws IOException
   */
  public static Attribute getAttributes( final File file ) throws IOException
  {
    final Elf elf = new Elf( file );
    final Attribute attrib = elf.getAttributes();
    elf.dispose();
    return attrib;
  }

  /**
   * @param aBytes
   * @return
   */
  static long createAddr32( final byte[] aBytes )
  {
    final long result = new BigInteger( 1, aBytes ).longValue();
    return result & 0xFFFFFFFF;
  }

  /**
   * @param aBytes
   * @return
   */
  static long createAddr64( final byte[] aBytes )
  {
    return new BigInteger( 1, aBytes ).longValue();
  }

  /**
   * @param file
   * @return
   * @throws IOException
   */
  static long readUnsignedLong( final ERandomAccessFile file ) throws IOException
  {
    final long result = file.readLongE();
    if ( result < 0 )
    {
      throw new IOException( "Maximal file offset is " + Long.toHexString( Long.MAX_VALUE ) + " given offset is "
          + Long.toHexString( result ) );
    }
    return result;
  }

  /**
   * Disposes this ELF object and closes all associated resources.
   */
  public void dispose()
  {
    try
    {
      if ( this.efile != null )
      {
        this.efile.close();
        this.efile = null;
      }
    }
    catch ( IOException exception )
    {
      // Ignore XXX
    }
  }

  /**
   * @return
   * @throws IOException
   */
  public Attribute getAttributes() throws IOException
  {
    return Attribute.create( this.ehdr, getSections() );
  }

  /**
   * @param section
   * @return
   * @throws IOException
   */
  public Dynamic[] getDynamicSections( final Section section ) throws IOException
  {
    return Dynamic.create( this.ehdr, section, this.efile );
  }

  /**
   * @return
   */
  public Symbol[] getDynamicSymbols()
  {
    if ( this.dynsym_symbols == null )
    {
      throw new IllegalStateException( "Dynamic symbols not yet loaded!" );
    }
    return this.dynsym_symbols;
  }

  /**
   * @return
   */
  public String getFilename()
  {
    return this.file;
  }

  /**
   * @return
   * @throws IOException
   */
  public ElfHeader getHeader() throws IOException
  {
    return this.ehdr;
  }

  /**
   * @return
   * @throws IOException
   */
  public ProgramHeader[] getProgramHeaders() throws IOException
  {
    return ProgramHeader.createHeaders( this.ehdr, this.efile );
  }

  /**
   * @param name
   * @return
   * @throws IOException
   */
  public Section getSectionByName( final String name ) throws IOException
  {
    final Section[] secs = getSections();
    for ( Section section : secs )
    {
      if ( name.equals( section.getName() ) )
      {
        return section;
      }
    }
    return null;
  }

  /**
   * @return
   * @throws IOException
   */
  public Section[] getSections() throws IOException
  {
    if ( this.sections == null )
    {
      this.sections = Section.create( this, this.ehdr, this.efile );
      for ( int i = 0; i < this.sections.length; i++ )
      {
        if ( this.sections[i].getType() == Section.SHT_SYMTAB )
        {
          this.syms = i;
        }
        if ( ( this.syms == 0 ) && ( this.sections[i].getType() == Section.SHT_DYNSYM ) )
        {
          this.syms = i;
        }
      }
    }
    return this.sections;
  }

  /**
   * @param type
   * @return
   * @throws IOException
   */
  public Section[] getSections( final int type ) throws IOException
  {
    final ArrayList<Section> result = new ArrayList<Section>();

    final Section[] secs = getSections();
    for ( Section section : secs )
    {
      if ( type == section.getType() )
      {
        result.add( section );
      }
    }

    return result.toArray( new Section[result.size()] );
  }

  /**
   * This section describes the default string table. String table sections hold
   * null-terminated character sequences, commonly called strings. The object
   * file uses these strings to represent symbol and section names.
   * 
   * @return the raw string table, never <code>null</code>.
   * @throws IOException
   *           in case of I/O problems.
   */
  public byte[] getStringTable() throws IOException
  {
    if ( this.section_strtab == null )
    {
      final int shstrndx = this.ehdr.getStringTableSectionIndex();
      if ( ( shstrndx > this.sections.length ) || ( shstrndx < 0 ) )
      {
        this.section_strtab = new byte[0];
      }

      final int size = ( int )this.sections[shstrndx].getSize();
      if ( ( size <= 0 ) || ( size > this.efile.length() ) )
      {
        this.section_strtab = new byte[0];
      }
      this.section_strtab = new byte[size];
      this.efile.seek( this.sections[shstrndx].getFileOffset() );
      this.efile.read( this.section_strtab );
    }
    return this.section_strtab;
  }

  /**
   * /* return the address of the function that address is in
   * 
   * @param vma
   * @return
   */
  public Symbol getSymbol( final long vma )
  {
    if ( this.symbols == null )
    {
      return null;
    }

    // @@@ If this works, move it to a single instance in this class.
    final SymbolComparator symbol_comparator = new SymbolComparator();

    int ndx = Arrays.binarySearch( this.symbols, vma, symbol_comparator );
    if ( ndx > 0 )
    {
      return this.symbols[ndx];
    }
    if ( ndx == -1 )
    {
      return null;
    }
    ndx = -ndx - 1;
    return this.symbols[ndx - 1];
  }

  /**
   * @return
   */
  public Symbol[] getSymbols()
  {
    return this.symbols;
  }

  /**
   * @return
   */
  public Symbol[] getSymtabSymbols()
  {
    return this.symtab_symbols;
  }

  /**
   * Loads the symbol tables.
   * 
   * @throws IOException
   *           in case of I/O problems.
   */
  public void loadSymbols() throws IOException
  {
    if ( this.symbols == null )
    {
      Section section[] = getSections( Section.SHT_SYMTAB );

      this.symtab_sym = null;
      if ( section.length > 0 )
      {
        this.symtab_sym = section[0];
      }
      this.symtab_symbols = loadSymbolsBySection( this.symtab_sym );

      section = getSections( Section.SHT_DYNSYM );

      this.dynsym_sym = null;
      if ( section.length > 0 )
      {
        this.dynsym_sym = section[0];
      }
      this.dynsym_symbols = loadSymbolsBySection( this.dynsym_sym );

      if ( this.symtab_sym != null )
      {
        // sym = symtab_sym;
        this.symbols = this.symtab_symbols;
      }
      else if ( this.dynsym_sym != null )
      {
        // sym = dynsym_sym;
        this.symbols = this.dynsym_symbols;
      }
    }
  }

  /**
   * Reads the program segment from the ELF file and writes it to the given
   * writer.
   * 
   * @param aHeader
   *          the program segment to read;
   * @param aWriter
   *          the writer to write the read data to.
   * @throws IOException
   *           in case of I/O problems.
   */
  public void readSegment( ProgramHeader aHeader, OutputStream aWriter ) throws IOException
  {
    this.efile.seek( aHeader.getFileOffset() );

    this.efile.setEndiannes( this.ehdr.isLittleEndian() );

    long size = aHeader.getFileSize();
    if ( this.ehdr.is32bit() )
    {
      size /= 4;
    }
    else if ( this.ehdr.is64bit() )
    {
      size /= 8;
    }

    while ( size-- >= 0 )
    {
      if ( this.ehdr.is32bit() )
      {
        byte[] buf = new byte[4];
        this.efile.readFullyE( buf );
        aWriter.write( buf, 0, 4 );
      }
      else
      {
        byte[] buf = new byte[8];
        this.efile.readFullyE( buf );
        aWriter.write( buf, 0, 8 );
      }
    }
  }

  /**
   * @param section
   * @param index
   * @return
   * @throws IOException
   */
  final String getStringFromSection( final Section section, final int index ) throws IOException
  {
    if ( index > section.getSize() )
    {
      return "";
    }

    final StringBuffer str = new StringBuffer();
    // Most string symbols will be less than 50 bytes in size
    final byte[] tmp = new byte[50];

    this.efile.seek( section.getFileOffset() + index );
    while ( true )
    {
      int len = this.efile.read( tmp );
      for ( int i = 0; i < len; i++ )
      {
        if ( tmp[i] == 0 )
        {
          len = 0;
          break;
        }
        str.append( ( char )tmp[i] );
      }
      if ( len <= 0 )
      {
        break;
      }
    }

    return str.toString();
  }

  /**
   * Make sure we do not leak the fds.
   */
  @Override
  protected void finalize() throws Throwable
  {
    try
    {
      dispose();
    }
    finally
    {
      super.finalize();
    }
  }

  /**
   * @param aSection
   * @return
   * @throws IOException
   */
  private Symbol[] loadSymbolsBySection( final Section aSection ) throws IOException
  {
    if ( aSection == null )
    {
      return new Symbol[0];
    }
    return aSection.loadSymbols( this.ehdr, this.efile );
  }
}
