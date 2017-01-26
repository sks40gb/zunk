/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendMailreceivedData.java,v 1.4.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * ClientTask to send a <code>Map</code> containing containing a
 * mailreceived_id/status pairs to change the status of mailreceived.
 * @see server.Handler_mailreceived_data
 * @see model.MailreceivedManagedModel
 * @see ui.MailPanel
 */
public class TaskSendMailreceivedData extends ClientTask {

    final private Map idMap;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param idMap Map containing mailreceived_id/status pairs with
     * updated statuses
     */
    public TaskSendMailreceivedData(Map idMap) {
        this.idMap = idMap;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        Log.print("TaskSendMailreceivedData.run");
        MessageWriter writer = scon.startMessage(T_MAILRECEIVED_DATA);
        // encode a map of key=mailreceived_id, value=[new mailreceived.status]
        MessageMap.encode(writer, idMap);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_MAILRECEIVED_DATA reply " + ok);
        setResult((Object) ok);
        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendMailreceivedData unexpected message type: " + ok);
        }
    }
}
