/*
 * Dbase3Reader.java
 *
 * Created on December 10, 2007, 6:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 *
 * @author Bala
 */


public class Dbase3Reader extends Reader {

    final private static byte DBASE3_MAGIC  = 0x03;
    final private static byte END_OF_HEADER = 0x0D;
    final private static byte END_OF_DATA   = 0x1A;
    final private static byte DELETED       = (byte) '*';
    final private static byte SPACE         = (byte) ' ';

    private DataInputStream stream;
    private byte[]          buffer = new byte[32];    
    private String[]        fieldName;
    private char[]          fieldType;
    private int[]           fieldLength;
    private int[]           fieldDecimals;
    private int[]           fieldOffset;
    private char[]          cbuf;
    private int             cbufOfs = 0;
    private int             cbufEnd = 0;
    private StringBuffer    sbuf;
    private byte[]          aOneByteArray = new byte[0];
    private boolean         endOfData = false;
    private int             recordLength;
    private int             fieldCount;

    final private static String hexTable = "0123456789ABCDEF";

    public Dbase3Reader(InputStream inStream) throws IOException {
        if (! (inStream instanceof BufferedInputStream)) {
            inStream = new BufferedInputStream(inStream);
        }
        stream = new DataInputStream(inStream);

        // Read the header
        readFully(stream,buffer,0,32);

        if (buffer[0] != DBASE3_MAGIC) {
            throw new IOException("Not a Dbase III file ("+hex(buffer,0,10)+")");
        }

        int numberOfDataRecs = int4(buffer,4);
        int headerLength     = int2(buffer,8);
        recordLength     = int2(buffer,10);
        fieldCount       = headerLength / 32 - 1;

        int expectedHeaderLength = fieldCount * 32 + 33;
        if (headerLength != expectedHeaderLength) {
            System.out.println("Wrong header length ("+headerLength+")");
        }

        fieldName =     new String[fieldCount];
        fieldType =     new char[fieldCount];
        fieldLength =   new int[fieldCount];
        fieldDecimals = new int[fieldCount];
        fieldOffset =   new int[fieldCount];  
        fieldOffset[0] = 1;

        for (int i = 0; i < fieldCount; i++) {

            stream.readFully(buffer);

            fieldName[i] = trimTrailingNulls(new String(buffer, 0, 11)).trim();
            fieldType[i] = (char) buffer[11];
            fieldLength[i] = buffer[16] & 0xFF;
            fieldDecimals[i] = buffer[17] & 0xFF;
            if (i != 0) {
                fieldOffset[i] = fieldOffset[i-1] + fieldLength[i-1];
            }
        }

        readFully(stream,buffer,0,1);
        if (buffer[0] != END_OF_HEADER) {
            throw new IOException("Missing end-of header ("+hex(buffer,0,1)+")");
        }

        if (numberOfDataRecs == 0) {
            // Note.  Have seen such with no end-of-data marker - avoid random crash
            throw new IOException("no data in file");
        }

        buffer = new byte[recordLength];
        cbuf = new char[recordLength + fieldCount];
        sbuf = new StringBuffer(recordLength + fieldCount);
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public String getFieldName(int index) {
        return fieldName[index];
    }

    public char getFieldType(int index) {
        return fieldType[index];
    }

    public int getFieldLength(int index) {
        return fieldLength[index];
    }

    public int getFieldDecimals(int index) {
        return fieldDecimals[index];
    }

    public int read(char[] buf, int ofs, int len) throws IOException {
        for (;;) {
            // there's still unread stuff, return as much as possible
            if (cbufEnd > cbufOfs) {
                int lenRead = Math.min(len, cbufEnd - cbufOfs);
                System.arraycopy(cbuf, cbufOfs, buf, ofs, lenRead);
                cbufOfs += lenRead;
                return lenRead;
            }
            if (endOfData) {
                return -1;
            }
            // get control byte, test for end
            stream.readFully(buffer,0,1);
            if (buffer[0] == END_OF_DATA) {
                endOfData = true;
                return -1;
            }
            // get rest of record
            stream.readFully(buffer, 1, recordLength - 1);
            if (buffer[0] == DELETED) {
                // skip it if deleted
                continue;
            }
            if (buffer[0] != SPACE) {
                System.out.println("Strange initial char of rec: "+hex(buffer,0,1));
            }
            // clear the StringBuffer and construct comma-delimited list
            sbuf.delete(0,sbuf.length());
            for (int i=0; i < fieldCount; i++) {
                sbuf.append(',');
                sbuf.append((new String(buffer,ofs+fieldOffset[i],fieldLength[i])).trim()); 
            }
            sbuf.append('\n');
            // move to output buffer and keep trying
            cbufOfs = 0;
            cbufEnd = sbuf.length() - 1;
            sbuf.getChars(1,cbufEnd+1,cbuf,0);
        }
    }

    //public int read() throws IOException {
    //    if (endOfData) {
    //        return -1;
    //    }
    //    int count = read(aOneByteArray,0,1);
    //    if (count < 0) {
    //        return -1;
    //    } else {
    //        return aOneByteArray[0] & 0Xff;
    //    }
    //}

    public void close() throws IOException {
        stream.close();
    }

    private void readFully(DataInputStream stream, byte[] buf, int off, int len)
    throws IOException {
        stream.readFully(buf, off, len);
        
    }

    // Extract little-endian short from buffer
    // Used so that the program still works on big-endian, e.g. Mac
    private short int2(byte[] buf, int off) {
        return (short) ((buf[off+1] << 8) | (buf[off+0] & 0xFF));
    }

    // Extract little-endian int from buffer
    // Used so that the program still works on big-endian, e.g. Mac
    private int int4(byte[] buf, int off) {
        return (int) ((buf[off+3] << 24) | ((buf[off+2] & 0xFF) << 16) | ((buf[off+1] & 0xFF) << 8) | (buf[off+0] & 0xFF));
    }

    // Return hex for a sequence of bytes
    private String hex(byte[] buf, int off, int len) {
        char[] cbuf = new char[2*len];
        for (int i=0; i < len; i++) {
            int hi = (buf[off+i] >> 4) & 0x0F; 
            int lo = (buf[off+i]) & 0x0F; 
            cbuf[2*i] = hexTable.charAt(hi);
            cbuf[2*i+1] = hexTable.charAt(lo);
        }
        return new String(cbuf);
    }

    //private String dump(byte[] buf, int off, int len) {
    //    StringBuffer sbuf = new StringBuffer((len*9)/4);
    //    for (int i=0; i < len; i+=4) {
    //        sbuf.append(" ");
    //        sbuf.append(hex(buf,off+i,Math.min(4,len-i)));
    //    }
    //    return sbuf.toString();
    //}

    // trim trailing nulls from a string
    private String trimTrailingNulls(String str) {
        int len = str.length();
        while (len > 0 && str.charAt(len - 1) == ' ') {
            len --;
        }
        return str.substring(0, len);
    }
}
