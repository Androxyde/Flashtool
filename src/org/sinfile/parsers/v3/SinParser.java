package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class SinParser {
	  public byte[] magic;
	  public int headerLen;
	  public int payloadType;
	  public int hashType;
	  public int reserved;
	  public int hashLen;
	  public byte[] hashBlocks;
	  public int certLen;
	  public byte[] cert;
}
