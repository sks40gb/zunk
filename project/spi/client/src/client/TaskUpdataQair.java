/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;

/**
 * ClientTask to update QAIR for a sampling.
 * @author bmurali
 */
public class TaskUpdataQair extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Sampling Id */
    private int sampling_Id = 0;

    /**
     * Create the instance of this class with the parameter
     * @param sampling_Id Sampling Id 
     */
    public TaskUpdataQair(int sampling_Id) {
        this.sampling_Id = sampling_Id;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = null;
        writer.writeAttribute(A_SAMPLING_ID, sampling_Id);
        writer.endElement();
        writer.close();
        scon.receiveMessage();
    }
}