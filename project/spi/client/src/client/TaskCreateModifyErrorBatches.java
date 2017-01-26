package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for preparing the batch to ModifyError.
 * @author bmurali
 */
public class TaskCreateModifyErrorBatches extends ClientTask {

    /** Batch Id */
    private int batchId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with the parameter
     * @param batchId Batch Id 
     */
    public TaskCreateModifyErrorBatches(int batchId) {
        this.batchId = batchId;

    }

    @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CREATE_MODIFY_ERRORS);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
