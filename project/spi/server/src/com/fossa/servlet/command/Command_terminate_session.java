/*
 * Command_terminate_session.java
 *
 * Created on November 21, 2007, 12:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the requests for terminating user sessions
 * @author bmurali
 */
public class Command_terminate_session implements Command {

    /** Creates a new instance of Command_terminate_session */
    public Command_terminate_session() {
    }

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {        
        int sessionId = Integer.parseInt(action.getAttribute(A_ID));
        int usersId = 0;        
        Statement getUserIdStatement = dbTask.getStatement();
        ResultSet getUserIdResulSet = null;        
        // Check that we don't delete our own session
        if (sessionId == user.getSessionId()) {
            // just ignore the request
            Log.print("****Attempt to delete own session ignored.");
            return null;
        }       
        try {
            //Get the user id  for selected user name
            getUserIdResulSet = getUserIdStatement.executeQuery(SQLQueries.SEL_TERMINATE_USERID + sessionId);
            if (getUserIdResulSet.next()) {
                usersId = getUserIdResulSet.getInt(1);
            }          
            //Delete the user session
            user.executeUpdate(dbTask, SQLQueries.DEL_TERMINATE_SESSION + sessionId);            
            EventLog.logout(user, dbTask, usersId);

        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "Exception while terminating the user session." , ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
