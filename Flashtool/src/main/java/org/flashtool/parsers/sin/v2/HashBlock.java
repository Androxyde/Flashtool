package org.flashtool.parsers.sin.v2;

import org.flashtool.system.OS;
import org.flashtool.util.HexDump;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashBlock {
	@Bin public int offset;
	@Bin public int length;
	@Bin public byte hashLen;
	@Bin public byte[] crc;
	
	public boolean validate(byte[] data) {
		String checksum="";
		if (hashLen==20)
			checksum = OS.getSHA1(data);
		if (hashLen==32)
			checksum = OS.getSHA256(data);
		return checksum.equals(HexDump.toHex(crc));
	}
}
