package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class LZ4ABlock {
	public int blockLen;
	public long dataOffset;
	public long uncompDataLen;
	public long compDataLen;
	public long fileOffset;
	public long reserved;
	public int hashType;
	public byte[] checksum;
}