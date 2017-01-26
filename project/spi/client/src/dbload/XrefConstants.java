/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/XrefConstants.java,v 1.3.6.3 2006/08/23 19:04:52 nancy Exp $ */
package dbload;

/**
 * Constants for cross-reference files.
 */
public interface XrefConstants {

    /** Rotation 0 degrees */
    final public static int ROTATE_0   = 0;
    /** Rotation 90 degrees */
    final public static int ROTATE_90  = 1;
    /** Rotation 180 degrees */
    final public static int ROTATE_180 = 2;
    /** Rotation 270 degrees */
    final public static int ROTATE_270 = 3;

    /** File is "standard single-image TIFF". */
    final public static int FILE_TYPE_TIF   = 2;
    /** File is color image (.bmp,.pcx,.jpeg,.png). */
    final public static int FILE_TYPE_COLOR = 4;

    /** cross-reference file type LFP (.lfp) -- LFP retained for historical reasons */
    final public static int LFP = 1;
    /** cross-reference file type DOCULEX (.dbf) */
    final public static int DOCULEX = 2;
    /** cross-reference file type OPTICON (.opt) */
    final public static int OPTICON = 3;
    /** cross-reference file type SUMMATION (.dii) */
    final public static int SUMMATION = 4;
    /** cross-reference file type BRS */
    final public static int BRS = 5;

    /** Table of cross-ref file type names */
    final public static String[] TYPE_NAMES
        = {"INVALID", "LFP", "DOCULEX", "OPTICON", "SUMMATION", "BRS" };
}
