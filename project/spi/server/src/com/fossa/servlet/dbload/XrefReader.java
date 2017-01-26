/*
 * XrefReader.java
 *
 * Created on December 10, 2007, 6:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

/**
 *
 * @author Bala
 */
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
     * Accept the three file name and  constructing the xref record and l1 record
     * Implementation should create Xref's and call 
     * @param brs_fileName A  BRS file name.
     * @param img_fileName A  IMG file name.
     * @param txt_fileName A  TXT file name or source file.
     */
        
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
