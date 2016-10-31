package org.ta.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import org.util.HexDump;
import com.igormaznitsa.jbbp.JBBPParser;
import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.mapper.Bin;

public class TARawBlock {
	  @Bin public int magic;
	  @Bin public int hash;
	  @Bin public byte unknown;
	  @Bin public byte partnumber;
	  @Bin public byte partition;
	  @Bin public byte numblocks;
	  @Bin public byte[] units;
	  Vector<TAUnit> unitList = null;


	  public String toString() {
		  return "Magic : " + HexDump.toHex(magic) + " unknown : "+unknown+" partnumber : "+partnumber+" partition : "+partition+" numblocks : "+numblocks;
	  }
	  
	  public void parseUnits() throws IOException {
		  unitList = new Vector<TAUnit>();
		  JBBPParser unitblock = JBBPParser.prepare(
				    "         <int unitNumber;"
				           + "<int length;"
				           + "<int magic;"
				           + "<int unknown;"
	      );
		  
		  JBBPBitInputStream unitsStream = new JBBPBitInputStream(new ByteArrayInputStream(units));
		  try {
		  while (unitsStream.hasAvailableData()) {
			  TARawUnit rawunit = unitblock.parse(unitsStream).mapTo(TARawUnit.class);
			  rawunit.fetchContent(unitsStream);
			  if (rawunit.isValid()) unitList.add(rawunit.getUnit());
		  }
		  } catch (Exception ioe) {}
		  unitsStream.close();
	  }
	  
	  public Vector<TAUnit> getUnits() {
		  return unitList;
	  }

}