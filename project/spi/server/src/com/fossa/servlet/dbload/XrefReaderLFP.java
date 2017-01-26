/*
 * XrefReaderLFP.java
 *
 * Created on December 10, 2007, 6:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.dbload;

import com.fossa.servlet.common.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
public class XrefReaderLFP extends XrefReader {

    // valid line types

    final private static String IM = "IM";
    final private static String IS = "IS";
    final private static String LC = "LC";

    // indicator for explicit volume and path

    final private static String EXPLICIT_PATH = "@";
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    /**
     * Create a new XrefReaderLFP for the given XrefAcceptor.
     */
    public XrefReaderLFP(XrefAcceptor acceptor) {
        super(acceptor);
    }

    /**
     * Accept and process the given input line.
     */
    public void acceptLine(String line) {

        try {
            // Break line into fields, on comma boundaries
            String[] fields = line.split(",");

            String lineType = fields[0].trim().toUpperCase();
            if (lineType.equals(IM) || lineType.equals(IS)) {
                if (fields.length < 2 || fields[1].trim().length() == 0) {
                    throw new ErrorException("Missing Bates number");
                }
            }

            // Dispatch, depending on line type
            if (lineType.equals(IM)) {
                acceptIM(fields);
            } else if (lineType.equals(IS)) {
                acceptIS(fields);
            } else if (lineType.equals(LC)) {
            // Ignore LC
            } else {
                //throw new ErrorException("invalid line type: "+lineType);
                Log.print("Unknown line type: " + lineType);
            }

        } catch (ErrorException e) {
            logger.error("Exception while reading lfp file" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            error(e.getMessage());
        }
    }

    // Accept IM line

    private void acceptIM(String[] fields) throws ErrorException {

        if (fields.length < 5 || fields.length > 6) {
            throw new ErrorException("Invalid number of comma-separated fields: " + fields.length);
        }

        ImageXref data = new ImageXref();

        // Obtain Bates (already checked that it is non-blank)
        data.bates = fields[1].trim().toUpperCase();

        // Obtain and check boundary flag.
        String boundaryString = fields[2].trim();
        if (boundaryString.length() > 1) {
            error("Boundary is not a single character: " + boundaryString);
        }
        char boundaryChar = (boundaryString.length() == 0 ? ' ' : boundaryString.charAt(0));
        if (boundaryChar == ' ' || boundaryChar >= 'A' && boundaryChar <= 'Z') {
            data.boundary = boundaryChar;
        } else {
            error("Boundary is not space or capital letter: " + boundaryChar);
        }

        // Obtain and check offset.
        try {
            data.offset = Integer.parseInt(fields[3].trim());
            if (data.offset < 0) {
                error("Offset is negative: " + data.offset);
                data.offset = 0;
            }
        } catch (NumberFormatException e) {
            // Not an integer.  Take blank as 0.
            if (fields[3].trim().length() > 0) {
                logger.error("Exception while getting the offset." + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
                error("Offset is not an integer: " + fields[3].trim());
            }
        }

        // Obtain and check volume/file/path
        String subFields[] = fields[4].trim().split(";");
        String fileTypeString = "";
        if (subFields[0].trim().startsWith(EXPLICIT_PATH)) {

            if (subFields.length < 3 || subFields.length > 4) {
                throw new ErrorException("Wrong number of semicolon-separated fields in file path");
            }
            data.volume = subFields[0].trim().substring(1).toUpperCase();
            data.originalVolume = subFields[0].trim().substring(1).toUpperCase();
            if (data.volume.length() == 0) {
                throw new ErrorException("Missing volume name");
            }

            // get path, changing backslash to forward slash
            StringBuffer pathBuffer = new StringBuffer(subFields[1].trim());
            int pos;
            while ((pos = pathBuffer.indexOf("\\")) >= 0) {
                pathBuffer.setCharAt(pos, '/');
            }
            data.path = pathBuffer.toString();

            data.fileName = subFields[2].trim();
            if (subFields.length > 3) {
                fileTypeString = subFields[3].trim();
            }
        } else {
            if (subFields.length > 2) {
                throw new ErrorException("Too many semicolon-separated fields in file path");
            }
            data.fileName = subFields[0].trim();
            if (subFields.length > 1) {
                fileTypeString = subFields[1].trim();
            }
        }
        try {
            if (fileTypeString.length() == 0) {
                data.fileType = 0;
            } else {
                data.fileType = Integer.parseInt(fileTypeString);
                if (data.fileType < 0) {
                    error("File type is negative: " + data.fileType);
                    data.fileType = 0;
                }
            }
        } catch (NumberFormatException e) {
            // Not an integer.  Take blank as 0.
            if (fields[2].trim().length() > 0) {
                logger.error("Exception while getting the file type ." + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
                error("File type is not an integer: " + fileTypeString);
            }
        }

        // Obtain and check rotation, if given
        if (fields.length >= 6) {
            try {
                data.rotation = Integer.parseInt(fields[5].trim());
                if (data.rotation < ROTATE_0 || data.rotation > ROTATE_270) {
                    error("Invalid rotation: " + data.rotation);
                    data.rotation = ROTATE_0;
                }
            } catch (NumberFormatException e) {
                // Not an integer.  Take blank as 0.
                if (fields[5].trim().length() > 0) {
                    logger.error("Exception while getting the rotation" + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    error("Rotation is not an integer: " + fields[5].trim());
                }
            }
        }

        // Accept the data.
        accept(data);
    }

    // Accept IS line

    private void acceptIS(String[] fields) throws ErrorException {

        IssueXref data = new IssueXref();

        // Obtain Bates (already checked that it is non-blank)
        data.bates = fields[1].trim().toUpperCase();

        // Obtain issue name
        if (fields.length < 3 || fields[2].length() == 0) {
            throw new ErrorException("No issue name given");
        }
        data.issueName = fields[2];

        // Accept the data.
        accept(data);
    }
}
