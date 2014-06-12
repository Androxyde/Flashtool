package com.btr.proxy.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.TimeZone;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Plist xml handling (serialization and deserialization)
 * <p>
 * <em>The xml plist dtd can be found at http://www.apple.com/DTDs/PropertyList-1.0.dtd</em>
 * <p>
 * The plist spec handles 8 types of objects: booleans, real, integers, dates, binary data,
 * strings, arrays (lists) and dictionaries (maps).
 * <p>
 * The java Plist lib handles converting xml plists to a nested {@code Map<String, Object>}
 * that can be trivially read from java. It also provides a simple way to convert a nested
 * {@code Map<String, Object>} into an xml plist representation.
 * <p>
 * The following mapping will be done when converting from plist to <tt>Map</tt>:
 * <pre>
 * true/false -> Boolean
 * real -> Double
 * integer -> Integer/Long (depends on size, values exceeding an int will be rendered as longs)
 * data -> byte[]
 * string -> String
 * array -> List
 * dict -> Map
 * </pre>
 * <p>
 * When converting from Map -> plist the conversion is as follows:
 * <pre>
 * Boolean -> true/false
 * Float/Double -> real
 * Byte/Short/Integer/Long -> integer
 * byte[] -> data
 * List -> array
 * Map -> dict
 * </pre> 
 *
 * @author Christoffer Lerno / Modified by Bernd Rosstauscher
 */
public final class PListParser
{
    /*****************************************************************************
	 * Exception is used for XML parse problems. 
	 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
	 ****************************************************************************/

	public static class XmlParseException extends Exception {

		/** Comment for <code>serialVersionUID</code>*/
		private static final long serialVersionUID = 1L;

		/*************************************************************************
		 * Constructor
		 ************************************************************************/
		
		public XmlParseException() {
			super();
		}
		
		/*************************************************************************
		 * Constructor
		 * @param msg the error message 
		 ************************************************************************/
		
		public XmlParseException(String msg) {
			super(msg);
		}

		/*************************************************************************
		 * Constructor
		 * @param msg error message 
		 * @param e the cause.
		 ************************************************************************/
		
		public XmlParseException(String msg, Exception e) {
			super(msg, e);
		}

	}
	
	/*****************************************************************************
	 * Small helper class representing a tree node. 
	 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
	 ****************************************************************************/
	
	public static class Dict implements Iterable<Map.Entry<String, Object>> {
		private Map<String, Object> children;
		
		/*************************************************************************
		 * Constructor
		 ************************************************************************/
		
		public Dict() {
			super();
			this.children = new HashMap<String, Object>();
		}

		/*************************************************************************
		 * @param key of the child node.
		 * @return the child node, null if not existing.
		 ************************************************************************/
		
		public Object get(String key) {
			return this.children.get(key);
		}

		/*************************************************************************
		 * iterator
		 * @see java.lang.Iterable#iterator()
		 ************************************************************************/

		public Iterator<Entry<String, Object>> iterator() {
			return this.children.entrySet().iterator();
		}

		/*************************************************************************
		 * @return the size of this dictionary.
		 ************************************************************************/
		
		public int size() {
			return this.children.size();
		}
		
	    /*************************************************************************
         * Dumps a dictionary with all sub-nodes to the console.
         ************************************************************************/
        
        public void dump() {
			System.out.println("PList");
        	dumpInternal(this, 1);
        }

		/*************************************************************************
		 * @param plist
		 * @param indent
		 ************************************************************************/
		
		private static void dumpInternal(Dict plist, int indent) {
			for (Map.Entry<String, Object> child : plist) {
				if (child.getValue() instanceof Dict) {
					for (int j = 0; j < indent; j++) {
						System.out.print("  ");
					}
					System.out.println(child.getKey());
					dumpInternal((Dict) child.getValue(), indent+1);
				} else {
					for (int j = 0; j < indent; j++) {
						System.out.print("  ");
					}
					System.out.println(child.getKey()+" = "+child.getValue());
				}
			}
			
		}
		
		/*************************************************************************
         * Get a node at a given path.
         * @param path a / separated path into the plist hirarchy.
         * @return the object located at the given path, null if it does not exist.
         ************************************************************************/
        
		public Object getAtPath(String path) {
        	Dict currentNode = this;
        	
        	String[] pathSegments = path.trim().split("/");
        	for (int i = 0; i < pathSegments.length; i++) {
        		String segment = pathSegments[i].trim();
        		if (segment.length() == 0) {
        			continue;
        		}
				Object o = currentNode.get(segment);
        		if (i >= pathSegments.length-1) {
        			return o;
        		}
        		if (o == null || !(o instanceof Dict)){
        			break;
        		}
        		currentNode = (Dict) o;
        	}
        	return null;
        }

		public void forEach(Consumer<? super Entry<String, Object>> arg0) {
			// TODO xperia 25 avr. 2014 Auto-generated method stub
			
		}

		public Spliterator<Entry<String, Object>> spliterator() {
			// TODO xperia 25 avr. 2014 Auto-generated method stub
			return null;
		}
		
	}
	
