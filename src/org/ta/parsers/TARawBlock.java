package org.ta.parsers;

import org.util.HexDump;

import com.igormaznitsa.jbbp.mapper.Bin;

public class TARawBlock {
	  @Bin public int pmagic;
	  @Bin public int phash;
	  @Bin public int punknown;

	  public String toString() {
		  return "Magic : " + HexDump.toHex(pmagic) + " Punknown : "+HexDump.toHex(punknown);
	  }
}
