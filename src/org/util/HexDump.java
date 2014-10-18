package org.util;

import java.io.*;
import java.text.DecimalFormat;

public class HexDump
{

    private HexDump()
    {
    }

    public static synchronized void dump(byte data[], long offset, OutputStream stream, int index, int length)
        throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        if(index < 0 || index >= data.length)
            throw new ArrayIndexOutOfBoundsException((new StringBuilder("illegal index: ")).append(index).append(" into array of length ").append(data.length).toString());
        if(stream == null)
            throw new IllegalArgumentException("cannot write to nullstream");
        long display_offset = offset + (long)index;
        StringBuffer buffer = new StringBuffer(74);
        int data_length = Math.min(data.length, index + length);
        for(int j = index; j < data_length; j += 16)
        {
            int chars_read = data_length - j;
            if(chars_read > 16)
                chars_read = 16;
            buffer.append(dump(display_offset)).append(' ');
            for(int k = 0; k < 16; k++)
            {
                if(k < chars_read)
                    buffer.append(dump(data[k + j]));
                else
                    buffer.append("  ");
                buffer.append(' ');
            }

            for(int k = 0; k < chars_read; k++)
                if(data[k + j] >= 32 && data[k + j] < 127)
                    buffer.append((char)data[k + j]);
                else
                    buffer.append('.');

            buffer.append(EOL);
            stream.write(buffer.toString().getBytes());
            stream.flush();
            buffer.setLength(0);
            display_offset += chars_read;
        }

    }

    public static synchronized void dump(byte data[], long offset, OutputStream stream, int index)
        throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException
    {
        dump(data, offset, stream, index, data.length - index);
    }

    public static String dump(byte data[], long offset, int index)
    {
        if(index < 0 || index >= data.length)
            throw new ArrayIndexOutOfBoundsException((new StringBuilder("illegal index: ")).append(index).append(" into array of length ").append(data.length).toString());
        long display_offset = offset + (long)index;
        StringBuffer buffer = new StringBuffer(74);
        for(int j = index; j < data.length; j += 16)
        {
            int chars_read = data.length - j;
            if(chars_read > 16)
                chars_read = 16;
            buffer.append(dump(display_offset)).append(' ');
            for(int k = 0; k < 16; k++)
            {
                if(k < chars_read)
                    buffer.append(dump(data[k + j]));
                else
                    buffer.append("  ");
                buffer.append(' ');
            }

            for(int k = 0; k < chars_read; k++)
                if(data[k + j] >= 32 && data[k + j] < 127)
                    buffer.append((char)data[k + j]);
                else
                    buffer.append('.');

            buffer.append(EOL);
            display_offset += chars_read;
        }

        return buffer.toString();
    }

    private static String dump(long value)
    {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for(int j = 0; j < 8; j++)
            buf.append(_hexcodes[(int)(value >> _shifts[(j + _shifts.length) - 8]) & 0xf]);

        return buf.toString();
    }

    private static String dump(byte value)
    {
        StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for(int j = 0; j < 2; j++)
            buf.append(_hexcodes[value >> _shifts[j + 6] & 0xf]);

        return buf.toString();
    }

    public static String toHex(byte value[])
    {
    	if (value==null) return "";
    	if (value.length==0) return "";
        StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for(int x = 0; x < value.length; x++)
        {
            retVal.append(toHex(value[x]));
            if (x<value.length-1)
            	retVal.append(", ");
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(byte value[], int bytesPerLine)
    {
        int digits = (int)Math.round(Math.log(value.length) / Math.log(10D) + 0.5D);
        StringBuffer formatString = new StringBuffer();
        for(int i = 0; i < digits; i++)
            formatString.append('0');

        formatString.append(": ");
        DecimalFormat format = new DecimalFormat(formatString.toString());
        StringBuffer retVal = new StringBuffer();
        retVal.append(format.format(0L));
        int i = -1;
        for(int x = 0; x < value.length; x++)
        {
            if(++i == bytesPerLine)
            {
                retVal.append('\n');
                retVal.append(format.format(x));
                i = 0;
            }
            retVal.append(toHex(value[x]));
            retVal.append(", ");
        }

        return retVal.toString();
    }

    public static String toHex(short value)
    {
        return toHex(value, 4);
    }

    public static String toHex(byte value)
    {
        return toHex(value, 2);
    }

    public static String toHex(int value)
    {
        return toHex(value, 8);
    }

    public static String toHex(long value)
    {
        return toHex(value, 16);
    }

    private static String toHex(long value, int digits)
    {
        StringBuffer result = new StringBuffer(digits);
        for(int j = 0; j < digits; j++)
            result.append(_hexcodes[(int)(value >> _shifts[j + (16 - digits)] & 15L)]);

        return result.toString();
    }

    public static void dump(InputStream in, PrintStream out, int start, int bytesToDump)
        throws IOException
    {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if(bytesToDump == -1)
        {
            for(int c = in.read(); c != -1; c = in.read())
                buf.write(c);

        } else
        {
            for(int bytesRemaining = bytesToDump; bytesRemaining-- > 0;)
            {
                int c = in.read();
                if(c == -1)
                    break;
                buf.write(c);
            }

        }
        byte data[] = buf.toByteArray();
        dump(data, 0L, ((OutputStream) (out)), start, data.length);
    }

    public static final String EOL = System.getProperty("line.separator");
    private static final char _hexcodes[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };
    private static final int _shifts[] = {
        60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 
        20, 16, 12, 8, 4, 0
    };

}
