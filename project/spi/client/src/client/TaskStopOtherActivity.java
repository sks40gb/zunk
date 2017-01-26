/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;

/**
 * ClientTask to stop the activity.
 * @see ui.ShowBreakDialog
 * @see ui.TimerDemo
 * @author yuvaraj
 */
public class TaskStopOtherActivity extends ClientTask {

    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;
    /** Closing time in a long data type*/
    private long close_timestamp;
    private int event_break_id = 0;

    /**
     * Create the instance of this class with the following parametes
     * @param event_break_id 
     * @param close_timestamp
     */
    public TaskStopOtherActivity(int event_break_id, long close_timestamp) {
        this.close_timestamp = close_timestamp;
        this.event_break_id = event_break_id;
    }
    //Sends request to server in xml.

    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_STOP_OTHER_ACTIVITY);
        writer.writeAttribute(A_EVENT_BREAK_ID, event_break_id);
        writer.writeAttribute(A_CLOSE_TIMESTAMP, Long.toString(close_timestamp));
        writer.endElement();
        writer.close();
        scon.receiveMessage();
    }
}
