/*******************************************************************************
 * Copyright (c) 2011, J.W. Janssen
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     J.W. Janssen - Cleanup and make API more OO-oriented.
 *******************************************************************************/
package org.flashtool.binutils.hex.util;


/**
 * Provides two checksum algorithms commonly used in many HEX-files.
 */
public enum Checksum
{
  ONES_COMPLEMENT, //
  TWOS_COMPLEMENT, //
  ;

  // METHODS

  /**
   * Calculates the checksum for the given array of byte-values.
   * 
   * @param aValues
   *          the values to calculate the checksum for, should be at least two
   *          values.
   * @return the 8-bit checksum.
   * @throws IllegalArgumentException
   *           in case the given values were <code>null</code> or did not
   *           contain at least two values.
   */
  public byte calculate(final byte... aValues) throws IllegalArgumentException
  {
    if (aValues == null)
    {
      throw new IllegalArgumentException("Values cannot be null!");
    }
    if (aValues.length < 2)
    {
      throw new IllegalArgumentException("Should have at least two values to calculate the checksum!");
    }

    int sum = 0;
    for (int value : aValues)
    {
      sum += value;
    }

    switch (this)
    {
      case ONES_COMPLEMENT:
        return (byte) (~sum);
      case TWOS_COMPLEMENT:
        return (byte) (~sum + 1);
      default:
        throw new IllegalArgumentException("Unhandled checksum!");
    }
  }

  /**
   * Calculates the checksum for the given array of integer-values.
   * 
   * @param aValues
   *          the values to calculate the checksum for, should be at least two
   *          values.
   * @return the 16-bit checksum.
   * @throws IllegalArgumentException
   *           in case the given values were <code>null</code> or did not
   *           contain at least one value.
   */
  public int calculate(final int... aValues) throws IllegalArgumentException
  {
    if (aValues == null)
    {
      throw new IllegalArgumentException("Values cannot be null!");
    }
    if (aValues.length < 0)
    {
      throw new IllegalArgumentException("Should have at least one values to calculate the checksum!");
    }

    int sum = 0;
    for (int value : aValues)
    {
      sum += value;
    }

    switch (this)
    {
      case ONES_COMPLEMENT:
        return (~sum) & 0xFFFF;
      case TWOS_COMPLEMENT:
        return (~sum + 1) & 0xFFFF;
      default:
        throw new IllegalArgumentException("Unhandled checksum!");
    }
  }

}
