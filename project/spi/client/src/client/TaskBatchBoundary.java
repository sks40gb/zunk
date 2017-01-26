/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskBatchBoundary.java,v 1.7.6.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to change the boundary of the current batch.
 * @see server.Handler_batch_boundary
 * @see server#BatchIo.batchBoundary
 */
public class TaskBatchBoundary extends ClientTask {

    /** Child Id*/
    private int childId;    
    private int delta;

    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Create a new ClientTask and store the given parameters.
     * @param childId the page id or 0
     * @param delta action indicator
     */
    public TaskBatchBoundary (int childId, int delta) {
        this.childId = childId;
        this.delta = delta;
    }

    /**
     * Write the message with parameters and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_BATCH_BOUNDARY);
        writer.writeAttribute(A_ID, childId);
        writer.writeAttribute(A_DELTA, delta);
        writer.endElement();
        writer.close();
        Log.print("(TaskBatchBoundary.run) child " + childId + "/" + delta);

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_BATCH_BOUNDARY reply " + ok);
        setResult((Object)reply);

        if (! T_OK.equals(ok)
            && ! T_FAIL.equals(ok)) {
            Log.quit("BatchBoundary unexpected message type: "+ok);
        }
    }
}
