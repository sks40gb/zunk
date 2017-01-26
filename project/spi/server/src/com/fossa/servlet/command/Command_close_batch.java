/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;

/**
 * This class handles the closing of batches.
 * @author ashish
 */
class Command_close_batch implements Command{
    public static int users_id = 0;
    
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {              
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        String oldStatus = action.getAttribute(A_STATUS);   //Holds the old status of batch
        boolean reject = ("YES".equalsIgnoreCase(action.getAttribute(A_REJECT)));
        Log.print("Command_close_batch: batch id: "+batchId+" reject="+reject);

        assert volumeId != 0;
        assert batchId != 0;
        try {
            users_id = task.getUsersId();
            //Updates the batch status
            BatchIO.updateStatus(task, dbTask, volumeId, batchId, oldStatus, reject);
            if (! task.isAdmin()) {
               //Log into event and close the batch
                EventLog.close(task, dbTask, volumeId, batchId, oldStatus);
            }
            Log.print("... updated batchId="+batchId);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while closing the batch." , exc);
        }
        return null;
    }
    
    public boolean isReadOnly() {
        return true;
    }
}
