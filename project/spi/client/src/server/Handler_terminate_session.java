/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_terminate_session.java,v 1.4.8.3 2006/03/22 20:27:15 nancy Exp $ */

package server;

import common.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * Handler for terminate_session message.  
 * (From Terminate button on session admin screen.)
 * TBD: the current implementation could be done by executing
 * a delete from the client, but we will have to add logging.
 * @see ui.SessionAdminPage
 */
final public class Handler_terminate_session extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_terminate_session() {}

    public void run (ServerTask task, Element action)
    throws SQLException {
        Log.print("in Handler_terminate_session.run");
        int sessionId = Integer.parseInt(action.getAttribute(A_ID));
        
        // Check that we don't delete our own session
        if (sessionId == task.getSessionId()) {
            // just ignore the request
            Log.print("****Attempt to delete own session ignored.");
            return;
        }

        int usersId = 0;
        Connection con = task.getConnection();
        Statement st = task.getStatement();
        ResultSet rs = st.executeQuery(
            "select users_id from session"
            +" where session_id="+sessionId);
        if (rs.next()) {
            usersId = rs.getInt(1);
        }

        //Tables.session.executeDelete(task, sessionId);
        task.executeUpdate("delete from session where session_id="+sessionId);
        EventLog.logout(task, usersId);
    }
}
