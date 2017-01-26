/*
 * XrefReaderDOCULEX.java
 *
 * Created on December 10, 2007, 6:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

/**
 *
 * @author Bala
 */
import com.fossa.servlet.common.Log;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import org.apache.log4j.Logger;

/**
 * Implementation of cross-reference reader for DOCULEX files.
 */
public class XrefReaderDOCULEX extends XrefReader {

    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");
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
                logger.error("Exception while getting PAGE_NUM" + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
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
            logger.error("Exception while reading DOCULEX" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            error(e.getMessage());
        }

        // Accept the data.
        accept(data);
    }
}
