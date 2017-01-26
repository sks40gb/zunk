/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_page_split.java,v 1.3.8.3 2007/03/25 04:48:18 bill Exp $ */
package server;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for page_boundary message.  Handles updating of boundary, without
 * being recordes as a page save.  This is NOT called for unitizing, it
 * is called when a boundary is changed in a non-unitizing viewer.
 * @see client.TaskSendSplit
 */
final public class Handler_page_split extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_page_split() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        int pageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        Connection con = task.getConnection();
        Statement st = task.getStatement();

        boolean unsplit = "YES".equalsIgnoreCase(action.getAttribute(A_UNSPLIT));
        boolean clone = "YES".equalsIgnoreCase(action.getAttribute(A_CLONE));
        Log.print("page split: unsplit="+unsplit+" clone="+clone+" pageId="+pageId);

        ResultSet rs1 = st.executeQuery(
            "select P.child_id, C.is_split, P.bates_number, P.seq, C.lft"
            +" from page P inner join child C using (child_id)"
            +" where page_id="+pageId);
        if (! rs1.next()) {
            throw new ServerFailException("Page not found");
        }
        int childId = rs1.getInt(1);
        boolean isSplit = rs1.getBoolean(2);
        String batesNumber = rs1.getString(3);
        int seq = rs1.getInt(4);
        int lft = rs1.getInt(5);
        rs1.close();
        int batesLength = batesNumber.length();

        int newPageId;
        if (unsplit) {
            Log.print("unsplit: childId="+childId+" isSplit="+isSplit+" batesNumber="+batesNumber);
            if (! isSplit) {
                throw new ServerFailException("Not a subdocument");
            }
            // after above test, doc must be a child
            // get the prior page
            ResultSet rs2 = st.executeQuery(
                "select P.bates_number, P.seq, C.is_split, P.page_id"
                +" from page P"
                +"   inner join child C using (child_id)"
                +" where P.seq < "+seq
                +" and P.volume_id="+volumeId
                +" order by P.seq desc"
                +" limit 1");
            if (! rs2.next()) {
                throw new ServerFailException("Prior page not found");
            }
            String priorBatesNumber = rs2.getString(1);
            int priorseq = rs2.getInt(2);
            boolean priorIsSplit = rs2.getBoolean(3);
            newPageId = rs2.getInt(4);
            rs2.close();

            if (priorIsSplit) {
                priorBatesNumber
                      = priorBatesNumber.substring(0, priorBatesNumber.length() - 3);
            }

            // make it not a child
            // Note: is_split goes away with the child row
            BoundaryMapper.store(task, volumeId, pageId, "", true);


            if (priorBatesNumber.length() == (batesLength - 3)
            && batesNumber.startsWith(priorBatesNumber)) {
                // this page is a clone, so delete it and adjust boundaries
                st.executeUpdate(
                    "delete from page"
                    +" where page_id="+pageId);
                adjustRgtValues(task, volumeId, seq, priorseq);
            } else {
                st.executeUpdate(
                    "update page"
                    +" set bates_number=substring("
                    +"   bates_number from 1 for "+(batesLength - 3)+")"
                    +" where page_id="+pageId);
                newPageId = pageId;
            }

        } else { // since request is to split

            //check that next child is not split
            ResultSet rs5=st.executeQuery(
                "select is_split from child"
                +" where lft > "+seq
                +"   and volume_id = "+volumeId
                +" order by lft"
                +" limit 1");
            if (rs5.next() && rs5.getBoolean(1)) {
                rs5.close();
                throw new ServerFailException("Cannot split - already split");
            }
            rs5.close();

            //get the new Bates number with suffix
            String newBates;
            if (isSplit && seq==lft) {
                char suffix = batesNumber.charAt(batesLength - 2);
                if (suffix == 'Z') {
                    throw new ServerFailException("Cannot split - no suffix letter");
                }
                suffix++;
                newBates = batesNumber.substring(0, batesLength - 2) + suffix + ']';
            } else {
                newBates = batesNumber + "[A]";
            }

            if (clone || seq == lft) {

                // clone the page
                // first make sure we can insert new page
                ResultSet rs6 = st.executeQuery(
                    "select 0 from page"
                    +" where volume_id="+volumeId
                    +"   and seq="+(seq+1));
                if (rs6.next()) {
                    adjustSeq(task, volumeId, seq+1, 100);
                }
                rs6.close();

                // create a new page
                PreparedStatement ps = con.prepareStatement(
                    "insert into page" 
                    +"   ( volume_id"
                    +"   , seq"
                    +"   , child_id"
                    +"   , bates_number"
                    +"   , original_flag"
                    +"   , original_rotate"
                    +"   , path"
                    +"   , filename"
                    +"   , offset"
                    +"   , file_type"
                    +"   , boundary_flag"
                    +"   , rotate"
                    +"   , boundary )"
                    +" select"
                    +"     volume_id"
                    +"   , ?"            
                    +"   , child_id"
                    +"   , ?"   
                    +"   , original_flag"
                    +"   , original_rotate"
                    +"   , path"
                    +"   , filename"
                    +"   , offset"
                    +"   , file_type"
                    +"   , boundary_flag"
                    +"   , rotate"
                    +"   , 'NONE'"
                    +" from page"
                    +" where page_id=?");
                ps.setInt(1, seq+1);
                ps.setString(2, newBates);
                ps.setInt(3, pageId);
                ps.executeUpdate();
                ps.close();

                ResultSet rs7 = st.executeQuery(
                    "select last_insert_id()");
                rs7.next();
                newPageId = rs7.getInt(1);
                rs7.close();

                adjustRgtValues(task, volumeId, seq, seq+1);

            } else {
                // we are not cloning
                // update the bates number
                PreparedStatement ps = con.prepareStatement(
                    "update page"
                    +" set bates_number = ?"
                    +" where page_id=?");
                ps.setString(1, newBates);
                ps.setInt(2, pageId);
                ps.executeUpdate();
                ps.close();

                newPageId = pageId;
            }

            // make the split page a child
            BoundaryMapper.store(task, volumeId, newPageId, "C", true);

            // add the split flag
            st.executeUpdate(
                "update child C"
                +"   inner join page P using (child_id)"
                +" set C.is_split=1"
                +" where P.page_id="+newPageId);
        }

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_OK);
        writer.writeAttribute(A_PAGE_ID, newPageId);
        writer.endElement();
    }

    // open up a hole of size delta in the seq numbers
    // note.  lft used by managed tables for child and batch
    private void adjustSeq(ServerTask task, int volumeId, int first, int delta)
    throws SQLException {
        Statement st = task.getStatement();

        ResultSet rs = st.executeQuery(
            "select project_id from volume"
            +" where volume_id="+volumeId);
        rs.next();
        int projectId = rs.getInt(1);
        rs.close();

        st.executeUpdate(
            "update page P"
            +"   inner join volume V using (volume_id)"
            +" set P.seq = P.seq+"+delta
            +" where V.project_id="+projectId
            +" and P.seq >= "+first);
        task.executeUpdate(
            "update child X"
            +"   inner join volume V using (volume_id)"
            +" set X.rgt = X.rgt+"+delta
            +"   , X.lft = if(X.lft >= "+first+",X.lft+"+delta+",X.lft)"
            +" where V.project_id="+projectId
            +" and X.rgt >= "+first);
        st.executeUpdate(
            "update range X"
            +"   inner join volume V using (volume_id)"
            +" set X.rgt = X.rgt+"+delta
            +"   , X.lft = if(X.lft >= "+first+",X.lft+"+delta+",X.lft)"
            +" where V.project_id="+projectId
            +" and X.rgt >= "+first);
        task.executeUpdate(
            "update batch X"
            +"   inner join volume V using (volume_id)"
            +" set X.rgt = X.rgt+"+delta
            +"   , X.lft = if(X.lft >= "+first+",X.lft+"+delta+",X.lft)"
            +" where V.project_id="+projectId
            +" and X.rgt >= "+first);
        st.executeUpdate(
            "update volume X"
            +" set X.rgt = X.rgt+"+delta
            +"   , X.lft = if(X.lft >= "+first+",X.lft+"+delta+",X.lft)"
            +" where X.project_id="+projectId
            +" and X.rgt >= "+first);
        st.executeUpdate(
            "update project X"
            +" set X.rgt = X.rgt+"+delta
            +" where X.project_id="+projectId
            +" and X.rgt >= "+first);
    }

    private void adjustRgtValues
            (ServerTask task, int volumeId, int oldSeq, int newSeq)
    throws SQLException {
        Statement st = task.getStatement();
        int count = st.executeUpdate(
            "update child"
            +" set rgt = "+newSeq
            +" where volume_id="+volumeId
            +"   and rgt="+oldSeq);
        if (count > 0) {
            count = st.executeUpdate(
                "update batch"
                +" set rgt = "+newSeq
                +" where volume_id="+volumeId
                +"   and rgt="+oldSeq);
            count *= st.executeUpdate(
                "update range"
                +" set rgt = "+newSeq
                +" where volume_id="+volumeId
                +"   and rgt="+oldSeq);
        }
        if (count > 0) {
            count = st.executeUpdate(
                "update volume"
                +" set rgt = "+newSeq
                +" where volume_id="+volumeId
                +"   and rgt="+oldSeq);
        }
        if (count > 0) {
            count = st.executeUpdate(
                "update project P"
                +"   inner join volume V using (project_id)"
                +" set P.rgt = "+newSeq
                +" where V.volume_id="+volumeId
                +"   and P.rgt="+oldSeq);
        }
    }
}
