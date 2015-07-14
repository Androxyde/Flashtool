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


import java.util.*;


/**
 * We have to implement a separate compararator since when we do the binary
 * search down below we are using a Long and a Symbol object and the Long
 * doesn't know how to compare against a Symbol so if we compare Symbol vs Long
 * it is ok, but not if we do Long vs Symbol.
 */
class SymbolComparator implements Comparator<Object>
{
  // METHODS

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(Object o1, Object o2)
  {
    Long val1, val2;
    if (o1 instanceof Long)
    {
      val1 = (Long) o1;
    }
    else if (o1 instanceof Symbol)
    {
      val1 = ((Symbol) o1).getValue();
    }
    else
    {
      return -1;
    }

    if (o2 instanceof Long)
    {
      val2 = (Long) o2;
    }
    else if (o2 instanceof Symbol)
    {
      val2 = ((Symbol) o2).getValue();
    }
    else
    {
      return -1;
    }

    return val1.compareTo(val2);
  }
}
