/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskCreateCodingBatches.java,v 1.4.6.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Following unitization, the unitize batches are divided by the user into coding
 * batches by creating batches containing documentCount documents with
 * status Coding.
 * @see server.Handler_create_coding_batches
 */
public class TaskCreateCodingBatches extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Batch Id */
    private int batchId;
    /** Number of docuemnts */
    private int documentCount;

    /**
     * Create an instance of this ClientTask and remember the parameters.
     * @param batchId the batch.batch_id of the unitize batch to split
     * @param documentCount the number of documents to include in each
     * Coding batch
     */
    public TaskCreateCodingBatches(int batchId, int documentCount) {
        this.batchId = batchId;
        this.documentCount = documentCount;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CREATE_CODING_BATCHES);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.writeAttribute(A_COUNT, documentCount);
        writer.endElement();
        writer.close();
        Log.print("(TaskCreateCodingBatches.run) " + batchId + "/" + documentCount);

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_CREATE_CODING_BATCHES reply " + ok);
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("CreateCodingBatches unexpected message type: " + ok);
        }
    }
}
