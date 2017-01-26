/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefReaderDOCULEX.java,v 1.2.6.1 2006/01/10 15:28:16 nancy Exp $ */
package dbload;

import common.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * Implementation of cross-reference reader for DOCULEX files.
 */
public class XrefReaderDOCULEX extends XrefReader {

    /**
     * Create a new XrefReaderDOCULEX for the given XrefAcceptor.
     */
    public XrefReaderDOCULEX(XrefAcceptor acceptor) {
        super(acceptor);
    }

    /**
     * Create a Reader for this file.  Default delegates
     * to FileReader; subclasses may override, e.g. to read
     * DBASE files.
     */
    public Reader makeFileReader(String fileName) throws IOException {
        Dbase3Reader reader = new Dbase3Reader(new FileInputStream(fileName));
        int fieldCount = reader.getFieldCount();
        boolean badFile = false;
        if (fieldCount == 6) {
            if (! "DOC_NAME".equals(reader.getFieldName(0))
            ||! "PAGE_NUM".equals(reader.getFieldName(1))
            ||! "PAGE_NAME".equals(reader.getFieldName(2))
            ||! "PATH".equals(reader.getFieldName(3))
            ||! "VOLUME".equals(reader.getFieldName(4))
            ||! "FILENAME".equals(reader.getFieldName(5)) ) {
                badFile = true;
            }
        } else {
            badFile = true;
        }
        if (badFile) {
            for (int i = 0; i < fieldCount; i++) {
                Log.print("Field "+i+": "+reader.getFieldName(i));
            }
            throw new IOException("Not a DOCULEX dbf");
        }
        return reader;
    }

    /**
     * Accept and process the given input line.
     */
    public void acceptLine(String line) {

        ImageXref data = new ImageXref();

        try {
            // Break line into fields, on comma boundaries
            String[] fields = line.split(",");
            assert fields.length == 6;

            data.bates     = fields[2].trim().toUpperCase();                // PAGE_NAME
            if (data.bates.length() == 0) {
                throw new ErrorException("Bates Number is missing");
            }
            try {
                data.boundary  = (Integer.parseInt(fields[1].trim()) == 1 ? 'D' : ' ');    // PAGE_NUM
            } catch (NumberFormatException e) {
                throw new ErrorException("PAGE_NUM field not numeric");
            }

            // get path, changing backslash to forward slash
            StringBuffer pathBuffer = new StringBuffer(fields[3].trim());
            int pos;
            while ((pos = pathBuffer.indexOf("\\")) >= 0) {
                pathBuffer.setCharAt(pos, '/');
            }
            data.path      = pathBuffer.toString();

            data.volume    = fields[4].trim();
            data.originalVolume    = fields[4].trim();
            data.fileName  = fields[5].trim();

        } catch (ErrorException e) {
            error(e.getMessage());
        }

        // Accept the data.
        accept(data);
    }
}
