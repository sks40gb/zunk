/* $Header: /home/common/cvsarea/ibase/dia/src/export/PageData.java,v 1.6.6.3 2006/08/23 19:04:52 nancy Exp $ */
package export;

/**
 * Collect the page data required for writing the data output and the LFP.
 */
public class PageData {
    public int seq = 0;
    public int lft = 0; 
    public int rgt = 0;
    public int rangeLft = 0;
    public int rangeRgt = 0;
    public String batesNumber = "";
    public String path = "";
    public String filename = "";
    public String groupOnePath  = ""; // BRS
    public String groupOneFileName  = ""; // BRS
    public String volumeName = "";
    public String originalVolume = "";
    public int fileType = 0;
    public String boundaryFlag = "";
    public int offset = 0;
    public int rotate = 0;
    public String status = "";
    public int batchNumber = 0;
    public String beginBatesNumber = "";
    public String endBatesNumber = "";
    public String documentNumber = "";
}
