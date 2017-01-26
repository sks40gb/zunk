/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to get the Fields count
 * @author bmurali
 */
public class TaskFieldValueCount extends ClientTask {

    /** Project Id */
    private int projectId;
    /* Volume Id */
    private int volumeId;
    /* Status (Listing, ListingQC, Tally, TallyQC etc .)*/
    private String status;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with following parameters. 
     * @param projectId Project Id
     * @param volumeId  Volume Id for the project.
     * @param status    belongs to which process.
     */
    public TaskFieldValueCount(int projectId, int volumeId, String status) {
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.status = status;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_FIELD_VALUE_COUNT);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        final ResultSet rs = Sql.resultFromXML(reply);
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            synchronized (this) {
                // force cache flush for rs
                // setResult(rs);
            }

        } else if (T_FAIL.equals(reply.getNodeName())) {
            setResult(reply);

        } else {
            Log.quit("TaskFieldValueCount: unexpected message type: " + reply.getNodeName());
        }
        setResult(rs);
    }
}
