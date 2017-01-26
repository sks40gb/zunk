/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/Xref.java,v 1.2.10.2 2006/08/23 19:04:52 nancy Exp $ */
package dbload;

/**
 * Parent class of image cross-references.  When loading a cross-reference
 * file, we collect the cross-references, then insert them all in the
 * database in a single transaction.
 */
public abstract class Xref implements XrefConstants {
    public String bates;
    public String documentNumber = ""; // BRS
    //public char   boundary  = ' ';
    //public int    offset    = 0;
    //public String volume    = "";
    //public String path      = "";
    //public String fileName  = "?unknown?";
    //public int    fileType  = 0;
    //public int    rotation  = ROTATE_0;

}
