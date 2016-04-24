package org.sinfile.parsers.v2;

import com.igormaznitsa.jbbp.mapper.Bin;

public class PartitionInfo {
	  @Bin public int mot1;
	  @Bin public int mot2;
	  @Bin public int offset;
	  @Bin public int blockcount;
}
