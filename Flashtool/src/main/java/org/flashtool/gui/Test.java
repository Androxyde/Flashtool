package org.flashtool.gui;

import java.io.File;
import java.io.FileInputStream;

import org.flashtool.parsers.sin.SinFile;

import com.igormaznitsa.jbbp.io.JBBPBitInputStream;
import com.igormaznitsa.jbbp.io.JBBPByteOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

	public static void main(String[] args) throws Exception {
		File f = new File("/tmp/emmc.infos");
		FileInputStream fin = new FileInputStream(f);
		JBBPBitInputStream emmcStream = new JBBPBitInputStream(fin);
		emmcStream.skip(0xd0);
		long lunSize1=emmcStream.readInt(JBBPByteOrder.LITTLE_ENDIAN);
		long lunSize=emmcStream.readInt(JBBPByteOrder.LITTLE_ENDIAN);
		lunSize+=lunSize1;
		//lunSize=249880576;
		//lunSize=244285440;
		System.out.println("Read size1 : "+lunSize1);
		System.out.println("Read size : "+lunSize);
		lunSize*=512;
		System.out.println("After Sector size : "+lunSize);
		lunSize/=1024;
		System.out.println("After /1024 : "+lunSize);
		lunSize-=4096;
		System.out.println("Minus 4096 : "+lunSize);
	}

}