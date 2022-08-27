package org.flashtool.parsers.sin.v1;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PartitionInfo {
	  @Bin public int mot1;
	  @Bin public int mot2;
	  @Bin public int offset;
	  @Bin public int blockcount;
}
