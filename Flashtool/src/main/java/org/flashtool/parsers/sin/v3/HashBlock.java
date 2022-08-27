package org.flashtool.parsers.sin.v3;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashBlock {
	@Bin public int length;
	@Bin public byte[] crc;
}
