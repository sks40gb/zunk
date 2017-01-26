/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to close the volume.
 * @author bmurali
 */
public class TaskCloseVolume extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Volume Id */
    private int volumeId;
    /** Project Id */
    private int projectId;
    /** Status for the batch */
    private String status;

    /**
     * Instantiate the object with the following parameters 
     * @param volumeId    Volume Id
     * @param status      Status
     * @param projectId   Poject Id
     */
    public TaskCloseVolume(int volumeId, String status, int projectId) {
        this.volumeId = volumeId;
        this.status = status;
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CLOSE_VOLUME);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_STATUS, status);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