		/**
         * Singleton instance.
         */
        private final static PListParser PLIST = new PListParser();

        /**
         * All element types possible for a plist.
         */
        private static enum ElementType
        {
                INTEGER,
                STRING,
                REAL,
                DATA,
                DATE,
                DICT,
                ARRAY,
                TRUE,
                FALSE,
        }

        private static final String BASE64_STRING
                        = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        private static final char[] BASE64_CHARS = BASE64_STRING.toCharArray();
        private final DateFormat m_dateFormat;
        private final Map<Class<?>, ElementType> m_simpleTypes;

        /**
         * Utility method to close a closeable.
         *
         * @param closeable or null.
         */
        static void silentlyClose(Closeable closeable)
        {
                try
                        {
                                if (closeable != null) {
									closeable.close();
								}
                }
                catch (IOException e)
                {
                        // Ignore
                }
        }

		/*************************************************************************
		 * @param input
		 * @return
		 * @throws XmlParseException
		 ************************************************************************/
		
		private static Dict parse(InputSource input)
				throws XmlParseException {
			try {
				DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				documentBuilder.setEntityResolver(new EmptyXMLResolver());
				Document doc = documentBuilder.parse(input);
				Element element = doc.getDocumentElement();
	            return PLIST.parse(element);
			} catch (ParserConfigurationException e) {
				throw new XmlParseException("Error reading input", e);
			} catch (SAXException e) {
				throw new XmlParseException("Error reading input", e);
			} catch (IOException e) {
				throw new XmlParseException("Error reading input", e);
			}
		}

        /**
         * Create a nested {@code map<String, Object>} from a plist xml file using the default mapping.
         *
         * @param file the File containing the the plist xml.
         * @return the resulting map as read from the plist data.
         * @throws XmlParseException if the plist could not be properly parsed.
         * @throws IOException if there was an issue reading the plist file.
         */
        public static Dict load(File file) throws XmlParseException, IOException
        {
			FileInputStream byteStream = new FileInputStream(file);
			try {
				InputSource input = new InputSource(byteStream);
				return parse(input);
			} finally {
				silentlyClose(byteStream);
			}
        }

        /**
         * Create a plist handler.
         */
        PListParser()
        {
                this.m_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                this.m_dateFormat.setTimeZone(TimeZone.getTimeZone("Z"));
                this.m_simpleTypes = new HashMap<Class<?>, ElementType>();
                this.m_simpleTypes.put(Integer.class, ElementType.INTEGER);
                this.m_simpleTypes.put(Byte.class, ElementType.INTEGER);
                this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
                this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
                this.m_simpleTypes.put(Long.class, ElementType.INTEGER);
                this.m_simpleTypes.put(String.class, ElementType.STRING);
                this.m_simpleTypes.put(Float.class, ElementType.REAL);
                this.m_simpleTypes.put(Double.class, ElementType.REAL);
                this.m_simpleTypes.put(byte[].class, ElementType.DATA);
                this.m_simpleTypes.put(Boolean.class, ElementType.TRUE);
                this.m_simpleTypes.put(Date.class, ElementType.DATE);
        }

        /**
         * Parses a plist top element into a map dictionary containing all the data
         * in the plist.
         *
         * @param element the top plist element.
         * @return the resulting data tree structure.
         * @throws XmlParseException if there was any error parsing the xml.
         */
		Dict parse(Element element) throws XmlParseException
        {
                if (!"plist".equalsIgnoreCase(element.getNodeName())) {
					throw new XmlParseException("Expected plist top element, was: " + element.getNodeName());
				}

                Node n = element.getFirstChild();
                while (n != null && !n.getNodeName().equals("dict")) {
                	n = n.getNextSibling();
                }

                Dict result = (Dict) parseElement(n);
				return result;
        }

        /**
         * Parses a (non-top) xml element.
         *
         * @param element the element to parse.
         * @return the resulting object.
         * @throws XmlParseException if there was some error in the xml.
         */
        private Object parseElement(Node element) throws XmlParseException
        {
                try
                {
                        return parseElementRaw(element);
                }
                catch (Exception e)
                {
                        throw new XmlParseException("Failed to parse: " + element.getNodeName(), e);
                }
        }


        /**
         * Parses a (non-top) xml element.
         *
         * @param element the element to parse.
         * @return the resulting object.
         * @throws ParseException if there was some error parsing the xml.
         */
        private Object parseElementRaw(Node element) throws ParseException 
        {
                ElementType type = ElementType.valueOf(element.getNodeName().toUpperCase());
                switch (type)
                {
                        case INTEGER:
                                return parseInt(getValue(element));
                        case REAL:
                                return Double.valueOf(getValue(element));
                        case STRING:
                                return getValue(element);
                        case DATE:
                                return this.m_dateFormat.parse(getValue(element));
                        case DATA:
                                return base64decode(getValue(element));
                        case ARRAY:
                             	return parseArray(element.getChildNodes());
                        case TRUE:
                                return Boolean.TRUE;
                        case FALSE:
                                return Boolean.FALSE;
                        case DICT:
                                return parseDict(element.getChildNodes());
                        default:
                                throw new RuntimeException("Unexpected type: " + element.getNodeName());
                }
        }
        
