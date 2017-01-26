

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 *ClientTask to create the new batches for the Listing.
 * @author bmurali
 */
public class TaskCreateListingBatches extends ClientTask {

    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with the following parameters 
     * @param volumeId   Volume id
     * @param userId     Project Id
     */
    public TaskCreateListingBatches(int volumeId, int userId) {
        this.volumeId = volumeId;
        this.userId = userId;
    }

    @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_CREATE_LISTING_BATCH);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
