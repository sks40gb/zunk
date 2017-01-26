/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.StatusConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.server.valueobjects.BatchHistoryData;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.server.valueobjects.ProjectHistoryData;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * This class handles the batch operations.
 * @author ashish
 */
class BatchIO implements MessageConstants {
   
   public static Logger logger = Logger.getLogger("com.fossa.servlet.command");

    /**
     * Store new status in the batch table when a batch is closed.
     * Batch may be rejected and sent to previous stage or accepted
     * and sent to next stage.
     * Also delete any current assignment.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param volume_id the volume.volume_id that contains the given batch_id
     * @param batch_id the batch.batch_id of the batch whose status
     * is about to be updated
     * @param status the current (old) status of the batch
     * @param reject true to reject this batch; false to close it for
     * the current status
     */
    public static void updateStatus(UserTask task, DBTask dbTask, int volume_id, int batch_id, String status, boolean reject)
            throws SQLException {

        Connection con = null;
        Statement st = null;                
        String level = "";        // Holds the treatment level for a batch
        String modify_error_status = "";
        PreparedStatement getBatchDetailsPreparedStatement = null;
        BatchHistoryData batchData;
        try {

            // TBD: don't really need to do query first
            con = dbTask.getConnection();
            st = dbTask.getStatement();
            int oldStatus = 0;      // Holds the old batch status
            int newStatus = 0;      // Holds the new batch status
            String strStatus = "";  
            String stroldStatus = "";
            ResultSet getTreatmentLevelResultSet = st.executeQuery("select batch_id,treatment_level from batch" +
                                                                           " where batch_id=" + batch_id);
            if (getTreatmentLevelResultSet.next()) {
                level = getTreatmentLevelResultSet.getString(2);
            }
            String rejectStatus = null;
            if (reject) {
                if (status.equals("CodingQC")) {
                    oldStatus = StatusConstants.S_CODINGQC;
                    newStatus = StatusConstants.S_CODING;
                    rejectStatus = "Coding";
                } else if (status.equals("UQC")) {
                    oldStatus = StatusConstants.S_UQC;
                    newStatus = StatusConstants.S_UNITIZE;
                    rejectStatus = "Unitize";
                } else {
                    throw new ServerFailException("Invalid reject batch status.");
                }
            } else { // accept
                if (status.equals("Unitize")) {
                    oldStatus = StatusConstants.S_UNITIZE;
                    newStatus = StatusConstants.S_UQC;
                } else if (status.equals("UQC")) {
                    oldStatus = StatusConstants.S_UQC;
                    newStatus = StatusConstants.S_UCOMPLETE;
                } else if (status.equals("Coding")) {
                    oldStatus = StatusConstants.S_CODING;
                    newStatus = StatusConstants.S_CODINGQC;
                } else if (status.equals("CodingQC") && !level.equals("L1")) {
                    oldStatus = StatusConstants.S_CODINGQC;
                    newStatus = StatusConstants.S_QCOMPLETE;
                } else if (status.equals("CodingQC") && level.equals("L1")) {
                    oldStatus = StatusConstants.S_CODINGQC;
                    newStatus = StatusConstants.S_MASKING;
                } else if (status.equals("Listing")) {
                    oldStatus = StatusConstants.S_QCOMPLETE;
                    newStatus = StatusConstants.S_LISTING;
                } else if (status.equals("Masking")) {
                    oldStatus = StatusConstants.S_MASKING;
                    newStatus = StatusConstants.S_MASKINGCOMPLETE;
                } else if (status.equals("ModifyErrors")) {
                    //This query will return only one result
                    ResultSet getModifyErrorStatusResultSet = st.executeQuery("select modify_error_status from batch " +
                                                                                       "where batch_id=" + batch_id);
                    if (getModifyErrorStatusResultSet.next()) {   
                        modify_error_status = getModifyErrorStatusResultSet.getString(1);
                        if (modify_error_status.equals("QAComplete")) {
                            newStatus = StatusConstants.S_QACOMPLETE;
                        } else if (modify_error_status.equals("TComplete")) {
                            newStatus = StatusConstants.S_TCOMPLETE;
                        } else if (modify_error_status.equals("LComplete")) {
                            newStatus = StatusConstants.S_LCOMPLETE;                           
                        }
                    }                    
                    oldStatus = StatusConstants.S_MODIFYERRORS;                
                } else {
                    throw new ServerFailException("Invalid close batch status.");
                }
            }

            // Make sure the batch is there and has the given status.
            // At the same time, determine the batch number and rework state.
            // wbe 2005-05-28 removed join with volume - not used, and was causing deadlock
            int batchNumber;
            int activeGroup;
            int rework;
            String treatment_level = "";
            if (oldStatus == 1) {
                stroldStatus = "Unitize";
            } else if (oldStatus == 2) {
                stroldStatus = "UQC";
            } else if (oldStatus == 3) {
                stroldStatus = "UComplete";
            } else if (oldStatus == 4) {
                stroldStatus = "UBatched";
            } else if (oldStatus == 5) {
                stroldStatus = "Coding";
            } else if (oldStatus == 6) {
                stroldStatus = "CodingQC";
            } else if (oldStatus == 7) {
                stroldStatus = "QCComplete";
            } else if (oldStatus == 8) {
                stroldStatus = "QA";
            } else if (oldStatus == 9) {
                stroldStatus = "QAComplete";
            } else if (oldStatus == 18) {
                stroldStatus = "Masking";
            } else if (oldStatus == 19) {
                stroldStatus = "MaskingComplete";
            } else if (oldStatus == 20) {
                stroldStatus = "ModifyErrors";
            }

            getBatchDetailsPreparedStatement = con.prepareStatement(SQLQueries.SEL_BATCH_NO);
            getBatchDetailsPreparedStatement.setInt(1, volume_id);
            getBatchDetailsPreparedStatement.setInt(2, batch_id);
            getBatchDetailsPreparedStatement.setString(3, stroldStatus);
            ResultSet rs = getBatchDetailsPreparedStatement.executeQuery();

            if (!rs.next()) {
                rs.close();
                Log.print("(BatchIO) Fu verification failed volume/batch/oldstatus " + volume_id + "/" + batch_id + "/" + oldStatus);
                throw new ServerFailException("Unable to close batch.");
            }

            batchNumber = rs.getInt(1);
            rework = rs.getInt(2) > 0 || rs.getInt(3) > 0 ? 1 : 0;
            activeGroup = rs.getInt(4);
            treatment_level = rs.getString(5);
            Log.print("(BatchIO) activeGroup is " + activeGroup);
            rs.close();

            // fectch the batch comments
            String comments = "";
            rs = st.executeQuery(SQLQueries.SEL_COMMENT + batch_id);
            if (rs.next()) {
                comments = rs.getString(1) + "\n";
            }
            rs.close();

            //fetch the current date & time along with the users_id.
            rs = st.executeQuery(SQLQueries.SEL_SUBSTRING + task.getUsersId());
            rs.next();
            comments += rs.getString(1) + " " + status + ((activeGroup > 0) ? ", Group " + activeGroup + "," : "") + 
                       (reject ? " REJECTED " : " ") + rs.getString(2);
            rs.close();
            
            // update the batch comments
            PreparedStatement updateBatchCommentsPreparedStatement = con.prepareStatement(SQLQueries.UPD_BATCH);
            updateBatchCommentsPreparedStatement.setInt(1, batch_id);
            updateBatchCommentsPreparedStatement.setString(2, comments);
            updateBatchCommentsPreparedStatement.executeUpdate();
            updateBatchCommentsPreparedStatement.close();

            // If this is a reject QC -> Coding AND the batch not already rework
            // add childcoded records so QCer gets credit for entire batch
            // these entries can be distinguisjed because coded_time = 0
            if (newStatus == StatusConstants.S_CODING && rework == 0) {
                PreparedStatement insertIntoChildCodedPreparedStatement = con.prepareStatement(SQLQueries.INS_CHILD);
                insertIntoChildCodedPreparedStatement.setInt(1, task.getUsersId());
                insertIntoChildCodedPreparedStatement.setInt(2, batch_id);
                insertIntoChildCodedPreparedStatement.executeUpdate();
            }

            // Write batchcredit record for payroll.
            // Note: this must come before status change, since status is used            
            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);            
            if (status.startsWith("U")) { // Unitize or UQC
                // credit last user to open the batch (if a batch has never
                // been opened, it's credited to nobody -- This could happen
                // if batch was opened only by an administrator AND an
                // administrator were allowed to close it.
                // In this case, the users_id is 0.)
                // wbe 2005-01-04 always report unitize/UQC as level 0
                if (level.equals("L1")) {
                    PreparedStatement insIntoBatchCreditPreparedStatement = con.prepareStatement(SQLQueries.INS_BATCH_CREDIT1);
                    insIntoBatchCreditPreparedStatement.setInt(1, batch_id);
                    insIntoBatchCreditPreparedStatement.setInt(2, batchNumber);
                    insIntoBatchCreditPreparedStatement.setTimestamp(3, timestamp);
                    insIntoBatchCreditPreparedStatement.setString(4, status);
                    insIntoBatchCreditPreparedStatement.setInt(5, rework);
                    insIntoBatchCreditPreparedStatement.setLong(6, activeGroup);
                    insIntoBatchCreditPreparedStatement.setLong(7, batch_id);
                    insIntoBatchCreditPreparedStatement.executeUpdate();
                } else {
                    PreparedStatement insIntoBatchCreditPreparedStatement = con.prepareStatement(SQLQueries.INS_BATCH_CREDIT);
                    insIntoBatchCreditPreparedStatement.setInt(1, batch_id);
                    insIntoBatchCreditPreparedStatement.setInt(2, batchNumber);
                    insIntoBatchCreditPreparedStatement.setTimestamp(3, timestamp);
                    insIntoBatchCreditPreparedStatement.setString(4, status);
                    insIntoBatchCreditPreparedStatement.setInt(5, rework);
                    insIntoBatchCreditPreparedStatement.setLong(6, activeGroup);
                    insIntoBatchCreditPreparedStatement.setLong(7, batch_id);
                    insIntoBatchCreditPreparedStatement.executeUpdate();
                }


                // Since this is Unitize or UQC, delete the high-water mark
                st.executeUpdate(SQLQueries.UPD_BATCH_USR + batch_id);

            } else { // Coding or CodingQC               
               if (level.equals("L1")) {
                    PreparedStatement insIntoBatchCreditPreparedStatement = con.prepareStatement(SQLQueries.INS_BATCH_CRDT1);
                    insIntoBatchCreditPreparedStatement.setInt(1, batch_id);
                    insIntoBatchCreditPreparedStatement.setInt(2, batchNumber);
                    insIntoBatchCreditPreparedStatement.setTimestamp(3, timestamp);
                    insIntoBatchCreditPreparedStatement.setString(4, status);
                    insIntoBatchCreditPreparedStatement.setInt(5, rework);
                    insIntoBatchCreditPreparedStatement.setInt(6, activeGroup);
                    insIntoBatchCreditPreparedStatement.setInt(7, batch_id);
                    insIntoBatchCreditPreparedStatement.executeUpdate();
                } else {
                    PreparedStatement insIntoBatchCreditPreparedStatement = con.prepareStatement(SQLQueries.INS_BATCH_CRDT);
                    insIntoBatchCreditPreparedStatement.setInt(1, batch_id);
                    insIntoBatchCreditPreparedStatement.setInt(2, batchNumber);
                    insIntoBatchCreditPreparedStatement.setTimestamp(3, timestamp);
                    insIntoBatchCreditPreparedStatement.setString(4, status);
                    insIntoBatchCreditPreparedStatement.setInt(5, rework);
                    insIntoBatchCreditPreparedStatement.setInt(6, activeGroup);
                    insIntoBatchCreditPreparedStatement.setInt(7, batch_id);
                    insIntoBatchCreditPreparedStatement.executeUpdate();
                }

                // force a credit record for current user, even if nothing saved
                PreparedStatement insIntoBatchCreditPreparedStatement = con.prepareStatement(SQLQueries.INS_BCH_CREDIT);
                insIntoBatchCreditPreparedStatement.setInt(1, batchNumber);
                insIntoBatchCreditPreparedStatement.setTimestamp(2, timestamp);
                insIntoBatchCreditPreparedStatement.setInt(3, task.getUsersId());
                insIntoBatchCreditPreparedStatement.setString(4, status);
                insIntoBatchCreditPreparedStatement.setInt(5, rework);
                insIntoBatchCreditPreparedStatement.setInt(6, activeGroup);
                insIntoBatchCreditPreparedStatement.setInt(7, task.getUsersId());
                insIntoBatchCreditPreparedStatement.setTimestamp(8, timestamp);
                insIntoBatchCreditPreparedStatement.setInt(9, batch_id);
                insIntoBatchCreditPreparedStatement.executeUpdate();

                // Change round 0 (current round) to use a new round number
                // TBD: do we need to keep these?  Maybe we only need current round.
                ResultSet getRoundResultSet = st.executeQuery(SQLQueries.SEL_MAX_ROUND + batch_id);
                getRoundResultSet.next();
                int round = getRoundResultSet.getInt(1);
                getRoundResultSet.close();

                PreparedStatement updatechildCodedResultSet = con.prepareStatement(SQLQueries.UPD_CHILD);
                updatechildCodedResultSet.setInt(1, round);
                updatechildCodedResultSet.setInt(2, batch_id);
                updatechildCodedResultSet.executeUpdate();

                if (oldStatus == StatusConstants.S_CODINGQC) {
                    // Roll error statistics into batcherror table
                    // Note: X.round=1 selects first Coding round,
                    //   so errors associated with original coder
                    st.executeUpdate("INSERT INTO batcherror (batch_id, users_id, " +
                                       "credit_time, round, rework, field_count, change_count, " +
                                       "error_count, active_group) " +                      
                                       "SELECT C.batch_id, X.users_id,'" + timestamp + "'," + round + "," + rework + ", " +
                                       "sum(E.field_count) AS 'sum(E.field_count)'," +
                                       "sum(E.change_count) AS 'sum(E.change_count)', " +
                                       "sum(E.error_count) AS 'sum(E.error_count)' ," + activeGroup + " FROM childcoded X " +
                                       "inner join childerror E ON E.child_id = X.child_id " +
                                       "inner join child C ON C.child_id = E.child_id " +
                                       "WHERE C.batch_id= " + batch_id + " " +
                                       "and X.round=1 GROUP BY users_id,C.batch_id");

                    // Clear error statistics for children
                    st.executeUpdate("DELETE from childerror  WHERE child_id in " +
                                       "(select child_id from child WHERE batch_id=" + batch_id + ")");
                }
            }

            // change the status
            if (newStatus == 1) {
                strStatus = "Unitize";
            } else if (newStatus == 2) {
                strStatus = "UQC";
            } else if (newStatus == 3) {
                strStatus = "UComplete";
            } else if (newStatus == 4) {
                strStatus = "UBatched";
            } else if (newStatus == 5) {
                strStatus = "Coding";
            } else if (newStatus == 6) {
                strStatus = "CodingQC";
            } else if (newStatus == 7) {
                strStatus = "QCComplete";
            } else if (newStatus == 8) {
                strStatus = "QA";
            } else if (newStatus == 9) {
                strStatus = "QAComplete";
            } else if (newStatus == 18) {
                strStatus = "Masking";
            } else if (newStatus == 19) {
                strStatus = "MaskingComplete";
            } else if (oldStatus == 20) {
                if (modify_error_status.equals("QAComplete")) {
                    strStatus = "QAComplete";
                } else if (modify_error_status.equals("TComplete")) {
                    strStatus = "TComplete";
                } else if (modify_error_status.equals("LComplete")) {
                    strStatus = "LComplete";
                    ResultSet getFieldIdResultSet = st.executeQuery("select distinct field_id" +
                                                                      " from listing_occurrence " +
                                                                      "where batch_id=" + batch_id);
                    while (getFieldIdResultSet.next()) {
                        int field_id = getFieldIdResultSet.getInt(1);
                        
                     PreparedStatement updateProjectfieldsPrepStatement = con.prepareStatement("UPDATE projectfields " +
                                                                                             "SET listing_marking =? " +
                                                                                          "WHERE projectfields_id = ?");
                     updateProjectfieldsPrepStatement.setString(1, "No");                     
                     updateProjectfieldsPrepStatement.setInt(2, field_id);
                     updateProjectfieldsPrepStatement.executeUpdate();
                    }                    
                     PreparedStatement updateListingOcurrencePrepStatement = con.prepareStatement("UPDATE listing_occurrence " +
                                                                                                  "SET marking =? ,view_marking =? " +
                                                                                                  "WHERE batch_id = ?");
                     updateListingOcurrencePrepStatement.setString(1, "No");
                     updateListingOcurrencePrepStatement.setString(2, "No");
                     updateListingOcurrencePrepStatement.setInt(3, batch_id);
                     updateListingOcurrencePrepStatement.executeUpdate();                  
                }
            }
            if (treatment_level.equals("L1")) {
                
                task.executeUpdate(dbTask, "UPDATE batch SET status = '" + strStatus + "',sub_process = 0 "+
                                             " WHERE volume_id = " + volume_id + " and batch_id = " + batch_id);
            } else {
                task.executeUpdate(dbTask, "UPDATE batch SET status = '" + strStatus + "',sub_process = 0 "+
                                             " WHERE volume_id = " + volume_id + " and batch_id = " + batch_id);
                
            }
            
            batchData = new BatchHistoryData(con, batch_id);
            batchData.setStatus(status);
            batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.STATUS_CHANGED);
            if (batch_id != 0) {
                // delete the assignment for the given batch
                task.executeUpdate(dbTask, SQLQueries.DEL_ASSIGN + batch_id);

                //insert into batch history
                BatchProcessHistroyData data = new BatchProcessHistroyData();
                data.setBatch_id(batch_id);
                data.setVolume_id(volume_id);
                data.setProcess(strStatus);
                data.setIs_ready("No");
                data.setIn_queue("No");
                data.setEnd_time(getCurrentDateTime());
                data.setEnded_by(Command_close_batch.users_id);
                data.insertIntoHistoryTable(con);
                // release from session
                task.executeUpdate(dbTask,"update session" + " set volume_id=0" + "   , batch_id=0" + " " +
                                             "where volume_id=" + volume_id + "   and batch_id=" + batch_id);
            }

