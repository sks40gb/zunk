/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestRecipientList.java,v 1.2.6.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to receive a list of mail recipients based on the
 * privileges of the requesting user and the projects available to the
 * user.
 * @see server.Handler_request_recipient_list
 */
public class TaskRequestRecipientList extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Request a list of recipients that are valid for the current user.
     */
    public TaskRequestRecipientList() {
    }

    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_RECIPIENT_LIST);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        Log.print("received "+reply.getNodeName());

        if (T_RESULT_SET.equals(reply.getNodeName())) {
            final ResultSet rs = Sql.resultFromXML(reply);
            // store the result so the callback can get it
            setResult(rs);
        } else {
            Log.quit("TaskRequestRecipientList unexpected message type: "+reply.getNodeName());
        }
    }
}
