/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_goodbye.java,v 1.13.8.3 2006/03/14 15:08:46 nancy Exp $ */
package server;

import common.Log;

import java.sql.SQLException;
//import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for goodbye (logout) message
 */
final public class Handler_goodbye extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_goodbye() {}

    public void run (ServerTask task, Element action)
    throws GoodbyeException, SQLException {
        // suppress table updates
        task.setAge(Integer.MAX_VALUE);

        int sessionId = task.getSessionId();
        if ("YES".equals(action.getAttribute(A_RESTART))) {
            // do nothing; session will be marked inactive
        } else {
            // delete the session, since it's a logout without restart
            // note.  there may not be one, if session was closed previously
            //Tables.session.executeDelete(task,sessionId);
            task.executeUpdate("delete from session where session_id="+sessionId);
            //EventLog.logout(task);
            Log.print("after delete updateAge="+task.getUpdateAge());
        }
        throw new GoodbyeException();
    }
}
