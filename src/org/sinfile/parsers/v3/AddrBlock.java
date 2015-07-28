package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class AddrBlock {
	public int blockLen;
	public long dataOffset;
	public long fileOffset;
	public long dataLen;
	public int hashType;
	public byte[] checksum;
}
