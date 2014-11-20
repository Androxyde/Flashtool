package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class DataHeader {
	public byte[] mmcfMagic;
	public int mmcfLen;
	public byte[] gptpMagic;
	public int gptpLen;
	public byte[] uuid;
	public byte[] addrList;
}
