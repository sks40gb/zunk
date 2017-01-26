/* $Header: /home/common/cvsarea/ibase/dia/src/server/BoundaryMapper.java,v 1.16.6.1 2006/03/21 16:42:41 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageConstants;

import java.sql.*;

/**
 * Change boundary level for a page.
 */
final public class BoundaryMapper implements MessageConstants {
            
    /**
     * Change boundary level for a page.  Adds or removes child and
     * range records as required from an unsplit document.
     */
    public static void store(ServerTask task, int volumeId
                             , int pageId, String boundaryFlag)
    throws SQLException {
        store(task,volumeId,pageId,boundaryFlag,false);
    }

    /**
     * Change boundary level for a page.  Adds or removes child and
     * range records as required.  Allows specification that
     * this was called from split/unsplit document.
     * @param task the ServerTask for the client
     * @param volumeId the volume.volume_id containing the page
     * @param pageId the page.page_id of the page whose boundary is changing
     * @param boundaryFlag the new boundary, one of B_CHILD, B_NONE, B_RANGE
     * @param splitOk is it legal to change the boundary of a split document?
     */
    public static void store(ServerTask task, int volumeId
                             , int pageId, String boundaryFlag
                             , boolean splitOk)
    throws SQLException {

        Statement st = task.getStatement();

        //Log.print("BoundaryMapper.store " + "'"+ boundaryFlag + "'");

        int boundaryEnum = boundaryFlag.equals("C") ? B_CHILD
                         : boundaryFlag.equals("")  ? B_NONE
                         :                            B_RANGE;

        // Get information about boundaries
        // TBD: should be checking batch -- also, we don't always need all fields
        ResultSet rs = st.executeQuery(
            "select P.seq, C.child_id, C.lft, C.rgt, R.range_id, R.lft, R.rgt"
            +"    , C.batch_id, C.is_split"
            +" from page P"
            +"   inner join child C using (child_id)"
            +"   inner join range R using (range_id)"
            +" where P.page_id="+pageId
            +"   and P.volume_id="+volumeId);
        if (! rs.next()) {
            Log.quit("page not found: "+pageId);
        }
        int seq  = rs.getInt(1);
        int childId  = rs.getInt(2);
        int childLft = rs.getInt(3);
        int childRgt = rs.getInt(4);
        int rangeId  = rs.getInt(5);
        int rangeLft = rs.getInt(6);
        int rangeRgt = rs.getInt(7);
        int batchId  = rs.getInt(8);
        boolean isSplit = rs.getBoolean(9);
        rs.close();

        if (isSplit && ! splitOk) {
            throw new ServerFailException(
                    "Cannot change boundary on split document.");
        }

        // Modify the data in page
        st.executeUpdate(
            "update page"
            +" set boundary="+boundaryEnum
            +"   , boundary_flag='"+boundaryFlag+"'"
            +" where page_id="+pageId);
        // Add the given child to the changes table, since this
        // may not happen below.
        // Note.  needed so child always changes when letter changed
        st.executeUpdate("insert ignore into changes"
            +" set table_nbr="+Tables.child.getTableNumber()
            +"   , id = "+childId
            //+"   , propagate=0"
            +"   , age="+task.getUpdateAgeForUpdate());

        ////// Adjust the child and range structure above this page.
        // Note that nothing is done if change between letters within B_RANGE

        if (boundaryEnum < B_RANGE && rangeLft == seq) {
            // no longer start of range
            // expand range to the left

            // find the prior range
            ResultSet rs2 = st.executeQuery(
                "select range_id, lft"
                +" from range"
                +" where volume_id="+volumeId
                +"   and rgt < "+rangeLft
                +" order by rgt desc"
                +" limit 1");     
            if (! rs2.next()) {
                Log.quit("prior range not found: "+rangeId);
            }
            int priorRangeId = rs2.getInt(1);
            int priorRangeLft = rs2.getInt(2);
            rs2.close();

            // note range is not managed
            st.executeUpdate(
                "update range"
                +" set rgt="+rangeRgt
                +" where range_id="+priorRangeId);
            st.executeUpdate(
                "delete from range"
                +" where range_id="+rangeId);
            st.executeUpdate(
                "update child"
                +" set range_id="+priorRangeId
                +" where range_id="+rangeId);

            rangeId = priorRangeId;
            rangeLft = priorRangeLft;
        }

        if (boundaryEnum < B_CHILD && childLft == seq) {
            // no longer start of child
            // expand child to the left

            ResultSet rs2 = st.executeQuery(
                "select child_id, lft"
                +" from child"
                +" where volume_id="+volumeId
                +"   and rgt < "+childLft
                +" order by rgt desc"
                +" limit 1");     
            if (! rs2.next()) {
                Log.quit("prior child not found: "+childId);
            }
            int priorChildId = rs2.getInt(1);
            int priorChildLft = rs2.getInt(2);
            rs2.close();

            task.executeUpdate(
                "update child"
                +" set rgt="+childRgt
                +" where child_id="+priorChildId);
            task.executeUpdate(
                "delete from child"
                +" where child_id="+childId);
            // Note. not managed
            st.executeUpdate(
                "update page"
                +" set child_id="+priorChildId
                +" where child_id="+childId);
            //// child count has changed for range
            //st.executeUpdate(
            //    "insert ignore into changes"
            //    +" set table_nbr="+Tables.range.getTableNumber()
            //    +"   , id="+rangeId
            //    +"   , age="+task.getUpdateAgeForUpdate());

            // delete data for the removed child
            st.executeUpdate(
                "delete from value"
                +" where child_id="+childId);
            st.executeUpdate(
                "delete from longvalue"
                +" where child_id="+childId);
            st.executeUpdate(
                "delete from namevalue"
                +" where child_id="+childId);

            childId = priorChildId;
            childLft = priorChildLft;
        }

        int priorSeq = 0;
        if (boundaryEnum >= B_CHILD && childLft != seq) {
            // new start of child
            // insert new child

            // find page to the left
            ResultSet rs2 = st.executeQuery(
                "select P.seq"
                +" from page P"
                +" where P.volume_id="+volumeId
                +"   and P.seq < "+seq
                +" order by P.seq desc"
                +" limit 1");
            if (! rs2.next()) {
                Log.quit("prior page not found: "+pageId);
            }
            priorSeq = rs2.getInt(1);
            rs2.close();

            task.executeUpdate(
                "update child"
                +" set rgt="+priorSeq
                +" where child_id="+childId);
            task.executeUpdate(
                "insert into child"
                +" set volume_id="+volumeId
                +"   , lft="+seq
                +"   , rgt="+childRgt
                +"   , range_id="+rangeId
                +"   , batch_id="+batchId);
            // Note. not managed
            st.executeUpdate(
                "update page"
                +" set child_id=last_insert_id()"
                +" where child_id="+childId
                +" and seq >= "+seq);
            //// child count has changed for range
            //// record in changes, unless it will be recorded below
            //if (boundaryEnum < B_RANGE) {
            //    st.executeUpdate(
            //        "insert ignore into changes"
            //        +" set table_nbr="+Tables.range.getTableNumber()
            //        +"   , id="+rangeId
            //        +"   , age="+task.getUpdateAgeForUpdate());
            //}
        }

        if (boundaryEnum >= B_RANGE && rangeLft != seq) {
            // new start of range
            // insert new range to the left

            // only get priorSeq if we didn't already
            // OOPS!!!  we had this without the where .. volume id -- wbe 2005-02-13
            if (priorSeq == 0) {
                ResultSet rs2 = st.executeQuery(
                    "select P.seq"
                    +" from page P"
                    +" where P.volume_id="+volumeId
                    +"   and P.seq < "+seq
                    +" order by P.seq desc"
                    +" limit 1");
                if (! rs2.next()) {
                    Log.quit("prior page not found: "+pageId);
                }
                priorSeq = rs2.getInt(1);
                rs2.close();
            }

            // Note. range not managed
            st.executeUpdate(
                "update range"
                +" set rgt="+priorSeq
                +" where range_id="+rangeId);
            st.executeUpdate(
                "insert into range"
                +" set volume_id="+volumeId
                +"   , lft="+seq
                +"   , rgt="+rangeRgt);
            st.executeUpdate(
                "update child"
                +" set range_id=last_insert_id()"
                +" where range_id="+rangeId
                +" and lft >= "+seq);
        }
    }
}
