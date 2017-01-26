/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to check whether a batch is assigned for the user 
 * in a status ListingQC.
 *
 * @author bmurali
 */
public class TaskIsAssignedForListingQc extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** User name */
    private String userName;
    /** status */
    private String status;

    public TaskIsAssignedForListingQc(String userName, String status) {
        this.userName = userName;
        this.status = status;
    }

    /**
     * Write the message with parameters and set the result.
     * Message should be in XML format.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_IS_ASSIGNED_LISTING_QC);
        writer.writeAttribute(A_USER_NAME, userName);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
