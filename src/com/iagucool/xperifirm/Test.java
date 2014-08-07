package com.iagucool.xperifirm;

import com.iagucool.xperifirm.CDFInfoLoader;
import javax.xml.parsers.ParserConfigurationException;

public class Test {
    public static void main(String[] args) throws ParserConfigurationException {
    	        //http://software.sonymobile.com/ns/usdoe1/2/script/search/TAC8=35809105/CDA=1276-5833_hunYpwMEnNvW40KV1P0QR8qH.ser.gz
            	//http://software.sonymobile.com/ns/usdoe1/2/script/search/TAC8=35809105/CDA=1276-5833_hunYpwMEnNvW40KV1P0QR8qH.ser.gz
    	        //http://software.sonymobile.com/ns/common/1/file/689/279716689_WWDhQQR1UME0trN57J1nxeAP.bin
    		CDFInfoLoader cdf = new CDFInfoLoader("usdoe1", "35809105", "1276-5833");
    		System.out.println(cdf.getRelease());
    		System.out.println(cdf.getSize()/1024);
    }
}