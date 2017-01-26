/* $Header: /home/common/cvsarea/ibase/dia/src/export/XrefWriter.java,v 1.3 2005/01/11 04:09:05 weaston Exp $ */
package com.fossa.servlet.writer;

import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.writer.PageissueData;
import com.fossa.servlet.writer.RunStats;
import java.io.IOException;

/**
 * Abstract class for writing cross-reference files.
 * To be extended for each export format
 */
public abstract class XrefWriter implements MessageConstants {

    /** file name to be written */
    protected String filename; // must contain path

    /** A structure used to collect export statistics */
    protected RunStats stats;

    public XrefWriter(String filename, RunStats stats) {
        this.filename = filename;
        this.stats = stats;
    }

    public abstract void writeXref(PageissueData[] page) throws IOException;

    /**
     * Convert slashes to backslashes
     */
    protected String slashToBackslash(String str) {
        StringBuffer buf = new StringBuffer(str);
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) == '/') {
                buf.setCharAt(i, '\\');
            }
        }
        return buf.toString();
    }
}
