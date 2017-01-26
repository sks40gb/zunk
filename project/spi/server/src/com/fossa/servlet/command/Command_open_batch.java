/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.server.valueobjects.VolumeHistoryData;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class is invoked whenever a new batch is opened.
 * @author ashish
 */
class Command_open_batch implements Command{
        private Connection connection;
        private PreparedStatement pstmt = null;
        private ResultSet rs =   null;
        private int fieldId;        
        private int ptojectId;
        private int volumeId;
        private String whichStatus;
        
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {        
        Log.print("in Command_open_batch.run"); 
        //holds the batch status like Unitize,Coding,Listing etc
        String status = action.getAttribute(A_STATUS);    
        String split[] = status.split("-");
        if(split.length >1){            
        whichStatus = split[0];        
        }else{
        whichStatus = status;
        }
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {            
            String projectIdAttribute = action.getAttribute(A_PROJECT_ID);
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));            
            String splitDocuments = "Yes";
            String userSessionId = task.getFossaSessionId();
            String project = "";
            int batchId = 0;
            Statement getProjectStatement = null;
            ResultSet getProjectResultSet = null;
            try {
                getProjectStatement = dbTask.getStatement();
                try {
                    getProjectResultSet = getProjectStatement.executeQuery("select project_name from project" + 
                                                             " where project_id=" + projectIdAttribute);
                    if (getProjectResultSet.next()) {
                        project = getProjectResultSet.getString(1);
                    }
                    //Log into event table
                    EventLog.open(task, dbTask, volumeId , whichStatus);
                } catch (SQLException ex) {
                    CommonLogger.printExceptions(this, "Exception while opening a new event." , ex);
                }                
                //Start writing the XML
                writer.startElement(T_BATCH_OPENED);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_PROJECT_ID, projectIdAttribute);                
                writer.writeAttribute(A_BATCH_ID, batchId);
                writer.writeAttribute(A_PROJECT_NAME, project);
                writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);
                writer.endElement();
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "Exception while writing a XML in opening a new event.", ex);
            }
        }else if("ListingQC".equals(whichStatus) || "TallyQC".equals(whichStatus)){         
            try {
                //Holds database table name like listing_qc or tally_qc
                String tableName = null;
                String newStatus = null;
                if("ListingQC".equals(whichStatus)){
                   tableName = "listing_qc";
                   newStatus =whichStatus;
                }else if ("TallyQC".equals(whichStatus)){
                   tableName = "tally_qc";
                   newStatus = "Tally";
                }              
               
                int userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
                String fieldName = action.getAttribute(A_FIELD_NAME);
                String splitDocuments = "Yes";
                String userSessionId = task.getFossaSessionId();
                connection = dbTask.getConnection();               
                //Get the project id & volume id for the respective table
                pstmt = connection.prepareStatement("SELECT  project_id,volume_id FROM "+ tableName +
                                                     " WHERE  field_name =? AND user_id = ?");
                pstmt.setString(1, fieldName);
                pstmt.setInt(2, userId);
                pstmt.executeQuery();
                rs = pstmt.getResultSet();
                while(rs.next()){
                     ptojectId = rs.getInt(1);
                     volumeId = rs.getInt(2);
                }
                pstmt.close();
                rs.close();
               
                //Update the batch status for the respective table.
                pstmt = connection.prepareStatement("UPDATE " + tableName + " SET  status =? where user_id =? AND field_name =?");
                pstmt.setString(1, "Assigned");
                pstmt.setInt(2, userId);
                pstmt.setString(3, fieldName);                
                pstmt.executeUpdate();             
                pstmt.close(); 
                
                pstmt = connection.prepareStatement("SELECT  projectfields_id FROM projectfields" +
                                                      " WHERE  field_name =? AND project_id = ?");
                pstmt.setString(1, fieldName);
                pstmt.setInt(2, ptojectId);
                pstmt.executeQuery();
                rs = pstmt.getResultSet();
                while(rs.next()){
                     fieldId = rs.getInt(1);
                }
                pstmt.close(); 
                rs.close();
                EventLog.open(task, dbTask, volumeId , newStatus);
                      
                //Start writing the XML
                writer.startElement(T_BATCH_OPENED);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);
                writer.writeAttribute(A_PROJECT_ID, ptojectId);
                writer.writeAttribute(A_VOLUME_ID, volumeId);
                writer.writeAttribute(A_FIELD_ID, fieldId);
                writer.endElement();
                 
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "IOException while updating listing/tally batch." , ex);
            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while updating listing/tally batch." , ex);
            }     
     }
     else{
        //Now the status will be either of Unitize,UQC,Coding,CodingQC.
        assert ! task.isAdmin();
        assert whichStatus.equals("Unitize")
            || whichStatus.equals("UQC")
            || whichStatus.equals("Coding")
            || whichStatus.equals("CodingQC")
            || whichStatus.equals("ModifyErrors");//added to have an entry in the event table

        Statement st = null;
        ResultSet getAssgndBatchResultSet = null;
        try{
            st = dbTask.getStatement();

            String batchIdAttribute = action.getAttribute(A_BATCH_ID);
            String projectIdAttribute = action.getAttribute(A_PROJECT_ID);
            int batchId = 0;            
            int returnProjectId = 0; // return to viewer
            int returnActiveGroup = 0; // return to viewer
            //L1 operations           
            String project = null;            
            int usersId = task.getUsersId();
            if (batchIdAttribute.length() > 0) {
                Log.print("(Command_open_batch.run) batchId " + batchIdAttribute);
                assert projectIdAttribute.length() == 0;
                batchId = Integer.parseInt(batchIdAttribute);
                
                // make sure it's assigned to user and in specified status
                 getAssgndBatchResultSet = st.executeQuery("SELECT 0 from assignment A WITH(HOLDLOCK) inner join batch B" +
                                                            " on B.batch_id = A.batch_id WHERE A.batch_id="+batchId+
                                                            " and users_id="+usersId+" and B.status='"+whichStatus+"'");

                //If above query didi'nt returned any result THEN the batch is not available for assignment.
                if (! getAssgndBatchResultSet.next()) {
                    throw new ServerFailException("Batch is not available for assignment.");
                }else{
                    //Batch is available for further assignments.
                }
            } else {
                assert projectIdAttribute.length() > 0;
                
                int projectId = Integer.parseInt(projectIdAttribute);                
                returnProjectId = projectId;
                Log.print("(Command_open_batch.run) projectId " + projectId + "/" + whichStatus);
                // First, find first highest-priority batch in usersqueue
                // No restriction on QCing ones own                
                ResultSet getBatchIdResultSet = st.executeQuery("SELECT TOP 1 Q.batch_id FROM usersqueue Q inner join batch B on " +
                        " B.batch_id = Q.batch_id inner join volume V on V.volume_id = B.volume_id WHERE Q.users_id="+task.getUsersId()+
                        " and V.project_id="+projectId+" and B.status='"+whichStatus+"' order by B.priority desc, B.batch_number");
                //Above query will return only one result
                if (getBatchIdResultSet.next()) {
                    batchId = getBatchIdResultSet.getInt(1);
                }

                // If none, find batch in teamsqueue
                // For UQC and CodingQC, don't allow QC of own batch
                // Also, don't select batch that is on some usersqueue
                if (batchId == 0) {                   
                    if (whichStatus.equals("Unitize") || whichStatus.equals("Coding")) {                        
                        getBatchIdResultSet = st.executeQuery("SELECT TOP 1 Q.batch_id FROM users U inner join teamsqueue Q on Q.teams_id = U.teams_id inner join batch B on B.batch_id = Q.batch_id inner join volume V on V.volume_id = B.volume_id left join usersqueue UQ on UQ.batch_id=Q.batch_id WHERE U.users_id= "+task.getUsersId()+" and V.project_id = "+projectId+" and B.status='"+whichStatus+"' and UQ.batch_id is null ORDER BY B.priority desc, B.batch_number");                        
                    } else { // since whichStatus in (UQC, CodingQC)                        
                        getBatchIdResultSet = st.executeQuery("SELECT TOP 1 Q.batch_id FROM users U  inner join teamsqueue Q on Q.teams_id = U.teams_id  inner join batch B on B.batch_id =Q.batch_id  inner join volume V on V.volume_id = B.volume_id  left join batchuser BU on BU.batch_id = B.batch_id  left join usersqueue UQ on UQ.batch_id=Q.batch_id WHERE U.users_id= "+task.getUsersId()+" and V.project_id="+projectId+" and B.status='"+whichStatus+"' and BU.coder_id <> U.users_id and UQ.batch_id is null ORDER BY B.priority desc, B.batch_number");
                    }
                    if (getBatchIdResultSet.next()) {
                        batchId = getBatchIdResultSet.getInt(1);
                    }
                }
                // Finally, find batch in teamsvolume - check that it's not assigned
                // or on usersqueue or teamsqueue
                if (batchId == 0 && (whichStatus.equals("Unitize") || whichStatus.equals("Coding"))) {                
                    getBatchIdResultSet = st.executeQuery("SELECT TOP 1 B.batch_id FROM users U    inner join teamsvolume Q on Q.teams_id = U.teams_id    inner join volume V on V.volume_id = Q.volume_id    inner join batch B on B.volume_id = V.volume_id left join assignment A on A.batch_id=B.batch_id left join usersqueue UQ on UQ.batch_id=B.batch_id left join teamsqueue TQ on TQ.batch_id=B.batch_id WHERE U.users_id="+task.getUsersId()+" and V.project_id="+projectId+" and B.status='"+whichStatus+"' and A.batch_id is null and UQ.batch_id is null and TQ.batch_id is null ORDER BY B.priority desc, B.batch_number");                    
                    if (getBatchIdResultSet.next()) {
                        batchId = getBatchIdResultSet.getInt(1);
                    }
                }
                getBatchIdResultSet.close();

                if (batchId != 0) {
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);
                    // sanity check -- we shouldn't have an assigned batch
                    // (We could do this with a unique index by catching the
                    // SQLException, but there's a problem with definitions
                    // of the error codes. )
                    ResultSet getUsrIdResultSet = st.executeQuery("SELECT users_id  FROM assignment A WITH(UPDLOCK) inner join batch B " +
                              " on B.batch_id = A.batch_id WHERE A.batch_id="+batchId);
  
                    if (getUsrIdResultSet.next()) {
                        Log.quit("Queued batch "+batchId+" already assigned to "+getUsrIdResultSet.getString(1));
                    }
                    getUsrIdResultSet.close();
                    //assign the batch to the user
                    task.executeUpdate(dbTask,"INSERT INTO assignment(batch_id,users_id,timestamp) " +
                                              "VALUES("+batchId+","+task.getUsersId()+",'"+timestamp+"')");                    
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

            ResultSet getBatchDetailsResultSet = st.executeQuery("SELECT V.volume_id, project_name, priority, split_documents," +
                     " P.project_id, active_group FROM batch B inner join volume V on V.volume_id = B.volume_id inner join " +
                     " project P on P.project_id = V.project_id where batch_id="+batchId);
            getBatchDetailsResultSet.next();
            volumeId = getBatchDetailsResultSet.getInt(1);
            project = getBatchDetailsResultSet.getString(2);
            int priority = getBatchDetailsResultSet.getInt(3);
            String splitDocuments = getBatchDetailsResultSet.getBoolean(4) ? "Yes" : "No";
            returnProjectId = getBatchDetailsResultSet.getInt(5);
            returnActiveGroup = getBatchDetailsResultSet.getInt(6);
            getBatchDetailsResultSet.close();
            task.lockBatch(dbTask,batchId);
                        
            //insert into batch history
            BatchProcessHistroyData data = new BatchProcessHistroyData();
            data.setBatch_id(batchId);
            data.setVolume_id(volumeId);
            data.setProcess(whichStatus);
            data.setIn_use("Yes");
            data.setIs_ready("No");
            data.setIn_queue("No");
            data.setAssigned_time(BatchIO.getCurrentDateTime());
            data.setAssigned_to(usersId);
            data.setStart_time(BatchIO.getCurrentDateTime());
            data.setStarted_by(usersId);
            data.insertIntoHistoryTable(dbTask.getConnection());
            
            VolumeHistoryData volumeData = new VolumeHistoryData(dbTask.getConnection(),volumeId);
            volumeData.insertIntoHistoryTable(dbTask.getConnection(),task.getUsersId(),Mode.EDIT);          
            
            // remove batch from queues            
            task.executeUpdate(dbTask,SQLQueries.DEL_OPEN_USERSQUEUE +batchId);            
            task.executeUpdate(dbTask,SQLQueries.DEL_OPEN_TEAMSQUEUE +batchId);
            // clear priority 
            // TBD: Is this right?
            if (priority != 0) {
                task.executeUpdate(dbTask,SQLQueries.UPD_OPEN_BATCH+batchId);
            }
            // record most recent user opening this batch
            if (whichStatus.equals("Coding") || whichStatus.equals("Unitize")) {
                Date date = new Date();
                long time = date.getTime();
                Timestamp timestamp = new Timestamp(time);     
                 int batchCount = st.executeUpdate(                
                     //Get the number of batches for that user
                    "Declare @count_batch_id as int SET @count_batch_id = " +
                    " (SELECT Count(*) from batchuser where batch_id = "+batchId+") If Not(@count_batch_id >0) " +
                    " INSERT into batchuser(batch_id,coder_id,mod_time) VALUES("+batchId+","+usersId+",'"+timestamp+"')"); 
                if (batchCount == 0) {
                    // must have been already been opened
                    st.executeUpdate(
                    "update batchuser"
                    +" set coder_id="+usersId
                    +"   , mod_time='"+timestamp+"'"
                    +" where batch_id="+batchId);
               }
            } else { // since whichStatus in (CodingQC, UQC)
                     // Note.  Assumed to have already been opened
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);                    
                    st.executeUpdate("update batchuser set qc_id="+usersId+", mod_time='"+timestamp+"'"
                                       +" where batch_id="+batchId);
                }            
             task.commitTransaction(dbTask);
            if (! task.isAdmin()) {
                // TBD:  log admin events?  Maybe log with different
                // event text so they can be included or not on the Timesheet.
                EventLog.open(task, dbTask, volumeId, batchId, whichStatus);
            }
            // send back info for batch -- may not need all this
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_BATCH_OPENED);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_BATCH_ID, batchId);
            writer.writeAttribute(A_PROJECT_ID, returnProjectId);
            writer.writeAttribute(A_GROUP, returnActiveGroup);            
            writer.writeAttribute(A_PROJECT_NAME, project);
            writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);            
            writer.endElement();
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while opening a new batch." , sql);
            return null;
        } catch (ServerFailException exc) {
            CommonLogger.printExceptions(this, "ServerFailException while opening a new batch." , exc);
            return exc.getMessage();
        }catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while opening a new batch." , exc);
            return null;
        }
     }
        return null;
  
    }

    public boolean isReadOnly() {
        return true;
    }

}
