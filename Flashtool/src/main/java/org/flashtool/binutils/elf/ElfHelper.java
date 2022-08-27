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
 * 
 */
public class ElfHelper
{
  // INNER TYPES

  /**
   * 
   */
  public static class Sizes
  {
    // VARIABLES

    public final long text;
    public final long data;
    public final long bss;
    public final long total;

    // CONSTRUCTORS

    /**
     * @param aText
     * @param aData
     * @param aBss
     */
    public Sizes(final long aText, final long aData, final long aBss)
    {
      this.text = aText;
      this.data = aData;
      this.bss = aBss;
      this.total = this.text + this.data + this.bss;
    }
  }

  // VARIABLES

  private Elf elf;
  private Symbol[] dynsyms;
  private Symbol[] symbols;
  private Section[] sections;
  private Dynamic[] dynamics;

  // CONSTRUCTORS

  /**
   * Create a new <code>ElfHelper</code> using an existing <code>Elf</code>
   * object.
   * 
   * @param aElf
   *          An existing Elf object to wrap.
   * @throws IOException
   *           Error processing the Elf file.
   */
  public ElfHelper(final Elf aElf) throws IOException
  {
    this.elf = aElf;
  }

  /**
   * Create a new <code>ElfHelper</code> based on the given filename.
   * 
   * @param aFile
   *          The file to use for creating a new Elf object.
   * @throws IOException
   *           Error processing the Elf file.
   * @see Elf#Elf(String )
   */
  public ElfHelper(final File aFile) throws IOException
  {
    this.elf = new Elf(aFile);
  }

  // METHODS

