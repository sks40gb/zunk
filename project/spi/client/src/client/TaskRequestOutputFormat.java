/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestProjectFields.java,v 1.14.8.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/*
 * Task to Request Output Format
 */
public class TaskRequestOutputFormat extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private int volumeId;

    public TaskRequestOutputFormat(int volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_OUTPUT_FORMAT);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        final ResultSet rs = Sql.resultFromXML(reply);        
        setResult(rs);
    }
}