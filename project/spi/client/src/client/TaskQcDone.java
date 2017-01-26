/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to close the batch for the QC and make it completed.
 * 
 * @author bmurali
 */
public class TaskQcDone extends ClientTask {

    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Field name */
    private String fieldName;
    /** Status or process like Tally, QA, Listing etc.. */
    private String status;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this ClientTask with the following parameters
     * @param projectId Project Id
     * @param volumeId  Voluem Id  
     * @param fieldName Field name
     * @param status    Status
     */
    public TaskQcDone(int projectId, int volumeId, String fieldName, String status) {
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.status = status;
        this.fieldName = fieldName;
    }

    /**
     * Write the message with attributes and set the result.
     * The message should be in XML format.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_TALLY_QC_DONE);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
    }
}
