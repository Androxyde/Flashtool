package org.flashtool.parsers.sin.v3;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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