        /*************************************************************************
         * @param n
         * @return
         ************************************************************************/
        
        private String getValue(Node n) {
        	StringBuilder sb = new StringBuilder();
        	Node c = n.getFirstChild();
        	while (c != null) {
        		if (c.getNodeType() == Node.TEXT_NODE) {
        			sb.append(c.getNodeValue());
        		}
        		c = c.getNextSibling(); 
        	}
        	return sb.toString();
        }

        /**
         * Parses a string into a Long or Integer depending on size.
         *
         * @param value the value as a string.
         * @return the long value of this string is the value doesn't fit in an integer,
         * otherwise the int value of the string.
         */
        private Number parseInt(String value)
        {
                Long l = Long.valueOf(value);
                if (l.intValue() == l) {
					return l.intValue();
				}
                return l;
        }

        /**
         * Parse a list of xml elements as a plist dict.
         *
         * @param elements the elements to parse.
         * @return the dict deserialized as a map.
         * @throws ParseException if there are any problems deserializing the map.
         */
        private Dict parseDict(NodeList elements) throws ParseException 
        {
        		Dict dict = new Dict();
            	for (int i = 0; i < elements.getLength(); i++) {
                		Node key = elements.item(i);
                		if (key.getNodeType() != Node.ELEMENT_NODE) {
                			continue;
                		}
                        if (!"key".equals(key.getNodeName())) {
							throw new ParseException("Expected key but was " + key.getNodeName(), -1);
						}
                        i++;
                		Node value = elements.item(i);
                		while (value.getNodeType() != Node.ELEMENT_NODE) {
                            i++;
                    		value = elements.item(i);
                		}
                        Object o = parseElementRaw(value);
                        String dictName = getValue(key);
						dict.children.put(dictName, o);
                }
                return dict;
        }
      
        /**
         * Parse a list of xml elements as a plist array.
         *
         * @param elements the elements to parse.
         * @return the array deserialized as a list.
         * @throws ParseException if there are any problems deserializing the list.
         */
        private List<Object> parseArray(NodeList elements) throws ParseException
        {
                ArrayList<Object> list = new ArrayList<Object>();
                for (int i = 0; i < elements.getLength(); i++) {
                	Node o = elements.item(i);
                	if (o.getNodeType() != Node.ELEMENT_NODE) {
            			continue;
            		}                        
                	list.add(parseElementRaw(o));
                }
                return list;
        }

        /**
         * Encode an array of bytes to a string using base64 encoding.
         *
         * @param bytes the bytes to convert.
         * @return the base64 representation of the bytes.
         */
        static String base64encode(byte[] bytes)
        {
                StringBuilder builder = new StringBuilder(((bytes.length + 2)/ 3) * 4);
                for (int i = 0; i < bytes.length; i += 3)
                {
                        byte b0 = bytes[i];
                        byte b1 = i < bytes.length - 1 ? bytes[i + 1] : 0;
                        byte b2 = i < bytes.length - 2 ? bytes[i + 2] : 0;
                        builder.append(BASE64_CHARS[(b0 & 0xFF) >> 2]);
                        builder.append(BASE64_CHARS[((b0 & 0x03) << 4) | ((b1  & 0xF0) >> 4)]);
                        builder.append(i < bytes.length - 1 ? BASE64_CHARS[((b1 & 0x0F) << 2) | ((b2 & 0xC0) >> 6)] : "=");
                        builder.append(i < bytes.length - 2 ? BASE64_CHARS[b2 & 0x3F] : "=");
                }
                return builder.toString();
        }

        /**
         * Converts a string to a byte array assuming the string uses base64-encoding.
         *
         * @param base64 the string to convert.
         * @return the resulting byte array.
         */
        static byte[] base64decode(String base64)
        {
                base64 = base64.trim();
                int endTrim = base64.endsWith("==") ? 2 : base64.endsWith("=") ? 1 : 0;
                int length = (base64.length() / 4) * 3 - endTrim;
                base64 = base64.replace('=', 'A');
                byte[] result = new byte[length];
                int stringLength = base64.length();
                int index = 0;
                for (int i = 0; i < stringLength; i += 4)
                {
                        int i0 = BASE64_STRING.indexOf(base64.charAt(i));
                        int i1 = BASE64_STRING.indexOf(base64.charAt(i + 1));
                        int i2 = BASE64_STRING.indexOf(base64.charAt(i + 2));
                        int i3 = BASE64_STRING.indexOf(base64.charAt(i + 3));
                        byte b0 = (byte) ((i0 << 2) | (i1 >> 4));
                        byte b1 = (byte) ((i1 << 4) | (i2 >> 2));
                        byte b2 = (byte) ((i2 << 6) | i3);
                        result[index++] = b0;
                        if (index < length)
                        {
                                result[index++] = b1;
                                if (index < length)
                                {
                                        result[index++] = b2;
                                }
                        }
                }
                return result;
        }
        
        
  
 
}

