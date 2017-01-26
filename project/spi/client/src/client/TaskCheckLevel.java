/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for checking the level of the bantch.
 * @author bmurali
 */
public class TaskCheckLevel extends ClientTask {

    /** Batch Id */
    private int batchId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    public TaskCheckLevel(int batchId) {

        this.batchId = batchId;

    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_CHECK_LEVEL);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();        
        setResult((String) reply.getAttribute(A_LEVEL));

    }
}
