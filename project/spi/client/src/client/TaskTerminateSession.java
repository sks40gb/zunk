/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskTerminateSession.java,v 1.5.8.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.msg.MessageConstants;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>ui.SessionAdminPage</code> to close a user's session.
 * @see ui.SessionAdminPage
 * @see server.Handler_terminate_session
 */
public class TaskTerminateSession extends ClientTask implements MessageConstants {

    int sessionId;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param sessionId the session.session_id of the user's client session
     */
    public TaskTerminateSession(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_TERMINATE_SESSION);
        writer.writeAttribute(A_ID, Integer.toString(sessionId));
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
