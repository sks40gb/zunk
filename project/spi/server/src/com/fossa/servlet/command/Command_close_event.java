/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the closing of events.
 * @author ashish
 */
class Command_close_event implements Command{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {        
        String volumeStatus ="";
        int volume_id =0;        
        int volumeId = task.getLockVolumeId();
        int batchId = task.getLockBatchId();
        if(action.hasAttribute(A_STATUS)){           
           volumeStatus = action.getAttribute(A_STATUS);
           volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        }
        String status = "";        
        Connection con = null;
        Statement st = null;
        ResultSet getBatchStatusResultSet = null;
        try {        
            con = dbTask.getConnection();
            st = dbTask.getStatement();
            getBatchStatusResultSet = st.executeQuery(SQLQueries.SELECT_STATUS+batchId);
            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);
            if (getBatchStatusResultSet.next()) {
                status = getBatchStatusResultSet.getString(1);
            } else {
                // batchId will be 0 for QA
                status = "QA";
            }
            getBatchStatusResultSet.close();
            //Update the event table 
            if("Listing".equals(volumeStatus)|| "ListingQC".equals(volumeStatus)
                    ||"Tally".equals(volumeStatus) || "TallyQC".equals(volumeStatus)){                          
                st.executeUpdate(
                    "update event set close_timestamp ='"+timestamp+"'"
                    +" where users_id="+task.getUsersId()
                    +"  and volume_id="+volume_id
                    +"  and batch_id=0"
                    +"  and status='"+volumeStatus+"'"
                    +"  and close_timestamp is NULL");
            }else if(CommonConstants.PROCESS_QA.equals(volumeStatus)) {
                st.executeUpdate(
                    "update event set close_timestamp ='"+timestamp+"'"
                    +" where users_id="+task.getUsersId()
                    +"  and volume_id="+volume_id
                    +"  and batch_id=0"
                    +"  and status='"+status+"'"
                    +"  and close_timestamp is NULL");
            }    
            else{
                st.executeUpdate(
                    "update event set close_timestamp ='"+timestamp+"'"
                    +" where users_id="+task.getUsersId()
                    +"  and volume_id="+volumeId
                    +"  and batch_id="+batchId
                    +"  and status='"+status+"'"
                    +"  and close_timestamp is NULL");
            }            
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while closing the event." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while closing the event." , exc);
            return null;
        } 
        return null;
    }
    
    public boolean isReadOnly() {
        return true;
    }
}
