/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefAcceptor.java,v 1.3 2003/11/24 21:02:31 weaston Exp $ */
package dbload;

/**
 * Interface for callbacks from XrefReader.  This is to allow the format-dependent reader
 * module to pass multiple results to the caller while it works.
 */
public interface XrefAcceptor {

    /** Receive an Xref specification */
    void accept(Xref spec);

    /** Receive an error message for the current source line */
    void error(String message);
}

