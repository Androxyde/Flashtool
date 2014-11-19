package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

public class HashBlock {
	@Bin public int length;
	@Bin public byte[] crc;
}
