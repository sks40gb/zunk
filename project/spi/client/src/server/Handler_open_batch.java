/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_open_batch.java,v 1.38.2.7 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for open_batch message.
 * There will be either a batch_id attribute or a project_id
 * attribute, but not both.  If a batch_id attribute, that
 * batch is to be open; if a project_id attribute, select
 * the first queued batch for that project.
 * Checks that assignment of requested batch is valid for user
 * and makes the assignment by inserting a row in assign.  The
 * open is logged in event for timesheet use.
 * Returns batch information.
 * @see client.TaskOpenBatch
 */
final public class Handler_open_batch extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_open_batch() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
        Log.print("in Handler_open_batch.run");
        String whichStatus = action.getAttribute(A_STATUS);
        assert ! task.isAdmin();
        assert whichStatus.equals("Unitize")
            || whichStatus.equals("UQC")
            || whichStatus.equals("Coding")
            || whichStatus.equals("CodingQC");

        Statement st = task.getStatement();

        String batchIdAttribute = action.getAttribute(A_BATCH_ID);
        String projectIdAttribute = action.getAttribute(A_PROJECT_ID);
        int batchId = 0;
        int volumeId = 0;
        int returnProjectId = 0; // return to viewer
        int returnActiveGroup = 0; // return to viewer
        String project = null;
        //String volume = null;
        int usersId = task.getUsersId();

        if (batchIdAttribute.length() > 0) {
            Log.print("(Handler_open_batch.run) batchId " + batchIdAttribute);

            assert projectIdAttribute.length() == 0;
            batchId = Integer.parseInt(batchIdAttribute);

            // make sure it's assigned to user and in specified status
            ResultSet rs0 = st.executeQuery(
                "select 0 from assignment A"
                +" inner join batch B using (batch_id)"
                +" where A.batch_id="+batchId
                +"   and users_id="+usersId
                +"   and B.status='"+whichStatus+"'"
                +" lock in share mode");
            if (! rs0.next()) {
                throw new ServerFailException("Batch is not available for assignment.");
            }

        } else {

            assert projectIdAttribute.length() > 0;
            int projectId = Integer.parseInt(projectIdAttribute);
            returnProjectId = projectId;
            Log.print("(Handler_open_batch.run) projectId " + projectId + "/" + whichStatus);

            // First, find first highest-priority batch in usersqueue
            // No restriction on QCing ones own
            ResultSet rs1 = st.executeQuery(
                "select Q.batch_id"
                +" from usersqueue Q"
                +"   inner join batch B using (batch_id)"
                +"   inner join volume V using (volume_id)"
                +" where Q.users_id="+task.getUsersId()
                +"   and V.project_id="+projectId
                +"   and B.status='"+whichStatus+"'"
                +" order by B.priority desc, B.batch_number"
                +" limit 1");
            if (rs1.next()) {
                batchId = rs1.getInt(1);
            }

            // If none, find batch in teamsqueue
            // For UQC and CodingQC, don't allow QC of own batch
            // Also, don't select batch that is on some usersqueue
            if (batchId == 0) {
                if (whichStatus.equals("Unitize") || whichStatus.equals("Coding")) {
                    rs1 = st.executeQuery(
                        "select Q.batch_id"
                        +" from users U"
                        +"   inner join teamsqueue Q using (teams_id)"
                        +"   inner join batch B using (batch_id)"
                        +"   inner join volume V using (volume_id)"
                        +"   left join usersqueue UQ on UQ.batch_id=Q.batch_id"
                        +" where U.users_id="+task.getUsersId()
                        +"   and V.project_id="+projectId
                        +"   and B.status='"+whichStatus+"'"
                        +"   and UQ.batch_id is null"
                        +" order by B.priority desc, B.batch_number"
                        +" limit 1");
                } else { // since whichStatus in (UQC, CodingQC)
                    rs1 = st.executeQuery(
                        "select Q.batch_id"
                        +" from users U"
                        +"   inner join teamsqueue Q using (teams_id)"
                        +"   inner join batch B using (batch_id)"
                        +"   inner join volume V using (volume_id)"
                        +"   left join batchuser BU on BU.batch_id = B.batch_id"
                        +"   left join usersqueue UQ on UQ.batch_id=Q.batch_id"
                        +" where U.users_id="+task.getUsersId()
                        +"   and V.project_id="+projectId
                        +"   and B.status='"+whichStatus+"'"
                        +"   and BU.coder_id <> U.users_id"
                        +"   and UQ.batch_id is null"
                        +" order by B.priority desc, B.batch_number"
                        +" limit 1");
                }
                if (rs1.next()) {
                    batchId = rs1.getInt(1);
                }
            }

            // Finally, find batch in teamsvolume - check that it's not assigned
            // or on usersqueue or teamsqueue
            if (batchId == 0
            && (whichStatus.equals("Unitize") || whichStatus.equals("Coding"))) {
                rs1 = st.executeQuery(
                    "select B.batch_id"
                    +" from users U"
                    +"   inner join teamsvolume Q using (teams_id)"
                    +"   inner join volume V using (volume_id)"
                    +"   inner join batch B using (volume_id)"
                    +"   left join assignment A on A.batch_id=B.batch_id"
                    +"   left join usersqueue UQ on UQ.batch_id=B.batch_id"
                    +"   left join teamsqueue TQ on TQ.batch_id=B.batch_id"
                    +" where U.users_id="+task.getUsersId()
                    +"   and V.project_id="+projectId
                    +"   and B.status='"+whichStatus+"'"
                    +"   and A.batch_id is null"
                    +"   and UQ.batch_id is null"
                    +"   and TQ.batch_id is null"
                    +" order by B.priority desc, B.batch_number"
                    +" limit 1");
                if (rs1.next()) {
                    batchId = rs1.getInt(1);
                }
            }
            rs1.close();

            if (batchId != 0) {

                // sanity check -- we shouldn't have an assigned batch
                // (We could do this with a unique index by catching the
                // SQLException, but there's a problem with definitions
                // of the error codes. )
                ResultSet rs2 = st.executeQuery(
                    "select users_id from assignment A"
                    +" inner join batch B using (batch_id)"
                    +" where A.batch_id="+batchId
                    +" for update");
                if (rs2.next()) {
                    Log.quit("Queued batch "+batchId+" already assigned to "+rs2.getString(1));
                }
                rs2.close();

                // assign the batch
                task.executeUpdate(
                    "insert into assignment"
                    +" set batch_id="+batchId
                    +"   , users_id="+task.getUsersId()
                    +"   , timestamp="+System.currentTimeMillis());
                
            }
        }

        if (batchId == 0) {
            // failure, roll back the transaction
            throw new ServerFailException("Unable to open selected batch.");
        }

        // sanity check -- user shouldn't have an active batch
        if (task.getLockVolumeId() != 0) {
            Log.quit("There is already an open batch");
        }

        // get the region bounds and lock region
        // while we're at it, get the project_id
        // TBD this is here because we used to need start and end id's for
        // the batch.  Do we really need all this info???
        ResultSet rs3 = st.executeQuery(
            "select V.volume_id, project_name, priority, split_documents"
            +" , P.project_id, active_group"
            +" from batch"
            +"   inner join volume V using (volume_id)"
            +"   inner join project P using (project_id)"
            +" where batch_id="+batchId);
        rs3.next();
        volumeId = rs3.getInt(1);
        project = rs3.getString(2);
        int priority = rs3.getInt(3);
        String splitDocuments = rs3.getBoolean(4) ? "Yes" : "No";
        returnProjectId = rs3.getInt(5);
        returnActiveGroup = rs3.getInt(6);
        rs3.close();

        task.lockBatch(batchId);

        // remove batch from queues
        task.executeUpdate(
            "delete from usersqueue"
            +" where batch_id="+batchId);
        task.executeUpdate(
            "delete from teamsqueue"
            +" where batch_id="+batchId);

        // clear priority 
        // TBD: Is this right?
        if (priority != 0) {
            task.executeUpdate(
                "update batch"
                +" set priority=0"
                +" where batch_id="+batchId);
        }

        // record most recent user opening this batch
        if (whichStatus.equals("Coding") || whichStatus.equals("Unitize")) {
            int count = st.executeUpdate(
                "insert ignore into batchuser"
                +" set batch_id="+batchId
                +"   , coder_id="+usersId
                +"   , mod_time="+System.currentTimeMillis());
            if (count == 0) {
                // must have been already been opened
                st.executeUpdate(
                "update batchuser"
                +" set coder_id="+usersId
                +"   , mod_time="+System.currentTimeMillis()
                +" where batch_id="+batchId);
            }
        } else { // since whichStatus in (CodingQC, UQC)
            // Note.  Assumed to have already been opened
            st.executeUpdate(
                "update batchuser"
                +" set qc_id="+usersId
                +"   , mod_time="+System.currentTimeMillis()
                +" where batch_id="+batchId);
            }
        task.commitTransaction();
        if (! task.isAdmin()) {
            // TBD:  log admin events?  Maybe log with different
            // event text so they can be included or not on the Timesheet.
            EventLog.open(task, volumeId, batchId, whichStatus);
        }

        // send back info for batch -- may not need all this
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_BATCH_OPENED);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.writeAttribute(A_PROJECT_ID, returnProjectId);
        writer.writeAttribute(A_GROUP, returnActiveGroup);
        //writer.writeAttribute(A_BATCH_NUMBER, batchNumber);
        writer.writeAttribute(A_PROJECT_NAME, project);
        writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);
        //if (activeGroup > 0) {
        //    writer.writeAttribute(A_ACTIVE_GROUP, group);
        //}
        //writer.writeAttribute(A_VOLUME, volume);
        writer.endElement();
    }
}
