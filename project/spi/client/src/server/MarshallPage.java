/* $Header: /home/common/cvsarea/ibase/dia/src/server/MarshallPage.java,v 1.33.6.4 2006/05/10 14:25:11 nancy Exp $ */
package server;


import common.CodingData;
import common.ImageData;
import common.Log;
import common.msg.MessageConstants;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * Given parameters, find the request page in the database.
 * @see MarshallQAPage
 */
public class MarshallPage implements MessageConstants {

    final protected static boolean DEBUG = false;
    protected Connection con;
    protected Statement st;
    
    protected ServerTask task;
    protected int volumeId;
    private int batchId = 0;
    private boolean isBinder = false;
    private String whichStatus;

    /**
     * Factory method to create MarshallPage.  Creates subclass
     * MarshallQAPage if this request is from a QA user.
     */
    public static MarshallPage makeInstance(ServerTask task, Element action)
    throws SQLException {
        String statusString = action.getAttribute(A_STATUS);
        if ("QA".equals(statusString)) {
            MarshallQAPage result = new MarshallQAPage(task);
            return result;
        } else {
            MarshallPage result;
            result = new MarshallPage(task, statusString);
            return result;
        }
    }

    /**
     * Create a new <code>MarshallPage</code>.
     * @param statusString the status of the client user
     */
    protected MarshallPage(ServerTask task, String statusString)
    throws SQLException {
        this.task = task;
        volumeId = task.getVolumeId();
        con= task.getConnection();
        st = task.getStatement();
        whichStatus = statusString;
        isBinder = "Binder".equals(whichStatus);
        if(isBinder) {
            // Make sure volumeId is for the binder volume and get batch
            // Note.  there is only one binder volume and batch
            ResultSet rs = task.getStatement().executeQuery(
                "select V.volume_id, B.batch_id"
                +" from volume V0"
                +"   inner join volume V using (project_id)"
                +"   inner join batch B using (volume_id)"
                +" where V0.volume_id="+volumeId
                +"   and V.sequence=-1");
            if (! rs.next()) {
                throw new ServerFailException(
                    "There is no binder for this project.");
            }
            volumeId = rs.getInt(1);
            batchId = rs.getInt(2);
            rs.close();
        } else { // since it's not for the binder
            batchId = task.getBatchId();
        }
    }

    /**
     * Return the volume determined for this MarshallPage instance.
     * @return the volume.volume_id
     */
    public int getVolumeId() {
        return volumeId;
    }

    /**
     * Return batch determined for this MarshallPage.
     */
    public int getBatchId() {
        return batchId;
    }

