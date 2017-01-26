/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to get the tally task from the queue.
 * 
 * @author bmurali
 */
public class TaskTallyQueue extends ClientTask {

    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters 
     * @param volumeId   Volume Id
     * @param userId     User Id
     */
    public TaskTallyQueue(int volumeId, int userId) {
        this.volumeId = volumeId;
        this.userId = userId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_TALLY_QUEUE);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_BATCH_QUEUE reply " + ok);
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("ListingQueue unexpected message type: " + ok);
        }
    }
}