            // queue the batch to the team or former QC'er for QC or reject to user
            if (newStatus == StatusConstants.S_UQC || newStatus == StatusConstants.S_CODINGQC || 
                newStatus == StatusConstants.S_UNITIZE || newStatus == StatusConstants.S_CODING) {
                if (reject) {
                    task.executeUpdate(dbTask, "INSERT INTO usersqueue (users_id, batch_id, timestamp) " +
                                                "SELECT coder_id, batch_id,'" + timestamp +
                                                "' FROM batchuser WHERE batch_id =" + batch_id);
                    ResultSet getQCIDResultSet = st.executeQuery("SELECT qc_id,batch_id FROM batchuser " +
                                                                  "WHERE batch_id =" + batch_id);
                    getQCIDResultSet.next();
                    
                    //insert into batch history
                    BatchProcessHistroyData data = new BatchProcessHistroyData();
                    data.setBatch_id(getQCIDResultSet.getInt(2));
                    data.setVolume_id(volume_id);
                    data.setProcess(strStatus);
                    data.setIs_ready("Yes");
                    data.setIn_queue("Yes");
                    data.setQueued_time(getCurrentDateTime());
                    data.setQueued_to(getQCIDResultSet.getInt(1));

                    data.insertIntoHistoryTable(con);

                    // mark batch as rework
                    st.executeUpdate(SQLQueries.UPD_REWORK + batch_id);
                } else {                    
                    if (rework != 0) {                        
                        task.executeUpdate(dbTask, "INSERT INTO usersqueue (users_id, batch_id, timestamp) " +
                                                   "SELECT qc_id,batch_id," + timestamp + 
                                                   " FROM batchuser WHERE batch_id =" + batch_id);
                        ResultSet getQCIDResultSet = st.executeQuery("SELECT qc_id,batch_id FROM batchuser " +
                                                                     "WHERE batch_id =" + batch_id);
                        getQCIDResultSet.next();

                        // insert into batch-history table
                        BatchProcessHistroyData data = new BatchProcessHistroyData();
                        data.setBatch_id(getQCIDResultSet.getInt(2));
                        data.setVolume_id(volume_id);
                        data.setProcess(strStatus);
                        data.setIs_ready("Yes");
                        data.setIn_queue("Yes");
                        data.setQueued_time(getCurrentDateTime());
                        data.setQueued_to(getQCIDResultSet.getInt(1));
                        data.insertIntoHistoryTable(con);
                    } else {
                        task.executeUpdate(dbTask, "INSERT INTO teamsqueue (teams_id, batch_id, timestamp) " +
                                                   "SELECT teams_id," + batch_id + ",'" + timestamp + 
                                                   "' from users WHERE users_id =" + task.getUsersId() +
                                                   " and teams_id != 0");
                    }
                }
            }
            dbTask.commitTransaction(task);            
        } catch (SQLException sql) {            
            printExceptions("SQLException during update batch status.",sql);
        } catch (Exception exc) {
            printExceptions("Exception during update batch status.", exc);
        }
    }

    /**
     * Create coding batches composed of documentCount documents in the 
     * batch table for a given volume and unitize batch.
     * The unitize batch becomes UBatched.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param batch_id the batch.batch_id of the batch to use as input to
     * the coding batches about to be created
     * @param documentCount the approximate number of documents to put in
     * each coding batch
     */
    public static void createCodingBatches(UserTask task, DBTask dbTask, int batch_id, int documentCount)
            throws SQLException {

        Connection con = null;
        Statement getBatchIDStatment = null;
        try {

            con = dbTask.getConnection();
            getBatchIDStatment = dbTask.getStatement();        

            // make sure count is positive
            if (documentCount <= 0) {
                throw new ServerFailException("Documents per batch is not positive");
            }

            // make sure the given batch_id is still status UComplete
            // also, get highest used batch number
            // Note: batch with UComplete status should never be active
            PreparedStatement getVolumeIdPrepStatement = con.prepareStatement(SQLQueries.SEL_VOLUME_ID);
            getVolumeIdPrepStatement.setInt(1, batch_id);
            ResultSet rs = getVolumeIdPrepStatement.executeQuery();
            if (!rs.next()) {
                throw new ServerFailException("Batch is not unitize complete");
            }
            int volumeId = rs.getInt(1);
            int highCodingBatch = rs.getInt(2);
            int ubatchLft = rs.getInt(3);
            int ubatchRgt = rs.getInt(4);
            int projectId = rs.getInt(5);
            int activeGroup = rs.getInt(6);
            String treatment_level = rs.getString(7);
            rs.close();

            PreparedStatement insIntoBatchPrepStatement = task.prepareStatement(dbTask, SQLQueries.INS_LFT);
            insIntoBatchPrepStatement.setInt(1, volumeId);

            // Note.  not managed -- new batch not expanded yet on client, old goes away
            PreparedStatement updateChildPrepStatement = con.prepareStatement(SQLQueries.UPD_CHILD_TOP);
            updateChildPrepStatement.setInt(1, volumeId);

            // loop through all children for this batch            
            PreparedStatement getlClftPrepStatement = con.prepareStatement(SQLQueries.SEL_CLFT, 
                                                          ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            getlClftPrepStatement.setInt(1, volumeId);
            getlClftPrepStatement.setInt(2, ubatchLft);
            getlClftPrepStatement.setInt(3, ubatchRgt);
            rs = getlClftPrepStatement.executeQuery();

            //create coding batch by document count and batch should start with document(D)
            String getRecordsfromproject_l1 = "select * from project_l1 where child_id=? and boundary_flag=?";
            String getRecordsfrompage = "select * from page where child_id=? and boundary_flag=?";

            PreparedStatement checkBoundaryPrepStatement = null;
            PreparedStatement checkBoundaryByIncChildPrepSatement = null;
            while (rs.next()) {
                final int lft = rs.getInt(1);
                int rgt = rs.getInt(2);
                int child_id = rs.getInt(4);
                boolean isEndRange = rs.getBoolean(3);
                // count up to documentCount children
                for (int i = 0; i < documentCount; i++) {
                    if (!rs.next()) {
                        break;
                    }
                    child_id = rs.getInt(4);
                    if (documentCount - 1 == i) {
                        for (int j = 1; j < child_id; j++) {
                            if (treatment_level.equals("L1")) {
                                checkBoundaryPrepStatement = con.prepareStatement(getRecordsfromproject_l1);
                                checkBoundaryPrepStatement.setInt(1, child_id);
                                checkBoundaryPrepStatement.setString(2, "D");
                            } else {
                                checkBoundaryPrepStatement = con.prepareStatement(getRecordsfrompage);
                                checkBoundaryPrepStatement.setInt(1, child_id);
                                checkBoundaryPrepStatement.setString(2, "D");
                            }
                            ResultSet checkBoundaryRS = checkBoundaryPrepStatement.executeQuery();

                            if (checkBoundaryRS.next()) {
                                rs.previous();
                                rgt = rs.getInt(2);
                                isEndRange = rs.getBoolean(3);
                                break;
                            } else {
                                if (!rs.next()) {
                                    break;
                                }
                                child_id = rs.getInt(4);
                                // child_id = child_id+1;
                                if (treatment_level.equals("L1")) {
                                    checkBoundaryByIncChildPrepSatement = con.prepareStatement(getRecordsfromproject_l1);
                                    checkBoundaryByIncChildPrepSatement.setInt(1, child_id);
                                    checkBoundaryByIncChildPrepSatement.setString(2, "D");
                                } else {
                                    checkBoundaryByIncChildPrepSatement = con.prepareStatement(getRecordsfrompage);
                                    checkBoundaryByIncChildPrepSatement.setInt(1, child_id);
                                    checkBoundaryByIncChildPrepSatement.setString(2, "D");
                                }
                                ResultSet checkBoundaryByIncChildRS = checkBoundaryByIncChildPrepSatement.executeQuery();
                                if (checkBoundaryByIncChildRS.next()) {
                                    rs.previous();
                                    rgt = rs.getInt(2);
                                    isEndRange = rs.getBoolean(3);
                                    break;
                                } else {
                                    rgt = rs.getInt(2);
                                    isEndRange = rs.getBoolean(3);
                                }
                            }
                        }
                    } else {
                        rgt = rs.getInt(2);
                        isEndRange = rs.getBoolean(3);
                    }
                }                
                // TBD: need to allow for continuing to end of range
                // continue until end of range                
                // create a new batch
                highCodingBatch++;
                insIntoBatchPrepStatement.setInt(2, lft);
                insIntoBatchPrepStatement.setInt(3, rgt);
                insIntoBatchPrepStatement.setInt(4, highCodingBatch);
                insIntoBatchPrepStatement.setInt(5, activeGroup > 0 ? 1 : 0);
                insIntoBatchPrepStatement.setString(6, treatment_level);
                insIntoBatchPrepStatement.executeUpdate();

                //select the last inserted batch id
                getBatchIDStatment = con.createStatement();
                ResultSet getBatchIDResultSet = getBatchIDStatment.executeQuery(SQLQueries.SEL_TOP_BATCH_ID);
                getBatchIDResultSet.next();
                int batch_Id = getBatchIDResultSet.getInt(1);
                
                //insert into batch-history
                BatchHistoryData batchData = new BatchHistoryData(con, batch_Id);
                batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.ADD);

                BatchProcessHistroyData data = new BatchProcessHistroyData();
                data.setBatch_id(batch_Id);
                data.setVolume_id(volumeId);
                data.setProcess("Coding");
                data.setIs_ready("Yes");
                data.insertIntoHistoryTable(con);

                // adjust batch_id in children
                updateChildPrepStatement.setInt(2, lft);
                updateChildPrepStatement.setInt(3, rgt);
                updateChildPrepStatement.executeUpdate();
            }

            insIntoBatchPrepStatement.close();
            updateChildPrepStatement.close();

            // Adjust the high-water mark
            // Note.  not managed.
            PreparedStatement updateProjectPrepStatement = con.prepareStatement(SQLQueries.UPD_PROJECT);
            updateProjectPrepStatement.setInt(1, highCodingBatch);
            updateProjectPrepStatement.setInt(2, projectId);
            updateProjectPrepStatement.executeUpdate();

            ProjectHistoryData projectData = new ProjectHistoryData(con, projectId);
            projectData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

            // update the status of the Unitize batch being split into batches
            PreparedStatement updateStatusPrepStatement = task.prepareStatement(dbTask, SQLQueries.UPD_BATCH_STATUS);
            updateStatusPrepStatement.setInt(1, batch_id);
            updateStatusPrepStatement.executeUpdate();

            BatchHistoryData batchData = new BatchHistoryData(con, batch_id);
            batchData.setStatus("UBatched");
            batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.STATUS_CHANGED);

        } catch (SQLException sql) {
            printExceptions("SQLException during creating coding batches." , sql);
        } catch (Exception exc) {
            printExceptions("Exception during creating coding batches." , exc);
        }
    }

    public static void createListingBatches(UserTask task, DBTask dbTask, int volumeId, int userId, MessageWriter writer) throws ServerFailException {
        Connection con = null;        
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        try {
            con = dbTask.getConnection();            
            PreparedStatement getListingBatchPrepStatement = con.prepareStatement(SQLQueries.SEL_BATCH_LISTING);
            getListingBatchPrepStatement.setInt(1, volumeId);
            ResultSet getListingBatchResultSet = getListingBatchPrepStatement.executeQuery();            
            ArrayList batchIdlist = new ArrayList();
            ArrayList batchStatuslist = new ArrayList();
            while (getListingBatchResultSet.next()) {
                int batchId = getListingBatchResultSet.getInt(1);
                String batchStatus = getListingBatchResultSet.getString(2);
                if ("Coding".equals(batchStatus) || "CodingQC".equals(batchStatus) || "Listing".equals(batchStatus) ||
                    "LComplete".equals(batchStatus) || "TComplete".equals(batchStatus) || "Tally".equals(batchStatus)) {
                    if ("Listing".equals(batchStatus) || "LComplete".equals(batchStatus)) {
                        throw new ServerFailException("Batches are already assigned for Listing!");
                    } else if ("TComplete".equals(batchStatus) || "Tally".equals(batchStatus)) {
                        throw new ServerFailException("Listing Completed, Can't assign again!!");
                    } else if ("Coding".equals(batchStatus) || "CodingQC".equals(batchStatus)) {
                        throw new ServerFailException("CodingQC is not Completed, Can't do Listing!!");
                    }
                }
                batchStatuslist.add(batchStatus);
                batchIdlist.add(batchId);
            }
            int count = 0;
            for (int i = 0; i < batchStatuslist.size(); i++) {
                if ("QCComplete".equals(batchStatuslist.get(i)) || "MaskingComplete".equals(batchStatuslist.get(i))) {
                    count++;                
                }
            }
            if (count == batchStatuslist.size()) {
                for (int i = 0; i < batchIdlist.size(); i++) {
                    task.executeUpdate(dbTask, SQLQueries.UPD_BATCH_LISTING + batchIdlist.get(i));
                    
                    BatchHistoryData batchData = new BatchHistoryData(con, (Integer) batchIdlist.get(i));
                    batchData.setStatus("Listing");
                    batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.STATUS_CHANGED);
                    task.executeUpdate(dbTask,"insert into usersqueue(batch_id,users_id,timestamp) " +
                                             "values(" + batchIdlist.get(i) + ", " + userId + ", '" + timestamp + "')");
                    BatchProcessHistroyData data = new BatchProcessHistroyData();
                    data.setBatch_id((Integer) batchIdlist.get(i));
                    data.setVolume_id(volumeId);
                    data.setProcess(batchStatuslist.get(i).toString());
                    data.setIs_ready("Yes");
                    data.setIn_queue("Yes");
                    data.setQueued_time(getCurrentDateTime());
                    data.setQueued_to(userId);
                    data.insertIntoHistoryTable(dbTask.getConnection());
                }
            } else {
                throw new ServerFailException("All the Batches Should be Coding QCComplete!");
            }

        } catch (SQLException sql) {
            printExceptions("SQLException during creating listing batches." , sql);

        } 
