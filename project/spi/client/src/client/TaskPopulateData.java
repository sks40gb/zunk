/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskPopulateData.java,v 1.3.6.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.PopulateData;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to import data to an existing project.  The input file must be one
 * of the supported data export formats and the order of the fields must match
 * those defined in projectfields from the Project screen.
 * @see common.PopulateData
 * @see server.Handler_populate_data
 */
public class TaskPopulateData extends ClientTask {

    PopulateData data;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param data a populated instance of PopulateData
     */
    public TaskPopulateData(PopulateData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_POPULATE_DATA);
        writer.encode(PopulateData.class, data);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        if (ok.equals(T_OK)) {
            setResult((Object) ok);
        } else {
            setResult((String) reply.getAttribute(A_DATA));
        }
    }
}