  /**
   * 
   */
  public void dispose()
  {
    if (this.elf != null)
    {
      this.elf.dispose();
      this.elf = null;
    }
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getCommonObjects() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();
    loadSections();

    for (final Symbol symbol : this.symbols)
    {
      if ((symbol.getBind() == Symbol.STB_GLOBAL) && (symbol.getType() == Symbol.STT_OBJECT))
      {
        final int idx = symbol.getSectionHeaderTableIndex();
        if (idx == Symbol.SHN_COMMON)
        {
          v.add(symbol);
        }
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * Give back the Elf object that this helper is wrapping
   */
  public Elf getElf()
  {
    return this.elf;
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getExternalFunctions() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();
    loadSections();

    for (final Symbol dynsym : this.dynsyms)
    {
      if ((dynsym.getBind() == Symbol.STB_GLOBAL) && (dynsym.getType() == Symbol.STT_FUNC))
      {
        final int idx = dynsym.getSectionHeaderTableIndex();
        if ((idx < Symbol.SHN_HIPROC) && (idx > Symbol.SHN_LOPROC))
        {
          final String name = dynsym.toString();
          if ((name != null) && (name.trim().length() > 0))
          {
            v.add(dynsym);
          }
        }
        else if ((idx >= 0) && (this.sections[idx].getType() == Section.SHT_NULL))
        {
          v.add(dynsym);
        }
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getExternalObjects() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();
    loadSections();

    for (final Symbol dynsym : this.dynsyms)
    {
      if ((dynsym.getBind() == Symbol.STB_GLOBAL) && (dynsym.getType() == Symbol.STT_OBJECT))
      {
        final int idx = dynsym.getSectionHeaderTableIndex();
        if ((idx < Symbol.SHN_HIPROC) && (idx > Symbol.SHN_LOPROC))
        {
          final String name = dynsym.toString();
          if ((name != null) && (name.trim().length() > 0))
          {
            v.add(dynsym);
          }
        }
        else if ((idx >= 0) && (this.sections[idx].getType() == Section.SHT_NULL))
        {
          v.add(dynsym);
        }
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getLocalFunctions() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();
    loadSections();

    for (final Symbol symbol : this.symbols)
    {
      if (symbol.getType() == Symbol.STT_FUNC)
      {
        final int idx = symbol.getSectionHeaderTableIndex();
        if ((idx < Symbol.SHN_HIPROC) && (idx > Symbol.SHN_LOPROC))
        {
          final String name = symbol.toString();
          if ((name != null) && (name.trim().length() > 0))
          {
            v.add(symbol);
          }
        }
        else if ((idx >= 0) && (this.sections[idx].getType() != Section.SHT_NULL))
        {
          v.add(symbol);
        }
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getLocalObjects() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();
    loadSections();

    for (final Symbol symbol : this.symbols)
    {
      if (symbol.getType() == Symbol.STT_OBJECT)
      {
        final int idx = symbol.getSectionHeaderTableIndex();
        if ((idx < Symbol.SHN_HIPROC) && (idx > Symbol.SHN_LOPROC))
        {
          final String name = symbol.toString();
          if ((name != null) && (name.trim().length() > 0))
          {
            v.add(symbol);
          }
        }
        else if ((idx >= 0) && (this.sections[idx].getType() != Section.SHT_NULL))
        {
          v.add(symbol);
        }
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * @return
   * @throws IOException
   */
  public Dynamic[] getNeeded() throws IOException
  {
    final List<Dynamic> v = new ArrayList<Dynamic>();

    loadDynamics();

    for (final Dynamic dynamic : this.dynamics)
    {
      if (dynamic.getTag() == Dynamic.DT_NEEDED)
      {
        v.add(dynamic);
      }
    }
    return v.toArray(new Dynamic[v.size()]);
  }

  /**
   * @return
   * @throws IOException
   */
  public Sizes getSizes() throws IOException
  {
    long text, data, bss;

    text = 0;
    data = 0;
    bss = 0;

    loadSections();

    for (final Section section : this.sections)
    {
      if (section.getType() != Section.SHT_NOBITS)
      {
        if (section.getFlags() == (Section.SHF_WRITE | Section.SHF_ALLOC))
        {
          data += section.getSize();
        }
        else if ((section.getFlags() & Section.SHF_ALLOC) != 0)
        {
          text += section.getSize();
        }
      }
      else
      {
        if (section.getFlags() == (Section.SHF_WRITE | Section.SHF_ALLOC))
        {
          bss += section.getSize();
        }
      }
    }

    return new Sizes(text, data, bss);
  }

  /**
   * @return
   * @throws IOException
   */
  public String getSoName() throws IOException
  {
    String soname = "";

    loadDynamics();

    for (final Dynamic dynamic : this.dynamics)
    {
      if (dynamic.getTag() == Dynamic.DT_SONAME)
      {
        soname = dynamic.toString();
      }
    }
    return soname;
  }

  /**
   * @return
   * @throws IOException
   */
  public Symbol[] getUndefined() throws IOException
  {
    final List<Symbol> v = new ArrayList<Symbol>();

    loadSymbols();

    for (final Symbol dynsym : this.dynsyms)
    {
      if (dynsym.getSectionHeaderTableIndex() == Symbol.SHN_UNDEF)
      {
        v.add(dynsym);
      }
    }

    return v.toArray(new Symbol[v.size()]);
  }

  /**
   * @throws IOException
   */
  private void loadDynamics() throws IOException
  {
    if (this.dynamics == null)
    {
      this.dynamics = new Dynamic[0];
      final Section dynSect = this.elf.getSectionByName(".dynamic");
      if (dynSect != null)
      {
        this.dynamics = this.elf.getDynamicSections(dynSect);
      }
    }
  }

  /**
   * @throws IOException
   */
  private void loadSections() throws IOException
  {
    if (this.sections == null)
    {
      this.sections = this.elf.getSections();
    }
  }

  /**
   * @throws IOException
   */
  private void loadSymbols() throws IOException
  {
    if (this.symbols == null)
    {
      this.elf.loadSymbols();
      this.symbols = this.elf.getSymtabSymbols();
      this.dynsyms = this.elf.getDynamicSymbols();

      if (this.symbols.length <= 0)
      {
        this.symbols = this.dynsyms;
      }
      if (this.dynsyms.length <= 0)
      {
        this.dynsyms = this.symbols;
      }
    }
  }
}
