/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_page_values.java,v 1.29.6.6 2006/03/21 16:42:41 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for page_values message.  Handles updating of the values in coding fields
 * by calling BoundaryMapper to store values.  This class also updates counts kept
 * for changes made by QCer and QAers.
 * @see BoundaryMapper
 * @see client.TaskSendCodingData
 * @see ui.SplitPaneViewer
 */
final public class Handler_page_values extends Handler {

    boolean rework = false;
    boolean childcoded = false;

    /**
     * This class cannot be instantiated.
     */
    public Handler_page_values() {}

    public void run (ServerTask task, Element action) throws SQLException {
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));

        // for event entry:
        // child_count, page_count, rows_deleted, field_count
        int updateCounts[] = { 0, 0, 0, 0 };

        //Log.print("page values: "+givenPageId);
        Statement st = task.getStatement();

        if (action.hasAttribute(A_BOUNDARY_FLAG)) {
            // if boundary_flag attribute doesn't exist,
            // the user hasn't updated it.
            String givenBoundaryFlag = action.getAttribute(A_BOUNDARY_FLAG);
            BoundaryMapper.store(task, volumeId, givenPageId, givenBoundaryFlag.trim());
        }

        // find the corresponding child for this page
        ResultSet rs = st.executeQuery (
            "select child_id"
            +" from page"
            +" where page_id="+givenPageId);
        if (! rs.next()) {
            Log.quit("Handler_page_values: child not found");
        }
        int childId = rs.getInt(1);
        rs.close();

        Node actionChild = action.getFirstChild();
        Element valueList = null;
        Element errorFlagList = null;
        while (actionChild != null) { 
            //Note.  actionChild may be ignored white space, if not validating parser
            if (actionChild.getNodeType() == Node.ELEMENT_NODE) {
                if (T_VALUE_LIST.equals(actionChild.getNodeName())) {
                    valueList = (Element) actionChild;
                } else if (T_ERROR_FLAG_LIST.equals(actionChild.getNodeName())) {
                    errorFlagList = (Element) actionChild;
                    break;
                }
            }
            actionChild = actionChild.getNextSibling();
        }
        String status = null;
        int pageCount = 0;
        rs = st.executeQuery(
            "select B.status, count(P.page_id), U.rework, U.qa_rework"
            +" from child C"
            +" left join page P on (P.child_id = C.child_id)"
            +" left join batch B on (B.batch_id = C.batch_id)"
            +" left join batchuser U on (U.batch_id = B.batch_id)"
            +" where C.child_id = "+childId
            +" group by B.batch_id, C.child_id");
        if (rs.next()) {
            status = rs.getString(1);
            pageCount = rs.getInt(2);
            rework = rs.getInt(3) == 1 || rs.getInt(4) == 1;
        }
        rs.close();

        if (valueList != null) {
            Map valueMap = MessageMap.decode(valueList);
            //updateCounts = ValueMapper.store(task, volumeId, childId, valueMap, status);
            ValueMapper.store(task, volumeId, childId, valueMap
                              , status, pageCount, rework);
        } else {
            Log.print("(Handler_page_values.run) no valueList");
            // make an entry into the event table
            // This should be a boundary update with no values updated.
            //if (! task.isAdmin()) {
            //    EventLog.update(task, volumeId, batchId, status
            //                    , /* child_count -> */ 1, pageCount, /* fields -> */ 1); 
            //}
        }

        if (errorFlagList != null) {
            Map errorFlagMap = MessageMap.decode(errorFlagList);
            ValueMapper.storeErrorFlags(task, volumeId, childId, errorFlagMap);
        }

        //Log.print("... updated pageId="+givenPageId);

        // check to see if this child has been updated by this user before.
        if ("CodingQC".equals(status)
        //|| "Coding".equals(status)
        || "QA".equals(status)) {
            rs = st.executeQuery(
                "select users_id, status"
                +" from childcoded"
                +" where child_id="+childId);
            if (rs.next()
                && rs.getInt(1) == task.getUsersId()
                && status.equals(rs.getString(2))) { // is this necessary?
                childcoded = true;
            }
            rs.close();
        }
        
        //String status = null;
        if (task.isAdmin()) {
            // do not record saved child
        } else if (batchId == 0) {
            // record saved child for QA
            // batchId should always be set, EXCEPT for QA
            // TBD: This is a bit of a kludge!!!

            // Insert users_id, etc, if this child has not
            // been saved for QA before.  (In which case,
            // there should be a childcoded record
            // with users_id = 0)
            status = "QA";
            st.executeUpdate(
                "update childcoded"
                +" set users_id="+task.getUsersId()
                +"   , coded_time="+System.currentTimeMillis()
                +" where child_id="+childId
                +"   and users_id = 0");
        } else {

            // get the batch status
            // TBD: this should probably be cached or saved in session
            //ResultSet rs2 = st.executeQuery(
            //    "select B.status"
            //    +" from batch B"
            //    +" where B.batch_id="+batchId);
            //rs2.next();
            //status = rs2.getString(1);
            //rs2.close();

            if ("Coding".equals(status)
            || "CodingQC".equals(status)) {
                // for coding, QC, QA remember who saved
                st.executeUpdate(
                    "insert ignore into childcoded"
                    +" set child_id="+childId
                    +"   , status='"+status+"'"
                    +"   , users_id="+task.getUsersId()
                    +"   , coded_time="+System.currentTimeMillis());
            } else if ("Unitize".equals(status)
            || "UQC".equals(status)) {
                // for Unitize, UnitizeQC record highest saved
                // First determine if new page higher than recorded
                ResultSet rs3 = st.executeQuery(
                    "select 0"
                    +" from page P0, batchuser U"
                    +"   left join page P"
                    +"     on P.page_id = U.last_unitized_page_id"
                    +" where P0.page_id="+givenPageId
                    +"   and U.batch_id="+batchId
                    +"   and (U.last_unitized_page_id = 0 or P0.seq > P.seq)"
                    +" for update");
                // now update page_unitized if required
                if (rs3.next()) {
                    st.executeUpdate(
                        "update batchuser"
                        +" set last_unitized_page_id="+givenPageId
                        +" where batch_id="+batchId);
                }
            }
        }

        //Log.print("status="+status);
        if ("CodingQC".equals(status)
        || "QA".equals(status)) {
            Log.print("(Handler_page_values.run) CodingQC or QA");
            // get the level
            // TBD: level and field count really should be cached
            ResultSet rs3 = st.executeQuery(
                "select TV.field_level"
                +" from volume V"
                +"   inner join project P using (project_id)"
                +"   inner join projectfields PF0"
                +"      on PF0.project_id = P.project_id"
                +"        and PF0.field_name = P.level_field_name"
                +"   inner join value VA"
                +"      on VA.child_id="+childId
                +"        and VA.field_name = P.level_field_name"
                +"   inner join tablevalue TV"
                +"      on TV.tablespec_id = PF0.tablespec_id"
                +"        and TV.value = VA.value"
                +" where V.volume_id ="+volumeId);
            int level = (rs3.next() ? rs3.getInt(1) : 0);
            // now get the field count for that level
            rs3 = st.executeQuery(
                "select count(*)"
                +" from projectfields PF"
                +"   inner join volume V using (project_id)"
                +" where V.volume_id ="+volumeId
                +"   and PF.field_level in (0, "+level+")");
            rs3.next();
            int fieldCount = rs3.getInt(1);
            //Log.print("fieldCount="+fieldCount);

            // get the change and error counts for this child
            // TBD should we get this from the message instead of the DB?
            // we only count fields for the proper level
            if (level == 0) {
                rs3 = st.executeQuery(
                    "select count(*), sum(codererror)"
                    +" from fieldchange"
                    +" where child_id="+childId);
            } else {
                // TBD check that this indexes on fieldchange first
                rs3 = st.executeQuery(
                    "select count(*), sum(codererror)"
                    +" from fieldchange FC"
                    +"   inner join volume V"
                    +"   inner join projectfields PF"
                    +"     on PF.project_id = V.project_id"
                    +"       and PF.field_name = FC.field_name"
                    +" where child_id="+childId
                    +"   and V.volume_id="+volumeId
                    +"   and PF.field_level in (0, "+level+")");
            }
            rs3.next();
            int changeCount = rs3.getInt(1);
            int errorCount = rs3.getInt(2);
            rs3.close();
            //Log.print("changeCount="+changeCount);
            //Log.print("errorCount="+errorCount);
            
            // TBD: for qc and qa, should we track actual updates or all fields?
            if (! childcoded && ! rework) {
                // If there is no childcoded row and it is not rework for this childId,
                // make an entry in event to record this CodingQC or QA action.
                EventLog.add(task, volumeId, batchId, status
                             , /* child count */ 1, pageCount, fieldCount); 
            }

            Log.print("(Handler_page_values.run) write childerror "
                      + fieldCount + "/" + changeCount + "/" + errorCount);
            // roll up the change and error counts
            int count = st.executeUpdate(
                "replace into childerror"
                +"  set child_id="+childId
                +"    , field_count="+fieldCount
                +"    , change_count="+changeCount
                +"    , error_count="+errorCount);
            
        }
    }
}
