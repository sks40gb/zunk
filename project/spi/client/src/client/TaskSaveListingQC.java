/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to save the Listing QC records to the DB.
 * @author bmurali
 */
public class TaskSaveListingQC extends ClientTask {

    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId;
    /** Selected Field of the project */
    private String selectedFieldName;
    /** Status or process */
    private String status;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this ClientTask class and remember the parameters.
     * @param projectId  Project Id that can retrieved from project.projectId 
     * @param volumeId   Voluem Id ; volume.voluem_id from volume to retrieve.
     * @param userId     User Id ; users.users_id from users to retrieve.
     * @param selectedFieldName Field name;
     * @param status     Status or process that should be Listing QC.
     */
    public TaskSaveListingQC(int projectId, int volumeId, int userId, String selectedFieldName, String status) {
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.userId = userId;
        this.selectedFieldName = selectedFieldName;
        this.status = status;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_SAVE_LISTING_QC);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.writeAttribute(A_FIELD_NAME, selectedFieldName);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
