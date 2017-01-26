/* $Header: /home/common/cvsarea/ibase/dia/src/server/MarshallQAPage.java,v 1.5.6.3 2006/05/10 14:25:11 nancy Exp $ */
package server;


import common.CodingData;
import common.ImageData;
import common.Log;
import common.msg.MessageConstants;

import java.sql.*;
import org.w3c.dom.Element;

/**
 * Given parameters, find the request page in the database for
 * a QA user.
 * @see MarshallPage
 */
final public class MarshallQAPage extends MarshallPage {
    int batchId = -1; //// STUB

    /**
     * Create a new MarshallQAPage.  Used only by
     * MarshallPage.makeInstance.
     */
    MarshallQAPage(ServerTask task)
    throws SQLException {
        super(task, "QA");
    }

    /**
     * Return batch determined for this MarshallPage.
     * There should be no batch for QA.
     * TBD: Should we return zero here?
     */
    public int getBatchId() {
        throw new UnsupportedOperationException();    }

    /**
     * Find page, given relative position of child in selected children.
     */
    public int findPositionInBatch(int delta, int boundary) throws SQLException {
        Log.print("findPositionInBatch "+delta+" "+boundary);
        long startTime = System.currentTimeMillis();
        int result = 0;
        assert delta != 0;  // delta == 0 handled by caller
        assert boundary == B_CHILD; // only one for QA
        String sql;
        sql = 
            "select P.page_id"
            +" from page P"
            +"   inner join child C on P.volume_id=C.volume_id and P.seq=C.lft"
            +"   inner join childcoded CC using (child_id)"
            +" where C.volume_id ="+volumeId
            +"   and CC.round = 0"
            +"   and CC.status='QA'"
            +(delta > 0 ? " order by C.lft limit "+(delta - 1)+",1"
                        : " order by C.rgt desc limit "+(- delta - 1)+",1");
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        Log.print("MarshallPage.findPositionInBatch: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }


    /**
     * Find the first page of first selected child, that has not been QAed.
     * @return The page_id of the page; 0 if the page was not found.
     */
    public int findUncoded() throws SQLException {
        Log.print("findUncoded");
        long startTime = System.currentTimeMillis();
        PreparedStatement pst = task.getConnection().prepareStatement(
            "select P.page_id"
            +" from page P"
            +"   inner join child C on P.volume_id=C.volume_id and P.seq=C.lft"
            +"   inner join childcoded CC using (child_id)"
            +" where C.volume_id ="+volumeId
            +"   and CC.round = 0"
            +"   and CC.status='QA'"
            +"   and CC.users_id = 0"
            +" order by C.lft"
            +" limit 1");
        ResultSet rs = pst.executeQuery();
        int result = 0;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        pst.close();
        Log.print("MarshallPage.findUncoded: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }

    //public int findRelativeInBatch(int pageId, int delta, int boundary, boolean findEnd)
    public int findRelativeInBatch(int pageId, int delta, int boundary)
    throws SQLException {
        //Log.print("findRelativeInBatch pageId="+pageId+" delta="+delta+" boundary="+boundary);
        long startTime = System.currentTimeMillis();
        boolean uncoded = false;
        if (boundary == B_UNCODED) {
            boundary = B_CHILD;
            uncoded = true;
        }

        String sql;
        switch (boundary) {
        case B_NONE:
            assert(! uncoded);
            if (delta == 0) {
                sql = 
                    "select page_id"
                    +" from page P"
                    +"  inner join childcoded CC using(child_id)"
                    +" where P.page_id="+pageId
                    +"   and CC.round = 0"
                    +"   and CC.status='QA'";
            } else { // since delta != 0
                sql = 
                    "select P2.page_id"
                    +" from page P, page P2"
                    +"  inner join childcoded C on P2.child_id = CC.child_id"
                    +" where P2.volume_id = P.volume_id"
                    +(delta > 0 ? " and P2.seq > P.seq"
                                : " and P2.seq < P.seq")
                    +"   and P.page_id="+pageId
                    +"   and CC.round = 0"
                    +"   and CC.status='QA'"
                    +(delta > 0 ? " order by P2.seq limit "+(delta - 1)+",1"
                                : " order by P2.seq desc limit "+(- delta - 1)+",1");
            }
            break;
        case B_CHILD:
            assert(delta > 0 || ! uncoded);
            sql = 
                "select P2.page_id"
                +" from page P, child C, page P2"
                +"  inner join childcoded CC on P2.child_id = CC.child_id"
                +" where C.volume_id = P.volume_id"
                +(delta > 0 ? " and C.lft > P.seq"
                            : " and C.lft <= P.seq")
                +"   and P.page_id="+pageId
                +"   and P2.volume_id=C.volume_id"
                +"   and P2.seq=C.lft"
                +"   and CC.round = 0"
                +"   and CC.status='QA'"
                +(uncoded ? " and CC.users_id = 0" : "")
                +(delta > 0 ? " order by C.rgt limit "+(delta - 1)+",1"
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
            Log.quit("MarshallQAPage: invalid boundary.findRelativeInBatch: "+boundary);
            sql = null;
        }

        int result = 0;
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();

        Log.print("MarshallQAPage.findRelativeInBatch: "+(System.currentTimeMillis() - startTime)+" ms.");
        return result;
    }


    public int findAbsolute(String bates) throws SQLException {
        Log.print("findAbsolute: "+bates);
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
        Log.print("MarshallPage.findAbsolute: "+(System.currentTimeMillis() - startTime)+" ms.");
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

    public ImageData collectImageData(int pageId) throws SQLException {
        Log.print("collectImageData: "+pageId);
        long startTime = System.currentTimeMillis();
        ImageData data = new ImageData();
        ResultSet rs = st.executeQuery(
            "select P.seq, 0, P.bates_number"        // batchId
            +"    , sum(P2.seq <= P.seq), count(*)"
            +"    , V.lft, V.rgt, 0, 1999999999"     // batch boundaries
            +"    , R.lft, R.rgt, C.lft, C.rgt"
            +"    , P.boundary_flag, P.path, P.filename, P.child_id"
            +"    , V.volume_id, V.volume_name, V.image_path"
            +"    , P.offset, P.group_one_path, P.group_one_filename"
            +"    , P.document_number"
            +" from page P"
            +"   inner join child C using (child_id)"
            +"   inner join range R using (range_id)"
            +"   inner join volume V using (volume_id)"
            +"   inner join page P2 on P2.child_id = C.child_id"
            +" where P.page_id="+pageId
            +" group by P.page_id");
        if (! rs.next()) {
            Log.quit("collectImageData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        rs.close();
        Log.print("MarshallPage.collectImageData: "+(System.currentTimeMillis() - startTime)+" ms.");
        return data;
    }

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
        //int blft = rs.getInt(8);
        //int brgt = rs.getInt(9);
        int rlft = rs.getInt(10);
        int rrgt = rs.getInt(11);
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);
        //if (clft == blft) { data.boundaryInfo |= FIRST_CHILD_OF_BATCH; }
        //if (crgt == brgt) { data.boundaryInfo |=  LAST_CHILD_OF_BATCH; }
        //if (seq == blft)  { data.boundaryInfo |= FIRST_PAGE_OF_BATCH; }
        //if (seq == brgt)  { data.boundaryInfo |=  LAST_PAGE_OF_BATCH; }
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

    public CodingData collectCodingData(int pageId) throws SQLException {
        Log.print("collectCodingData: "+pageId);
        long startTime = System.currentTimeMillis();
        CodingData data = new CodingData();
        ResultSet rs = st.executeQuery(
            "select P.seq, 0, P.bates_number"        // batch id
            +"    , sum(P2.seq <= P.seq), count(*)"
            +"    , V.lft, V.rgt, 0, 1999999999"     // batch boundaries
            +"    , R.lft, R.rgt, C.lft, C.rgt"
            +"    , P.boundary_flag, P.path, P.filename, P.child_id"
            +"    , V.volume_id, V.volume_name, V.image_path"
            +"    , P.offset"
            +"    , FCP.bates_number, LCP.bates_number"
            +"    , FRP.bates_number, LRP.bates_number"
            +"    , 0"                               // batch number
            +"    , C.is_split"
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
        //int blft = rs.getInt(8);
        //int brgt = rs.getInt(9);
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);
        rs.close();

        // For QA batch boundaries, use first and last selected children

        //rs = st.executeQuery(
        //    "select sum(lft <= "+seq+"), count(*)"
        //    +" from child"
        //    +" where volume_id="+volumeId
        //    +"   and lft between "+blft+" and "+brgt);

        rs = st.executeQuery(
            "select sum(lft <= "+seq+"), count(*), min(lft), max(rgt)"
            +" from child C"
            +"   inner join childcoded CC using (child_id)"
            +" where volume_id="+volumeId
            +"   and CC.status = 'QA'"
            +"   and CC.round = 0");
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result #2");
        }
        data.batchChildPosition = rs.getInt(1);
        data.batchChildCount = rs.getInt(2);
        int blft = rs.getInt(3);
        int brgt = rs.getInt(4);
        rs.close();

        if (clft == blft) { data.boundaryInfo |= FIRST_CHILD_OF_BATCH; }
        if (crgt == brgt) { data.boundaryInfo |=  LAST_CHILD_OF_BATCH; }
        if (seq == blft)  { data.boundaryInfo |= FIRST_PAGE_OF_BATCH; }
        if (seq == brgt)  { data.boundaryInfo |=  LAST_PAGE_OF_BATCH; }

        Log.print("MarshallPage.collectCodingData: "+(System.currentTimeMillis() - startTime)+" ms.");
        return data;
    }
}


