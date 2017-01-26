/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestMail.java,v 1.1.2.3 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.MailText;
import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Get values for a specific mailsent or mailreceived entry.
 * @see common.MailText
 * @see server.Handler_request_mail
 */
public class TaskRequestMail extends ClientTask {

    private String id;
    private String type;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create and instance of this class and remember the parameters.
     * @param type "mailreceived" or "mailsent"
     * @param id the mailreceived.mailreceived_id or mailsent.mailsent_id
     * of the mail being requested
     */
    public TaskRequestMail(String type, String id) {
        this.id = id;
        this.type = type;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into <code>common.MailText</code>.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_MAIL);
        writer.writeAttribute(A_ID, id);
        writer.writeAttribute(A_TYPE, type);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        if (T_MAIL_DATA.equals(reply.getNodeName())) {
            MailText data = new MailText();
            // fill in the int and String fields of the MailreceivedData
            MessageReader.decode(reply, data);
            // store the result so the callback can get it
            setResult(data);
        } else {
            Log.quit("Mailreceived unexpected message type: " + reply.getNodeName());
        }
    }
}
