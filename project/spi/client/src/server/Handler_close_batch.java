package server;

import common.Log;

import java.sql.SQLException;
import org.w3c.dom.Element;
//import client.SelectValidation;

/**
 * Update the status of a batch after close.
 * Call BatchIO.updateStatus to handle the closing of a batch.
 * @see client.TaskSendBatchStatus
 * @see BatchIO
 */
public class Handler_close_batch extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_close_batch() {}

    public void run (ServerTask task, Element action) 
    throws SQLException {
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        String oldStatus = action.getAttribute(A_STATUS);
        boolean reject = ("YES".equalsIgnoreCase(action.getAttribute(A_REJECT)));
        Log.print("Handler_close_batch: batch id: "+batchId+" reject="+reject);

        assert volumeId != 0;
        assert batchId != 0;

        BatchIO.updateStatus(task, volumeId, batchId, oldStatus, reject);
       
        if (! task.isAdmin()) {
            EventLog.close(task, volumeId, batchId, oldStatus);
        }

        Log.print("... updated batchId="+batchId);
    }
}
