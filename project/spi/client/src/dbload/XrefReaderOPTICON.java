/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefReaderOPTICON.java,v 1.2.6.1 2006/01/10 15:28:16 nancy Exp $ */
package dbload;

import common.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * Implementation of cross-reference reader for OPTICON files.
 */
public class XrefReaderOPTICON extends XrefReader {

    /**
     * Create a new XrefReaderOPTICON for the given XrefAcceptor.
     */
    public XrefReaderOPTICON(XrefAcceptor acceptor) {
        super(acceptor);
    }

    /**
     * Accept and process the given input line.
     */
    public void acceptLine(String line) {

        ImageXref data = new ImageXref();

        try {
            // Break line into fields, on comma boundaries
            String[] fields = line.split(",", -1);
            //for (int i = 0; i < fields.length; i++) {
            //    System.out.println(i+": "+fields[i]);
            //}

            if (fields.length < 3) {
                throw new ErrorException("Too few fields");
            }

            data.bates     = fields[0].trim().toUpperCase();
            if (data.bates.length() == 0) {
                throw new ErrorException("Bates Number is blank");
            }

            data.volume    = fields[1].trim();
            data.originalVolume    = fields[1].trim();

            // get path, changing backslash to forward slash and removing drive letter
            StringBuffer pathBuffer = new StringBuffer(fields[2].trim());
            int pos;
            while ((pos = pathBuffer.indexOf("\\")) >= 0) {
                pathBuffer.setCharAt(pos, '/');
            }
            if ((pos = pathBuffer.indexOf(":")) >= 0) {
                pathBuffer.delete(0, pos+1);
            }

            // separate filename from path
            if ((pos = pathBuffer.lastIndexOf("/")) >= 0) {
                data.path = pathBuffer.substring(0, pos);
                data.fileName = pathBuffer.substring(pos+1);
            } else {
                data.path = "";
                data.fileName = pathBuffer.toString();
            }

            data.boundary = ' ';
            if (fields.length > 3 && "Y".equalsIgnoreCase(fields[3].trim())) {
                data.boundary = 'D';
            }
            if (fields.length > 4 && "Y".equalsIgnoreCase(fields[4].trim())) {
                data.boundary = 'F';
            }
            if (fields.length > 5 && "Y".equalsIgnoreCase(fields[5].trim())) {
                data.boundary = 'B';
            }

        } catch (ErrorException e) {
            error(e.getMessage());
        }

        // Accept the data.
        accept(data);
    }
}
