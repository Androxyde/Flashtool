package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class AddrBlock {
	public byte[] addrMagic;
	public long offset;
	public long length;
	public long dest;
	public byte hashType;
	public byte[] crc;
}
