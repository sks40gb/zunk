
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to create the batches for the Tally process.
 * @author bmurali
 */
public class TaskCreateTallyBatches extends ClientTask{
    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId; 
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Instantiate the object with following parameters 
     * @param volumeId Volume Id for which the batches are being created.
     * @param userId   User Id who is creating the batches.
     */
    public TaskCreateTallyBatches(int volumeId,int userId){
       this.volumeId = volumeId;
       this.userId = userId;
    }
    
     @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CREATE_TALLY_BATCH);

        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.endElement();
        writer.close(); 
        
        Element reply = scon.receiveMessage();
         setResult(reply);
    }
}
