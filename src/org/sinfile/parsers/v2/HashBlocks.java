package org.sinfile.parsers.v2;

import com.igormaznitsa.jbbp.mapper.Bin;

@Bin  
public class HashBlocks {
    public HashBlock [] block;
 

    public void setSpare(int sparetype) {
    	if (sparetype==0x0A && block.length>1 && block[0].length==16) {
    		int spare = block[1].length%131072;
    		for (int i=1;i<block.length;i++) {
    			int newoffset=block[i].offset+(spare*(i-1));
    			block[i].offset = block[i].offset+(spare*(i-1));
    		}
    	}
    }

}