package org.sinfile.parsers.v2;

import com.igormaznitsa.jbbp.mapper.Bin;

public class HashBlock {
	@Bin public int offset;
	@Bin public int length;
	@Bin public byte hashLen;
	@Bin public byte[] crc;
}
