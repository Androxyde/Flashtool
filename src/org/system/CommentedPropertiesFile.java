package org.system;

import java.io.*;
import java.util.*;
	 
	/**
	 * The CommentedProperties class is an extension of java.util.Properties
	 * to allow retention of comment lines and blank (whitespace only) lines
	 * in the properties file.
	 *
	 */
public class CommentedPropertiesFile extends java.util.Properties {
	 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
	     * Use a Vector to keep a copy of lines that are a comment or 'blank'
	     */
	    private LinkedList<String> lines = new LinkedList<String>();
	    private Properties commented = new Properties();
	 
	    /**
	     * Load properties from the specified InputStream.
	     * Overload the load method in Properties so we can keep comment and blank lines.
	     * @param   inStream   The InputStream to read.
	     */
	    public void load(File f) throws IOException
	    {
	    	FileInputStream fin = new FileInputStream(f);
	    	load(fin);
	    	fin.close();
	    	FileReader fread = new FileReader(f);
	    	Scanner sc = new Scanner(fread);
	    	while (sc.hasNextLine()) {
	    		String line = sc.nextLine();
	    		if (line.trim().startsWith("#") || line.trim().length()==0) {
	    			if (!line.trim().startsWith("##") && line.trim().contains("=")) {
	    				setCommentedProperty(line.trim().split("=")[0].substring(1).trim(),line.trim().split("=")[1].trim());
	    			} else {
	    				lines.add(line);
	    			}
	    		}
	    		else {
	    			// Adding a key
	    			lines.add(line.trim().split("=")[0].trim());
	    		}
	    	}
	    	fread.close();
	    }
	 
	    /**
	     * Write the properties to the specified OutputStream.
	     *
	     * Overloads the store method in Properties so we can put back comment 
	     * and blank lines.                                                  
	     *
	     * @param out   The OutputStream to write to.
	     * @param header Ignored, here for compatability w/ Properties.
	     *
	     * @exception IOException
	     */
	    public void store(OutputStream out, String header) throws IOException
	    {
        // The spec says that the file must be encoded using ISO-8859-1.
	        PrintWriter writer
	        = new PrintWriter(new OutputStreamWriter(out, "ISO-8859-1"));
	 
	        // We ignore the header, because if we prepend a commented header
	        // then read it back in it is now a comment, which will be saved
	        // and then when we write again we would prepend Another header...
	 
	        Iterator<String> i = lines.iterator();
	        while (i.hasNext()) {
	        	String line = i.next();
	        	if (containsKey(line))
	        		writer.println(line+"="+getProperty(line));
	        	else if (commented.containsKey(line)) writer.println("#"+line.substring(2)+"="+commented.getProperty(line));
	        	else writer.println(line);
	        }
	        writer.flush ();
	        writer.close();
	        out.flush();
	        out.close();
	    }
	    
	    public void updateWith(File f) throws IOException {
	    	CommentedPropertiesFile p = new CommentedPropertiesFile();
	    	p.load(f);
	    	Iterator<Object> i = p.keySet().iterator();
	    	while (i.hasNext()) {
	    		String key = (String)i.next();
	    		unComment(key);
	    		setProperty(key,p.getProperty(key));
	    	}
	    	Iterator<Object> i1 = p.commentedKeySet().iterator();
	    	while (i1.hasNext()) {
	    		String key = ((String)i1.next()).substring(2);
	    		comment(key);
	    		setCommentedProperty(key,p.getCommentedProperty(key));
	    	}
	    }
	 
	    public String getCommentedProperty(String key) {
	    	return commented.getProperty("_c"+key);
	    }
	    
	    public Set commentedKeySet() {
	    	return commented.keySet();
	    }
	    
	    public void updateProperty(String key, String value) {
	    	if (isCommented(key) && !isNotCommented(key)) unComment(key);
	    	setProperty(key,value);
	    }
	    
	    public void updateCommentedProperty(String key, String value) {
	    	if (isNotCommented(key) && !isCommented(key)) comment(key);
	    	setCommentedProperty(key,value);
	    }
	    
	    public void setCommentedProperty(String key, String value) {
	    	commented.setProperty("_c"+key, value);
	    	if (lines.indexOf("_c"+key)<0) lines.add("_c"+key);
	    }
	    
	    public Object setProperty(String key, String value) {
	    	if (lines.indexOf(key)<0) lines.add(key);
	    	return super.setProperty(key,value);
	    }
	    
	    public boolean isCommented(String key) {
	    	return commented.containsKey("_c"+key);
	    }

	    public boolean isNotCommented(String key) {
	    	return containsKey(key);
	    }
	    
	    public void removeCommented(String key) {
	    	commented.remove("_c"+key);
	    }
	    
	    public void remove(String key) {
	    	super.remove(key);
	    }
	    
	    public void unComment(String key) {
	    	if (getCommentedProperty(key)!=null) {
		    	setProperty(key, getCommentedProperty(key));
		    	removeCommented(key);
		    	int index = lines.indexOf("_c"+key);
		    	lines.set(index, key);
	    	}
	    	
	    }
	    
	    public void comment(String key) {
	    	if (getProperty(key)!=null) {
	    		commented.setProperty("_c"+key, getProperty(key));
	    		remove(key);
	    		int index = lines.indexOf(key);
	    		lines.set(index, "_c"+key);
	    	}
	    }

	    /**
	     * Add a comment or blank line or comment to the end of the CommentedProperties.
	     *
	     * @param   line The string to add to the end, make sure this is a comment
	     *             or a 'whitespace' line.
	     */
	    public void addComment(String line) {
	        lines.add(line);
	    }

}