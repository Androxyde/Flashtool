package org.ta.parsers;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TAFileParser {
    private static final Pattern COMMENT_LINE_PATTERN = Pattern.compile("^//.*");
    private static final Pattern BLANK_LINE_PATTERN = Pattern.compile("^[\\s]*$");
    private static final Pattern PARTITION_LINE_PATTERN = Pattern.compile("^\\p{XDigit}{2}$");
    private static final Pattern DATA_LINE_PATTERN = Pattern.compile("^(\\p{XDigit}{8})(?: |\\t)+(\\p{XDigit}{4,8})((?:(?: |\\t)+\\p{XDigit}{2}{2})*)\\s*$");
    private static final Pattern CONTINUATION_DATA_LINE_PATTERN = Pattern.compile("^((?:(?: |\\t)+\\p{XDigit}{2})+)\\s*$");
    private BufferedReader aReader;
    protected int aPartition;
    private long aUnit;
    private int aUnitSize;
    private ArrayList<String> aContinuationUnitDataList = new ArrayList();
    private byte[] aUnitDataArray;
    private boolean aFoundPartition = false;
    private boolean aContinuationData = false;
    private Vector<TAUnit> units = new Vector<TAUnit>();
    private File tafile;

    public TAFileParser(File taf)  throws TAFileParseException, IOException {
    	tafile=taf;
    	FileInputStream inputStream = new FileInputStream(taf);
    	this.parse(inputStream);
        inputStream.close();
    }

    public String getName() {
    	return tafile.getName();
    }
    
    public TAFileParser(InputStream inputStream) throws TAFileParseException, IOException {
        this.parse(inputStream);
        inputStream.close();
    }


    public void parse(InputStream inputStream) throws TAFileParseException, IOException {
        this.aReader = new BufferedReader(new InputStreamReader(inputStream));
        this.aPartition = -1;
        this.aFoundPartition = false;
        this.aContinuationData = false;
        this.parsePartition();
        while (getNextUnit()>0) {
        	getUnitData();
        	TAUnit unit = new TAUnit((int)this.aUnit,this.aUnitDataArray);
        	units.addElement(unit);
        }
    }

    public Vector<TAUnit> entries() {
    	return units;
    }

    public int getPartition() {
        return this.aPartition;
    }


    public boolean overridePartition() {
        return false;
    }


    public long getNextUnit() throws TAFileParseException, IOException {
        this.aUnitDataArray = null;
        this.aContinuationUnitDataList = new ArrayList();
        if (this.aContinuationData) {
            throw new TAFileParseException("Expecting more data from unit :[" + this.aUnit + "] before reading next unit.");
        }
        String string = "";
        while ((string = this.aReader.readLine()) != null) {
            if (this.matchDataLine(string)) {
                return this.aUnit;
            }
            if (this.matchComment(string) || this.matchBlankLine(string)) continue;
            throw new TAFileParseException("Expected unit data line, at line:[" + string + "].");
        }
        return -1;
    }

    public byte[] getUnitData() throws TAFileParseException, IOException {
        while (this.aContinuationData) {
            String string = "";
            string = this.aReader.readLine();
            if (string == null) {
                throw new TAFileParseException("Expected more data for unit:[" + this.aUnit + "].");
            }
            if (this.matchContinuationDataLine(string)) continue;
            throw new TAFileParseException("Expected more data for unit:[" + this.aUnit + "] at line:[" + string + "].");
        }
        return this.aUnitDataArray;
    }

    private int parsePartition() throws TAFileParseException, IOException {
        if (this.aPartition != -1) {
            return this.aPartition;
        }
        String string = "";
        while ((string = this.aReader.readLine()) != null) {
            if (this.matchPartition(string)) {
                return this.aPartition;
            }
            if (this.matchComment(string) || this.matchBlankLine(string)) continue;
            throw new TAFileParseException("Expected partition at line:[" + string + "].");
        }
        throw new TAFileParseException("No partition found in data.");
    }

    private boolean matchComment(String string) {
        Matcher matcher = COMMENT_LINE_PATTERN.matcher((CharSequence)string);
        return matcher.matches();
    }

    private boolean matchPartition(String string) throws TAFileParseException {
        Matcher matcher = PARTITION_LINE_PATTERN.matcher((CharSequence)string);
        if (matcher.matches()) {
            if (this.aFoundPartition) {
                throw new TAFileParseException("Parse exception: Duplicate partitions in data.");
            }
            String string2 = matcher.group();
            this.aPartition = Integer.parseInt(string2, 16);
            if (this.aPartition != 1 && this.aPartition != 2) {
                throw new TAFileParseException("Unexpected partition in data :[" + string2 + "]");
            }
            this.aFoundPartition = true;
            return true;
        }
        return false;
    }

    private boolean matchDataLine(String string) throws TAFileParseException {
        Matcher matcher = DATA_LINE_PATTERN.matcher((CharSequence)string);
        if (matcher.matches()) {
            if (!this.aFoundPartition) {
                throw new TAFileParseException("Parse exception: Expected partition.");
            }
            String string2 = matcher.group(1);
            this.aUnit = Long.parseLong(string2, 16);
            string2 = matcher.group(2);
            this.aUnitSize = Integer.parseInt(string2, 16);
            string2 = matcher.group(3);
            String[] arrstring = string2.trim().split("(?: |\\t)+");
            this.aContinuationUnitDataList = new ArrayList();
            if (arrstring.length < this.aUnitSize) {
                for (int i = 0; i < arrstring.length; ++i) {
                    this.aContinuationUnitDataList.add(arrstring[i]);
                }
                this.aContinuationData = true;
                return true;
            }
            if (arrstring.length > this.aUnitSize) {
                if (this.aUnitSize == 0 && arrstring.length == 1 && arrstring[0].equals("")) {
                    this.aUnitDataArray = new byte[0];
                    return true;
                }
                throw new TAFileParseException("Parse exception: Too much data for unit [" + this.aUnit + "]");
            }
            this.aUnitDataArray = this.asBytes(arrstring);
            return true;
        }
        return false;
    }

    private boolean matchContinuationDataLine(String string) throws TAFileParseException {
        if (!this.aFoundPartition) {
            throw new TAFileParseException("Parse exception: Expected partition.");
        }
        Matcher matcher = CONTINUATION_DATA_LINE_PATTERN.matcher((CharSequence)string);
        if (matcher.matches()) {
            String string2 = matcher.group(1);
            String[] arrstring = string2.trim().split("(?: |\\t)+");
            long l = this.aContinuationUnitDataList.size() + arrstring.length;
            for (int i = 0; i < arrstring.length; ++i) {
                this.aContinuationUnitDataList.add(arrstring[i]);
            }
            if (l < (long)this.aUnitSize) {
                this.aContinuationData = true;
                return true;
            }
            if (l > (long)this.aUnitSize) {
                throw new TAFileParseException("Parse exception: Too much data for unit [" + this.aUnit + "] : " + l);
            }
            this.aUnitDataArray = this.asBytes(this.aContinuationUnitDataList);
            this.aContinuationData = false;
            return true;
        }
        return false;
    }

    private boolean matchBlankLine(String string) {
        Matcher matcher = BLANK_LINE_PATTERN.matcher((CharSequence)string);
        return matcher.matches();
    }

    private byte[] asBytes(String[] arrstring) {
        byte[] arrby = new byte[arrstring.length];
        for (int i = 0; i < arrstring.length; ++i) {
            arrby[i] = (byte)Integer.parseInt(arrstring[i], 16);
        }
        return arrby;
    }

    private byte[] asBytes(Collection<String> collection) {
        byte[] arrby = new byte[collection.size()];
        int n = 0;
        Iterator<String> iterator = collection.iterator();
        while (iterator.hasNext()) {
            arrby[n++] = (byte)Integer.parseInt(iterator.next(), 16);
        }
        return arrby;
    }
}

