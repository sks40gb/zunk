/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the disposal of user session
 * @author ashish
 */
class Command_goodbye implements Command {

    public Command_goodbye() {
    }

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {
        task.setAge(Integer.MAX_VALUE);
        try {
            int sessionId = task.getSessionId();

            if ("YES".equals(action.getAttribute(A_RESTART))) {
                // do nothing; session will be marked inactive
            } else {
                // delete the session, since it's a logout without restart
                // note.  there may not be one, if session was closed previously
                //Tables.session.executeDelete(task,sessionId);
                if (logger.isInfoEnabled()) {
                    logger.info("Deleting Session --> " + sessionId);
                }
                task.executeUpdate(dbTask, SQLQueries.DEL_SESSION + sessionId);                
                Log.print("after delete updateAge=" + task.getUpdateAge());
            }
            return null;
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQL Exception while deleting the user session." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while deleting the user session." , exc);
            return null;
        }
    }

    public boolean isReadOnly() {
        return true;
    }
}
