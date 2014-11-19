package org.sinfile.parsers.v3;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin
public class SinParser {
	  public byte[] magic;
	  public int header_len;
	  public int payload_type;
	  public int hash_type;
	  public int reserved;
	  public int hash_len;
	  public HashBlock[] hashblocks;
	  public int cert_len;
	  public byte[] cert;
}
