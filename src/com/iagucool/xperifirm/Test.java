package com.iagucool.xperifirm;

import java.io.IOException;

import com.iagucool.xperifirm.CDFInfoLoader;

import javax.xml.parsers.ParserConfigurationException;

import org.system.Proxy;

public class Test {
    public static void main(String[] args) throws ParserConfigurationException, IOException {
    	Proxy.setProxy();
		CDFInfoLoader cdf = new CDFInfoLoader("35809105", "1276-5833");
		System.out.println(cdf.getRelease());
		System.out.println(cdf.getSize()/1024);
		System.out.println(cdf.getFiles());
    }
}