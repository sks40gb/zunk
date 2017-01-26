/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to put the batch (of a volume) in userqueque for the user.
 * @author bmurali
 */
public class TaskListingQueue extends ClientTask {

    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with following parameters.
     * @param volumeId Volume Id
     * @param userId   User Id
     */
    public TaskListingQueue(int volumeId, int userId) {
        this.volumeId = volumeId;
        this.userId = userId;
    }

    /**
     * Write the message with parameters and set the result.
     * Message should be in XML format.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_LISTING_QUEUE);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
    }
}