//        catch (Exception exc) {
//            printExceptions("Exception during creating listing batches." , exc);
//        }

    }

    public static void createTallyBatches(UserTask task, DBTask dbTask, int volumeId, int userId, MessageWriter writer)
            throws ServerFailException {
       
        Connection con = null;        
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        try {
            con = dbTask.getConnection();
            PreparedStatement getTallyBatchPrepStatement = con.prepareStatement(SQLQueries.SEL_BATCH_TALLY);
            getTallyBatchPrepStatement.setInt(1, volumeId);
            ResultSet getTallyBatchResultSet = getTallyBatchPrepStatement.executeQuery();            
            ArrayList batchIdlist = new ArrayList();
            ArrayList batchStatuslist = new ArrayList();
            while (getTallyBatchResultSet.next()) {
                int batchId = getTallyBatchResultSet.getInt(1);
                String batchStatus = getTallyBatchResultSet.getString(2);
                if ("Coding".equals(batchStatus) || "CodingQC".equals(batchStatus) || "QCComplete".equals(batchStatus)||
                     "Listing".equals(batchStatus) || "LComplete".equals(batchStatus) || "TComplete".equals(batchStatus)
                     || "Tally".equals(batchStatus)) {
                    if ("Listing".equals(batchStatus)) {
                        throw new ServerFailException("Listing not completed, Can't assign for Tally!");
                    } else if ("TComplete".equals(batchStatus) || "Tally".equals(batchStatus)) {
                        throw new ServerFailException("Batches are already assigned for Tally!");
                    } else if ("Coding".equals(batchStatus) || "CodingQC".equals(batchStatus)) {
                        throw new ServerFailException("CodingQC is not Completed, Can't assign for Tally!!");
                    } else if ("QCComplete".equals(batchStatus)) {
                        throw new ServerFailException("Listing is not Completed, Can't assign for Tally!!");
                    }
                }
                batchStatuslist.add(batchStatus);
                batchIdlist.add(batchId);
            }
            int count = 0;
            for (int i = 0; i < batchStatuslist.size(); i++) {
                if ("LComplete".equals(batchStatuslist.get(i))) {
                    count++;                     
                }
            }
            if (count == batchStatuslist.size()) {
                for (int i = 0; i < batchIdlist.size(); i++) {
                    task.executeUpdate(dbTask, SQLQueries.UPD_BATCH_TALLY + batchIdlist.get(i));
                    
                    BatchHistoryData batchData = new BatchHistoryData(con, (Integer) batchIdlist.get(i));
                    batchData.setStatus("Tally");
                    batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.STATUS_CHANGED);
                    task.executeUpdate(dbTask, "INSERT INTO usersqueue(batch_id,users_id,timestamp)" +
                                               " values(" + batchIdlist.get(i) + "," + userId + ",'" + timestamp + "')");

                    BatchProcessHistroyData data = new BatchProcessHistroyData();
                    data.setBatch_id((Integer) batchIdlist.get(i));
                    data.setVolume_id(volumeId);
                    data.setProcess(batchStatuslist.get(i).toString());
                    data.setIs_ready("Yes");
                    data.setIn_queue("Yes");
                    data.setQueued_time(getCurrentDateTime());
                    data.setQueued_to(userId);
                    data.insertIntoHistoryTable(dbTask.getConnection());
                }
            } else {
                throw new ServerFailException("All the Batches Should be ListingQC Complete!");
            }
        } catch (SQLException sql) {
            printExceptions("Exception during creating Tally batches." , sql);
        }

    }

    public static void createModifyErrorBatches(UserTask task, DBTask dbTask, int batchId) {
        try {
            Statement getBatchStatusPrepStatement = dbTask.getStatement();
            ResultSet getBatchStatusResultSet = getBatchStatusPrepStatement.executeQuery("select batch_id,status from batch where batch_id ='" + batchId + "'");
            if (getBatchStatusResultSet.next()) {
                String status = getBatchStatusResultSet.getString(2);
                
                if(CommonConstants.PROCESS_QA_ERROR.equals(status)) {
                    task.executeUpdate(dbTask, "update  batch  set status ='ModifyErrors',modify_error_status ='QAComplete"+
                                             "'  where batch_id =" + batchId);
                }else {
                    task.executeUpdate(dbTask, "update  batch  set status ='ModifyErrors',modify_error_status ='"+status+
                                             "'  where batch_id =" + batchId);
                }
            }
        } catch (SQLException ex) {            
            printExceptions("Exception during creating ModifyError batches." , ex);
        }
    }

    /**
     * Add or remove a batch based on the values of batch_id and child_id; move
     * a document to the next or the previous batch.  
     * <table border="1">
     * <th>    batchId     <th>childId     <th>delta     <th>action            <tb>
     * <tr><td>batch       <td>any         <td>any       <td>remove batch bdry </tr>
     * <tr><td>0           <td>range       <td>0         <td>add batch bdry    </tr>
     * <tr><td>0           <td>range       <td>-1        <td>move range up     </tr>
     * <tr><td>0           <td>range       <td>+1        <td>move range down   </tr>
     * </table>
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param batchId - 0 if this is an add batch or move range up or down;
     * otherwise, the batch_id of the batch to be removed
     * @param childId - 0 of this is a remove batch; otherwise, the child_id of the
     * first page of the batch being added.
     * @param delta - 0 if this is an add or remove batch; 1 if it is a move
     * document up; -1 if move document down
     */
    public static void batchBoundary(UserTask task, DBTask dbTask, int batchId,
                                      int childId, int delta) throws SQLException {
       
        if (batchId > 0) {
            removeBatch(task, dbTask, batchId);
        } else if (delta == 0) {
            addBatch(task, dbTask, childId);
        } else if (delta > 0) {
            moveChildDown(task, dbTask, childId);
        } else { // since delta < 0
            moveChildUp(task, dbTask, childId);
        }
    }

    private static void removeBatch(UserTask task, DBTask dbTask, int batchId) throws SQLException {
        Statement getBatchIdStatement = dbTask.getStatement();
        Connection con = null;
        
        // find the preceding batch
        ResultSet getBatchIdResultSet = getBatchIdStatement.executeQuery("SELECT Top 1 PB.batch_id, B.rgt, dbo.fn_status(B.status,PB.status,S.batch_id,PS.batch_id)" +
                                       " FROM batch B inner join batch PB on B.volume_id = PB.volume_id " +
                                       "left join session S on S.batch_id=B.batch_id left join session PS " +
                                       "on PS.batch_id=PB.batch_id WHERE B.batch_id=" + batchId + " and  " +
                                       "PB.status not in ('Unitize', 'UQC', 'UComplete', 'UBatched') " +
                                       "and PB.lft < B.lft ORDER BY PB.lft desc");

        if (!getBatchIdResultSet.next()) {
            throw new ServerFailException("no prior batch for remove");
        }
        int priorBatchId = getBatchIdResultSet.getInt(1);
        int batchRgt = getBatchIdResultSet.getInt(2);
        boolean ok = getBatchIdResultSet.getBoolean(3);
        getBatchIdResultSet.close();
        if (!ok) {
            throw new ServerFailException("1 batches in use or different status");
        }
        BatchHistoryData batchData = new BatchHistoryData(con, batchId);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.DELETE);

        // delete the batch
        task.executeUpdate(dbTask, SQLQueries.DEL_BATCH + batchId);
        // fix up boundary of preceding batch
        // Note.  does not need to be managed
        PreparedStatement updateRGTPrepStatement = con.prepareStatement(SQLQueries.UPD_RGT);
        updateRGTPrepStatement.setInt(1, batchRgt);
        updateRGTPrepStatement.setInt(2, priorBatchId);
        updateRGTPrepStatement.executeUpdate();

        // move the children to the preceding batch
        PreparedStatement updateRgtPrepStatement = task.prepareStatement(dbTask, SQLQueries.UPD_RGT);
        updateRgtPrepStatement.setInt(1, priorBatchId);
        updateRgtPrepStatement.setInt(2, batchId);
        updateRgtPrepStatement.executeUpdate();

        // remove from the assignment table and the queues
        task.executeUpdate(dbTask, SQLQueries.DEL_ASSGN + batchId);
        task.executeUpdate(dbTask, SQLQueries.DEL_TEAMSQUEUE + batchId);
        task.executeUpdate(dbTask, SQLQueries.DEL_USERSQUEUE + batchId);
    }

    // move the child to the preceding batch
    private static void moveChildUp(UserTask task, DBTask dbTask, int childId)
            throws SQLException {

        Statement getBatchIDStatement = dbTask.getStatement();
        Connection con = null;

        // find the current and preceding batches
        ResultSet rs = getBatchIDStatement.executeQuery("select Top 1 C.batch_id, PB.batch_id, C.rgt,  case   " +
                                                         "when((B.status=PB.status and S.batch_id is null " +
                                                         "and PS.batch_id is null))   then 1   else 0 end as " +
                                                         "'B.status=PB.status and S.batch_id is null and " +
                                                         "PS.batch_id is null' from child C   inner join batch B " +
                                                         "on B.batch_id =C.batch_id   inner join batch PB " +
                                                         "on PB.volume_id = B.volume_id   left join session S " +
                                                         "on S.batch_id=B.batch_id   left join session PS " +
                                                         "on PS.batch_id=PB.batch_id where C.child_id=" + childId + 
                                                         " and PB.volume_id = C.volume_id and PB.rgt < C.lft " +
                                                         "order by PB.rgt desc");

        if (!rs.next()) {
            throw new ServerFailException("no prior child for move up");
        }
        int batchId = rs.getInt(1);
        int priorBatchId = rs.getInt(2);
        int childRgt = rs.getInt(3);
        boolean ok = rs.getBoolean(4);
        rs.close();
        if (!ok) {
            throw new ServerFailException("2 batches in use or different status");
        }
        // find the following child
        // must be in current batch
        PreparedStatement getLFTPrepStmt = con.prepareStatement(SQLQueries.SEL_LFT);
        getLFTPrepStmt.setInt(1, batchId);
        getLFTPrepStmt.setInt(2, childRgt);
        rs = getLFTPrepStmt.executeQuery();

        if (!rs.next()) {
            throw new ServerFailException("no next child for move up");
        }
        int nextChildLft = rs.getInt(1);
        rs.close();

        // fix up boundaries of batches
        // Note.  does not need to be managed
        PreparedStatement updateBatchLftPrepStmt = con.prepareStatement(SQLQueries.UPD_BATCH_LFT);
        updateBatchLftPrepStmt.setInt(1, nextChildLft);
        updateBatchLftPrepStmt.setInt(2, batchId);
        updateBatchLftPrepStmt.executeUpdate();

        BatchHistoryData batchData = new BatchHistoryData(con, batchId);
        batchData.setLft(nextChildLft);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

        PreparedStatement updateBatchRgtPrepStmt = con.prepareStatement(SQLQueries.UPD_BATCH_RGT);
        updateBatchRgtPrepStmt.setInt(1, childRgt);
        updateBatchRgtPrepStmt.setInt(2, priorBatchId);
        updateBatchRgtPrepStmt.executeUpdate();

        // move the children to the preceding batch
        PreparedStatement updateChildPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_CHILD_BATCHID);
        updateChildPrepStmt.setInt(1, priorBatchId);
        updateChildPrepStmt.setInt(2, batchId);
        updateChildPrepStmt.setInt(3, childRgt);
        updateChildPrepStmt.executeUpdate();
    }

    // move the child to the following batch
    private static void moveChildDown(UserTask task, DBTask dbTask, int childId)
            throws SQLException {

        Statement st = dbTask.getStatement();
        Connection con = null;
        // find the current and following batches
        ResultSet rs = st.executeQuery("select Top 1 C.batch_id, NB.batch_id, C.lft,   case when " +
                                       "((B.status=NB.status and S.batch_id is null and NS.batch_id is null)) " +
                                       "then 1 else 0 end as 'B.status=NB.status and S.batch_id is null " +
                                       "and NS.batch_id is null' from child C inner join batch B on " +
                                       "B.batch_id = C.batch_id    inner join batch NB on NB.volume_id =B.volume_id " +
                                       "left join session S on S.batch_id=B.batch_id " +
                                       "left join session NS on S.batch_id=NB.batch_id where C.child_id= " + childId + 
                                       " and NB.volume_id = C.volume_id  and NB.lft > C.rgt order by NB.lft");

        if (!rs.next()) {
            throw new ServerFailException("no next batch for move down");
        }
        int batchId = rs.getInt(1);
        int nextBatchId = rs.getInt(2);
        int childLft = rs.getInt(3);
        boolean ok = rs.getBoolean(4);
        rs.close();

        if (!ok) {
            throw new ServerFailException("3 batches in use or different status");
        }

        // find the preceding child
        // must be in current batch
        PreparedStatement getRGTPrepStmt = con.prepareStatement(SQLQueries.SEL_RGT);
        getRGTPrepStmt.setInt(1, batchId);
        getRGTPrepStmt.setInt(2, childLft);
        rs = getRGTPrepStmt.executeQuery();

        if (!rs.next()) {
            throw new ServerFailException("no prior child for move down");
        }
        int priorChildRgt = rs.getInt(1);
        rs.close();

        // fix up boundaries of batches
        // Note.  does not need to be managed
        PreparedStatement updateRgtPrepStmt = con.prepareStatement(SQLQueries.UPD_BATCHRGT);
        updateRgtPrepStmt.setInt(1, priorChildRgt);
        updateRgtPrepStmt.setInt(2, batchId);
        updateRgtPrepStmt.executeUpdate();

        BatchHistoryData batchData = new BatchHistoryData(con, batchId);
        batchData.setRgt(priorChildRgt);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

        PreparedStatement updateLFTPrepStmt = con.prepareStatement(SQLQueries.UPD_BATCHLFT);
        updateLFTPrepStmt.setInt(1, childLft);
        updateLFTPrepStmt.setInt(2, nextBatchId);
        updateLFTPrepStmt.executeUpdate();

        batchData = new BatchHistoryData(con, nextBatchId);
        batchData.setLft(childLft);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

        // move the children to the preceding batch
        PreparedStatement updateChildPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_BATCHID);
        updateChildPrepStmt.setInt(1, nextBatchId);
        updateChildPrepStmt.setInt(2, batchId);
        updateChildPrepStmt.setInt(3, childLft);
        updateChildPrepStmt.executeUpdate();
    }

    // create a new batch starting at the given child
    private static void addBatch(UserTask task, DBTask dbTask, int childId)
            throws SQLException {

        Statement st = dbTask.getStatement();
        Connection con = null;

        // find the current batch and volume, as well as the preceding child
        ResultSet getBatchDetailsResultSet = st.executeQuery("select Top 1 C.batch_id, CR.rgt, C.lft, B.rgt, B.volume_id, B.status, B.priority, P.high_coding_batch, P.high_unitize_batch, Case when (S.batch_id is null)  then 1 else 0 end as 'S.batch_id is null'         , P.project_id, B.active_group from child C     inner join batch B on B.batch_id = C.batch_id     inner join volume V on V.volume_id=C.volume_id     inner join project P on P.project_id=V.project_id     inner join child CR on CR.volume_id=C.volume_id     left join session S on S.batch_id=B.batch_id where C.child_id=" + childId + " and CR.rgt < C.lft and V.sequence > 0 order by CR.rgt desc");

        if (!getBatchDetailsResultSet.next()) {
            throw new ServerFailException("no prior child for add batch");
        }
        int batchId = getBatchDetailsResultSet.getInt(1);
        int priorChildRgt = getBatchDetailsResultSet.getInt(2);
        int childLft = getBatchDetailsResultSet.getInt(3);
        int batchRgt = getBatchDetailsResultSet.getInt(4);
        int volumeId = getBatchDetailsResultSet.getInt(5);
        String status = getBatchDetailsResultSet.getString(6);
        int priority = getBatchDetailsResultSet.getInt(7);
        int highCoding = getBatchDetailsResultSet.getInt(8);
        int highUnitize = getBatchDetailsResultSet.getInt(9);
        boolean ok = getBatchDetailsResultSet.getBoolean(10);
        int projectId = getBatchDetailsResultSet.getInt(11);
        int activeGroup = getBatchDetailsResultSet.getInt(12);
        getBatchDetailsResultSet.close();

        if (!ok) {
            throw new ServerFailException("batch is in use");
        }

        // fix up boundary of old batches
        // Note.  does not need to be managed
        PreparedStatement updateRGTPrepStmt = con.prepareStatement(SQLQueries.UPD_RGT_BATCH);
        updateRGTPrepStmt.setInt(1, priorChildRgt);
        updateRGTPrepStmt.setInt(2, batchId);
        updateRGTPrepStmt.executeUpdate();

        //insert into batch history
        BatchHistoryData batchData = new BatchHistoryData(con, batchId);
        batchData.setRgt(priorChildRgt);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

        // compute new batch number and update volume fields
        // Note.  does not need to be managed
        int batchNumber;
        if (status.charAt(0) == 'U') {
            batchNumber = highUnitize + 1;
            PreparedStatement updateProjectPrepStmt = con.prepareStatement(SQLQueries.UPD_PROJ_UNITIZE);
            updateProjectPrepStmt.setInt(1, batchNumber);
            updateProjectPrepStmt.setInt(2, projectId);
            updateProjectPrepStmt.executeUpdate();

        } else { // since it's a coding batch

            batchNumber = highCoding + 1;
            PreparedStatement updateProjectPrepStmt = con.prepareStatement(SQLQueries.UPD_PROJ_CODING);
            updateProjectPrepStmt.setInt(1, batchNumber);
            updateProjectPrepStmt.setInt(2, projectId);
            updateProjectPrepStmt.executeUpdate();
        }

        ProjectHistoryData projectData = new ProjectHistoryData(con, projectId);
        projectData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);

        // create the new batch
        PreparedStatement insertIntoLftRgtPrepStmt = task.prepareStatement(dbTask, SQLQueries.INS_LFT_RGT);
        insertIntoLftRgtPrepStmt.setInt(1, volumeId);
        insertIntoLftRgtPrepStmt.setInt(2, childLft);
        insertIntoLftRgtPrepStmt.setInt(3, batchRgt);
        insertIntoLftRgtPrepStmt.setInt(4, batchNumber);
        insertIntoLftRgtPrepStmt.setString(5, status);
        insertIntoLftRgtPrepStmt.setInt(6, priority);
        insertIntoLftRgtPrepStmt.setInt(7, activeGroup);
        insertIntoLftRgtPrepStmt.executeUpdate();

        //---------insert into history_batch for audit-history.---------------- 
        st = con.createStatement();
        ResultSet getBatchIdResultSet = st.executeQuery(SQLQueries.SEL_TOP_BATCH_ID);
        getBatchIdResultSet.next();
        int batch_Id = getBatchIdResultSet.getInt(1);

        batchData = new BatchHistoryData(con, batch_Id);
        batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.ADD);

        BatchProcessHistroyData data = new BatchProcessHistroyData();
        data.setBatch_id(batch_Id);
        data.setVolume_id(volumeId);
        data.setProcess(status);
        data.setIs_ready("Yes");        
        data.insertIntoHistoryTable(con);

        // move the children to the new batch
        PreparedStatement updateChildPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_CHILD_BATCH);
        updateChildPrepStmt.setInt(1, batchId);
        updateChildPrepStmt.setInt(2, childLft);
        updateChildPrepStmt.executeUpdate();
    }

    /**
     * This method calculate the current date & time
     * @return the timestamp object
     */
    public static Timestamp getCurrentDateTime() {
        java.util.Date date = new java.util.Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
    
     /**
     * This methods prints the stack trace of generated exceptions
     * @param customMessage : is the error message to be shown
     * @param execption : is the exception object caught or thrown
     */
    private static void printExceptions(String customMessage,Exception execption){
       logger.error(customMessage + execption);            
       StringWriter swt = new StringWriter();
       execption.printStackTrace(new PrintWriter(swt));
       logger.error(swt.toString());
    }
}
