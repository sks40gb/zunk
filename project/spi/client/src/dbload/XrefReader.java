
/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefReader.java,v 1.3 2005/01/09 03:54:17 weaston Exp $ */
package dbload;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Parent class for cross-reference readers.  There will be one
 * class for each type of cross-reference files.
 */
public abstract class XrefReader implements XrefConstants {

    // The XrefAcceptor which accepts specs read by this XrefReader.
    XrefAcceptor acceptor;

    /**
     * Create a new XrefReader for the given XrefAcceptor.
     * @param acceptor The XrefAcceptor
     */
    public XrefReader(XrefAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    /**
     * Accept and process the given input line.
     * Implementation should create Xref's and call 
     * @param line A source line.
     */
    public abstract void acceptLine(String line);

    /**
     * Create a Reader for this file.  Default delegates
     * to FileReader; subclasses may override, e.g. to read
     * DBASE files.
     */
    public Reader makeFileReader(String fileName) throws IOException {
        return new FileReader(fileName);
    }

    /**
     * Process end of cross-reference file.  Default implementation
     * does nothing. 
     */
    public void close() {
    }

    /**
     * Return an Xref to the current XrefAcceptor. 
     */
    final protected void accept(Xref aXref) {
        acceptor.accept(aXref);
    }

    /**
     * Return an error message (for the current line) to the current XrefAcceptor. 
     * Note that more than one message may be returned for a line and data may
     * still be accepted.
     */
    final protected void error(String message) {
        acceptor.error(message);
    }

    /**
     * Exception to return an error message (for the current line) to the
     * current XrefAcceptor.  Alternative to error(), provided as convenience.
     * Implementations should catch this in acceptLine and call error().
     * Intended for cases where scanning of the line should terminate.
     */
    final protected class ErrorException extends Exception {
        ErrorException (String message) {
            super(message);
        }
    }

}
