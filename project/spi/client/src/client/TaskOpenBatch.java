/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskOpenBatch.java,v 1.13.8.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to complete opening a batch for a user logging into DIA.  The user
 * will open the previously-assigned batch or, if no batch has been assigned, 
 * the user will select a project and the next priority batch will be assigned.
 * @see server.Handler_open_batch
 */
public class TaskOpenBatch extends ClientTask {

    private int batchId = 0;
    private int projectId = 0;
    private int userId = 0;
    private String whichStatus;
    private String fieldName;
    private int volumeId = 0;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create and instance of this ClientTask and remember the parameters.
     * @param batchId the batch.batch_id of the batch to be opened; -1 if
     * the user has no assigned batch and one should be assigned
     * @param projectId -1 if the user has a batch assigned; otherwise, the
     * project.project_id the user selected for a new batch assignment
     * @param whichStatus the status of the user, who is loggin into DIA as
     * one of: "Unitize", "UQC", "Coding" or "CodingQC"
     */
    public TaskOpenBatch(int batchId, int projectId, String whichStatus) {
        this.batchId = batchId;
        this.projectId = projectId;
        this.whichStatus = whichStatus;
    }

    public TaskOpenBatch(int projectId, String whichStatus) {
        this.projectId = projectId;
        this.whichStatus = whichStatus;
    }

    public TaskOpenBatch(int projectId, String whichStatus, int volumeId) {
        this.projectId = projectId;
        this.whichStatus = whichStatus;
        this.volumeId = volumeId;
    }

    public TaskOpenBatch(int userId, String whichStatus, String fieldName) {
        this.userId = userId;
        this.whichStatus = whichStatus;
        this.fieldName = fieldName;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        //assert ((batchId != 0) ^ (projectId != 0) ^ (userId != 0) ^ (volumeId !=0)) ; // exactly one is non-zero        
        MessageWriter writer;
        writer = scon.startMessage(T_OPEN_BATCH);
        if (batchId != 0) {
            writer.writeAttribute(A_BATCH_ID, Integer.toString(batchId));
        }
        if (projectId != 0) { // since projectId != 0

            writer.writeAttribute(A_PROJECT_ID, Integer.toString(projectId));
        }
        if (volumeId != 0) { // since projectId != 0

            writer.writeAttribute(A_VOLUME_ID, volumeId);
        }
        if (userId != 0) {
            writer.writeAttribute(A_USERS_ID, Integer.toString(userId));
            writer.writeAttribute(A_FIELD_NAME, fieldName);
        }
        writer.writeAttribute(A_STATUS, whichStatus);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
