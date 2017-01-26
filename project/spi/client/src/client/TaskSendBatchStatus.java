/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendBatchStatus.java,v 1.8.8.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from SplitPaneViewer to close the 
 * currently-open batch for this user.
 * @see server.Command_close_batch
 */
public class TaskSendBatchStatus extends ClientTask {

    /** Status represents the process like Listing, Tally, QA etc.. */
    private String status;
    /** true for closing the batch else make it false. */
    private boolean closingBatch;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param status the status of the client user
     * @param closingBatch true to accept and close a batch; false to reject a batch
     */
    public TaskSendBatchStatus(String status, boolean closingBatch) {        
        this.status = status;
        this.closingBatch = closingBatch;
    }

     /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CLOSE_BATCH);
        writer.writeAttribute(A_STATUS, status);
        writer.writeAttribute(A_REJECT, closingBatch ? "NO" : "YES");
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();
        Log.print("(TaskSendBatchStatus.run) " + status);

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();        
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("close_batch unexpected reply message type: " + ok);
        }
    }
}
