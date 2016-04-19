package org.sinfile.parsers.v2;

import org.system.OS;
import org.util.HexDump;

import com.igormaznitsa.jbbp.mapper.Bin;

public class HashBlock {
	@Bin public int offset;
	@Bin public int length;
	@Bin public byte hashLen;
	@Bin public byte[] crc;
	
	public boolean validate(byte[] data) {
		String checksum = OS.getSHA256(data);
		return checksum.equals(HexDump.toHex(crc));
	}
}
