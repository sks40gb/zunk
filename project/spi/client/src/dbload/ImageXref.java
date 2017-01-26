/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/ImageXref.java,v 1.2.10.4 2006/08/23 19:04:52 nancy Exp $ */
package dbload;

/**
 * Specification for an image.  This corresponds to an IM line in an LFP file.
 */
public class ImageXref extends Xref {

    //public String bates;
    public char   boundary        = ' ';
    public int    offset          = 0;
    public String volume          = "";
    public String originalVolume = "";
    public String path            = "";
    public String groupOnePath  = ""; // BRS
    public String groupOneFileName  = ""; // BRS
    public String fileName        = "?unknown?";
    public int    fileType        = 0;
    public int    rotation        = ROTATE_0;
    public int    activeGroup     = 0;
}