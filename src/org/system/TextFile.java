package org.system;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

public class TextFile {

	/** Constructor. */
	public TextFile(String aFileName, String aEncoding){
		fFileName = aFileName;
		fEncoding = aEncoding;
	}
	
	public void open(boolean append) throws IOException {
		File f = new File(fFileName);
		f.getParentFile().mkdirs();
		pwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fFileName), fEncoding));
	}
	
	public void write(String aText) throws IOException  {
		pwriter.print(aText);
	}

	public void writeln(String aText) throws IOException  {
		pwriter.print(aText+"\n");
	}

	public void close() throws IOException {
		try {
			pwriter.flush();
			pwriter.close();
		}
		catch (NullPointerException npe) {}
	}

	public void delete() {
		try {
			close();
		}
		catch (Exception e) {}
		File f = new File(fFileName);
		f.delete();
	}

	public void readLines() throws IOException {
		lines = new HashMap<Integer,String>();
		Scanner scanner = new Scanner(new FileInputStream(fFileName));
		try {
			int linenumber=1;
			while (scanner.hasNextLine()){
				String aline = scanner.nextLine();
				lines.put(linenumber++,aline);
			}
		}
		finally{
			scanner.close();
		}
	}
	
	public Collection<String> getLines() throws IOException {
		if (lines==null) readLines();
		return lines.values();
	}
  
	public Map<Integer,String> getMap() throws IOException {
		if (lines==null) readLines();
		return lines;
	}
	
	public static boolean exists(String name) {
		File f = new File(name);
		return f.exists();
	}

	public void setProperty(String property, String value) throws Exception {
		String content = IOUtils.toString(new FileInputStream(fFileName), fEncoding);
		content = content.replaceAll(property, value);
		IOUtils.write(content, new FileOutputStream(fFileName), fEncoding);	
	}
	
	public String getFileName() {
		return fFileName;
	}
	
	// PRIVATE 
	protected String fFileName;
	private final String fEncoding;
	private PrintWriter pwriter;
	private HashMap<Integer,String> lines=null;

}