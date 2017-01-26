/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to check the batch availablity for a volume of a project.
 * @author bmurali
 */
public class TaskCheckBatchAvailable extends ClientTask {

    /** Project Id */
    private int projectId;
    /** Voluem Id */
    private int volumeId;
    /** Status of the batch */
    private String status;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * 
     * @param projectId  Project Id
     * @param volumeId   Volume Id
     * @param status     Status for the batch. That could be like
     *                   1. Listing
     *                   2. ListingQC
     *                   3. Tally
     *                   4. TallyQC
     *                   5. Unitization etc.
     */
    public TaskCheckBatchAvailable(int projectId, int volumeId, String status) {

        this.projectId = projectId;
        this.volumeId = volumeId;
        this.status = status;
    }

    /**
     * Write the message with attributes.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_CHECK_BATCH_AVAILABLE);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
