/*
 * TaskCodingManualTracking.java
 *
 * Created on January 28, 2008, 6:47 PM
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for the coding manual for the project with start date and 
 * end date.
 * @author bmurali
 */
public class TaskCodingManualTracking extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Project Id */
    private int projectId;
    /** Start date of the project */
    private String start_date;
    /** End date of the project */
    private String end_date;

    public TaskCodingManualTracking(int projectid, long start_date, long end_date) {
        this.projectId = projectid;
        this.start_date = Long.toString(start_date);
        this.end_date = Long.toString(end_date);
    }

    @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_CODING_MANUAL_TRACKING);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_START_DATE, start_date);
        writer.writeAttribute(A_END_DATE, end_date);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);

    }
}
