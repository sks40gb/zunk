/* $Header: /home/common/cvsarea/ibase/dia/src/export/Attic/XrefWriterBRS.java,v 1.1.2.1 2007/03/21 10:46:12 nancy Exp $ */
package export;

import common.msg.MessageConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Provides method for writing an BRS cross-reference file.
 */
public class XrefWriterBRS extends XrefWriter implements MessageConstants {

    private PrintWriter writer;

    public XrefWriterBRS(String filename, RunStats stats) {
        super(filename, stats);
    }

    private String FIELD_SEPARATOR = "|";

    public void writeXref(PageissueData[] page)
    throws IOException {
        FileOutputStream fos = new FileOutputStream(filename) ;
        writer = new PrintWriter(
                     new OutputStreamWriter(fos, MessageConstants.UTF_8));

        int docCount = 0;
        String doc = "";
        for (int i = 0; i < page.length; i++) {
            if (page[i].boundaryFlag.equals("D")
                || page[i].boundaryFlag.equals("F")) {
                docCount++;
            }
            if (! doc.equals(page[i].documentNumber)) {
                // write the group one image line
                doc = page[i].documentNumber;
                writer.println(page[i].path 
                              + FIELD_SEPARATOR + page[i].groupOneFileName
                              + FIELD_SEPARATOR + "31"
                              + FIELD_SEPARATOR + Integer.toString(docCount)
                               + FIELD_SEPARATOR + "??"
                              + FIELD_SEPARATOR + page[i].documentNumber
                              + FIELD_SEPARATOR + "00000000"

                              + FIELD_SEPARATOR + FIELD_SEPARATOR);
            }
            writer.println(page[i].path 
                          + FIELD_SEPARATOR + page[i].filename
                          + FIELD_SEPARATOR + "31"
                          + FIELD_SEPARATOR + Integer.toString(docCount)
                           + FIELD_SEPARATOR + "??"
                          + FIELD_SEPARATOR + page[i].documentNumber
                          + FIELD_SEPARATOR + page[i].batesNumber
                                              
                          + FIELD_SEPARATOR + FIELD_SEPARATOR);
            stats.pageCount++;
        }

        writer.close();
    }
}
