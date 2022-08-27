package org.flashtool.parsers.sin.v3;

import com.igormaznitsa.jbbp.mapper.Bin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Bin
public class AddrBlock {
	public int blockLen;
	public long dataOffset;
	public long fileOffset;
	public long dataLen;
	public int hashType;
	public byte[] checksum;
}
