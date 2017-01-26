/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskBatchRemove.java,v 1.5.6.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for removing batches and volumes from usersqueue and teamsqueue.
 * @see server.Handler_batch_boundary
 */
public class TaskBatchRemove extends ClientTask {

    /** Batch Id */
    private int batchId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the given parameter.
     * @param batchId the batch.batch_id of the batch to be queued; 0 to queue a volume
     */
    public TaskBatchRemove(int batchId) {
        this.batchId = batchId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_BATCH_BOUNDARY);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.endElement();
        writer.close();
        Log.print("(TaskBatchRemove.run) batch " + batchId);

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_BATCH_BOUNDARY reply " + ok);
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("BatchBoundary unexpected message type: " + ok);
        }
    }
}
