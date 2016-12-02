package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;


public class DataHeader {
	@Bin public byte[] mmcfMagic;
	@Bin public int mmcfLen;
	@Bin public byte[] gptpMagic;
	@Bin public int gptpLen;
	@Bin public byte[] gptpuid;
}
