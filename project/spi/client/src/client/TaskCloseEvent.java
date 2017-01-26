/* $Header: /home/common/cvsarea/ibase/dia/src/client/Attic/TaskCloseEvent.java,v 1.1.2.2 2006/03/14 15:08:46 nancy Exp $ */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to update close_timestamp in event.  This timestamp
 * can be used as the ending time in the timesheet report.
 */
public class TaskCloseEvent extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Status */
    private String status;
    /** Volume Id */
    private int volumeId;
    public TaskCloseEvent() {}
        
    public TaskCloseEvent(String status,int volumeId) {
        this.status = status;
        this.volumeId = volumeId;
     }
    
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_CLOSE_EVENT);
        if (volumeId != 0) {
            writer.writeAttribute(A_STATUS, status);
            writer.writeAttribute(A_VOLUME_ID, volumeId);
        }
        writer.endElement();
        writer.close();

        // SplitPaneViewer is closing, so no reply necessary
        Element reply = scon.receiveMessage();
    }

}
