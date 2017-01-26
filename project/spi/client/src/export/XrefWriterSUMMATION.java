/* $Header: /home/common/cvsarea/ibase/dia/src/export/XrefWriterSUMMATION.java,v 1.2.6.1 2006/01/10 15:28:16 nancy Exp $ */
package export;

import common.msg.MessageConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Provides method for writing an SUMMATION cross-reference file.
 */
public class XrefWriterSUMMATION extends XrefWriter implements MessageConstants {

    private PrintWriter writer;

    public XrefWriterSUMMATION(String filename, RunStats stats) {
        super(filename, stats);
    }

    // field separator
    String COMMA = ",";

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
        if (j < page.length) {
            PageissueData[] newPage = new PageissueData[j];
            System.arraycopy(page, 0, newPage, 0, j);
            page = newPage;
        }

        // Count images in documents and save last Bates numbers
        int[] imageCount = new int[page.length];
        String[] lastBates = new String[page.length];
        int count = 0;
        String holdBates = null;
        for (int i = page.length - 1; i >= 0; i--) {
            if (page[i].seq == page[i].rgt) {
                count = 0;
                holdBates = page[i].batesNumber;
            }
            count++;
            if (page[i].seq == page[i].lft) {
                imageCount[i] = count;
                lastBates[i] = holdBates;
            }
        }

        // write the .dii file
        FileOutputStream fos = new FileOutputStream(filename) ;
        writer = new PrintWriter(
                     new OutputStreamWriter(fos, MessageConstants.UTF_8));
        int recordNumber = 0;

        for (int i = 0; i < page.length; i++) {

            if (page[i].seq == page[i].lft) {
                recordNumber++;
                writer.println("; Record "+recordNumber);
                writer.println("@C ENDDOC# "+lastBates[i]);
                writer.println("@C PAGEAMT "+imageCount[i]);
                writer.println("@T "+page[i].batesNumber);
                writer.println("@D @V "+page[i].originalVolume
                               +":\\"+slashToBackslash(page[i].path)
                               +(page[i].path.length() == 0 ? "" : "\\"));
            }

            writer.println(page[i].filename); 
            stats.pageCount++;

            if (page[i].seq == page[i].rgt) {
                writer.println();
            }
        }

        writer.close();
    }
}
