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
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.server.valueobjects.BatchHistoryData;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.session.UserTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class assigns a batch to the user and puts the batch in usersqueue. This also removes batch assignment of user.
 * The batch assignment history is also done.
 * @author Bala
 */
public class Command_batch_queue implements Command{
 
    public String execute(Element action, UserTask task, DBTask dbTask,  MessageWriter writer) {
        
        Statement st = null;
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        
        try {
            st = dbTask.getStatement();
            /** 0 if queuing a volume; else batch_id to queue */
            int batch_id = Integer.parseInt(action.getAttribute(A_BATCH_ID));
            /** required - volume to queue if batch_id is 0; else volume_id containing the batch */
            int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            /** 0 if queuing to a team; else users_id to use in usersqueue */
            int users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
            /** 0 if queuing to a user; else teams_id to use in teamsqueue */
            int teams_id = Integer.parseInt(action.getAttribute(A_TEAMS_ID));

            // clearQueues means a drop happened from the usersTree and all
            // occurrences of this batch should be removed from the queues
            // before the batch is added to the destination queue.
            boolean clearQueues = action.getAttribute(A_DELETE).equals("true") ? true : false;

            // make sure the batch is not assigned to a user
            if (batch_id > 0) {            	
            	ResultSet getUserAssignedResultSet = st.executeQuery("SELECT 0 as '0' FROM assignment A WITH(HOLDLOCK) " +
                                                "inner join batch B on B.batch_id = A.batch_id " +
                                                "WHERE A.batch_id= " +batch_id);
                if (getUserAssignedResultSet.next()) {
                    throw new ServerFailException(UserErrorMessage.alreadyAssigned);
                }
            }
            
            if (clearQueues) {
                task.executeUpdate(dbTask,SQLQueries.DEL_USR_QUEUE+batch_id);
                task.executeUpdate(dbTask,SQLQueries.DEL_TEAM_QUEUE +batch_id);
            }
          if (batch_id == 0) {
                // create the volumequeue row
                Log.print("(Command_batch_queue.run) insert volumequeue volume/team "+ volume_id + "/" + teams_id);
                
                // queue all of the batches in the volume
                // See if there is a batch that can be queued.
                PreparedStatement selectbatchid =  task.prepareStatement(dbTask,SQLQueries.SEL_BATCH_ID);
                selectbatchid.setInt(1, volume_id);
                
                ResultSet rs = selectbatchid.executeQuery();                
                if (rs.next()) {                	
                    PreparedStatement insteamvolume =  task.prepareStatement(dbTask,SQLQueries.INS_TEAMS_VOLUME);
                    insteamvolume.setInt(1, volume_id);
                    insteamvolume.setInt(2, teams_id);
                    insteamvolume.setTimestamp(3, timestamp);
                    insteamvolume.executeUpdate();                    
                }
                
            } else {
                ResultSet rs = st.executeQuery("select status from batch where batch_id = "+batch_id);
                rs.next();
                if (users_id > 0) {  
                    // queue the batch to the user
                        task.executeUpdate(dbTask,"INSERT INTO usersqueue(batch_id,users_id,timestamp) " +
                                                   "VALUES ("+batch_id+","+users_id+",'"+timestamp+"')");
                        //Take history of the batch which is put in usersqueue                       
                        BatchProcessHistroyData data = new BatchProcessHistroyData();
                        data.setBatch_id(batch_id);
                        data.setVolume_id(volume_id);
                        data.setProcess(rs.getString(1));
                        data.setIs_ready("Yes");
                        data.setIn_queue("Yes");
                        data.setQueued_time(BatchIO.getCurrentDateTime());
                        data.setQueued_to(users_id);                       
                        data.insertIntoHistoryTable(dbTask.getConnection());                    
                } else {
                       // queue the batch to the team                   
                       PreparedStatement insteamsqueue =  task.prepareStatement(dbTask,SQLQueries.INSERT_TEAMS_QUEUE);
                       insteamsqueue.setInt(1, batch_id);
                       insteamsqueue.setInt(2, teams_id);
                       insteamsqueue.setTimestamp(3, timestamp);
                       insteamsqueue.executeUpdate();                    
                }
                // increase the priority of the queued batch
                task.executeUpdate(dbTask,SQLQueries.UPD_BATCH_PRIORITY+batch_id);
                //Log batch history
                BatchHistoryData batchData = new  BatchHistoryData(dbTask.getConnection(), batch_id);        
                batchData.insertIntoHistoryTable(dbTask.getConnection(), task.getUsersId(), Mode.EDIT);
            }  

        } catch (ServerFailException e) {
            CommonLogger.printExceptions(this, "Server Fail Exception thrown in batch queue." , e);
            return e.getMessage();
            
        } catch (SQLException e) {
            // ignore dups for now
            String sqlState = e.getSQLState();
            int errorCode = e.getErrorCode();
            Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
            if (errorCode == UserTask.ER_DUP_ENTRY ) {
                // it's a dup, ignore it
                Log.print("(Command_batch_queue.run) duplicate queue key");
            }         
            CommonLogger.printExceptions(this, "Exception while queueing the batches." , e);
        }    
        return null;
    }

     public boolean isReadOnly() {
        return true;
    }
   
}
