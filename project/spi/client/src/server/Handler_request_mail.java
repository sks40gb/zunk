/* $Header: /home/common/cvsarea/ibase/dia/src/server/Attic/Handler_request_mail.java,v 1.1.2.4 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageWriter;
import common.MailText;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for request mail message. Return the values for
 * a specific mailsent or mailreceived entry.
 * @see common.MailText
 * @see server.Handler_request_mail
 */
final public class Handler_request_mail extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_mail() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
        String type = action.getAttribute(A_TYPE);
        int id = Integer.parseInt(action.getAttribute(A_ID));

        Statement st = task.getStatement();
        
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_MAIL_DATA);

        MailText data = new MailText();
        ResultSet rs;
        if (type.equals("mailreceived")) {
            rs = st.executeQuery(
                "select U.user_name, MS.recipient_list, MS.text, U2.user_name, MR.status"
                +" from mailreceived MR, users U"
                +" inner join mailsent MS on (MS.mailsent_id = MR.mailsent_id)"
                +" inner join users U2 on (MR.recipient_users_id = U2.users_id)"
                +" where MR.mailreceived_id = "+id+" and U.users_id = MS.sender_users_id");
            if (! rs.next()) {
                throw new ServerFailException("Mailreceived is not found.");
            }
            Log.print("(Handler_request_mail) rec id = " + id);
            data.mailsentUserName = rs.getString(1);
            data.recipientList = rs.getString(2);
            data.text = rs.getString(3);
            Log.print("(Handler_request_mail.run) text is: " + data.text);
            data.recipientUserName = rs.getString(4);
            data.status = rs.getString(5);
        } else {
            rs = st.executeQuery(
                "select U.user_name, recipient_list, text, status"
                +" from mailsent"
                +" join users U on (U.users_id = sender_users_id)"
                +" where mailsent_id = "+id+"");
            if (! rs.next()) {
                throw new ServerFailException("Mailsent is not found.");
            }
            Log.print("(Handler_request_mail) sent id = " + id);
            data.mailsentUserName = rs.getString(1);
            data.recipientUserName = "";
            data.recipientList = rs.getString(2);
            data.text = rs.getString(3);
            data.status = rs.getString(4);
        }

        writer.encode(MailText.class, data);
        writer.endElement();
    }
}
