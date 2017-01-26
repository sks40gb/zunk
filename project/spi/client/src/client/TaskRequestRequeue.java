/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestRequeue.java,v 1.1.6.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Task to request requeueing of a batch to QC after a given number of hours.
 * QCer is given this option when rejecting a batch.
 * @see server.Handler_request_requeue
 */
public class TaskRequestRequeue extends ClientTask {

    /** Batch Id */
    private int batchId;
    /** hours to wait before requeuing */
    private int hours;

    /**
     * Create an instance of this class and remember the parameters.
     * @param batchId the batch.batch_id of the batch to be requeued
     * @param hours the number of hours to wait before requeuing
     */
    public TaskRequestRequeue(int batchId, int hours) {
        this.batchId = batchId;
        this.hours = hours;
    }

    /**
     * Write the message with attributes and set the result.    
     */
    public void run() throws IOException {
        ServerConnection scon = Global.theServerConnection;
        MessageWriter writer = scon.startMessage(T_REQUEST_REQUEUE);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.writeAttribute(A_HOURS, hours);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("RequestRequeue unexpected message type: " + ok);
        }
    }
}