    /**
     * Find page, given relative position of page or child in entire batch.
     * @param delta the relative position of the return page to the beginning
     * or end of the batch; cannot be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @return the page.page_id that fits the given parameters
     */
    public int findPositionInBatch(int delta, int boundary) throws SQLException {
        //Log.print("findPositionInBatch "+delta+" "+boundary);
        long startTime = System.currentTimeMillis();
        int result = 0;
        assert delta != 0;  // delta == 0 handled by caller
        String sql;
        switch (boundary) {
        case B_NONE:
            assert (! isBinder);
            sql = 
                "select P.page_id"
                +" from page P, batch B"
                +" where B.batch_id="+batchId
                +"   and P.volume_id = B.volume_id"
                +"   and P.seq between B.lft and B.rgt"
                +(delta > 0 ? " order by P.seq limit "+(delta - 1)+",1"
                            : " order by P.seq desc limit "+(- delta - 1)+",1");
            break;
        case B_CHILD:
            sql = 
                "select P.page_id"
                +" from page P"
                +"   inner join child C on P.volume_id=C.volume_id and P.seq=C.lft"
                +"   , batch B"
                +" where B.batch_id="+batchId
                +"   and C.volume_id = B.volume_id"
                +"   and C.lft between B.lft and B.rgt"
                +(delta > 0 ? " order by C.lft limit "+(delta - 1)+",1"
                            : " order by C.rgt desc limit "+(- delta - 1)+",1");
            break;
        default:
            Log.quit("MarshallPage.findPositionInBatch: invalid boundary: "+boundary);
            sql = null;
        }
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        //Log.print("MarshallPage.findPositionInBatch: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }


    /**
     * Find the first page of first child, within the batch, that has not been coded.
     * @return The page_id of the page; 0 if the page was not found.
     */
    public int findUncoded() throws SQLException {
        //Log.print("findUncoded");
        long startTime = System.currentTimeMillis();
        int result = 0;
        ResultSet rs;
        if (whichStatus.startsWith("U")) {
            rs = st.executeQuery(
                "select P.seq"
                +" from batchuser BU"
                +"   inner join page P"
                +"      on P.page_id=BU.last_unitized_page_id"
                +" where BU.batch_id="+batchId);
            int highUnitizedSeq = (rs.next() ? rs.getInt(1) : 0);
            rs = st.executeQuery(
                "select page_id"
                +" from page P"
                +"   inner join batch B"
                +"     on P.volume_id=B.volume_id"
                +"       and P.seq between B.lft and B.rgt"
                +" where P.seq > "+highUnitizedSeq
                +"   and B.batch_id="+batchId
                +" order by seq"
                +" limit 1");
        } else {
            rs = st.executeQuery(
                "select P.page_id"
                +" from child C, batch B, page P"
                +" left join childcoded CC"
                +"   on P.child_id =  CC.child_id"
                +"   and CC.round = 0"
                +" where B.batch_id = "+batchId
                +"   and C.volume_id = B.volume_id"
                +"   and C.lft between B.lft and B.rgt"
                +"   and P.volume_id = C.volume_id"
                +"   and P.seq = C.lft"
                +"   and CC.child_id is null"
                +" order by C.lft"
                +" limit 1");
        }
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        //Log.print("MarshallPage.findUncoded: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }

    /**
     * Return the page.page_id of a page of type <code>boundary</code> and
     * <code>delta</code> number of boundaries away from <code>pageId</code>
     * Note: The page returned can be beyond the limit of the current batch
     * @param pageId must be > 0, the page.page_id of the current page
     * @param delta the relative position of the return page to pageId; can be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @param findEnd true to find the last child in the range; false for the first
     * @return the page.page_id that fits the given parameters
     */
    public int findRelative(int pageId, int delta, int boundary, boolean findEnd)
    throws SQLException {
        //Log.print("findRelative "+pageId+" "+delta+" "+boundary+" "+findEnd);
        long startTime = System.currentTimeMillis();

        String sql;
        switch (boundary) {
        case B_NONE:
            //Log.print("case B_NONE");
            if (delta == 0) {
                sql =
                    "select page_id"
                    +" from page P"
                    +" where page_id="+pageId;
            } else {  // since delta != 0
                sql = 
                    "select P2.page_id"
                    +" from page P, page P2"
                    +" where P2.volume_id = P.volume_id"
                    +(delta > 0 ? " and P2.seq > P.seq"
                                : " and P2.seq < P.seq")
                    +"   and P.page_id="+pageId
                    +(delta > 0 ? " order by P2.seq limit "+(delta - 1)+",1"
                                : " order by P2.seq desc limit "+(- delta - 1)+",1");
            }
            break;
        case B_CHILD:
            //Log.print("case B_CHILD");
            sql = 
                "select P2.page_id"
                +" from page P, child C, page P2"
                +" where C.volume_id = P.volume_id"
                +(delta >= 0 ? " and C.rgt >= P.seq"
                            : " and C.lft <= P.seq")
                +"   and P.page_id="+pageId
                +"   and P2.volume_id=C.volume_id"
                +"   and P2.seq="+(findEnd ? "C.rgt" : "C.lft")
                +(delta >= 0 ? " order by C.rgt limit "+delta+",1"
                             : " order by C.lft desc limit "+(- delta)+",1");
            break;
        case B_RANGE:
            //Log.print("case B_RANGE");
            sql = 
                "select P2.page_id"
                +" from page P, range R, page P2"
                +" where R.volume_id = P.volume_id"
                +(delta >= 0 ? " and R.rgt >= P.seq"
                            : " and R.lft <= P.seq")
                +"   and P.page_id="+pageId
                +"   and P2.volume_id=R.volume_id"
                +"   and P2.seq="+(findEnd ? "R.rgt" : "R.lft")
                +(delta >= 0 ? " order by R.rgt limit "+delta+",1"
                             : " order by R.lft desc limit "+(- delta)+",1");
            break;
        default:
            Log.quit("MarshallPage: invalid boundary.findRelative: "+boundary);
            sql = null;
        }

        int result = 0;
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();

        //Log.print("MarshallPage.findRelative: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }

    /**
     * Return the page.page_id of a page of type <code>boundary</code> and
     * <code>delta</code> number of boundaries away from <code>pageId</code>
     * @param pageId must be > 0, the page.page_id of the current page
     * @param delta the relative position of the return page to pageId; can be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @return the page.page_id that fits the given parameters
     */
    public int findRelativeInBatch(int pageId, int delta, int boundary)
    throws SQLException {
        //Log.print("findRelativeInBatch pageId="+pageId+" delta="+delta+" boundary="+boundary);
        long startTime = System.currentTimeMillis();

        String sql;
        switch (boundary) {
        case B_NONE:
            if (delta == 0) {
                sql = 
                    "select page_id"
                    +" from page P"
                    +"  inner join child C using(child_id)"
                    +" where P.page_id="+pageId
                    +"   and C.batch_id="+batchId;
            } else { // since delta != 0
                sql = 
                    "select P2.page_id"
                    +" from page P, page P2, batch B"
                    +" where P2.volume_id = P.volume_id"
                    +(delta > 0 ? " and P2.seq > P.seq"
                                : " and P2.seq < P.seq")
                    +"   and P.page_id="+pageId
                    +"   and B.batch_id="+batchId
                    +"   and P2.seq between B.lft and B.rgt"
                    +(delta > 0 ? " order by P2.seq limit "+(delta - 1)+",1"
                                : " order by P2.seq desc limit "+(- delta - 1)+",1");
            }
            break;
        case B_CHILD:
            sql = 
                "select P2.page_id"
                +" from page P, child C, batch B, page P2"
                +" where C.volume_id = P.volume_id"
                +(delta > 0 ? " and C.rgt >= P.seq"
                            : " and C.lft <= P.seq")
                +"   and P.page_id="+pageId
                +"   and B.batch_id="+batchId
                +"   and C.rgt between B.lft and B.rgt"
                +"   and P2.volume_id=C.volume_id"
                //+"   and P2.seq="+(findEnd ? "C.rgt" : "C.lft")
                +"   and P2.seq=C.lft"
                +(delta > 0 ? " order by C.rgt limit "+delta+",1"
                             : " order by C.lft desc limit "+(- delta)+",1");
            break;
        //// (Should never go to relative range on coding panel.)
        //case B_RANGE:
        //    sql = 
        //        "select lft,rgt"
        //        +" from page P, range R"
        //        +(delta > 0 ? " and R.rgt >= P.seq"
        //                    : " and R.lft <= P.seq")
        //        +"   and P.page_id="+pageId
        //        +"   and R.batch_id="+batchId
        //        +(delta >= 0 ? " order by R.rgt limit "+delta+",1"
        //                     : " order by R.lft desc limit "+(- delta)+",1");
        //    break;
        default:
            Log.quit("MarshallPage: invalid boundary.findRelativeInBatch: "+boundary);
            sql = null;
        }

        int result = 0;
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            //Log.print("findRelativeInBatch: found");
            result = rs.getInt(1);
        }
        rs.close();

        //Log.print("MarshallPage.findRelativeInBatch: result="+result+" "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }


    public int findAbsolute(String bates) throws SQLException {
        //Log.print("findAbsolute: "+bates);
        long startTime = System.currentTimeMillis();
        PreparedStatement ps = con.prepareStatement(
            "select page_id"
            +" from page"
            +" where volume_id=?"
            +"   and bates_number=?");
        ps.setInt(1,volumeId);
        ps.setString(2,bates);
        ResultSet rs = ps.executeQuery();
        int result = 0;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        ps.close();
        //Log.print("MarshallPage.findAbsolute: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }

    // not used
    //public int findAbsoluteInBatch(String bates) throws SQLException {
    //    Log.print("findAbsoluteInBatch: "+bates);
    //    long startTime = System.currentTimeMillis();
    //    PreparedStatement ps = con.prepareStatement(
    //        "select P.page_id"
    //        +" from page P, batch B"
    //        +" where P.volume_id=?"
    //        +"   and P.bates_number=?"
    //        +"   and B.batch_id=?"
    //        +"   and P.seq between B.lft and B.rgt");
    //    ps.setInt(1,volumeId);
    //    ps.setString(2,bates);
    //    ps.setInt(3,batchId);
    //    ResultSet rs = ps.executeQuery();
    //    int result = 0;
    //    if (rs.next()) {
    //        result = rs.getInt(1);
    //    }
    //    ps.close();
    //    Log.print("MarshallPage.findAbsoluteInBatch: "+(System.currentTimeMillis() - startTime)+" ms.");
    //    return result;
    //}
    
    /**
     * Find the page_id corresponding to a given child.
     * (Used by validate_batch.)
     */
    public int findChild(int childId) throws SQLException {
        ResultSet rs = st.executeQuery(
            "select P.page_id"
            +" from child C inner join page P"
            +"     on C.volume_id=P.volume_id and C.lft=P.seq"
            +" where C.child_id="+childId);
        if (! rs.next()) {
            Log.quit("findChild: child not found");
        }
        int result = rs.getInt(1);
        rs.close();
        return result;
    }

    /**
     * For the given page.page_id, create an instance of ImageData for use
     * in retrieving an image from the server.
     * @param pageId the page.page_id for which image data is being requested
     * @return an ImageData instance for the given pageId
     * @see common.ImageData
     */
    public ImageData collectImageData(int pageId) throws SQLException {
        //Log.print("collectImageData: "+pageId);
        long startTime = System.currentTimeMillis();
        ImageData data = new ImageData();
        ResultSet rs = st.executeQuery(
            "select P.seq, B.batch_id, P.bates_number"
            +"    , sum(P2.seq <= P.seq), count(*)"
            +"    , V.lft, V.rgt, B.lft, B.rgt"
            +"    , R.lft, R.rgt, C.lft, C.rgt"
            +"    , P.boundary_flag, P.path, P.filename, P.child_id"
            +"    , V.volume_id, V.volume_name, V.image_path"
            +"    , P.offset, P.group_one_path, P.group_one_filename"
            +"    , P.document_number"
            +" from page P"
            +"   inner join child C using (child_id)"
            +"   inner join range R using (range_id)"
            +"   inner join batch B on C.batch_id=B.batch_id"
            +"   inner join volume V using (volume_id)"
            +"   inner join page P2 on P2.child_id = C.child_id"
            +" where P.page_id="+pageId
            +" group by P.page_id");
        if (! rs.next()) {
            Log.quit("collectImageData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        rs.close();
        //Log.print("MarshallPage.collectImageData: "+(System.currentTimeMillis() - startTime)+" ms.");
        return data;
    }

    /**
     * Fill in the given ImageData instance with the given pageId and
     * ResultSet columns.
     * @param pageId the page.page_id that keys the given ResultSet
     * @param data an instance of ImageData that will be returned to the caller
     * @param rs a ResultSet containing the ImageData information
     */
    protected void storeImageData(int pageId, ImageData data, ResultSet rs)
    throws SQLException {
        data.pageId = pageId;
        int seq = rs.getInt(1);
        data.batchId = rs.getInt(2);
        data.batesNumber = rs.getString(3);
        data.childImagePosition = rs.getInt(4);
        data.childImageCount = rs.getInt(5);
        int vlft = rs.getInt(6);
        int vrgt = rs.getInt(7);
        int blft = rs.getInt(8);
        int brgt = rs.getInt(9);
        int rlft = rs.getInt(10);
        int rrgt = rs.getInt(11);
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);
        if (clft == blft) { data.boundaryInfo |= FIRST_CHILD_OF_BATCH; }
        if (crgt == brgt) { data.boundaryInfo |=  LAST_CHILD_OF_BATCH; }
        if (seq == blft)  { data.boundaryInfo |= FIRST_PAGE_OF_BATCH; }
        if (seq == brgt)  { data.boundaryInfo |=  LAST_PAGE_OF_BATCH; }
        if (rlft == vlft) { data.boundaryInfo |= FIRST_RANGE_OF_VOLUME; }
        if (rrgt == vrgt) { data.boundaryInfo |=  LAST_RANGE_OF_VOLUME; }
        if (clft == vlft) { data.boundaryInfo |= FIRST_CHILD_OF_VOLUME; }
        if (crgt == vrgt) { data.boundaryInfo |=  LAST_CHILD_OF_VOLUME; }
        if (seq == vlft)  { data.boundaryInfo |= FIRST_PAGE_OF_VOLUME; }
        if (seq == vrgt)  { data.boundaryInfo |=  LAST_PAGE_OF_VOLUME; }
        if (seq == clft)  { data.boundaryInfo |= FIRST_PAGE_OF_CHILD; }
        if (seq == crgt)  { data.boundaryInfo |=  LAST_PAGE_OF_CHILD; }
        data.boundaryFlag = rs.getString(14);  // TBD: move to coding?
        data.path = rs.getString(15);
        data.filename = rs.getString(16);
        data.childId = rs.getInt(17);
        data.volumeId = rs.getInt(18);
        data.volumeName = rs.getString(19);
        data.imagePath = rs.getString(20);
        data.offset = rs.getInt(21);
        data.groupOnePath = rs.getString(22);
        data.groupOneFilename = rs.getString(23);
        data.documentNumber = rs.getString(24);
    }

    /**
     * Gather image, type and positional data on the given page, as well
     * as its associated bates.
     * @param pageId the page.page_id of the child whose data is
     * being requested
     * @return an instance of <code>common.CodingData</code> whose fields
     * contain the data for the given <code>pageId</code>
     */
    public CodingData collectCodingData(int pageId) throws SQLException {
        //Log.print("collectCodingData: "+pageId);
        long startTime = System.currentTimeMillis();
        CodingData data = new CodingData();
        ResultSet rs = st.executeQuery(
            "select P.seq, B.batch_id, P.bates_number"
            +"    , sum(P2.seq <= P.seq), count(*)"
            +"    , V.lft, V.rgt, B.lft, B.rgt"
            +"    , R.lft, R.rgt, C.lft, C.rgt"
            +"    , P.boundary_flag, P.path, P.filename, P.child_id"
            +"    , V.volume_id, V.volume_name, V.image_path"
            +"    , P.offset"
            +"    , FCP.bates_number, LCP.bates_number"
            +"    , FRP.bates_number, LRP.bates_number"
            +"    , B.batch_number, C.is_split"
            +"    , P.group_one_path, P.group_one_filename"
            +"    , P.document_number, B.active_group"
            +" from page P"
            +"   inner join child C using (child_id)"
            +"   inner join range R using (range_id)"
            +"   inner join batch B on C.batch_id=B.batch_id"
            +"   inner join volume V using (volume_id)"
            +"   inner join page FCP on FCP.volume_id=C.volume_id"
            +"     and FCP.seq=C.lft"
            +"   inner join page LCP on LCP.volume_id=C.volume_id"
            +"     and LCP.seq=C.rgt"
            +"   inner join page FRP on FRP.volume_id=R.volume_id"
            +"     and FRP.seq=R.lft"
            +"   inner join page LRP on LRP.volume_id=R.volume_id"
            +"     and LRP.seq=R.rgt"
            +"   inner join page P2 on P2.child_id = C.child_id"
            +" where P.page_id="+pageId
            +" group by P.page_id");
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        data.currentBatesOfChild = rs.getString(3);
        data.firstBatesOfChild = rs.getString(22);
        data.lastBatesOfChild = rs.getString(23);
        data.firstBatesOfRange = rs.getString(24);
        data.lastBatesOfRange = rs.getString(25);
        data.batchNumber = rs.getInt(26);
        data.isSplit = rs.getBoolean(27);
        data.groupOnePath = rs.getString(28);
        data.groupOneFilename = rs.getString(29);
        data.documentNumber = rs.getString(30);
        data.activeGroup = rs.getInt(31);
        int seq = rs.getInt(1);
        int blft = rs.getInt(8);
        int brgt = rs.getInt(9);
        rs.close();

        rs = st.executeQuery(
            "select sum(lft <= "+seq+"), count(*)"
            +" from child"
            +" where volume_id="+volumeId
            +"   and lft between "+blft+" and "+brgt);
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result #2");
        }
        data.batchChildPosition = rs.getInt(1);
        data.batchChildCount = rs.getInt(2);
        rs.close();

        //Log.print("MarshallPage.collectCodingData: "+(System.currentTimeMillis() - startTime)+" ms.");
        return data;
    }

    public Map collectErrorFlagData(int childId)
    throws SQLException {
        Map result = null;
        ResultSet rs = st.executeQuery(
            "select field_name, codererror"
            +" from fieldchange"
            +" where child_id="+childId);
        while (rs.next()) {
            if (result == null) {
                result = new HashMap();
            }
            String fieldName = rs.getString(1);
            boolean coderError = rs.getBoolean(2);
            result.put(fieldName, (coderError ? "Yes" : "No"));
        }
        rs.close();
        return result;
    }
}


