/* $Header: /home/common/cvsarea/ibase/dia/src/common/ImageData.java,v 1.7.8.3 2006/08/23 18:34:22 nancy Exp $ */
package common;

/**
 * A container for data required for the image viewer.
 */
public class ImageData {

    /** page_id of the image */
    public int pageId;

    /** batch_id of the batch containing this page */
    // TBD: do we use this?
    public int batchId;

    /** Bates number of the image */
    public String batesNumber;

    /** Boundary flag of the image */
    public String boundaryFlag;

    /** path from volume.image_path to page.filename */
    public String path;

    /** filename that, with volume.image_path and page.filename, identifies image */
    public String filename;

    /** Boundary information flag word. 
     * @see common.msg.MessageConstants
     */
    public int boundaryInfo = 0;

    /** Relative position of image in child */
    public int childImagePosition;

    /** Count of images in child */
    public int childImageCount;

    /** child_id of child containing this page */
    public int childId;

    /** volume_id containing this page.
     * used by admin user to determine volume
     */
    public int volumeId;

    /** name of volume.  Temporary -- eventually use volume_id on image server */
    public String volumeName;
    /** the path to the image on the server */
    public String imagePath;
    /** the path to use for groups when group=1 */
    public String groupOnePath;
    /** the filename to use with groupOnePath when group=1 */
    public String groupOneFilename;
    /** the documentNumber from the brs */
    public String documentNumber;

    /**
     * Page offset in TIFF file.  (FIrst page has offset 0.)
     */
    public int offset;
    //for F10
    public String queryRaised;
    
    public String serverIP_port;
    
}
