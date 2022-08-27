package org.flashtool.parsers.ta;

import java.io.IOException;

import org.flashtool.util.HexDump;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TARawUnit {
	@Bin public int unitNumber;
	@Bin public int length;
	@Bin public int magic;
	@Bin public int unknown;
	TAUnit unit=null;

	public void fetchContent(JBBPBitInputStream stream) throws IOException {
		  if (magic==0x3BF8E9C1) {
			  unit = new TAUnit(unitNumber,stream.readByteArray(length));
			  if (length % 4 != 0) {
				  stream.skip(4 - length % 4);
			  }
		  }
	}
	
	public boolean isValid() {
		return (magic==0x3BF8E9C1);
	}
	
	public String toString() {
		return unit.toString();
	}

	public TAUnit getUnit() {
		return unit;
	}

}