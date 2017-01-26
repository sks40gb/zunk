/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to start other activity based on the <code>status.</code>.
 * @see ui.SplitPaneViewer
 * @see ui.QAProofReadingpage
 * @author sunil
 */
public class TaskStartOtherActivity extends ClientTask {

    /** User Id */
    private int userId;
    /** Status it may be Listing, Tally, TallyQC, QA etc .. */
    private String status;
    /** A short notes */
    private String notes;
    //Time duration of opening activity.
    private long open_timestamp;
    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class with the following parameters
     * @param userId  User Id; who is opening the activity.
     * @param status  Status
     * @param notes   Notes
     * @param open_timestamp Time duration
     */
    public TaskStartOtherActivity(int userId, String status, String notes, long open_timestamp) {
        this.userId = userId;
        this.status = status;
        this.notes = notes;
        this.open_timestamp = open_timestamp;
    }
    
    // Prepares the xml format for sending to server.   
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_START_OTHER_ACTIVITY);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.writeAttribute(A_STATUS, status);
        writer.writeAttribute(A_NOTES, notes);
        writer.writeAttribute(A_OPEN_TIMESTAMP, Long.toString(open_timestamp));
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult((Object) reply);

    }
}
