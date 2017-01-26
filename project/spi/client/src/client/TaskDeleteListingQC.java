/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to delete the Listing QC
 * @author bmurali
 */
public class TaskDeleteListingQC extends ClientTask {

    /** Listing QC Id */
    private int listingQcId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with the parameter
     * @param listingQcId Listing QC Id which is going to be deleted.
     */
    public TaskDeleteListingQC(int listingQcId) {
        this.listingQcId = listingQcId;
    }

    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_DELETE_LISTING_QC);
        writer.writeAttribute(A_LISTING_ID, listingQcId);
        writer.endElement();
        writer.close();
        //reply is not required.
        Element reply = scon.receiveMessage();
    }
}
