/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskDeleteMailsent.java,v 1.4.6.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * ClientTask to change mailsent.status to Deleted for the given
 * mailsent.mailsent_id's.
 * @see server.Handler_delete_mailsent
 */
public class TaskDeleteMailsent extends ClientTask {

    final private Map idMap;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this ClientTask and remember the parameter.
     * @param idMap a Map of mailsent.mailsent_id's
     */
    public TaskDeleteMailsent(Map idMap) {
        this.idMap = idMap;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        Log.print("TaskDeleteMailsent.run");
        MessageWriter writer = scon.startMessage(T_DELETE_MAILSENT);
        
        // encode a map of key=mailsent_id, value=""
        MessageMap.encode(writer, idMap);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        //Log.print("T_DELETE_MAILSENT reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("DeleteMailsent unexpected message type: " + ok);
        }
    }
}
