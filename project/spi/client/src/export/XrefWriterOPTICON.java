/* $Header: /home/common/cvsarea/ibase/dia/src/export/XrefWriterOPTICON.java,v 1.2.6.1 2006/01/10 15:28:16 nancy Exp $ */
package export;

import common.msg.MessageConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Provides method for writing an OPTICON cross-reference file.
 */
public class XrefWriterOPTICON extends XrefWriter implements MessageConstants {

    private PrintWriter writer;

    public XrefWriterOPTICON(String filename, RunStats stats) {
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

        // Count images in documents
        int[] imageCount = new int[page.length];
        int count = 0;
        for (int i = page.length - 1; i >= 0; i--) {
            if (page[i].seq == page[i].rgt) {
                count = 0;
            }
            count++;
            if (page[i].seq == page[i].lft) {
                imageCount[i] = count;
            }
        }

        // write the .opt file
        FileOutputStream fos = new FileOutputStream(filename) ;
        writer = new PrintWriter(
                     new OutputStreamWriter(fos, MessageConstants.UTF_8));

        for (int i = 0; i < page.length; i++) {
            writer.println(page[i].batesNumber
                          + COMMA + page[i].originalVolume
                          + COMMA + "C:\\" + slashToBackslash(page[i].path)
                          + "\\" + page[i].filename
                          + (page[i].seq != page[i].lft  ? ",,,,"
                            :page[i].boundaryFlag =="B"  ? ",,,Y," + imageCount[i] 
                            :page[i].boundaryFlag =="F"  ? ",,Y,," + imageCount[i] 
                                                         : ",Y,,," + imageCount[i])); 
            stats.pageCount++;
        }

        writer.close();
    }
}
