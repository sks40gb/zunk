/* $Header: /home/common/cvsarea/ibase/dia/src/export/Attic/XrefWriterLFP.java,v 1.1.2.1 2007/03/21 10:46:12 nancy Exp $ */
package export;

import common.msg.MessageConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Provides method for writing an LFP cross-reference file.
 */
public class XrefWriterLFP extends XrefWriter implements MessageConstants {

    private PrintWriter writer;

    public XrefWriterLFP(String filename, RunStats stats) {
        super(filename, stats);
    }

    // valid line types
    final private static String IM = "IM";
    final private static String IS = "IS";

    // indicator for explicit volume and path
    final private static String EXPLICIT_PATH = "@";

    // split volume and filename values 
    final private static String SPLIT = ";";

    // field separator
    String COMMA = ",";

    public void writeXref(PageissueData[] page)
    throws IOException {
        FileOutputStream fos = new FileOutputStream(filename) ;
        writer = new PrintWriter(
                     new OutputStreamWriter(fos, MessageConstants.UTF_8));

        int seq = 0;

        for (int i = 0; i < page.length; i++) {
            // write the lfp line
            writer.println(IM + COMMA + page[i].batesNumber
                          + COMMA + (page[i].boundaryFlag.length() == 0
                                     ? " " : page[i].boundaryFlag)
                          + COMMA + page[i].offset
                          + COMMA + EXPLICIT_PATH + page[i].originalVolume
                          + SPLIT + slashToBackslash(page[i].path)
                          + SPLIT + page[i].filename
                          + SPLIT + page[i].fileType
                          + COMMA + page[i].rotate);
            stats.pageCount++;
            seq = page[i].seq;

            // write the pageissues, if any
            if (page[i].issueName != null
            && ! page[i].issueName.trim().equals("")) {
                // write the issue
                writer.println(IS + COMMA + page[i].batesNumber
                              + COMMA + page[i].issueName);
            }
        }

        writer.close();
    }
}
