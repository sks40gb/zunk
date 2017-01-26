/* $Header: /home/common/cvsarea/ibase/dia/src/export/XrefWriterDOCULEX.java,v 1.1.6.1 2006/01/10 15:28:16 nancy Exp $ */
package com.fossa.servlet.writer;

import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.writer.PageissueData;
import com.fossa.servlet.writer.RunStats;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Provides method for writing an DOCULEX cross-reference file.
 */
public class XrefWriterDOCULEX extends XrefWriter implements MessageConstants {

    final private static byte DBASE3_MAGIC  = 0x03;
    final private static byte END_OF_HEADER = 0x0D;
    final private static byte END_OF_DATA   = 0x1A;
    final private static byte SPACE         = (byte) ' ';

    public XrefWriterDOCULEX(String filename, RunStats stats) {
        super(filename, stats);
    }

    public void writeXref(PageissueData[] page)
    throws IOException {
        // discard multiple issues per image (issues not used)
        int seq = 0;
        int j = 0;
        for (int i = 0; i < page.length; i++ ) {
            if (page[i].seq != seq) {
                seq = page[i].seq;
                page[j] = page[i];
                j++;
            }
        }

        // write the .dbf file
        OutputStream stream = new BufferedOutputStream(new FileOutputStream(filename));

        // write initial block of header
        // 1-byte Dbase level
        stream.write(DBASE3_MAGIC);
        // 3-byte date written
        Calendar cal = new GregorianCalendar();
        stream.write(cal.get(Calendar.YEAR) - 1900);
        stream.write(cal.get(Calendar.MONTH)+1);
        stream.write(cal.get(Calendar.DAY_OF_MONTH));
        // number of data records
        putInt4(stream, page.length);
        // Header Length - 6 fields
        putInt2(stream, 6 * 32 + 33);
        // Record length
        putInt2(stream, 92+6+92+128+14+92+1);
        // Reserved
        putInt4(stream, 0);
        putInt4(stream, 0);
        putInt4(stream, 0);
        putInt4(stream, 0);
        putInt4(stream, 1);

        // write field definitions
        writeFieldDef(stream, "DOC_NAME", 92);
        writeFieldDef(stream, "PAGE_NUM", 6);
        writeFieldDef(stream, "PAGE_NAME", 92);
        writeFieldDef(stream, "PATH", 128);
        writeFieldDef(stream, "VOLUME", 14);
        writeFieldDef(stream, "FILENAME", 92);
        stream.write(END_OF_HEADER);

        String docNum = null;
        int imageCount = 0;
        for (int i = 0; i < page.length; i++) {
            if (page[i].seq == page[i].lft) {
                docNum = page[i].batesNumber;
                imageCount = 0;
            }
            imageCount++;
            stream.write(SPACE);
            stream.write(padBlanks(docNum, 92));
            stream.write(padBlanks(Integer.toString(1000000+imageCount).substring(1), 6));
            stream.write(padBlanks(page[i].batesNumber, 92));
            stream.write(padBlanks(slashToBackslash(page[i].path), 128));
            stream.write(padBlanks(page[i].originalVolume, 14));
            stream.write(padBlanks(page[i].filename, 92));
            stats.pageCount++;
        }

        stream.write(END_OF_DATA);
        stream.close();
    }

    private void writeFieldDef(OutputStream stream, String fieldName, int fieldLength)
    throws IOException {
        stream.write(padNulls(fieldName,11));
        stream.write((byte) 'C');
        putInt4(stream, 0);
        stream.write(fieldLength);
        stream.write(0);  // decimal count
        putInt4(stream, 0);
        putInt4(stream, 0);
        putInt4(stream, 0);
        putInt2(stream, 0);
    }

    private byte[] padNulls(String str, int length) {
        byte[] buf = new byte[length];
        for (int i=0; i < str.length(); i++) {
            buf[i] = (byte) str.charAt(i);
        }
        for (int i=str.length(); i < length; i++) {
            buf[i] = 0;
        }
        return buf;
    }

    private byte[] padBlanks(String str, int length) {
        byte[] buf = new byte[length];
        for (int i=0; i < str.length(); i++) {
            buf[i] = (byte) str.charAt(i);
        }
        for (int i=str.length(); i < length; i++) {
            buf[i] = ' ';
        }
        return buf;
    }

    // write little-endian short
    private void putInt2(OutputStream stream, int n)
    throws IOException {
        stream.write((byte) n);
        stream.write((byte) (n >> 8));
    }

    // write little-endian int
    private void putInt4(OutputStream stream, int n)
    throws IOException {
        stream.write((byte) n);
        stream.write((byte) (n >> 8));
        stream.write((byte) (n >> 16));
        stream.write((byte) (n >> 24));
    }

}
