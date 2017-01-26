/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendMailsentData.java,v 1.3.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.MailsentData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.ComposeDialog</code> to send a
 * newly-composed <code>mailsent</code> item to the server for storage.
 * @see common.MailsentData
 * @see server.Handler_mailsent_data
 */
public class TaskSendMailsentData extends ClientTask {

    final private MailsentData mailsentData;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param mailsentData an instance of <code>common.MailsentData</code>
     * containing a new <code.mailsent</code> item
     */
    public TaskSendMailsentData(MailsentData mailsentData) {
        this.mailsentData = mailsentData;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        Log.print("TaskSendMailsentData.run mailsentId=" + mailsentData.mailsentId);
        MessageWriter writer = scon.startMessage(T_MAILSENT_DATA);
        writer.encode(MailsentData.class, mailsentData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_MAILSENT_DATA reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendMailData unexpected message type: " + ok);
        }
    }
}
