/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the requests for updating the users requeue time.
 * @author ashish
 */
class Command_request_requeue implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {           
        int batchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));                
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        try{            
            PreparedStatement update_reque_uqueue =  task.prepareStatement(dbTask,SQLQueries.UPD_REQUE_UQUEUE);
            timestamp = new Timestamp(new Long(timestamp.toString()) + 10*60000);
            update_reque_uqueue.setTimestamp(1, timestamp);
            update_reque_uqueue.setInt(2, batchId);
            update_reque_uqueue.setInt(3, task.getUsersId());
            update_reque_uqueue.executeUpdate();            
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while updating the requeue time." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while updating the requeue time." , exc);
            return null;
        } 
    return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
