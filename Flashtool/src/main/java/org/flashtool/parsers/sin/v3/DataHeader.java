package org.flashtool.parsers.sin.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataHeader {
	@Bin public byte[] mmcfMagic;
	@Bin public int mmcfLen;
	@Bin public byte[] gptpMagic;
	@Bin public int gptpLen;
	@Bin public byte[] gptpuid;
}
