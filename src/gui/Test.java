package gui;

import java.io.File;

import org.sinfile.parsers.SinFile;

public class Test {

	public static void main(String[] args) throws Exception {
		SinFile sin = new SinFile(new File("D:\\Sony\\XZprem\\repair_decrypted\\bluetooth_X-FLASH-ALL-42E5.sin"));
		System.out.println(sin.getShortName());
		System.out.println(sin.getHeader().length);
	}

}