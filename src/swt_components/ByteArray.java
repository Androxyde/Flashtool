/*******************************************************************************
 * Copyright (c) 2004 Robert K"opferl
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * or in the file license.txt
 * 
 * Contributors:
 *      Robert K"opferl <quellkode@koepferl.de> - Initial writing
 * Created:
 * 		16.02.2004
 * Project:
 *      SWT-Hexedit
 * Version:
 * 		$Version$
 * Last Change:
 *      $Log$ 
 *******************************************************************************/
package swt_components;

/**
 * @author robert 
 *
 * This class provides QnD utility functions concernig the conversion
 * of Byte[] to String and vice versa. These functions are used by
 * the control to implement the copy and paste functionality. 
 */
public class ByteArray
{
	/// convert a bytearray to a String like that:  "33 45 09 FE 3C"
	static public String bytearray2string(byte[] ba)
	{
		StringBuffer buf = new StringBuffer(ba.length*3);
		for (int i = 0; i < ba.length; i++)
		{
			byte nwn = (byte) (ba[i] & 0x0F);
			byte hwn = (byte) ((ba[i]>>4) &0xF);
			buf.append(" "+Integer.toHexString(hwn)+Integer.toHexString(nwn));
		}
		return buf.toString();
	}
	
	/// convert a string like that: "33 45 09 FE 3C" to a array of byte.
	static public byte[] string2bytearray(String st)
	{
		st = st.replaceAll("^[ \t\n]+|[ \t\n]+$","");
		String[] sa = st.split("[ \t\n]+");
		byte[] ba = new byte[sa.length];
		for (int i = 0; i < sa.length; i++)
		{
			System.out.println(sa[i]);
			ba[i] = Integer.valueOf(sa[i],16).byteValue();
		}
		return ba;
	}

} 
