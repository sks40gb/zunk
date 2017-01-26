/* $Header: /home/common/cvsarea/ibase/dia/src/client/Attic/TaskSendCustomerprice.java,v 1.1.2.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.CustomerpriceData;
import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called by AddEditCustomerprice to send updated customerprice
 * data to the server for storage.
 * @see common.CustomerpriceData
 * @see server.Handler_customerprice
 */
public class TaskSendCustomerprice extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private CustomerpriceData data;

    /**
     * Create an instance of this class and remember the parameter.
     * @param data an instance of <code>common.CustomerpriceData</code>
     * containing the client-updated values for the customerprice table
     */
    public TaskSendCustomerprice(CustomerpriceData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_CUSTOMERPRICE);
        writer.encode(CustomerpriceData.class, data);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("TaskSendCustomerprice unexpected message type: " + ok);
        }
    }
}