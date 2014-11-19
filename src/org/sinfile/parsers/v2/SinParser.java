package org.sinfile.parsers.v2;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class SinParser {
	  public byte multipleHeaders;
	  public int headerLen;
	  public byte payloadType;
	  public short unknown;
	  public byte memId;
	  public byte compression;
	  public int hashLen;
	  public byte[] hashblocks;
	  public int certLen;
	  public byte[] cert;
}